package com.android.designpattern.structural.flyweight.graphic;

public class UnsharedConcreteFlyweight extends Flyweight {

    public int allState;

    public UnsharedConcreteFlyweight(int allState) {
        this.allState = allState;
    }

    @Override
    public void operation() {
        System.out.println(allState + " do operation");
    }
}
