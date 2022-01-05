package com.android.designpattern.structural.decorator;

public class Shirt implements IClothes {

    private final IClothes clothes;

    public Shirt(IClothes clothes) {
        this.clothes = clothes;
    }

    @Override
    public int getWarmValue() {
        return clothes.getWarmValue() + 30;
    }

}
