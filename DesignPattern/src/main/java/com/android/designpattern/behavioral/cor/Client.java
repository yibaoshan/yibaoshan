package com.android.designpattern.behavioral.cor;

import org.junit.Test;

public class Client {

    @Test
    public void main() {
        Handler handler1 = new ConcreteHandler();
        Handler handler2 = new ConcreteHandler();
        handler1.setNext(handler2);
        //提交请求
        handler1.execute("");
    }

}
