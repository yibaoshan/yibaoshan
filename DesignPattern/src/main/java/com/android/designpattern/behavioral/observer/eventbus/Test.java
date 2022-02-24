package com.android.designpattern.behavioral.observer.eventbus;

public class Test {

    @org.junit.Test
    public void main() {
        register();
        LoginPage loginPage  = new LoginPage();
        loginPage.login("admin","admin");
    }

    private void register(){
        LoginBroadcastReceiver mainReceiver = new LoginBroadcastReceiver("主页");
        LoginBroadcastReceiver basketReceiver = new LoginBroadcastReceiver("购物车");
        LoginBroadcastReceiver UCReceiver = new LoginBroadcastReceiver("用户中心");

        LocalBroadcastManager.register(mainReceiver);
        LocalBroadcastManager.register(basketReceiver);
        LocalBroadcastManager.register(UCReceiver);
    }

}
