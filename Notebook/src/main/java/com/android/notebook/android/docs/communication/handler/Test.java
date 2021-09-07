package com.android.notebook.android.docs.communication.handler;

import org.jetbrains.annotations.NotNull;

import java.util.PriorityQueue;
import java.util.Random;

public class Test {

    @org.junit.Test
    public void main() {
        Looper.prepare();
        Handler handler = new Handler() {
            @Override
            void handleMessage(Message msg) {
                super.handleMessage(msg);
                System.err.println("来自" + msg.what + "线程的信息：" + msg.obj);
            }
        };
        new TestHandlerThread(1, handler).start();
        new TestHandlerThread(2, handler).start();
        new TestHandlerThread(10, handler).start();
        Looper.loop();
    }

    private static class TestHandlerThread extends Thread {

        private int what;
        private Handler mHandler;

        public TestHandlerThread(int what, Handler mHandler) {
            this.what = what;
            this.mHandler = mHandler;
        }

        @Override
        public void run() {
            super.run();
            for (; ; ) {
                Message msg = Message.obtain();
                msg.what = what;
                switch (what) {
                    case 1:
                        msg.obj = "Who are you?";
                        break;
                    case 2:
                        msg.obj = "I am your father";
                        break;
                    default:
                        msg.obj = "All of thread is little brother!";
                        break;
                }
                mHandler.sendMessageDelayed(msg, msg.what * new Random().nextInt(1000));
                try {
                    sleep(new Random().nextInt(1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class Message implements Comparable<Message> {

        int what;
        long when;
        Object obj;
        Handler target;
        Runnable callback;

        static Message obtain() {
            return new Message();
        }

        @Override
        public int compareTo(@NotNull Message message) {
            return (int) (when - message.when);
        }
    }

    private static class MessageQueue {

        PriorityQueue<Message> messages = new PriorityQueue<>();

        synchronized void queueMessage(Message msg) {
            messages.offer(msg);
            System.out.println("消息队列数量：" + messages.size());
        }

        Message next() {
            for (; ; ) {
                Message msg = messages.peek();
                if (msg != null) {
                    synchronized (this) {
                        if (System.currentTimeMillis() >= msg.when) {
                            return messages.poll();
                        } else continue;
                    }
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private final static class Looper {

        final MessageQueue mQueue;

        private static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal<>();

        private Looper() {
            mQueue = new MessageQueue();
        }

        static Looper getLooper() {
            return sThreadLocal.get();
        }

        static void prepare() {
            if (sThreadLocal.get() != null) throw new RuntimeException("looper only init once");
            sThreadLocal.set(new Looper());
        }

        static void loop() {
            Looper looper = getLooper();
            if (looper == null) throw new RuntimeException("please init looper");
            MessageQueue queue = looper.mQueue;
            for (; ; ) {
                Message msg = queue.next();
                if (msg == null) return;
                msg.target.dispatchMessage(msg);
            }
        }

    }

    private static class Handler {

        private Looper mLooper;
        private MessageQueue mQueue;
        private Callback mCallback;

        public Handler() {
            mLooper = Looper.getLooper();
            if (mLooper == null) throw new RuntimeException("mLooper is null");
            mQueue = mLooper.mQueue;
        }

        void handleMessage(Message msg) {

        }

        void dispatchMessage(Message msg) {
            if (msg.callback != null) {
                msg.callback.run();
            } else {
                if (mCallback != null) {
                    if (mCallback.handleMessage(msg)) return;
                }
                handleMessage(msg);
            }
        }

        void sendMessage(Message msg) {
            sendMessageDelayed(msg, 0);
        }

        void sendMessageDelayed(Message msg, int delay) {
            msg.when = System.currentTimeMillis() + delay;
            enqueueMessage(msg);
        }

        void post(Runnable runnable) {
            postDelayed(runnable, 0);
        }

        void postDelayed(Runnable runnable, long delay) {
            Message msg = Message.obtain();
            msg.callback = runnable;
            if (delay < 0) delay = 0;
            msg.when = System.currentTimeMillis() + delay;
            enqueueMessage(msg);
        }

        void enqueueMessage(Message msg) {
            msg.target = this;
            mQueue.queueMessage(msg);
        }

        public interface Callback {
            boolean handleMessage(Message msg);
        }

    }

}
