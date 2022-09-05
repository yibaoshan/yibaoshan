package com.android.blog.android_view.demo.v4;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

public class TestViewGroup extends ViewGroup {

    public TestViewGroup(Context context) {
        super(context);
    }

    public TestViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TestViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }
}
