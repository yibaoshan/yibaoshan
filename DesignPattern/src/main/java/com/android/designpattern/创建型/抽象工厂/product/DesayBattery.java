package com.android.designpattern.创建型.抽象工厂.product;

import com.android.designpattern.创建型.抽象工厂.abstractproduct.AbstractProductBattery;

public class DesayBattery extends AbstractProductBattery {

    @Override
    public String getBatteryName() {
        return "德赛(Desay)";
    }

    @Override
    protected void createBattery() {
        System.out.println("加班加点生产德赛电池中...");
    }
}
