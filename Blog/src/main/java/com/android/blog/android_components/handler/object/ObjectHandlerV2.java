package com.android.blog.android_components.handler.object;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.*;

/**
 * 使用Java Object并发方法实现简单Handler
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class ObjectHandlerV2 {

    public static void main(String[] args) {
        Looper.prepare();
        //生成两个创建在主线程的Handler，这样共享的才是主线程的消息队列
        Handler handler1 = new Handler("Handler1号");
        Handler handler2 = new Handler("Handler2号");
        //创建两个子线程，将主线程创建的Handler传递进去，子线程使用该Handler发送的消息会同步到主线程
        new AnotherThread(handler1).start();
        new AnotherThread(handler2).start();
        Looper.loop();
        throw new RuntimeException("Main thread loop unexpectedly exited");
    }

    /*子线程*/
    private static class AnotherThread extends Thread {

        private Handler handler;

        public AnotherThread(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {
            while (true) {
                int anInt = new Random().nextInt(20000);
                if (anInt % 5 == 0) handler.sendMessage(new Message("普通任务"));
                else handler.sendMessageDelayed(new Message("延迟任务，延迟时间：" + anInt + "ms"), anInt);
                try {
                    Thread.sleep(new Random().nextInt(20000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*消息的生产者与执行者*/
    final static class Handler {

        private final Looper mLooper;
        private final MessageQueue mQueue;

        private final String name;

        public Handler(String name) {
            this.name = name;
            mLooper = Looper.myLooper();
            mQueue = mLooper.mQueue;
        }

        /**
         * 模仿消息分发
         */
        public void dispatchMessage(Message msg) {
            System.err.println("-------------------------------" + name + "执行：" + msg.obj);
        }

        /**
         * 发送普通消息
         *
         * @param msg
         */
        public final boolean sendMessage(Message msg) {
            return sendMessageDelayed(msg, 0);
        }

        /**
         * 发送延迟消息
         *
         * @param msg
         * @param delayMillis 毫秒
         */
        public final boolean sendMessageDelayed(Message msg, long delayMillis) {
            msg.target = this;
            msg.when = System.currentTimeMillis() + delayMillis;
            System.out.println(name + "发送：" + msg.obj);
            return mQueue.enqueueMessage(msg);
        }

    }

    /*消息的消费者，内部持有消息队列*/
    final static class Looper {

        private final MessageQueue mQueue;

        private static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal<>();


        private Looper() {
            mQueue = new MessageQueue();
        }

        /**
         * 创建消息队列
         */
        public static void prepare() {
            if (sThreadLocal.get() != null) return;
            sThreadLocal.set(new Looper());
        }

        public static final Looper myLooper() {
            return sThreadLocal.get();
        }

        /**
         * 开始轮询消息队列
         */
        public static void loop() {
            Looper looper = myLooper();
            for (; ; ) {
                Message message = looper.mQueue.next();
                message.target.dispatchMessage(message);
            }
        }

    }

    /*共享的消息队列*/
    final static class MessageQueue {

        final Queue<Message> messageQueue;

        @RequiresApi(api = Build.VERSION_CODES.N)
        public MessageQueue() {
            Comparator<Message> comparator = (o1, o2) -> (int) (o1.when - o2.when);
            messageQueue = new PriorityQueue<>(comparator);
        }

        /**
         * 添加消息进队列
         *
         * @param message
         */
        public boolean enqueueMessage(Message message) {
            synchronized (this) {
                boolean res = messageQueue.add(message);
                this.notify();
                return res;
            }
        }

        /**
         * 获取消息，三种情况
         * 1. 有消息，且消息到期可以执行，返回消息
         * 2. 有消息，消息未到期，进入限时等待状态
         * 3. 没有消息，进入无限期等待状态，直到被唤醒
         */
        public Message next() {
            for (; ; ) {
                synchronized (this) {
                    //消息队列为空，无限期等待
                    if (messageQueue.isEmpty()) {
                        try {
                            this.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    Message msg = messageQueue.peek();
                    if (msg.when - System.currentTimeMillis() > 0) {
                        //消息队列有消息，但消息未到期，等待到期时间
                        try {
                            this.wait(msg.when - System.currentTimeMillis());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }
                    return messageQueue.poll();
                }
            }
        }

    }

    /*消息载体*/
    final static class Message {

        Object obj;
        long when;
        Handler target;

        public Message(Object obj) {
            this.obj = obj;
        }

    }

}
