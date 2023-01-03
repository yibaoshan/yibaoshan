
四大组件中，除了 Activity 外，其他耗时过久都会触发 ANR（AppNotResponding）

- Service：比如前台服务在 20s 内未执行完成，后台服务 Timeout 时间是前台服务的 10 倍，200s；
- BroadcastQueue：比如前台广播在 10s 内未执行完成，后台 60s
- ContentProvider：内容提供者,在 publish 过超时 10s;
- InputDispatching: 输入事件分发超时 5s，包括按键和触摸事件。

最常见的还是 Input 超时，因此，原则上不要在 ui 线程执行任何耗时操作

我的项目方案，腾讯的 bugly

## ANR 发生原因

应用层导致 ANR：

- 函数阻塞：如死循环、主线程IO、处理大数据
- 锁出错：主线程等待子线程的锁

系统导致 ANR：

- CPU 被抢占负载高：一般来说，前台在玩游戏，可能会导致你的后台广播被抢占，无法得到执行
- 系统服务无法及时响应：比如获取系统联系人等，系统的服务都是Binder机制，服务能力也是有限的，有可能系统服务长时间不响应导致ANR

## 如何避免 ANR

- debug 包启用 StrictMode 严格模式
  - setThreadPolicy，设置检测主线程的磁盘读/写、网络策略
  - setVmPolicy，检测 Activity 、SQL 泄漏
  - StrictMode#enableDefaults()启用全部

几个常见的 ANR

- SP 在初始化、commit、apply 导致的 ANR
  - 尽可能要拆分 sp 的大小，比如，是否展示引导页的key，单独使用一个文件

- 主线程等待子线程锁释放导致消息队列阻塞，没人处理
  - 比如 gsy/exo 播放器，release 时 ANR ，原因是主线程等待子线程锁释放

- View#getDrawingCache 进行 bitmap 转换
  - 保存图片到相册需要视图截图，从上报的 ANR 信息来看获取视图缓存时有阻塞
  - 开子线程执行

## ANR 监控原理

### 1、监听anr目录的变化

使用 FileProvider 监听 /data/anr/traces.txt 文件的变化，并捕获现场进行上报。

不过 Android 6.0 以上版本系统文件权限收紧后，没有读取这个文件的权限

之前我们采用这个监控方案导致大量高版本设备ANR问题漏报。

### 2、主线程超时监测

开启一个子线程定期 post 一个 message 到主线程

每隔一段时间（比如5秒）监测该 message 是否被消费掉

如果没有被处理，则说明主线程被卡住，可能发生了ANR，再通过系统服务获取当前进程的错误信息，判断是否有ANR发生

但这个会存在大量漏报的情况，并且轮询的方案性能不佳。

### 3、监听 SIGQUIT 信号

系统服务在触发ANR后，会发送一个SIGQUIT信号到应用进程来触发 dump traces

在应用侧我们可以监听 SIGQUIT 信号来判断是否发生了 ANR

具体实现需要 native 开发，注册 Signal Handler 收到消息后，需要重新向 Signal Catcher线程发送一个SIGQUIT信号

否则原先的 Signal Catcher线程中的sigwait就不再能收到SIGQUIT了，原本的dump堆栈的逻辑就无法完成

另外，SIGQUIT 可能会误报

发生ANR的进程一定会收到SIGQUIT信号；但是收到SIGQUIT信号的进程并不一定发生了ANR。

解决方案是

在ANR弹窗前，会执行到 makeAppNotRespondingLocked方法中，在这里会给发生ANR进程标记一个NOT_RESPONDING的flag。

而这个flag我们可以通过ActivityManager来获取：

```
private static boolean checkErrorState()
```

监控到SIGQUIT后，我们在20秒内（20秒是ANR dump的timeout时间）不断轮询自己是否有NOT_RESPONDING对flag

一旦发现有这个flag，那么马上就可以认定发生了一次ANR。

第3种方案准确率高，性能损耗小，也是业界目前主流 APP 采用监控方案。matrix、xCrash都在使用这种方案

Crash sdk 在监听SIGQUIT信号后，会调用art虚拟机内部dump堆栈的接口，获取ANR traces信息，包含ANR进程中所有线程的堆栈

## 实现原理

### 1、埋定时炸弹

在 startService 流程中，在通知app进程启动Service之前，会进行预埋一个炸弹

也就是延迟发送一个消息给AMS的mHandler。

当 AMS 内部的 Handler收到 SERVICE_TIMEOUT_MSG 这个消息时，就认为 Service 超时了，触发ANR。

也就是说，特定时间内，没人来拆这个炸弹，这个炸弹就会爆炸。

### 2、拆炸弹

在app进程这边启动完Service之后，需要IPC通信告知AMS我这边已经启动完成了。

AMS.serviceDoneExecuting()->ActiveServices.serviceDoneExecutingLocked()

就是把之前延迟发送的SERVICE_TIMEOUT_MSG消息给移除掉，也就是拆炸弹

### 3、引爆炸弹

最终由 ProcessRecord#appNotResponding() 方法来执行 ANR

- 每个进程都会创建一个SignalCatcher守护线程，用于捕获SIGQUIT、SIGUSR1信号，收集的第一个，也是一定会被收集到的进程，就是发生ANR的进程
- 针对几种特殊情况：正在重启、已经处于ANR流程中、正在crash、app已经被killed和app已经死亡了，不用处理ANR，直接return。
- 后台ANR跟前台ANR表现不同，前台ANR会弹出无响应的Dialog，后台ANR会直接杀死进程
- 发生ANR时，为了方便定位问题，会dump很多信息到Trace文件中。而Trace文件里包含着与ANR相关联的进程的Trace信息，因为产生ANR的原因有可能是其他的进程抢占了太多资源，或者IPC到其他进程的时候卡住导致的
- 拿到需要dump的所有进程的pid后，AMS开始按照firstPids、nativePids、extraPids的顺序dump这些进程的堆栈

```
//com.android.server.am.ProcessRecord.java
void appNotResponding(...) {
    ArrayList<Integer> firstPids = new ArrayList<>(5);
    SparseArray<Boolean> lastPids = new SparseArray<>(20);
    ...
}
```

### Input 触发 ANR

Service、Broadcast、Provider 组件类是埋炸弹、拆炸弹的过程

而 Input 稍微有点特殊，它是在当前分发过程中，检测上一次是否有超时

如果单次消费超时，只要后续没有事件，不判定为 ANR

超时的检测逻辑发生在 InputDispatcher#handleTargetsNotReadyLocked()

确认发生 ANR 后，将请求发送到 AMS 的 inputDispatchingTimedOut() 方法，最终还是由 appNotResponding() 方法执行

## 参考资料

- [ANR 触发、监控、分析 一网打尽 - 潇风寒月](https://juejin.cn/post/7171684761327370277)
- [钉钉 ANR 治理最佳实践](https://juejin.cn/post/7181731795439157306)
- [西瓜卡顿 & ANR 优化治理及监控体系建设 - 字节跳动团队](https://blog.csdn.net/ByteDanceTech/article/details/119769715)
- [手把手教你高效监控ANR](https://zhuanlan.zhihu.com/p/439930689)