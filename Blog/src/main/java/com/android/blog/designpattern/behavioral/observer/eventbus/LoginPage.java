package com.android.blog.designpattern.behavioral.observer.eventbus;

import com.android.designpattern.behavioral.observer.eventbus.LocalBroadcastManager;

import java.util.HashMap;

public class LoginPage {

    private HashMap<String, String> dp;

    public LoginPage() {
        dp = new HashMap<>();
        dp.put("admin", "admin");
    }

    public void login(String name, String pwd) {
        if (dp.containsKey(name)) {
            if (dp.get(name).equals(pwd)) LocalBroadcastManager.sendBroadcast("successful login");
            else LocalBroadcastManager.sendBroadcast("login failed, access denied");
        } else {
            LocalBroadcastManager.sendBroadcast("login failed, user does not exist");
        }
    }
}
