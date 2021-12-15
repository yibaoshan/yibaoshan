package com.android.designpattern.创建型.单例模式;

public class Singleton3 {

    private volatile static Singleton3 instance;

    private Singleton3() {
    }

    {
        System.out.println(this.getClass().getName()+"init");
    }

    public static Singleton3 getInstance() {
        if (instance == null) {
            synchronized (Singleton3.class) {
                if (instance == null) {
                    instance = new Singleton3();
                }
            }
        }
        return instance;
    }

    void print() {
        System.out.println(Thread.currentThread().getId() + "," + this);
    }

}
