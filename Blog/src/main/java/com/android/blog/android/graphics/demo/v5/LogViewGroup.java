package com.android.blog.android.graphics.demo.v5;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

/**
 * Created on 2022/11/24
 */
public class LogViewGroup extends ViewGroup {

    private static String TAG = "LogView";

    public LogViewGroup(Context context) {
        super(context);
    }

    public LogViewGroup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LogViewGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e(TAG, "onTouchEvent: " + event.toString());
//        return super.onTouchEvent(event);
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.e(TAG, "dispatchTouchEvent: " + event.toString());
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

}
