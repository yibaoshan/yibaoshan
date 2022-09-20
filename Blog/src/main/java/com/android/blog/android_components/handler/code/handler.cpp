//
// Created by Bob on 2022/9/14.
//

///frameworks/base/core/java/android/os/Looper.java
class Looper {

    public static void loop() {
        for (;;) {
            Message msg = queue.next(); // might block
            if (msg == null) {
                // No message indicates that the message queue is quitting.
                return;
            }
            msg.target.dispatchMessage(msg);
            msg.recycleUnchecked();
        }
    }
}

///frameworks/base/core/java/android/os/Handler.java
class Handler {

    Handler(Looper looper, Callback callback, boolean async) {
        mLooper = looper;
        mQueue = looper.mQueue;
        mCallback = callback;
        mAsynchronous = async;
    }

    //发送消息
    boolean sendMessageAtTime(Message msg, long uptimeMillis) {
        MessageQueue queue = mQueue;
        return enqueueMessage(queue, msg, uptimeMillis);
    }

    //将消息入列
    boolean enqueueMessage(MessageQueue queue, Message msg, long uptimeMillis) {
        msg.target = this;
        return queue.enqueueMessage(msg, uptimeMillis);
    }

    //消息分发
    void dispatchMessage(Message msg) {
       if (msg.callback != null) {
           handleCallback(msg);
       } else {
           if (mCallback != null) {
               if (mCallback.handleMessage(msg)) {
                   return;
                }
            }
            handleMessage(msg);
        }
    }
}

///frameworks/base/core/java/android/os/MessageQueue.java
class MessageQueue {

    private native static long nativeInit();
    private native void nativePollOnce(long ptr, int timeoutMillis); /*non-static for callbacks*/
    private native static void nativeWake(long ptr);

    public MessageQueue() {
        mPtr = nativeInit();//调用 jni 初始化创建消息队列
    }

    Message next() {

    }
}

//----------------------------------------------------- Native 分割线 ------------------------------------------------------

//frameworks/base/core/jni/android_os_MessageQueue.cpp
class android_os_MessageQueue {

    //jni方法，初始化函数，创建的 NativeMessageQueue 对象内部又创建了 Looper，Native 层的
    void android_os_MessageQueue_nativeInit() {
        NativeMessageQueue* nativeMessageQueue = new NativeMessageQueue();
    }

    //jni方法，转到 pollOnce
    void android_os_MessageQueue_nativePollOnce(){
        nativeMessageQueue->pollOnce(env, obj, timeoutMillis);
    }

    class NativeMessageQueue : MessageQueue {

        void wake();//向 mWakeEventFd 写入 1

        //构造函数，创建了 Native Looper 对象，同样保存到线程静态变量保证唯一
        //注意，这里的 Looper 对象不是 Java 层的那个 Looper ，也不是简单的 jni 桥接类，我们可以从它所在的文件路径得知一二，native 实现的 service 需要用到这个 Looper 对象
        ///frameworks/base/native/android/looper.cpp
        NativeMessageQueue() {
            mLooper = Looper::getForThread();
            if (mLooper == NULL) {
                //创建了Looper，其内部创建了一个 epoll 池，用于监听各个fd事件
                //同时，还创建了一个 eventfd ，专门用于监听消息队列的可读事件
                mLooper = new Looper(false);
                Looper::setForThread(mLooper);
            }
        }

        //什么都没做，转到 Looper#pollOnce() 方法
        void pollOnce(){
            mLooper->pollOnce(timeoutMillis);
        }
    }
}

//system/core/libutils/Looper.cpp
class looper {

    // Locked list of file descriptor monitoring requests.
    KeyedVector<int, Request> mRequests;  // guarded by mLock

    int mWakeEventFd;//用于监听 MessageQueue
    int mEpollFd;//用来装fd的epoll池

    Looper::Looper() {
        mWakeEventFd = eventfd();//fd是文件描述符的缩写，每一个fd会与一个打开的文件相对应，既然是文件，那么就只有读和写两种操作，
        rebuildEpollLocked();
    }

    void rebuildEpollLocked(){
        mEpollFd = epoll_create();//哎，这儿非常重要，在 Looper 初始化时创建了 epoll 对象
        epoll_ctl(mEpollFd, EPOLL_CTL_ADD, mWakeEventFd, & eventItem);//把用于唤醒消息队列的eventfd 添加到 epoll 池
    }

    int pollOnce(int timeoutMillis){
        for (;;) {
            while (mResponseIndex < mResponses.size()) {
                int ident = response.request.ident;
                if (ident >= 0) {
                    return ident;
                }
            }
            if (result != 0) {
                return result;
            }
            result = pollInner(timeoutMillis);
        }
    }

    int addFd(int fd){
        mRequests.add(fd);
    }

    int removeFd(int fd, int seq){
        mRequests.removeF(fd);
    }

    int pollInner(int timeoutMillis){
        int result = POLL_WAKE;
        //step1，清空 mResponses
        mResponses.clear();
        mResponseIndex = 0;
        struct epoll_event eventItems[EPOLL_MAX_EVENTS];//step2，创建用来从内核得到事件的集合
        int eventCount = epoll_wait(mEpollFd, eventItems, EPOLL_MAX_EVENTS, timeoutMillis);//调用 epoll_wait() 等待事件的产生
        //达到设定的超时时间
        if (eventCount == 0) {
            result = POLL_TIMEOUT;
        }
        //执行到for循环说明内核返回事件了
        for (int i = 0; i < eventCount; i++) {
            //如果事件是消息队列的 eventfd ，说明有人向消息队列提交了需要马上执行的消息，或者延迟消息到期了
            if (fd == mWakeEventFd) {
                awoken();//唤醒消息队列
            } else {
                //不是消息队列的 eventfd，说明有其他地方注册了监听 fd，将返回事件保存到 mResponses 集合中，然后需要对这个事件做出响应
                Response response
                response.events = eventItems[i].events;;
                response.request = mRequests.valueAt(requestIndex);
                mResponses.push(response);
            }
        }
        //分发native层的消息队列中的消息
        while (mMessageEnvelopes.size() != 0) {
            if (messageEnvelope.uptime <= now) {//如果有到期消息
                // obtain handler
                sp<MessageHandler> handler = messageEnvelope.handler;
                Message message = messageEnvelope.message;
                handler->handleMessage(message);//分发执行，参考 Java Handler 消息分发
            }
        }
        //先把其他地方注册的自定义 fd 消费掉，响应它们的回调方法
        for (size_t i = 0; i < mResponses.size(); i++) {
            response.request.callback->handleEvent(fd, events, data);
        }
        return result;
    }

    //调用 read() 方法将 eventfd 里面的计数读出来，使 eventfd 重新变成监听可读事件就行了，因为 epoll_wait() 方法返回后，pollInner() 方法也会返回到 pollOnce()
    //一直返回到 MessageQueue#next() 方法，阻塞的方法返回了
    void awoken() {
        read(mWakeEventFd);
    }

    void sendMessageAtTime(nsecs_t uptime, const sp<MessageHandler>& handler,
            const Message& message) {
        size_t i = 0;
        { // acquire lock
            AutoMutex _l(mLock);

            size_t messageCount = mMessageEnvelopes.size();
            while (i < messageCount && uptime >= mMessageEnvelopes.itemAt(i).uptime) {
                i += 1;
            }

            MessageEnvelope messageEnvelope(uptime, handler, message);
            mMessageEnvelopes.insertAt(messageEnvelope, i, 1);

            if (mSendingMessage) {
                return;
            }
        } // release lock
        // Wake the poll loop only when we enqueue a new message at the head.
        if (i == 0) {
            wake();
        }
    }

    void wake() {
        //向 mWakeEventFd 写入1，epoll 将会通知收到可读事件
        write(mWakeEventFd, 1)
    }

}

