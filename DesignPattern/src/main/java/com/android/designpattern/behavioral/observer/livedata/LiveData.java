package com.android.designpattern.behavioral.observer.livedata;

import java.util.LinkedList;
import java.util.List;

public class LiveData<T> {

    private final List<Observer<T>> observers = new LinkedList<>();

    public void addObserver(Observer<T> observer) {
        observers.add(observer);
    }

    public void remove(Observer<T> observer) {
        observers.remove(observer);
    }

    public void setValue(T t) {
        for (Observer<T> observer : observers) observer.onChanged(t);
    }

}
