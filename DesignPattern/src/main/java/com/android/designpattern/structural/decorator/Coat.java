package com.android.designpattern.structural.decorator;

public class Coat implements IClothes {

    private final IClothes clothes;

    public Coat(IClothes clothes) {
        this.clothes = clothes;
    }

    @Override
    public int getWarmValue() {
        return clothes.getWarmValue() + 50;
    }
}