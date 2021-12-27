package com.android.designpattern.structural.decorator;

import org.junit.Test;

public class Main {

    @Test
    public void main() {
        //没有打扮时
        IBeauty alicia = new Alicia();
        System.out.println(alicia.getBeautyValue());
        //带上耳环
        alicia = new EarringDecorator(alicia);
        System.out.println(alicia.getBeautyValue());
        //带上项链
        alicia = new EarringDecorator(alicia);
        System.out.println(alicia.getBeautyValue());
        //带上戒指
        alicia = new RingDecorator(alicia);
        System.out.println(alicia.getBeautyValue());
    }

}
