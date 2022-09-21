### Android组件系列：再谈Handler机制（Native层）

之前已经写过一篇关于 Java 层 Handler 机制的文章，从应用开发的角度出发，详细介绍了 Handler 机制的设计背景，以及如何自己如何实现一套 Handler

本篇文章我们将深入 Native 层，解开 Looper#loop() 不会卡死主线程背后的秘密

以下，enjoy

### 一、开篇

从 Android 2.3 开始，Google 把 Handler 的阻塞/唤醒方案从 `Object#wait() / notify()`，改成了用 Linux epoll 来实现

原因是 Native 层也引入了一套消息管理机制，用于提供给 C/C++ 开发者使用，而现有的阻塞/唤醒方案是为 Java 层准备的，只支持 Java，现在 Native 希望能够像 Java 一样： main 线程在没有消息时进入阻塞状态，有到期消息需要执行时，main 线程能及时醒过来处理，怎么办？有两种选择

要么，继续使用 Object#wait() / notify( )，Native 向消息队列添加新消息时，通知 Java 层自己需要什么时候被唤醒

要么，在 Native 层重新实现一套阻塞/唤醒方案，弃用 Object#wait() / notify() ，Java 通过 jni 调用 Native 进入阻塞态

结局我们都知道了，Google 选择了后者

其实如果只是将 Java 层的阻塞/唤醒移植到 Native 层，倒也不用祭出 epoll 这个大杀器 ，Native 调用 pthread_cond_wait 也能达到相同的效果

选择 epoll 的另一个原因是， Native 层支持监听自定义 Fd （***比如 Input 事件就是通过 epoll 监听 socketfd 来实现将事件转发到 APP 进程的***），而一旦有监听多个流事件的需求，那就只能使用 Linux I/O 多路复用，epoll 就是 Linux I/O 多路复用的其中一个实现

#### 理解 I/O多路复用之epoll

说了这么多，那到底什么是 epoll ？

epoll 全称 eventpoll，是 Linux 中的一种 I/O 多路复用技术，除了 epoll 外，还有 select 和 poll 两种不同的实现方式，我们这只讨论 epoll

要理解 epoll  ，我们首先需要理解什么是 "流"

在 Linux 中，任何可以进行 I/O 操作的对象都可以看做是流，一个文件，socket，pipe，我们都可以把他们看作流

接着我们来讨论流的 I/O 操作，通过调用 read() ，我们可以从流中读入数据；通过 write() ，我们可以往流写入数据

现在假定一个情形，我们需要从流中读数据，但是流中还没有数据

```cpp
int socketfd = socket();
connect(socketfd,serverAddr);
int n = send(socketfd,'在吗');
n = recv(socketfd); //等待接受服务器端 发过来的信息
...//处理服务器返回的数据
```

一个典型的例子为，客户端要从 socket 中读数据，但是服务器还没有把数据传回来，这时候该怎么办？

- **阻塞：线程阻塞到 recv() 方法，直到读到数据后再继续向下执行**
- **非阻塞：recv() 方法没读到数据立刻返回 -1 ，用户线程按照固定间隔轮询调用 recv() 方法，直到有数据返回**

好，现在我们有了阻塞和非阻塞两种解决方案，接着我们同时发起100个网络请求，看看这两种方案各自会怎么处理？

先说阻塞模式，在阻塞模式下，一个线程一次只能处理一个流的 I/O 事件，想要同时处理多个流，只能使用多线程 + 阻塞 I/O 的方案。但是，每个 socket 对应一个线程会造成很大的资源占用，尤其是对于长连接来说，线程资源一直不会释放，如果后面陆续有很多连接的话，很容易造成性能上的瓶颈

在非阻塞模式下，我们发现单线程可以同时处理多个流了，只要不停的把所有流从头到尾的问一遍是否有返回（返回值大于-1 ）就可以得知哪些流有数据，但这样的做法效率也不高，因为如果所有的流都没有数据，那么只会白白浪费CPU

发现问题了吗？只有阻塞和非阻塞这两种方案时，一旦有监听多个流事件的需求，用户程序只能选择，要么浪费线程资源（阻塞 I/O），要么浪费 CPU 资源（非阻塞 I/O），没有其他更高效的解决方案

并且在用户程序端这个问题是无解的，必须让内核创建某种机制，把这些流的监听事件接管过去，因为任何事件都必须通过内核读取转发，内核总是能在第一时间知晓事件发生

**这种能够让用户程序拥有同时监听多个流读写事件的机制，就被称为 I/O 多路复用！**

然后我们来看 epoll 提供的函数：

```cpp
int epoll_create(int size);
int epoll_ctl(int epfd, int op, int fd, struct epoll_event *event);
int epoll_wait(int epfd, struct epoll_event *events, int maxevents, int timeout);
```

一共就三个函数，epoll_create() 用于创建一个 epoll 池

epoll_ctl() 用来控制需要监听的 fd 的增删改操作，最后一个参数 event 是告诉内核需要监听什么事件，比如上面的 socketfd 监听的就是可读事件，一旦接收到服务器返回的数据，监听 socketfd 的对象将会收到回调通知，表示 socket 中有数据可以读了

最后一个 epoll_wait() 方法是使用户程序阻塞的方法，它的第二个参数 events 接受的是一个集合对象，如果有多个事件同时发生，events 可以从内核得到事件的集合

#### 理解 Linux eventfd

理解了 epoll 以后再来看 eventfd 就简单多了

对于 eventfd 来说，通常监听的是可读事件，也就是说，一旦有人向 eventfd 写入值，监听该 fd 的应用程序便会收到一个"你有新的值可以读啦"的事件回调

### 二、进入Native Handler

#### 消息队列的初始化

#### 消息的循环与阻塞

#### 消息的发送/唤醒机制

#### Looper监听自定义Fd

### 三、结语

启动应用进程后，创建 eventfd 用来监听消息队列的可读事件，这个 epoll 用来监听APP进程的，

### 四、参考资料

在阻塞 I/O 模型中，当用户线程发出 I/O 请求之后，内核会去查看数据是否就绪，如果没有就绪就会等待数据就绪，而用户线程就会处于阻塞状态，用户线程交出CPU。当数据就绪之后，内核会将数据拷贝到用户线程，并返回结果给用户线程，用户线程才解除block状态。

而在非阻塞 I/O 模型中，用户线程需要不断地询问内核数据是否就绪，会一直占用 CPU


### 理解 Linux eventfd



epoll 监听的是可读可写事件，那么 eventfd 的可读可写事件是什么呢？

从上面的例子也可以看到，eventfd 实现的是计数功能，因此当 eventfd 计数不为0时，那么 fd 时可读的。由于eventfd是一直可写的（也即是一直可以累积计数），因此 eventfd 一直有可写事件。

所以当 eventfd 结合 epoll 使用时，一般使用可读事件，因为eventfd一直可写，监听可写事件也没什么意义

那我们就创建一个文件，叫做事件文件，它的职责是内容发生变化后

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

### Java Handler使用流程

有了 epoll 基础和 eventfd 基础，我们开始正式进入的 Native Handler 一探究竟

熟悉 Java Handler 机制的同学肯定知道，在 MessageQueue 中有几个 native 方法

```java
/frameworks/base/core/java/android/os/MessageQueue.java
class MessageQueue {
    private native static long nativeInit();
    private native static void nativeDestroy(long ptr);
    private native void nativePollOnce(long ptr, int timeoutMillis); /*non-static for callbacks*/
    private native static void nativeWake(long ptr);
}
```

MessageQueue 类的构造方法中调用了 nativeInit() 方法，用于初始化，在创建 Java 消息队列的时候也在 Native 层创建了一个消息队列

```java
MessageQueue(boolean quitAllowed) {
    mQuitAllowed = quitAllowed;
    mPtr = nativeInit();
}
```

### Native消息队列初始化

1. 创建 NativeMessageQueue 对象，Native 层的消息队列
2. 创建 Looper 对象，Native 层没有 Handler 类，所有消息队列的操作都是通过 Looper 来完成，同时 Looper 还兼任增删 fd 的功能
3. 创建 epoll 对象 mEpollFd，它是整个 Handler 机制的核心
4. 创建 eventfd 对象 mWakeEventFd，用于监听消息队列的可读事件，唤醒消息队列

到这里其实文章就可以结束了，因为接下来的阻塞和唤醒

### Handler消息的循环与阻塞

消息队列创建完以后，如果消息队列里面一条消息都没有，整个线程就会阻塞到 Looper#loop() 方法中，Java 层的的调用链大致是这样的

```java
Looper#loop()
    -> MessageQueue#next()
        -> MessageQueue#nativePollOnce()
}
```

Native 层是怎么实现的呢？我们来跟一下

Looper#loop()
    -> MessageQueue#next()
        -> MessageQueue#nativePollOnce()
            -> NativeMessageQueue#pollOnce()//进入 Native 层
                -> Looper#pollOnce()
                    -> Looper#pollInner()
                        -> epoll_wait()


pollInner() 方法是 native 消息机制的核心，理解它的内部逻辑对于理解消息机制非常重要

最终阻塞到 epoll_wait() 方法

### 消息的发送与唤醒

好了，现在消息队列里面是空的，并且阻塞到 native 层的 Looper#pollInner() 方法中，我们来向消息队列发送一条消息唤醒它

这是用于初始化native

我们从这几个 nativeInit() 方法作为入口，进入到 Native 层一探究竟

好，总结下来 Handler 机制主要在创建、获取有效消息、唤醒

epoll_wait() 返回后，

总结一下唤醒以后做了那些事情

1. 清零消息队列的 eventfd 计数（如果是消息队列 eventfd 类型的话）
2. 执行 native handler 消息分发
3. 

### Native自定义Fd机制

自定义 Fd 有什么用呢？了解 Android 触摸事件分发的朋友会知道，input 事件就是通过向来实现，通信的

input 分发的大致流程是，InputManagerService

比如触摸事件的分发就利用了 Native Looper 中的自定义 Fd 机制

### 进入 Native 层

### 总结

分析下来 Native 的实现不算复杂，利用了 eventfd 作为监听消息队列有没有消息，关键的阻塞与唤醒部分是借助了 Linux 系统 epoll 机制来实现的

Native 中的 Looper 创建的 epoll 不止监听消息队列的可读时间，同时还提供了 addFd() 方法支持进程内其他监听 fd 的需求，这一点非常重要，Android 的 input 事件就是通过 Native Looper 单独注册 fd 监听来通知到 APP 进程的

不复杂

Java 和 Native 各自维护一套消息队列，使用 Java 开发可以通过 Handler 类向 Java 层的消息队列提交消息，使用 C/C++ 开发可以通过 Looper 类向 Native 层的消息队列提交消息

他们共用Java 层的阻塞与唤醒机制，

不同点是 Java 为了系统消息的优先级，引入了同步屏障和异步消息的概念

而 Native 则支持监听自定义 Fd

总的来说 Handler 机制并不复杂

到这里就结束了，希望能对大家有所帮助

全文完

理解了 epoll 就理解了 Native 层的消息队列机制

利用内核的 I/O 多路复用完成消息队列的休眠与唤醒，

在文章的最后我们来总结一下 Native 层的消息队列

### 笔记


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
- [epoll 或者 kqueue 的原理是什么？- 知乎](https://www.zhihu.com/question/20122137)
- [Linux 网络编程的5种IO模型：阻塞IO与非阻塞IO](https://www.cnblogs.com/schips/p/12543650.html)
