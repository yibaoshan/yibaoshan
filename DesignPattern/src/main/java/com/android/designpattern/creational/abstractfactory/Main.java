package com.android.designpattern.creational.abstractfactory;

import com.android.designpattern.creational.abstractfactory.abstractfactory.AbstractPhoneFactory;
import com.android.designpattern.creational.abstractfactory.factory.HuaWeiPhoneFactory;
import com.android.designpattern.creational.abstractfactory.factory.XiaoMiPhoneFactory;

import org.junit.Test;


public class Main {

    @Test
    public void main() {
        AbstractPhoneFactory phoneFactory1 = new HuaWeiPhoneFactory();
        AbstractPhoneFactory phoneFactory2 = new XiaoMiPhoneFactory();
        print(phoneFactory1);
        print(phoneFactory2);
    }

    private void print(AbstractPhoneFactory phoneFactory) {
        System.out.println("产线品牌：" + phoneFactory.getBrand()
                + ",生产型号：" + phoneFactory.getModel()
                + ",电池厂商：" + phoneFactory.getPhoneBattery().getBatteryName()
                + ",屏幕厂商：" + phoneFactory.getPhoneScreen().getScreenName());
    }

}
