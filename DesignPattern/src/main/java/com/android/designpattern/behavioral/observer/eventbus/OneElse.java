package com.android.designpattern.behavioral.observer.eventbus;

public class OneElse implements BroadcastReceiver {

    @Override
    public void onReceive(Object obj) {
        if (obj.toString().contains("涨")) System.err.println(obj + ",别人：哇赚好多");
        else if (obj.toString().contains("跌")) System.err.println(obj + ",别人：亏了一点，继续持有");
    }
}
