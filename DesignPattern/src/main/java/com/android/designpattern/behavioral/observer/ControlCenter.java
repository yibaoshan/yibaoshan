package com.android.designpattern.behavioral.observer;

import java.util.ArrayList;
import java.util.List;

public class ControlCenter   {

    private static List<IObserver> list = new ArrayList<>();

    static void registerObserver(IObserver observer) {
        list.add(observer);
    }

    static void removeObserver(IObserver observer) {
        list.remove(observer);
    }

    static void notifyObservers(String msg) {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).update(msg);
        }
    }
}
