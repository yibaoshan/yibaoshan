package com.android.blog.android_view.demo.v4.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.blog.android_view.demo.v4.util.PrintUtil;

/**
 * Created on 2022/8/23
 */
public class LogTextView extends androidx.appcompat.widget.AppCompatTextView {

    private static final String TAG = "LogTextView";

    public LogTextView(Context context) {
        super(context);
    }

    public LogTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        PrintUtil.measure(TAG + "【" + getText().toString() + "】", widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        PrintUtil.layout(TAG + "【" + getText().toString() + "】", left, top, right, bottom);
    }


}