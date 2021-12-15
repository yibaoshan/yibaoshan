package com.android.designpattern.创建型.单例模式;

public class Singleton2 {

    private Singleton2() {

    }

    {
        System.out.println(this.getClass().getName()+"init");
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
