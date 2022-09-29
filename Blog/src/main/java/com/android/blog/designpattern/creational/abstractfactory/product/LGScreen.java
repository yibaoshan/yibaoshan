package com.android.blog.designpattern.creational.abstractfactory.product;

import com.android.designpattern.creational.abstractfactory.abstractproduct.AbstractProductScreen;

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
