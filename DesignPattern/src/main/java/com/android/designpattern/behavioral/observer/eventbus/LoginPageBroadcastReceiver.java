package com.android.designpattern.behavioral.observer.eventbus;

public class LoginPageBroadcastReceiver implements BroadcastReceiver {

    @Override
    public void onReceive(Object obj) {
        System.out.println(obj);
    }
}
