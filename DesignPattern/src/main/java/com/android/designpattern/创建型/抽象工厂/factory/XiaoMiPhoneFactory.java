package com.android.designpattern.创建型.抽象工厂.factory;

import com.android.designpattern.创建型.抽象工厂.abstractfactory.AbstractPhoneFactory;
import com.android.designpattern.创建型.抽象工厂.abstractproduct.AbstractBattery;
import com.android.designpattern.创建型.抽象工厂.abstractproduct.AbstractScreen;
import com.android.designpattern.创建型.抽象工厂.product.BOEScreen;
import com.android.designpattern.创建型.抽象工厂.product.DesayBattery;

public class XiaoMiPhoneFactory extends AbstractPhoneFactory {

    public XiaoMiPhoneFactory() {
        super("小米(XiaoMi)", "10");
    }

    @Override
    protected AbstractScreen createPhoneScreen() {
        return new BOEScreen();
    }

    @Override
    protected AbstractBattery createPhoneBattery() {
        return new DesayBattery();
    }
}
