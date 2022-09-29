package com.android.blog.designpattern.behavioral.cor;

public class ConcreteHandler2 extends Handler {

    @Override
    public void handleRequest(String msg) {
        if (msg.equals("ConcreteHandler2")) System.out.println("ConcreteHandler2 handled");
        else if (next != null) next.handleRequest(msg);
    }
}