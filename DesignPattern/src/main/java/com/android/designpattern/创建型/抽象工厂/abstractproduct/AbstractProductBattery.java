package com.android.designpattern.创建型.抽象工厂.abstractproduct;

public abstract class AbstractProductBattery {

    public AbstractProductBattery() {
        createBattery();
    }

    public abstract String getBatteryName();

    protected abstract void createBattery();

}
