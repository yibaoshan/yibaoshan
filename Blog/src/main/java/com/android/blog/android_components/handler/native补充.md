

- 大部分的binder用来跨进程将消息送到目标进程的消息队列
- MessageQueue 实际存在于 native 层，名为 NativeMessageQueue
- 在 native 创建 NativeMessageQueue 的同时，也会创建一个 Looper ，它用于处理 native 注册的自定义 Fd 引起的 Request 消息

从后台数据来看，



这都不重要

###  native Handler 核心流程

1. Java 层的 MessageQueue 初始化方法中，实际上是调用 nativeInit() 方法，在 native 层创建了一个 NativeMessageQueue 对象
2. 创建 NativeMessageQueue 对象的同时，也会创建一个 Looper ，它用于处理 native 注册的自定义 Fd 引起的 Request 消息
3. 创建 native Looper 的过程中，调用 eventfd() 生成 mWakeEventFd 它是后续用于唤醒该looper的核心，eventfd()函数与多路I/O复用函数epoll()是消息机制的底层核心
4. 在创建 native Looper 的最后一步调用了 rebuildEpollLocked() 方法，在其中调用 epoll_create()初始化了一个epoll实例 mEpollFd ，然后调用 epoll_ctl() 方法将 mEpollFd 传递给epoll
5. 至此，native handler 创建完成


### Java层消息循环与阻塞

1. Java 层的 looper 会轮询 messagequeue 的 next 方法
2. 在 next 方法中，首先会调用 nativePollOnce() 方法，这是个阻塞调用，最终调用到 native 层的 pollInner() 方法
3. 在 pollInner() 方法中会调用 epoll_wait() 方法，这是一个阻塞调用，只有到注册的fd有有效消息时才会返回，记得在创建 native looper 的最后一步中，将 mWakeEventFd 注册到了 mEpollFd 中，所以如何 mWakeEventFd 有消息就返回，解除阻塞
4. nativePollOnce() 方法返回以后，接着去读取 messagequeue 的消息，返回给looper去分发

### Java层的消息发送/唤醒机制

1. 使用 Java 层的 Handler 发送消息时，最终会调用到消息队列的 enqueueMessage() 方法
2. 如果插入的消息是异步消息或者需要马上执行，那么调用 nativeWake() 执行唤醒
3. 唤醒的操作在 native 层，唤醒的操作也很简单，调用 write() 方法向 mWakeEventFd 写入 1
4. 由于为它注册了epoll监听，所以一旦有来自于 mWakeEventFd 的新内容，NativePollOnce()方法中中的epoll_wait()调用就会返回，这里就已经起到了唤醒队列的作用


### Java层消息的分发处理

1. 调用消息的 target，也就是 Handler 的 dispatchMessage() 方法来分发消息
2. 如果msg的callback不为空，说明是runnable，调用 handleCallback 方法运行该runnable
3. 否则，判断handler 的callback是否为空，不为空的话调用 callback的handleMessage分发，注意，该方法有bool类型返回值
4. 如果返回true，那么接下来handler 自身的 handleMessage不执行，否则，执行 handler 的 handleMessage 方法

### native层的消息循环与阻塞

1. Java 层中的 next() 方法中，首先会调用 nativePollOnce() 方法，一来是为了检测有没有消息，二来是为了优先处理 native 层消息
