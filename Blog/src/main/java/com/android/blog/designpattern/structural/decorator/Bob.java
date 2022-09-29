package com.android.blog.designpattern.structural.decorator;


import com.android.designpattern.structural.decorator.IClothes;

public class Bob implements IClothes {
    @Override
    public int getWarmValue() {
        return 0;
    }
}
