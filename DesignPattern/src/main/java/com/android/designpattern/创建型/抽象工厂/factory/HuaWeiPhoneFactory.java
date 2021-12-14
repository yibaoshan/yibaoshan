package com.android.designpattern.创建型.抽象工厂.factory;

import com.android.designpattern.创建型.抽象工厂.abstractfactory.AbstractPhoneFactory;
import com.android.designpattern.创建型.抽象工厂.abstractproduct.AbstractBattery;
import com.android.designpattern.创建型.抽象工厂.abstractproduct.AbstractScreen;
import com.android.designpattern.创建型.抽象工厂.product.DesayBattery;
import com.android.designpattern.创建型.抽象工厂.product.LGScreen;

public class HuaWeiPhoneFactory extends AbstractPhoneFactory {

    public HuaWeiPhoneFactory() {
        super("华为(HuaWei)", "P50");
    }

    @Override
    protected AbstractScreen createPhoneScreen() {
        return new LGScreen();
    }

    @Override
    protected AbstractBattery createPhoneBattery() {
        return new DesayBattery();
    }
}
