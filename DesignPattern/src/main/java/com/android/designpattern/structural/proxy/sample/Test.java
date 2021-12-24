package com.android.designpattern.structural.proxy.sample;

import com.android.designpattern.structural.proxy.sample.proxy.ProxyStack;

public class Test {

    @org.junit.Test
    public void main() {
        ProxyStack stack = new ProxyStack();
        for (int i = 0; i < 10; i++) {
            stack.push(i);
        }
        while (!stack.empty()) {
            System.out.println(stack.pop() + "," + stack.size());
        }
    }

}
