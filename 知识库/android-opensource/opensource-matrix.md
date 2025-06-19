
# TraceCanary

TraceCanary 分为帧率监控、慢方法监控、ANR监控 以及 启动耗时 这4个功能。这些功能基本都需要 插桩 去 trace 过程中每个函数的调用。

在插桩之外，还需要监控主线程 MessageQueue(LooperMonitor) 以及 Choreographer(UIThreadMonitor)，从里面抽象出一个可以通知 Message 开始执行、执行完成、Choreographer开始渲染的接口(LooperObserver)。

重点就是 插桩插件 以及 LooperMonitor、UIThreadMonitor 是如何实现的

## UIThreadMonitor

UIThreadMonitor 现在被 matrix 标注为弃用了，但没写弃用原因，它负责两件事情：

1. 设置 Looper#Printer，来判断 Message 的执行起止时间。
   - 根据 dispatch 打印的开始/结束时间，来判断主线程是否被阻塞
2. Hook 掉 Choreographer 的 Input、Animation、Traversal 这三个的 Callback Queue
   - Input callback 耗时	比如触摸事件处理
   - Animation callback 耗时 包括 ViewPropertyAnimator 等动画逻辑
   - Traversal callback 耗时，UI 布局测量绘制流程
   - 是否是 vsync 帧，是否正常一帧
   - IntendedFrameTimeNs，目标 vsync 时间
   - InputEventCost，输入事件分发耗时（Android 6.0+ 支持）

监控 FPS（通过 vsync 和帧间耗时计算），最后将这些数据抛出给各个 Tracer 作为判断的依据。

hook 掉的 Choreographer 三个回调对列，是 Android 图形系统在一帧内执行的三个阶段

1. Input，最早执行，对应输入事件处理，比如触摸事件、键盘输入等分发
2. animation，第二阶段，处理/播放动画，ViewPropertyAnimator、ObjectAnimator
3. Traversal 是最后阶段，执行 View 的界面绘制三部曲，measure()、layout()、draw()

上面这三步得到每一帧中三段关键逻辑的耗时，对比单帧时长，60fps 设备 frameIntervalNanos = 16.67 ms，初始化的时候会反射读取 Choreographer 的 mFrameIntervalNanos

- 如果一帧总耗时 > 16.6ms，就掉了 1 帧 
- 耗时 > 33ms → 掉 2 帧，以此类推

## LooperMonitor

利用 Looper.setMessageLogging() 监听主线程每条消息的分发开始与结束

负责 主线程消息耗时统计、卡顿检测、堆栈采样、日志记录、异常分析，其他还有 

- 保留最近一段时间的消息分发耗时、顺序等信息
- 有个 idle 任务，防止 Printer 被其他库覆盖，定期恢复，牛逼

## AppMethodBeat

编译期对大部分的函数 进行插桩，方法开始结束前分别调用 AppMethodBeat 的 i、o 函数

运行期，配合 LooperMonitor 使用，looper 里面的每个消息开始、结束都会通知 AppMethodBeat

msg 执行结束后，一个函数调用链以及函数耗时就统计出来了，解决了 msg 不知道哪个函数耗时的问题

只在 ui thread，即 looper thread 生效，子线程不管

## IdleHandlerLagTracer

https://github.com/Tencent/matrix/blob/master/matrix/matrix-android/matrix-trace-canary/src/main/java/com/tencent/matrix/trace/tracer/IdleHandlerLagTracer.java

- addIdleHandler 把自己注册为主线程消息队列的 IdleHandler 
- 然后在重写的 queueIdle 函数中，统计 idle 任务的耗时 
- 还有一些去重的处理，防止自己统计自己

## LooperAnrTracer

核心还是监控 Looper 消息处理时间，超过长时间阈值时采样调用栈分析 ANR。

## SignalAnrTracer

主要工作在 trace-canary.so 库里面，SignalHandler.cc 文件，监听 SIGQUIT 信号，用来判定是否发生了 ANR，如果是 ，dump trac

## StartupTracer

监控应用的冷启动和温启动

```
firstMethod.i       LAUNCH_ACTIVITY   onWindowFocusChange   LAUNCH_ACTIVITY    onWindowFocusChange
^                         ^                   ^                     ^                  ^
|                         |                   |                     |                  |
|---------app---------|---|---firstActivity---|---------...---------|---careActivity---|
|<--applicationCost-->|
|<--------------firstScreenCost-------------->|
|<---------------------------------------coldCost------------------------------------->|
.                         |<-----warmCost---->|
```

Application 的创建时间，由 ActivityThreadHacker 提供