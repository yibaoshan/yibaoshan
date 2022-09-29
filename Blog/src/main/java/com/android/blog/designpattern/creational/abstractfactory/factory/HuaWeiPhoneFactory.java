package com.android.blog.designpattern.creational.abstractfactory.factory;

import com.android.designpattern.creational.abstractfactory.abstractfactory.AbstractPhoneFactory;
import com.android.designpattern.creational.abstractfactory.abstractproduct.AbstractProductBattery;
import com.android.designpattern.creational.abstractfactory.abstractproduct.AbstractProductScreen;
import com.android.designpattern.creational.abstractfactory.product.DesayBattery;
import com.android.designpattern.creational.abstractfactory.product.LGScreen;

public class HuaWeiPhoneFactory extends AbstractPhoneFactory {

    public HuaWeiPhoneFactory() {
        super("华为(HuaWei)", "P50");
    }

    @Override
    protected AbstractProductScreen createPhoneScreen() {
        return new LGScreen();
    }

    @Override
    protected AbstractProductBattery createPhoneBattery() {
        return new DesayBattery();
    }
}
