package com.android.blog.designpattern.behavioral.observer.eventbus;

import java.util.LinkedList;
import java.util.List;

public class LocalBroadcastManager {

    private static final List<BroadcastReceiver> broadcasts = new LinkedList<>();

    public static void sendBroadcast(Object obj) {
        for (BroadcastReceiver receiver : broadcasts) receiver.onReceive(obj);
    }

    public static void register(BroadcastReceiver broadcastReceiver) {
        broadcasts.add(broadcastReceiver);
    }

    public static void unregister(BroadcastReceiver broadcastReceiver) {
        broadcasts.remove(broadcastReceiver);
    }

}
