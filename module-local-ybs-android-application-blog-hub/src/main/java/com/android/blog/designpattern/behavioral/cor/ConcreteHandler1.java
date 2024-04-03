package com.android.blog.designpattern.behavioral.cor;

public class ConcreteHandler1 extends Handler {

    @Override
    public void handleRequest(String msg) {
        if (msg.equals("ConcreteHandler1")) System.out.println("ConcreteHandler1 handled");
        else if (next != null) next.handleRequest(msg);
    }
}