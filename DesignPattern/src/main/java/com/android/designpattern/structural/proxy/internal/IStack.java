package com.android.designpattern.structural.proxy.internal;

public interface IStack {

    void push(int val);

    int pop();

    int peek();

    int size();

    boolean empty();

}
