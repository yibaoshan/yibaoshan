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

import java.util.Random;

/**
 * Created by yibs.space on 2022/4/21
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MyView extends View {

    private final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 2);

    public MyView(Context context) {
        this(context, null);
    }

    public MyView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public MyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.addUpdateListener(animation -> {
//            sleep();
            Log.e("TAG", "start: " + animation.toString());
            invalidate();
        });
    }

    private void sleep() {
        if (new Random().nextInt(100) % 3 == 0) {
            try {
                Thread.sleep(new Random().nextInt(100));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean changed = true;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (changed) canvas.drawColor(Color.RED);
        else canvas.drawColor(Color.BLUE);
        changed = !changed;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        valueAnimator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        valueAnimator.end();
    }

}
