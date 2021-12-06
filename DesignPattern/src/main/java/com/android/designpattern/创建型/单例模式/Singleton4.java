package com.android.designpattern.创建型.单例模式;

public enum Singleton4 {

    getInstance();

    void print() {
        System.out.println(Thread.currentThread().getId() + "," + this);
    }

}
