package com.android.blog.android_view.demo.v4;

import android.view.View;

/**
 * Created on 2022/8/29
 *
 */
public class MeasureSpecUtil {

    public static String getMode(int spec) {
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
