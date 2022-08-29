package com.android.blog.android_view.demo.v4;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.Nullable;

/**
 * Created on 2022/8/23
 */
public class LogScrollView extends ScrollView {

    private static final String TAG = "LogScrollView";

    public LogScrollView(Context context) {
        super(context);
    }

    public LogScrollView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.e(TAG, "widthMeasureSpec: " + MeasureSpecUtil.getMode(widthMeasureSpec));
        Log.e(TAG, "heightMeasureSpec: " + MeasureSpecUtil.getMode(heightMeasureSpec));
    }


}