package com.android.designpattern.creational.singleton;

public class Singleton2 {

    private Singleton2() {

    }

    public static Singleton2 getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final Singleton2 instance = new Singleton2();
    }

    void print() {
        System.out.println(Thread.currentThread().getId() + "," + this);
    }

}
