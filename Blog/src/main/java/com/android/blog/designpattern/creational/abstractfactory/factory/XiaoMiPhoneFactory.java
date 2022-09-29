package com.android.blog.designpattern.creational.abstractfactory.factory;

import com.android.designpattern.creational.abstractfactory.abstractfactory.AbstractPhoneFactory;
import com.android.designpattern.creational.abstractfactory.abstractproduct.AbstractProductBattery;
import com.android.designpattern.creational.abstractfactory.abstractproduct.AbstractProductScreen;
import com.android.designpattern.creational.abstractfactory.product.BOEScreen;
import com.android.designpattern.creational.abstractfactory.product.DesayBattery;

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
