package com.android.designpattern.创建型.抽象工厂.abstractproduct;

public abstract class AbstractProductScreen {

    public AbstractProductScreen() {
        createScreen();
    }

    public abstract String getScreenName();

    protected abstract void createScreen();

}
