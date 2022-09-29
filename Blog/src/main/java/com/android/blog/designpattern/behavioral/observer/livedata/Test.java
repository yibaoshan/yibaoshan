package com.android.blog.designpattern.behavioral.observer.livedata;


import com.android.designpattern.behavioral.observer.livedata.LiveData;
import com.android.designpattern.behavioral.observer.livedata.LiveDataObserver;

public class Test {

    @org.junit.Test
    public void main() {
        LiveData<String> liveData = new LiveData<>();
        liveData.addObserver(new LiveDataObserver());
        liveData.setValue("404");
    }

}
