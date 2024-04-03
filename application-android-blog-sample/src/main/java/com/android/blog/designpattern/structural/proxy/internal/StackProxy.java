package com.android.blog.designpattern.structural.proxy.internal;

public class StackProxy implements IStack {

    private final IStack stack;

    public StackProxy(IStack stack) {
        this.stack = stack;
    }

    @Override
    public void push(int val) {
        long start = System.currentTimeMillis();
        stack.push(val);
        System.err.println("push:" + (System.currentTimeMillis() - start));
    }

    @Override
    public int pop() {
        long start = System.currentTimeMillis();
        int res = stack.pop();
        System.err.println("pop:" + (System.currentTimeMillis() - start));
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
