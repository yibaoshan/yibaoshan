package com.android.designpattern.behavioral;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Scanner;

public class ProducerConsumerPattern {

    public static void main(String[] args) {
        ProducerThread producerThread = new ProducerThread();
        producerThread.start();//启动生产者线程
        ConsumerThread consumerThread1 = new ConsumerThread("消费者1号");
        ConsumerThread consumerThread2 = new ConsumerThread("消费者2号");
        //启动消费者线程
        consumerThread1.start();
        consumerThread2.start();
    }

    static final Queue<String> messageQueue = new ArrayDeque<>();

    final static class ProducerThread extends Thread {
        @Override
        public void run() {
            Scanner scanner = new Scanner(System.in);
            System.out.println("please input:");
            while (true) {
                String line = scanner.next();
                if (line.equals("exit")) break;
                synchronized (messageQueue) {
                    messageQueue.add(line);
                    messageQueue.notify();
                }
            }
        }
    }

    final static class ConsumerThread extends Thread {

        public ConsumerThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            synchronized (messageQueue) {
                //消费者一直不停的从共享消息队列取消息
                while (true) {
                    if (messageQueue.isEmpty()) {
                        try {
                            messageQueue.wait();// 没有消息则阻塞，等待唤醒
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    // 被唤醒后会执行该方法
                    execute(messageQueue.poll());
                }
            }
        }

        private void execute(String msg) {
            //do something
            System.out.println(getName() + " take msg:" + msg);
        }

    }

}
