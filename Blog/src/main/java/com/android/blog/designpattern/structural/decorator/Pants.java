package com.android.blog.designpattern.structural.decorator;

import com.android.designpattern.structural.decorator.IClothes;

public class Pants implements IClothes {
    private final IClothes clothes;

    public Pants(IClothes clothes) {
        this.clothes = clothes;
    }

    @Override
    public int getWarmValue() {
        return clothes.getWarmValue() + 50;
    }

}
