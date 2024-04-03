package com.android.blog.designpattern.behavioral.observer.livedata;

public interface Observer<T> {

    void onChanged(T t);

}
