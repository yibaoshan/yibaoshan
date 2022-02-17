package com.android.designpattern.behavioral.observer.eventbus;

public class LoginResultBroadcastReceiver implements BroadcastReceiver {

    @Override
    public void onReceive(Object obj) {
        //login result
        System.out.println(obj);
    }
}
