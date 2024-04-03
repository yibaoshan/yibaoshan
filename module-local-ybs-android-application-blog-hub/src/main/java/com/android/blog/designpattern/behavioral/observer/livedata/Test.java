package com.android.blog.designpattern.behavioral.observer.livedata;



public class Test {

    @org.junit.Test
    public void main() {
        LiveData<String> liveData = new LiveData<>();
        liveData.addObserver(new LiveDataObserver());
        liveData.setValue("404");
    }

}
