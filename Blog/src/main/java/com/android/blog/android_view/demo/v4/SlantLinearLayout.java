package com.android.blog.android_view.demo.v4;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.android.blog.android_view.demo.v4.util.PrintUtil;

import java.lang.reflect.Field;

/**
 * Created on 2022/8/26
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class SlantLinearLayout extends LinearLayout {

    private boolean slant = false;

    private static final String TAG = "SlantLinearLayout";

    public SlantLinearLayout(Context context) {
        super(context);
    }

    public SlantLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        PrintUtil.measure(TAG, widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        PrintUtil.layout(TAG, l, t, r, b);
        if (getOrientation() == HORIZONTAL) throw new RuntimeException("horizontal orientation is not supported");
        layoutVertical(l, t, r, b);
    }

    void enableSlant(boolean enable) {
        if (enable != slant) requestLayout();
        slant = enable;
    }

    //偏移量，单位为像素，有需求的话可以改为用户自定义，这里只是个demo所以写死了
    private static final int offset = 60;

    void layoutVertical(int left, int top, int right, int bottom) {
        int childLeft;//每个子View距离左边间距大小，这里偷懒全部居中了
        int childTop = getPaddingTop() + (bottom - top - getTotalLength()) / 2;//垂直方向也直接居中处理了，这里计算的是第一个子View的起始位置
        int childSpace = right - left - getPaddingLeft() - getPaddingRight();//减去父视图的padding后，所有子View实际可用的宽度

        final int count = getChildCount();

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            final int childWidth = child.getMeasuredWidth();//获取子View的宽高
            final int childHeight = child.getMeasuredHeight();
            final LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) child.getLayoutParams();//获取子View的LayoutParams属性，主要为了拿到margin属性

            childLeft = getPaddingLeft() + ((childSpace - childWidth) / 2) + lp.leftMargin - lp.rightMargin;//根据父视图的padding和子View的margin、自身测量的宽高，计算每个子View起始位置
            childTop += lp.topMargin;//起始位置加上子View设定的顶部距离

            int childOffset = 0;
            if (slant) childOffset = offset * i - (count - i - 1) * offset;//如果用户启用倾斜属性，那么计算每个子View在原先的基础上的偏移量

            setChildFrame(child, childLeft + childOffset, childTop, childWidth, childHeight);//通知子View你的位置已经确定好了
            childTop += childHeight + lp.bottomMargin;//别忘了View可能是距离底部的属性，加上它
        }

    }

    private int getTotalLength() {
        try {
            Field field = this.getClass().getSuperclass().getDeclaredField("mTotalLength");
            field.setAccessible(true);
            return field.getInt(this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void setChildFrame(View child, int left, int top, int width, int height) {
        child.layout(left, top, left + width, top + height);
    }
}
