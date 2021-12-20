package com.android.designpattern.creational.singleton;

public class Singleton1 {

    private static final Singleton1 instance = new Singleton1();

    public Singleton1() {
    }

    public static Singleton1 getInstance() {
        return instance;
    }

    void print() {
        System.out.println(Thread.currentThread().getId() + "," + this);
    }

}
