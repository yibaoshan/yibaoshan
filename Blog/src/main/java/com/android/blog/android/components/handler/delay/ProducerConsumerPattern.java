package com.android.blog.android.components.handler.delay;

import java.util.Scanner;
import java.util.concurrent.*;

public class ProducerConsumerPattern {

    public static void main(String[] args) {
        ProducerThread producerThread = new ProducerThread();
        producerThread.start();
        ConsumerThread consumerThread = new ConsumerThread();
        consumerThread.start();
    }

    static volatile BlockingQueue<Message> messageQueue = new DelayQueue<>();
//    static volatile BlockingQueue<Message> messageQueue = new ArrayBlockingQueue<>(10);

    final static class ProducerThread extends Thread implements Handler {

        @Override
        public void run() {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.err.println("please input message and delayMillis:");
                try {
                    messageQueue.put(new Message(scanner.next(), scanner.nextLong(), this));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void handleMessage(Message msg) {
            System.out.println("执行消息任务:" + msg.obj);
        }
    }

    final static class ConsumerThread extends Thread {
        @Override
        public void run() {
            while (true) {
                Message message = null;
                try {
                    message = messageQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                message.handler.handleMessage(message);
            }
        }
    }

    final static class Message implements Delayed {

        Object obj;
        long when;
        Handler handler;

        public Message(Object obj, long when, Handler handler) {
            this.obj = obj;
            this.when = when + System.currentTimeMillis();
            this.handler = handler;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(when - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            return (int) (getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
        }
    }

    interface Handler {

        void handleMessage(Message msg);

    }

}
