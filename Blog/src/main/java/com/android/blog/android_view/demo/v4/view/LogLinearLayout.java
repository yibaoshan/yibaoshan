package com.android.blog.android_view.demo.v4.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.android.blog.android_view.demo.v4.util.PrintUtil;

/**
 * Created on 2022/8/23
 */
public class LogLinearLayout extends LinearLayout {

    private static final String TAG = "LogLinearLayout";

    public LogLinearLayout(Context context) {
        super(context);
    }

    public LogLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        PrintUtil.measure(TAG, widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        PrintUtil.layout(TAG, left, top, right, bottom);
    }


}