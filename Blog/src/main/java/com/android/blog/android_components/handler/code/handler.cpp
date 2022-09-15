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

    void android_os_MessageQueue_nativeInit() {
        NativeMessageQueue* nativeMessageQueue = new NativeMessageQueue();
    }

    //实际的消息队列，提供 pollOnce() 和 wake() 两个方法
    class NativeMessageQueue : MessageQueue {

        void pollOnce();//阻塞调用方法，提供的功能和 Java 的 Object.wait()/notify() 类似，但 epoll 不空转 CPU
        void wake();//向 mWakeEventFd 写入 1

        //在 MessageQueue 的构造函数中创建了 Native Looper 对象，同样保存到线程静态变量保证唯一
        //注意，这里的 Looper 对象不是 Java 层的那个 Looper ，也不是简单的 jni 桥接类，我们可以从它所在的文件路径得知一二，native 实现的 service 需要用到这个 Looper 对象
        ///frameworks/base/native/android/looper.cpp
        NativeMessageQueue::NativeMessageQueue() {
            mLooper = Looper::getForThread();
            if (mLooper == NULL) {
                mLooper = new Looper(false);
                Looper::setForThread(mLooper);
            }
        }
    }
}

///system/core/libutils/Looper.cpp
class looper {

    int mWakeEventFd;//用于监听 MessageQueue
    int mEpollFd;//用来装fd的epoll池

    Looper::Looper() {
        mWakeEventFd = eventfd();//fd是文件描述符的缩写，每一个fd会与一个打开的文件相对应，既然是文件，那么就只有读和写两种操作，
        rebuildEpollLocked();
    }

}










