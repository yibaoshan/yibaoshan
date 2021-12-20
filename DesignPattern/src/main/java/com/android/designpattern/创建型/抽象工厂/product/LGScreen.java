package com.android.designpattern.创建型.抽象工厂.product;

import com.android.designpattern.创建型.抽象工厂.abstractproduct.AbstractProductScreen;

public class LGScreen extends AbstractProductScreen {

    @Override
    public String getScreenName() {
        return "LG";
    }

    @Override
    protected void createScreen() {
        System.out.println("加班加点生产LG屏幕中...");
    }

}
