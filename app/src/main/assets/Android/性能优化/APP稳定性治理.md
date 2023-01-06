
APP 的稳定性治理包括 ANR 、Java / Native Crash、内存溢出（OOM）等几个方面

就 Crash 而言，目前行业内崩溃率的底线是千分位，比如 0.5% 是个坎

我负责的项目体量都不大，稳定性监控方案都是使用腾讯免费的 bugly

## 常见异常

- binder 携带数据过大导致崩溃，注意，此时 system_server 进程可能直接 kill 我们进程，这种情况下连错误日志都不会有

## Activity

### ActivityNotFoundException

- 隐式 startActivity，可能因为目标 action 改动，导致找不到 Activity
- 用户 Root 后删除系统类，比如直接把相册删除了，也会导致找不到类

> 解决方案：跳转页面由全局路由负责，路由中加入 try catch，必要时 hook startActivity()

### ClassCastException

- intent 获取参数时，对方未按要求传参，此时可能会发生类型转换异常

> 解决方案：基本类型尽可能使用 string 传递，然后转换时做检查。引用类型参数一定要 try catch

### 其他

- binder 携带大数据，1M - 8k
  - 注意，此时 system_server 进程可能直接 kill 我们进程，这种情况下连错误日志都不会有

## 线程资源超限 / 死锁

在 Android 中

- 如果主线程发生死锁，那么通常会以 ANR 结束 app 的生命周期
- 如果是两个子线程，那么就会白白浪费 cpu。就像一颗“肿瘤”，永远藏在 app 中

### 死锁条件

在业务开发中，有两把锁并且有两个及以上的线程嵌套使用，这就形成了循环依赖

运行过程中有可能会导致死锁发生，如下：

```
public fun main() {
    val o1,o2;
    thread {
        synchronized(o1) sleep(10);
        synchronized(o2) print("t1 take o2")
    }
    thread {
        synchronized(o2) sleep(10);
        synchronized(o1) print("t2 take o1")
    }
}
```

### 检测死锁

我们知道，在使用 synchronized 做并发同步时

如果某个线程多次获取不到锁，线程就会进入悲观锁状态，然后就会尝试进入阻塞状态，避免进一步的 cpu 资源消耗

那我们可以通过遍历所有线程，查看是否处于 block 状态，来进行死锁判断的第一步

如果有两个线程长期处于 block 阻塞的状态，可以猜测这两条线程就有可能产生死锁（只是有可能）

```
val threads = Thread.currentThread().threadGroup.toArray;
threads.forEach {
    if(it?.isAlive == true && it.state == Thread.State.BLOCKED){
       进入死锁判断
    }
}
```

如果判断可能发生了死锁，那么下一步，就是要检测当前线程所持有的锁

如果确定两个线程同时持有对方所需要的锁，那一定要有一方先释放资源才能解除死锁

### 解决死锁

Java 层没有相关 api 可以让我们知道：当前线程想要请求的锁，所以只能在 native 层想想办法

我们可以通过 Thread#GetContendedMonitor() 函数查看当前线程持有的“锁”，然后检测 Java 传入的线程中，有没有发生死锁

具体操作可以查看参考资料列表中《死锁监控与其背后的小知识》这篇文章

### 线程资源超限

线程数超限是一种典型的 OOM 异常

```
java.lang.OutOfMemoryError: pthread_create (1040KB stack) failed: Try again
```

即 proc/pid/status 中记录的线程数，超过了内核限制

每个设备可使用的最大线程数量都不一样，我们可以使用 /proc/sys/kernel/threads-max 查看当前系统规定的最大线程数

我手里的 Pixel 3 最大线程数为 26155，华为某些机型控制在 500 左右

线程数超限的解决方案比较简单：使用线程池来规范线程的使用

对于三方库，像 Glide 之类的都支持配置线程池，不支持自定义配置的，我们可以 hook 字节码来收敛线程的创建

## OOM

OutOfMemoryError 内存溢出是一类难以解决的 Crash

上报的堆栈信息，用户日志等都没有太多的参考价值

因为内存已经在其他位置增加到内存溢出的临界点，而上报的位置很可能不存在内存问题

而引起内存溢出主要的原因有：

- 文件描述符(fd)数目超限，即proc/pid/fd下文件数目突破/proc/pid/limits中的限制。可能的发生场景有：
  - 短时间内大量请求导致socket的fd数激增，大量（重复）打开文件等
- 线程数超限，即proc/pid/status中记录的线程数（threads项）突破/proc/sys/kernel/threads-max中规定的最大线程数。可能的发生场景有：
- app内多线程使用不合理，如多个不共享线程池的 OKhttpclient 等等
- 传统的java堆内存超限，即申请堆内存大小超过了 Runtime.getRuntime().maxMemory()
  - 内存泄漏累积导致
  - 申请大对象
- （低概率）32为系统进程逻辑空间被占满导致OOM

## 内存泄漏

## 字节码 hook 解决三方库崩溃

如果因为三方库的代码错误引发的奔溃，而我们又没有源码的情况下

那就只有选择 hook 一条路了，hook 分为两种，字节码 hook 和运行时 hook

对于三方库，通常情况下，我们会选择字节码 hook 的方案，框架的话可以选择：

- 饿了么的 lancet
- 滴滴的 booster

使用方式参见 github 首页说明文件

## 参考资料

- [货拉拉Android稳定性治理](https://juejin.cn/post/7100743641953468452)
- [美团外卖Android Crash治理之路](https://juejin.cn/post/6844903620920492046)
- [得物App Android Crash治理演进](https://blog.csdn.net/SmartCodeTech/article/details/119958923)
- [Android 无所不能的 hook，让应用不再崩溃](https://juejin.cn/post/7034178205728636941)
- [ASM 字节码插桩 ：线程治理](https://juejin.cn/post/7165499628304449567)
- [不可思议的OOM](https://www.jianshu.com/p/e574f0ffdb42)
- [Android性能优化 - 死锁监控与其背后的小知识](https://juejin.cn/post/7159784805293359141)
- [黑科技！让Native Crash 与ANR无处发泄！- Pika](https://juejin.cn/post/7159784805293359141)