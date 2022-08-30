package com.android.blog.android_view.demo.v4;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.Nullable;

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
        Log.e(TAG, "widthMeasureSpec: " + MeasureSpecUtil.getMode(widthMeasureSpec));
        Log.e(TAG, "heightMeasureSpec: " + MeasureSpecUtil.getMode(heightMeasureSpec));
    }


}