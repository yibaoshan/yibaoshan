package com.android.designpattern.structural.proxy.sample.proxy;

import com.android.designpattern.structural.proxy.sample.IStack;
import com.android.designpattern.structural.proxy.sample.product.UnsupportedAppUsageStack;

import java.util.Stack;

public class ProxyStack implements IStack {

//    private final UnsupportedAppUsageStack stack;
    private final Stack<Integer> stack;

    public ProxyStack() {
//        stack = new UnsupportedAppUsageStack();
        stack = new Stack<>();
    }

    @Override
    public void push(int val) {
        long start = System.currentTimeMillis();
        stack.push(val);
        System.out.println("push:" + (System.currentTimeMillis() - start));
    }

    @Override
    public int pop() {
        long start = System.currentTimeMillis();
        int res = stack.pop();
        System.out.println("pop:" + (System.currentTimeMillis() - start));
        return res;
    }

    @Override
    public int peek() {
        return stack.peek();
    }

    @Override
    public int size() {
        return stack.size();
    }

    @Override
    public boolean empty() {
        return stack.empty();
    }

}
