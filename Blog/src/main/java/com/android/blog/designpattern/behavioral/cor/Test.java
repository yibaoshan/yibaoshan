package com.android.blog.designpattern.behavioral.cor;


public class Test {

    @org.junit.Test
    public void main() {
        ConcreteHandler1 handler1 = new ConcreteHandler1();
        ConcreteHandler2 handler2 = new ConcreteHandler2();
        handler1.next = handler2;
        handler2.next = handler1;
        handler1.handleRequest("ConcreteHandler2");
    }

}
