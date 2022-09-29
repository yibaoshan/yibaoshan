package com.android.blog.designpattern.structural.flyweight.graphic;

import com.android.designpattern.structural.flyweight.graphic.Flyweight;
import com.android.designpattern.structural.flyweight.graphic.FlyweightFactory;

import org.junit.Test;

public class Main {

    /**
     * 该实现原型来源于：https://design-patterns.readthedocs.io/zh_CN/latest/structural_patterns/flyweight.html
     */

    @Test
    public void main() {
        Flyweight flyweight1 = FlyweightFactory.getFlyweight("one");
        Flyweight flyweight2 = FlyweightFactory.getFlyweight("two");
        Flyweight flyweight3 = FlyweightFactory.getFlyweight("one");
        flyweight1.operation();
        flyweight2.operation();
        flyweight3.operation();
    }

}
