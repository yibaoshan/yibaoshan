package com.android.designpattern.创建型.抽象工厂.factory;

import com.android.designpattern.创建型.抽象工厂.abstractfactory.AbstractPhoneFactory;
import com.android.designpattern.创建型.抽象工厂.abstractproduct.AbstractProductBattery;
import com.android.designpattern.创建型.抽象工厂.abstractproduct.AbstractProductScreen;
import com.android.designpattern.创建型.抽象工厂.product.BOEScreen;
import com.android.designpattern.创建型.抽象工厂.product.DesayBattery;

public class XiaoMiPhoneFactory extends AbstractPhoneFactory {

    public XiaoMiPhoneFactory() {
        super("小米(XiaoMi)", "10");
    }

    @Override
    protected AbstractProductScreen createPhoneScreen() {
        return new BOEScreen();
    }

    @Override
    protected AbstractProductBattery createPhoneBattery() {
        return new DesayBattery();
    }
}
