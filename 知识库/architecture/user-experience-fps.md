
FPS 可以用来衡量一个页面的流程程度，如果页面 FPS 比较低，并不一定意味着发生卡顿，但是，发生卡顿的话 FPS 一定不高

一般来说，即使 FPS 只有 30 帧，也能保证基础的用户体验，但如果发生掉帧，以 60FPS 的设备距离，某次渲染大于 300ms 用户就可以感知到卡顿。

所以，是否发生卡顿，可以被定义为掉帧次数，微信团队对掉帧的判定的标准是

- 小于 3 次，非常流畅
- 3~9 次，还算流程
- 10~24 次，能感知有卡顿，但还能接受，能用
- 25~42 次，卡顿频繁，如果维持一段时间，用户可能就烦了，什么垃圾应用
- 42 次以上，卡顿严重

# 卡顿监控

对于卡顿的解决，我们的思路是 重监控

虽然 卡顿、ANR 的原因有很多，但往往是因为某个函数执行超时，我们如果把耗时长的函数找出来并解决，那也就能大大降低 ANR 的概率。

从监控主线程的实现原理上，主要分为两种：

1. Handler 机制，监控主线程 Looper dispatchMessage 的执行耗时。（BlockCanary）

2、基于 Choreographer，监控相邻两次 Vsync 事件通知的时间差（ArgusAPM、LogMonitor）

我们目前的方案是通过设置 Looper#setMessageLogging() 设置 Printer，监听每个 msg 执行的时间，超过 3s 认为发生了卡顿

局限性是，这个方案只能知道是哪个 msg 执行过长，不知道具体是哪个函数，一个 msg 里面可能会执行好多个函数，比如系统消息生命周期的调度，我们需要一种手段，知道具体是哪个函数执行过长。

```java
public static void loop() {
    ...
    for (;;) {
        ...
        // This must be in a local variable, in case a UI event sets the logger
        Printer logging = me.mLogging;
        if (logging != null) {
            logging.println(">>>>> Dispatching to " + msg.target + " " +
                    msg.callback + ": " + msg.what);
        }
        msg.target.dispatchMessage(msg);
        if (logging != null) {
            logging.println("<<<<< Finished to " + msg.target + " " + msg.callback);
        }
        ...
    }
}
```

另一个基于 Choreographer 的方案

```java
Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {
    @Override    
    public void doFrame(long frameTimeNanos) {
        if(frameTimeNanos - mLastFrameNanos > 100) {
            ...
        }
        mLastFrameNanos = frameTimeNanos;
        Choreographer.getInstance().postFrameCallback(this);
    }
});
```

局限性和 Handler 类似，能够感知发生了卡顿，但不知道具体是哪个函数执行时间过长

关于方法监控，腾讯 matrix 介绍了两种方案：

1、在应用启动时，默认打开 Trace 功能（Debug.startMethodTracing），应用内所有函数在执行前后将会经过该函数（dalvik 上 dvmMethodTraceAdd 函数 或 art 上 Trace::LogMethodTraceEvent 函数）， 通过hack手段代理该函数，在每个执行方法前后进行打点记录。

2、修改字节码的方式，在编译期修改所有 class 文件中的函数字节码，对所有函数前后进行打点插桩。

第一种方案，最大的好处是能统计到包括系统函数在内的所有函数出入口，对代码或字节码不用做任何修改，所以对apk包的大小没有影响，但由于方式比较hack，在兼容性和安全性上存在一定的风险。

第二种方案，利用 Java 字节码修改工具（如 BCEL、ASM、Javassis等），在编译期间收集所有生成的 class 文件，扫描文件内的方法指令进行统一的打点插桩，同样也可以高效的记录函数执行过程中的信息，相比第一种方案，除了无法统计系统内执行的函数，其它应用内实现的函数都可以覆盖到。而往往造成卡顿的函数并不是系统内执行的函数，一般都是我们应用开发实现的函数，所以这里无法统计系统内执行的函数对卡顿的定位影响不大。此方案无需 hook 任何函数，所以在兼容性方面会比第一个方案更可靠。

matrix 最终选择了修改字节码的方案，来实现 Matrix-TraceCannary 模块，解决其它方案中卡顿堆栈无耗时信息的主要问题，来帮助开发者发现及定位卡顿问题。

插桩过程有几个关键点：

1、选择在该编译任务执行时插桩，是因为 proguard 操作是在该任务之前就完成的，意味着插桩时的 class 文件已经被混淆过的。而选择 proguard 之后去插桩，是因为如果提前插桩会造成部分方法不符合内联规则，没法在 proguard 时进行优化，最终导致程序方法数无法减少，从而引发方法数过大问题。

2、为了减少插桩量及性能损耗，通过遍历 class 方法指令集，判断扫描的函数是否只含有 PUT/READ FIELD 等简单的指令，来过滤一些默认或匿名构造函数，以及 get/set 等简单不耗时函数。

3、针对界面启动耗时，因为要统计从 Activity#onCreate 到 Activity#onWindowFocusChange 间的耗时，所以在插桩过程中需要收集应用内所有 Activity 的实现类，并覆盖 onWindowFocusChange 函数进行打点。

4、为了方便及高效记录函数执行过程，我们为每个插桩的函数分配一个独立 ID，在插桩过程中，记录插桩的函数签名及分配的 ID，在插桩完成后输出一份 mapping，作为数据上报后的解析支持。
