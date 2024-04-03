package com.android.blog.designpattern.behavioral.strategy;

public class StrategyA implements IStrategy {

    @Override
    public void doSomething() {
        System.out.println("StrategyA");
    }
}
