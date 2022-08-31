package com.android.blog.android_view.demo.v4.util;

import android.util.Log;
import android.view.View;

/**
 * Created on 2022/8/29
 */
public class PrintUtil {

    public static void measure(String TAG, int widthMeasureSpec, int heightMeasureSpec) {
        Log.e(TAG, "[measure] widthMeasureSpec: " + getMode(widthMeasureSpec) + " , heightMeasureSpec: " + getMode(heightMeasureSpec));
    }

    public static void layout(String TAG, int left, int top, int right, int bottom) {
        Log.e(TAG, "[layout] left: " + left + " , top: " + top + " , right: " + right + " , bottom: " + bottom);
    }

    private static String getMode(int spec) {
        switch (View.MeasureSpec.getMode(spec)) {
            case View.MeasureSpec.AT_MOST:
                return "AT_MOST(" + View.MeasureSpec.getSize(spec) + ")";
            case View.MeasureSpec.EXACTLY:
                return "EXACTLY(" + View.MeasureSpec.getSize(spec) + ")";
            case View.MeasureSpec.UNSPECIFIED:
                return "UNSPECIFIED(" + View.MeasureSpec.getSize(spec) + ")";
        }
        return "UNKNOWN";
    }
}
