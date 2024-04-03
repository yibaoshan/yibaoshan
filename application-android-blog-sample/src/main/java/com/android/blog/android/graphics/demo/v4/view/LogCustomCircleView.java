package com.android.blog.android.graphics.demo.v4.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.android.blog.android.graphics.demo.v4.util.PrintUtil;

/**
 * Created on 2022/8/31
 */
public class LogCustomCircleView extends View {

    private static final String TAG = "LogCustomCircleView";

    public LogCustomCircleView(Context context) {
        super(context);
    }

    public LogCustomCircleView(Context context, @Nullable AttributeSet attrs) {
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(getWidth(), getWidth(), getWidth(), new Paint());
    }
}
