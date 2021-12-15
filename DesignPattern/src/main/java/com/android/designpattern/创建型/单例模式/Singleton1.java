package com.android.designpattern.创建型.单例模式;

public class Singleton1 {

    private static final Singleton1 instance = new Singleton1();

    public Singleton1() {
    }

    {
        System.out.println(this.getClass().getName()+"init");
    }

    public static Singleton1 getInstance() {
        return instance;
    }

    void print() {
        System.out.println(Thread.currentThread().getId() + "," + this);
    }

}
