
目前行业内崩溃率的底线是千分位，比如 0.5% 是个坎

我负责的项目体量都不大，稳定性监控方案都是使用腾讯免费的 bugly

## 常见异常

- binder 携带数据过大导致崩溃，注意，此时 system_server 进程可能直接 kill 我们进程，这种情况下连错误日志都不会有

### Activity

- ActivityNotFoundException
  - 隐式 startActivity，可能因为 action 改动，导致找不到 Activity
  - 用户 Root 后删除系统类，比如直接把相册删除了，也会导致找不到类
  - 解决方案：跳转页面由全局路由负责，路由中加入 try catch，必要时 hook startActivity()
- ClassCastException
  - intent 获取参数时，对方未按要求传参，此时可能会发生类型转换异常
  - 解决方案：基本类型尽可能使用 string 传递，然后转换时做检查。引用类型参数一定要 try catch

### 其他

- binder 携带大数据，1M - 8k
  - 注意，此时 system_server 进程可能直接 kill 我们进程，这种情况下连错误日志都不会有

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