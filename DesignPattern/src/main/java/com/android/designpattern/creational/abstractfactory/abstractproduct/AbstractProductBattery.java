package com.android.designpattern.creational.abstractfactory.abstractproduct;

public abstract class AbstractProductBattery {

    public AbstractProductBattery() {
        createBattery();
    }

    public abstract String getBatteryName();

    protected abstract void createBattery();

}
