package com.android.designpattern;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class HandlerTest {


    @Test
    public void main() {
        ConsumerThread consumerThread = new ConsumerThread();
        consumerThread.start();
        ProducerThread producerThread = new ProducerThread();
        producerThread.start();
    }

    static final Queue<String> queue = new ArrayDeque<>();

    final static class ProducerThread extends Thread {
        @Override
        public void run() {
            Scanner scanner = new Scanner(System.in);
            System.out.println("please input:");
            while (true) {
                String line = scanner.next();
                if (line.equals("exit")) break;
                synchronized (queue) {
                    queue.add(line);
                    queue.notify();
                }
            }
        }
    }

    final static class ConsumerThread extends Thread {

        @Override
        public void run() {
            synchronized (queue) {
                while (true) {
                    if (queue.isEmpty()) {
                        try {
                            queue.wait();// 没有消息则阻塞，等待唤醒
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    // 被唤醒后会执行该方法
                    execute(queue.poll());
                }
            }
        }

        private void execute(String msg) {
            //do something
            System.out.println("(ConsumerThread)take msg conent:" + msg);
        }

    }

}
