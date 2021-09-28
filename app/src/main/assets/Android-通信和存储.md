### 一、通信

#####进程间通信方式IPC方式

- 共享内存
- 管道
- 信号
- socket
- binder

##### 线程通信方式

- 共享内存
- 回调callback
- handler

##### Binder

简介：binder本质可以是两个进程间数据共享的在内核空间区域

我个人理解：Android物理内存分为两部分，一部分给内核空间，一部分给用户空间。

假设4G内存情况下，1G内核空间，3G用户空间。binder驱动是直接控制内核空间的物理内存，

两个进程需要通信时，读写的实际是同一块物理内存

其中有几个概念需要搞清楚,Binder的四大组成部分

1. binder驱动
2. servicemanager
3. server [BBinder]->[Binder.java]->[ServiceManager.java addService()]
4. client [BpBinder]->[BinderProxy.java]->[ServiceManager.java getService()]

BinderProxy有transact方法用来发送数据

Binder有onTransact方法

ps：Android引入treble机制，拓展vendor区。所以后续新增vndBinder和hwBinder

![img](https://upload-images.jianshu.io/upload_images/14919101-b90dd39c6a81ff27.png?imageMogr2/auto-orient/strip|imageView2/2/format/webp)

**通信流程：**

C/S架构：server端和client端

server端注册到servicemanager

client端从servicemanager端获取service代理类

finish

server端注册到servicemanager，client通过名称获取server代理类，client可以通过代理类来进行对象的传输。

同时代理类指向的内存空间是server进程的一块地址，所以说只有一次copy，效率高

所以方法要支持序列化，因为要跨进程传输

**如何解决鸡生蛋蛋生鸡**

servicemanager本身是server端，其他进程全部可以使用

tips：

1. binder的优势：中心化，方便管理。其中发起请求的为client端，目标进程为server端
2. 正常同步1m-8k，异步oneway(1m-8k)/2

##### Handler

handler使用：

1. new handler()创建handler对象
2. handler构造函数初始化mLooper对象：mLooper = Looper.myLooper()，从ThreadLocal中获取
3. handler里面的mQueue复制：mQueue = mLooper.mQueue
4. 完事

唤醒流程：

post/send进入enqueueMessage队列，调用nativeWake

tips：

1. Looper.loop()后面的代码不会执行
2. HandlerThread是集成自Thread封装了looper的线程
3. messageQueue是包内可见，messageQueue初始化时调用nativeInit新建了native的looper，并将地址保存在mPtr变量中
4. 不建议使用new message，因为使用完后会调用recycle回收，把新建的对象放入缓冲池，太多了会很占内存，同时，message缓存池最大50个
5. 早期版本有enqueueSyncBarrier方法，可以提交一个同步屏障，现在的版本方法改名叫postSyncBarrier
6. message可以通过setAsynchronous来设置为异步消息
7. 调用enqueueMessage方法时如果target为null会抛异常

**Handler**

```
publc class Handler{

    public Handler() {
        mLooper = Looper.myLooper()
        if (mLooper == null) {
            throw new RuntimeException(
                "Can't create handler inside thread " + Thread.currentThread()
                        + " that has not called Looper.prepare()");
        }
        mQueue = looper.mQueue;
    }

    public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
        return enqueueMessage(queue, msg, uptimeMillis);
    }

    private boolean enqueueMessage(MessageQueue queue, Message msg, long uptimeMillis) {
        msg.target = this;
        msg.workSourceUid = ThreadLocalWorkSource.getUid();
        if (mAsynchronous) {
            msg.setAsynchronous(true);
        }
        return queue.enqueueMessage(msg, uptimeMillis);
    }
    
}
```

**Looper**

```
public class Looper{

    private Looper(boolean quitAllowed) {
        mQueue = new MessageQueue(quitAllowed);
        mThread = java.lang.Thread.currentThread();
    }
    
    public static Looper myLooper() {
        return sThreadLocal.get();
    }

    private static void prepare(boolean quitAllowed) {
        if (sThreadLocal.get() != null) {
            throw new RuntimeException("Only one Looper may be created per thread");
        }
        sThreadLocal.set(new android.os.Looper(quitAllowed));
    }

    public static void loop() {
        
    }

}
```

**MessageQueue**

```
public class MessageQueue{

    MessageQueue(boolean quitAllowed) {
        mQuitAllowed = quitAllowed;
        mPtr = nativeInit();
    }

    public void addIdleHandler(IdleHandler handler) {
            mIdleHandlers.add(handler);
    }
    
    Message next() {
        
    }

    public int postSyncBarrier() {
        synchronized (this) {
            final int token = mNextBarrierToken++;
            return token;
        }
    }

}
```

**Message**

```
public class Message{

    public int what;
    public int arg1;
    public int arg2;
    public Object obj;
    /*package*/ Handler target;
    /*package*/ Runnable callback;

    private static final int MAX_POOL_SIZE = 50;

}
```

##### 

### 二、存储

#####Android常用存储方式

SharedPreferences、SQLite、ContentProvider和File

##### 问题分析

- 为什么用binder？

  答：实用性CS架构/效率/安全性可知的UIP和PID

- 为什么说binder是一次拷贝？

  答：因为两个进程对应的是同一块物理内存，进程A写入进程B读就行了

- 如何使用binder传输大数据？

  答：共享文件/匿名共享内存：使用MemoryFile创建匿名共享内存，然后将文件句柄fd通过binder传输

- AIDL是什么？

  答：对binder机制的封装

- AIDL的in、out、inout、oneway关键字分别有什么作用？

  答：in、out、inout描述的是对象的流向，oneway是指异步执行，reply不调用，同时oneway只能串行

- Android6.0以后Looper的wake机制为什么用eventfd代替pipe

  答：fd减少一个，每个进程的fd上线是1024个。pipe需要维护一个4096B的内存缓冲区，eventfd只需要无符号64整形计数器
  
- Handler的delay的时间可靠吗？

  答：不可靠

- Handler优化？

  答：队列消息优化，重复消息和队列消息优化