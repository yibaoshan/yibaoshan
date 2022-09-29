package com.android.blog.android.graphics.demo.v4.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.android.blog.android.graphics.demo.v4.util.PrintUtil;

/**
 * Created on 2022/8/23
 */
public class LogFrameLayout extends FrameLayout {

    private static final String TAG = "LogFrameLayout";

    public LogFrameLayout(Context context) {
        super(context);
    }

    public LogFrameLayout(Context context, @Nullable AttributeSet attrs) {
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