package com.android.designpattern.creational.abstractfactory.product;

import com.android.designpattern.creational.abstractfactory.abstractproduct.AbstractProductBattery;

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
