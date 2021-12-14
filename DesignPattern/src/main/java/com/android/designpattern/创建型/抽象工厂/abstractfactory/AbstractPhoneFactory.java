package com.android.designpattern.创建型.抽象工厂.abstractfactory;

import com.android.designpattern.创建型.抽象工厂.abstractproduct.AbstractBattery;
import com.android.designpattern.创建型.抽象工厂.abstractproduct.AbstractScreen;

public abstract class AbstractPhoneFactory {

    protected String brand;
    protected String model;
    protected AbstractScreen phoneScreen;
    protected AbstractBattery phoneBattery;

    public AbstractPhoneFactory(String brand, String model) {
        this.brand = brand;
        this.model = model;
        this.phoneScreen = createPhoneScreen();
        this.phoneBattery = createPhoneBattery();
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public AbstractScreen getPhoneScreen() {
        return phoneScreen;
    }

    public AbstractBattery getPhoneBattery() {
        return phoneBattery;
    }

    protected abstract AbstractScreen createPhoneScreen();

    protected abstract AbstractBattery createPhoneBattery();
}
