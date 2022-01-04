package com.android.designpattern.structural.decorator;

public class Pants implements IClothes {
    private final IClothes me;

    public Pants(IClothes me) {
        this.me = me;
    }

    @Override
    public int getWarmValue() {
        return me.getWarmValue() + 50;
    }

}
