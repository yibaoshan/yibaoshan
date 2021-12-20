package com.android.designpattern.创建型.抽象工厂.factory;

import com.android.designpattern.创建型.抽象工厂.abstractfactory.AbstractPhoneFactory;
import com.android.designpattern.创建型.抽象工厂.abstractproduct.AbstractProductBattery;
import com.android.designpattern.创建型.抽象工厂.abstractproduct.AbstractProductScreen;
import com.android.designpattern.创建型.抽象工厂.product.DesayBattery;
import com.android.designpattern.创建型.抽象工厂.product.LGScreen;

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
