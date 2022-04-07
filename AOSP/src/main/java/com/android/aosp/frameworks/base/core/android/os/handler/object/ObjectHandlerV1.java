package com.android.aosp.frameworks.base.core.android.os.handler.object;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 * 使用Java Object并发方法实现生产者消费者模式
 */
public class ObjectHandlerV1 {

    public static void main(String[] args) {
        new ProducerThread("boss张三").start();
        new ProducerThread("boss李四").start();
        new ConsumerThread("小明").start();
        new ConsumerThread("小红").start();
    }

    /*共享的消息队列*/
    final static Queue<Message> messageQueue = new LinkedList<>();

    /*消息载体*/
    final static class Message {

        Object obj;
        long when;

        public Message(Object obj) {
            this.obj = obj;
        }
    }

    /*生产者线程*/
    final static class ProducerThread extends Thread {

        private int number = 0;

        public ProducerThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            for (; ; ) {
                synchronized (messageQueue) {
                    messageQueue.add(new Message("来自" + getName() + "的" + (number++) + "号消息"));
                    //生产者线程每次发送完消息后需要唤醒可能正在等待的消费者线程
                    messageQueue.notify();
                    try {
                        messageQueue.wait(new Random().nextInt(10000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    /*消费者线程*/
    final static class ConsumerThread extends Thread {

        public ConsumerThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            for (; ; ) {
                synchronized (messageQueue) {
                    //消费者线程不停轮询消息队列，有消息就执行，没消息就等
                    if (messageQueue.isEmpty()) {
                        try {
                            messageQueue.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    Message message = messageQueue.poll();
                    System.out.println(getName() + "消费者线程，执行消息：" + message.obj);
                }
            }
        }

    }



}
