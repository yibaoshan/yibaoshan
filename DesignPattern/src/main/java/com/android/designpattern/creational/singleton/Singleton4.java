package com.android.designpattern.creational.singleton;

public enum Singleton4 {

    /**
     * 使用反射创建枚举类：
     * java.lang.IllegalArgumentException: Cannot reflectively create enum objects
     */

    getInstance();

    {
        System.out.println(this.getClass().getName()+"init");
    }

    void print() {
        System.out.println(Thread.currentThread().getId() + "," + this);
    }

}
