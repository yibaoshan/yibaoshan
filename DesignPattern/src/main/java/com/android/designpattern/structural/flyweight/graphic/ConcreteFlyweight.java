package com.android.designpattern.structural.flyweight.graphic;

public class ConcreteFlyweight extends Flyweight {

    public String intrinsicState;

    public ConcreteFlyweight(String intrinsicState) {
        this.intrinsicState = intrinsicState;
    }

    @Override
    public void operation() {
        System.out.println(intrinsicState+" do operation");
    }

}
