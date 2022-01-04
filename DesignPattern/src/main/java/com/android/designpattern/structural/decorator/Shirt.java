package com.android.designpattern.structural.decorator;

public class Shirt implements IClothes {

    private final IClothes me;

    public Shirt(IClothes me) {
        this.me = me;
    }

    @Override
    public int getWarmValue() {
        return me.getWarmValue() + 30;
    }

}
