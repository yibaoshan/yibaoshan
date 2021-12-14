package com.android.designpattern.创建型.抽象工厂.product;

import com.android.designpattern.创建型.抽象工厂.abstractproduct.AbstractBattery;

public class BYDBattery extends AbstractBattery {

    @Override
    public String getBatteryName() {
        return "比亚迪(BYD)";
    }

    @Override
    protected void createBattery() {
        System.out.println("加班加点生产比亚迪电池中...");
    }
}
