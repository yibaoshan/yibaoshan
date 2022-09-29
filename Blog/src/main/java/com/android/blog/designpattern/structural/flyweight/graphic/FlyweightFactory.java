package com.android.blog.designpattern.structural.flyweight.graphic;

import com.android.designpattern.structural.flyweight.graphic.ConcreteFlyweight;

import java.util.HashMap;

public class FlyweightFactory {

    private static HashMap<String, Flyweight> mpFlyweight = new HashMap<>();

    public static Flyweight getFlyweight(String str) {
        if (mpFlyweight.containsKey(str)) return mpFlyweight.get(str);
        Flyweight flyweight = new ConcreteFlyweight(str);
        mpFlyweight.put(str, flyweight);
        return flyweight;
    }

}
