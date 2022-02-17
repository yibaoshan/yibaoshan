package com.android.designpattern.behavioral.observer.eventbus;

public class Me implements BroadcastReceiver {

    @Override
    public void onReceive(Object obj) {
        System.out.println(obj+",我：您的基金");
    }
}
