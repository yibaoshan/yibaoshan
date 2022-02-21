package com.android.designpattern.behavioral.observer.livedata;

public class LiveDataObserver implements Observer<String>{

    @Override
    public void onChanged(String s) {
        System.out.println("received an message:"+s);
    }
}
