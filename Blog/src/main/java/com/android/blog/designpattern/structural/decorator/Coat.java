package com.android.blog.designpattern.structural.decorator;

import com.android.designpattern.structural.decorator.IClothes;

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