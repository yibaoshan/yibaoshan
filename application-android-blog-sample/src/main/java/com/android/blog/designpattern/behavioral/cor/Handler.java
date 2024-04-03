package com.android.blog.designpattern.behavioral.cor;

public abstract class Handler {

    protected Handler next;

    public abstract void handleRequest(String msg);

}
