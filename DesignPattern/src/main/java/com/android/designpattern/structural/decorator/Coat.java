package com.android.designpattern.structural.decorator;

public class Coat implements IClothes {

    private final IClothes me;

    public Coat(IClothes me) {
        this.me = me;
    }

    @Override
    public int getWarmValue() {
        return me.getWarmValue() + 50;
    }
}