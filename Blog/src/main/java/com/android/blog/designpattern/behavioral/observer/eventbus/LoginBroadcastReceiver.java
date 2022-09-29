package com.android.blog.designpattern.behavioral.observer.eventbus;

public class LoginBroadcastReceiver implements BroadcastReceiver {

    private String pageName;

    public LoginBroadcastReceiver(String pageName) {
        this.pageName = pageName;
    }

    @Override
    public void onReceive(Object obj) {
        System.out.println(pageName+" : "+obj);
    }
}
