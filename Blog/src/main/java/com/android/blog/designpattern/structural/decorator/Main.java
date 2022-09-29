package com.android.blog.designpattern.structural.decorator;

import com.android.designpattern.structural.decorator.IClothes;
import com.android.designpattern.structural.decorator.Pants;
import com.android.designpattern.structural.decorator.Shirt;

import org.junit.Test;

public class Main {

    @Test
    public void main() {
        IClothes bob = new Bob();
        System.out.println("什么都没穿时的保暖值：" + bob.getWarmValue());
        bob = new Shirt(bob);
        System.out.println("穿了件衬衫时的保暖值：" + bob.getWarmValue());
        bob = new Pants(bob);
        System.out.println("又穿了条裤子时的保暖值：" + bob.getWarmValue());
        bob = new Coat(bob);
        System.out.println("又穿了件外套时的保暖值：" + bob.getWarmValue());
    }

}
