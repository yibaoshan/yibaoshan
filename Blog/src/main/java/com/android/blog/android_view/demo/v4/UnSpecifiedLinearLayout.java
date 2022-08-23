package com.android.blog.android_view.demo.v4;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

/**
 * Created on 2022/8/23
 */
public class UnSpecifiedLinearLayout extends LinearLayout {

    private static final String TAG = "UnSpecifiedLinearLayout";

    public UnSpecifiedLinearLayout(Context context) {
        super(context);
    }

    public UnSpecifiedLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.e(TAG, "widthMeasureSpec: " + getMode(widthMeasureSpec));
        Log.e(TAG, "heightMeasureSpec: " + getMode(heightMeasureSpec));
    }

    private String getMode(int spec) {
        switch (MeasureSpec.getMode(spec)) {
            case MeasureSpec.AT_MOST:
                return "AT_MOST";
            case MeasureSpec.EXACTLY:
                return "EXACTLY";
            case MeasureSpec.UNSPECIFIED:
                return "UNSPECIFIED";
        }
        return "UNKNOWN";
    }
}