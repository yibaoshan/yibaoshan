package com.android.blog.android_view.demo.v4;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * Created on 2022/8/26
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class SlantLayout extends LinearLayout {

    public SlantLayout(Context context) {
        super(context);
    }

    public SlantLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (getOrientation() == HORIZONTAL) throw new RuntimeException("horizontal orientation is not supported");
        layoutVertical(l, t, r, b);
    }


    void layoutVertical(int left, int top, int right, int bottom) {
        final int paddingLeft = getPaddingLeft();

        int childTop;
        int childLeft;

        // Where right end of child should go
        final int width = right - left;
        int childRight = width - getPaddingRight();

        // Space available for child
        int childSpace = width - paddingLeft - getPaddingRight();

        final int count = getChildCount();

        final int majorGravity = getGravity() & Gravity.VERTICAL_GRAVITY_MASK;
        final int minorGravity = getGravity() & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK;

//        switch (majorGravity) {
//            case Gravity.BOTTOM:
//                // mTotalLength contains the padding already
//                childTop = getPaddingTop() + bottom - top - mTotalLength;
//                break;
//
//            // mTotalLength contains the padding already
//            case Gravity.CENTER_VERTICAL:
//                childTop = getPaddingTop() + (bottom - top - mTotalLength) / 2;
//                break;
//
//            case Gravity.TOP:
//            default:
//                childTop = getPaddingTop();
//                break;
//        }
//
//        for (int i = 0; i < count; i++) {
//            final View child = getChildAt(i);
//            if (child == null) {
//            } else if (child.getVisibility() != GONE) {
//                final int childWidth = child.getMeasuredWidth();
//                final int childHeight = child.getMeasuredHeight();
//
//                final LinearLayout.LayoutParams lp =
//                        (LinearLayout.LayoutParams) child.getLayoutParams();
//
//                int gravity = lp.gravity;
//                if (gravity < 0) {
//                    gravity = minorGravity;
//                }
//                final int layoutDirection = getLayoutDirection();
//                final int absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection);
//                switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
//                    case Gravity.CENTER_HORIZONTAL:
//                        childLeft = paddingLeft + ((childSpace - childWidth) / 2)
//                                + lp.leftMargin - lp.rightMargin;
//                        break;
//
//                    case Gravity.RIGHT:
//                        childLeft = childRight - childWidth - lp.rightMargin;
//                        break;
//
//                    case Gravity.LEFT:
//                    default:
//                        childLeft = paddingLeft + lp.leftMargin;
//                        break;
//                }
//
//                if (hasDividerBeforeChildAt(i)) {
//                    childTop += mDividerHeight;
//                }
//
//                childTop += lp.topMargin;
//                setChildFrame(child, childLeft, childTop + getLocationOffset(child),
//                        childWidth, childHeight);
//                childTop += childHeight + lp.bottomMargin + getNextLocationOffset(child);
//
//                i += getChildrenSkipCount(child, i);
//            }
//        }
    }
}
