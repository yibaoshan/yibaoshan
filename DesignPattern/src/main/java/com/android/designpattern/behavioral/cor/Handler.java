package com.android.designpattern.behavioral.cor;

public abstract class Handler {

    protected Handler next;

    public abstract void execute(Object obj);

    public Handler getNext() {
        return next;
    }

    public void setNext(Handler next) {
        this.next = next;
    }

}
