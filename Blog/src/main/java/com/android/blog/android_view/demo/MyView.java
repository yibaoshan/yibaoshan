package com.android.blog.android_view.demo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * Created by yibs.space on 2022/4/21
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MyView extends View {

    public MyView(Context context) {
        super(context);
    }

    public MyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        init();
    }

    private void init() {
        start();
    }

    private boolean changed = true;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (changed) canvas.drawColor(Color.RED);
        else canvas.drawColor(Color.BLUE);
        changed = !changed;
    }

    private void start() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 2);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.addUpdateListener(animation -> {
            Log.e("TAG", "start: " + animation.toString());
            postInvalidate();
        });
        valueAnimator.start();
    }
}
