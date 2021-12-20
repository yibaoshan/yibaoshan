package com.android.designpattern.创建型.抽象工厂.product;

import com.android.designpattern.创建型.抽象工厂.abstractproduct.AbstractProductBattery;

public class BYDBattery extends AbstractProductBattery {

    @Override
    public String getBatteryName() {
        return "比亚迪(BYD)";
    }

    @Override
    protected void createBattery() {
        System.out.println("加班加点生产比亚迪电池中...");
    }
}
