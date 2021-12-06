package com.android.designpattern.创建型.单例模式;

public class Singleton1 {

    private static final Singleton1 instance = new Singleton1();

    private Singleton1() {
    }

    public static Singleton1 getInstance() {
        return instance;
    }

    void print() {
        System.out.println(Thread.currentThread().getId() + "," + this);
    }

}
