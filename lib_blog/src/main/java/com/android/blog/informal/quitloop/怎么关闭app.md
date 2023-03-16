

如果主线程的 Looper#loop() 退出了， APP 还会继续运行吗？

如果有后台 service 正在运行，loop 停止后，service 会紧跟着停止吗？

Activity 的创建

Activity 的保存

Activity 的退出与销毁

杀死进程

关闭进程中最后一个 Activity

应用的几种退出方式

主动 finish()

发生 crash 崩溃

低内存/adb/多任务等系统行为

嗯~ 这个过程稍微有那么一点点绕。。。和 Handler 机制有关系（重点是 native 层）

先说结论：事件分发是运行在 main 线程

再来说说过程

首先我们要知道一点，native 层也有一个消息队列，并且，native 层的消息会被优先处理的，它是依赖 Java 层的 Looper#loop() 来驱动

在 Java 层的 MessageQueue#next() 方法获取到一条新消息之前，会先保证 native 层的消息已经被处理完

触摸事件就是利用 native 层的消息队列的

在 "sockets[1] 回传给 APP" 一小节中提到，用于监听 IMS 事件的 socketfd 会被添加到了 native 层的 Looper 里面

那么当事件到来时，由于 native 层会被优先处理，因此，消息会交给 native 层的 NativeInputEventReceiver 类

然后通过 CallVoidMethod 手段，native 层反调 java 层的方法，最终把事件传递到 ViewRootImpl，接着交给 DecorView#dispatchPointerEvent() 分发

事件分发特殊的地方在于， native 拿到消息后，主动调用 java 把消息抛上来

但是，这一切，都还是发生在主线程的 Looper#loop() 当中的

这也是为啥不能在消息分发 dispatchXXXEvent 方法中，执行耗时操作的原因，因为会导致主线程的消息队列中，后续所有消息都无法被消费

说了稍微有点多，可能没讲清楚，如果感觉哪个点不好理解的话可以继续私信我~

总结，native 层 Looper 接收到触摸消息后，传递给 Java 层的 ViewRootImpl 处理，过程是发生在 ActivityThread 的 Looper#loop() 中，也就是运行在主线程中

另外再补充两点

ActivityThread 类中，大多是和 AMS 打交道，各个组件（Activity、Service等）的控制基本都在这

ViewRootImpl 类中，大多数是和 WMS/IMS 打交道，控制 View 的逻辑基本上都在这
