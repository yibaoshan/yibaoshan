package com.android.designpattern.创建型.抽象工厂.abstractproduct;

public abstract class AbstractBattery {

    public AbstractBattery() {
        createBattery();
    }

    public abstract String getBatteryName();

    protected abstract void createBattery();

}
