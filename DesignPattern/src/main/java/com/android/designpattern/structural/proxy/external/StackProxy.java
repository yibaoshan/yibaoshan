package com.android.designpattern.structural.proxy.external;

public class StackProxy<E> extends java.util.Stack<E> {

    @Override
    public E push(E item) {
        long start = System.currentTimeMillis();
        E push = super.push(item);
        System.err.println("push:" + (System.currentTimeMillis() - start));
        return push;
    }

    @Override
    public synchronized E pop() {
        long start = System.currentTimeMillis();
        E pop = super.pop();
        System.err.println("pop:" + (System.currentTimeMillis() - start));
        return pop;
    }
}
