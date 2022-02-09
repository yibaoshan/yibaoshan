package com.android.designpattern.behavioral.cor;

public class ConcreteHandler extends Handler {

    @Override
    public void execute(Object obj) {
        System.out.println(obj + " execute");
        if (getNext() != null) getNext().execute(obj);
    }
}
