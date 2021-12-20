package com.android.designpattern.创建型.抽象工厂;

import com.android.designpattern.创建型.抽象工厂.abstractfactory.AbstractPhoneFactory;
import com.android.designpattern.创建型.抽象工厂.factory.HuaWeiPhoneFactory;
import com.android.designpattern.创建型.抽象工厂.factory.XiaoMiPhoneFactory;

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
