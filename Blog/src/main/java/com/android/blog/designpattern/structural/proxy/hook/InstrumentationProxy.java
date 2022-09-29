package com.android.blog.designpattern.structural.proxy.hook;

import android.app.Instrumentation;

public class InstrumentationProxy extends Instrumentation {

    private final Instrumentation instrumentation;

    public InstrumentationProxy(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
    }

}
