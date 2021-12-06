package com.android.designpattern.创建型.单例模式;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    /**
     * 保证在同一进程中一个类仅有一个实例，并提供一个访问它的全局访问点。
     *
     * 克隆/反射/序列化
     */

    @Test
    public void main() {
        ExecutorService executor = Executors.newFixedThreadPool(100);
        int taskCount = 100;
        for (int i = 0; i < taskCount; i++) {
            executor.submit(new Run());
        }
    }

    private static class Run implements Runnable {

        @Override
        public void run() {
//            Singleton1.getInstance().print();
//            Singleton2.getInstance().print();
            Singleton3.getInstance().print();
//            Singleton4.getInstance.print();
        }
    }

}
