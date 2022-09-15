### 理解 Linux eventfd

Linux 系统中，把一切都看做是文件，当进程打开现有文件或创建新文件时，内核向进程返回一个文件描述符

fd是文件描述符的缩写，每一个fd会与一个打开的文件相对应，既然是文件，那么就只有读和写两种操作

```cpp
fd = open(pathname, flags, mode)//根据路径打开一个文件
readLen = read(fd, buf, count)//buf表示内存空间，count表示希望读取的字节数，返回值为实际读到的字节数
writeLen = write(fd, buf, count)//参数含义同上
status = close(fd)
```


监听的是可读可写事件


这里的 wait() 和 Java 中 Object.wait() 可不一样，Object#wait() 必须要写在同步代码块里面，同步代码块就意味着要竞争锁，这是的CPU

以 eventfd 举例，任意一个地方调用了 write(fd) 方法向该 fd 写入值时，此时这个事件源里面就有值供其他程序读取了，那么注册该 fd 的监听回调便会收到内核发送的一条可读事件，表示该 eventfd 支持读值了


同样，我们在读写 fd 的时候，可能会遇到阻塞，对于 eventfd 来说，没有数据可以读的时候，就阻塞了


对于 Java Handler 来说，只要收到来自内核的可读事件，说明此时消息队列有消息了，那么 nativePollOnce() 方法会释放返回，继续执行获取消息的 next() 方法

### 理解 Linux epoll


之后我们来讨论I/O的操作，通过read，我们可以从流中读入数据；通过write，我们可以往流写入数据。

现在假定一个情形，我们需要从流中读数据，但是流中还没有数据

（典型的例子为，客户端要从socket读如数据，但是服务器还没有把数据传回来），这时候该怎么办？


同步阻塞和异步轮询

现在假定一个情形，我们需要从文件中读数据，但这个文件中现在还没有数据

一个典型的例子为，客户端向服务器发起一个请求，服务器返回处理需要一段时间，这段时间内客户端会一直向 socket 读数据

这时候我们应该怎么办？

这不是用户进程该考虑的事情，

这就是内核的 I/O 多路复用机制诞生背景，Linux 为我们提供了 select() / poll() / epoll() 三种复用机制，我们这里只讨论 epoll()

算了，胆子再大一点，同时发起一万个网络请求，操作系统会为我们创建一万个socket



- 大部分的binder用来跨进程将消息送到目标进程的消息队列
- MessageQueue 实际存在于 native 层，名为 NativeMessageQueue
- 在 native 创建 NativeMessageQueue 的同时，也会创建一个 Looper ，它用于处理 native 注册的自定义 Fd 引起的 Request 消息
- Native 层的 Looper 用来封装 epoll

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

### 几个jni方法

Java Handler 机制是基于生产者消费者模型，

在 Java 层可以叫做 Handler 机制，在 Native 层，可以叫做 Looper 机制

在 MessageQueue 中，我们需要重点关注3个 jni 方法，nativeInit() nativePollOnce nativeWake()

关键就在于 messagequeue 里面的几个 jni 方法


重点关注 阻塞调用和唤醒这两个jni 实现原理

藏在背后的 native 层的逻辑

### 参考资料

- [Scalable Event Multiplexing: epoll vs. kqueue](https://long-zhou.github.io/2012/12/21/epoll-vs-kqueue.html)