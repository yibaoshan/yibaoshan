package com.android.blackboard.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * author : xiaobao
 * e-mail : yibaoshan@foxmail.com
 * time   : 2023/04/10
 * desc   : 自定义倒计时 TextView
 * <p>
 * 1. 支持自定义时间格式，比如满足 >=1 天，只显示天数，否则只显示时分秒。具体规则请查看：
 *
 * @see DateFormatStrategy
 * simple 演示请查看：
 * @see DefaultStrategyDate
 */
public class CountDownTextView extends AppCompatTextView {

    public CountDownTextView(Context context) {
        super(context);
    }

    public CountDownTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CountDownTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private CountDownTimer mCountDownTimer;
    private DateFormatStrategy mDateFormatStrategy = new DefaultStrategyDate();
    private final List<CountDownListener> mCountDownListeners = new CopyOnWriteArrayList<>();

    /**
     * 启动倒计时
     *
     * @param millisInFuture 倒计时时间，单位为毫秒 ms
     */
    public void startCountdown(long millisInFuture) {
        startCountdown(millisInFuture, 1000);
    }

    /**
     * 启动倒计时
     *
     * @param millisInFuture    倒计时时间，单位为毫秒 ms
     * @param countDownInterval 倒计时间隔，多久回调一次 onTick() ，单位同样是毫秒
     */
    public synchronized void startCountdown(long millisInFuture, long countDownInterval) {
        cancelCountdown();
        mCountDownTimer = new CountDownTimer(millisInFuture + 50, countDownInterval) {

            public void onTick(long millisUntilFinished) {
                notifyTickChange(millisUntilFinished);
                setText(formatTime(millisUntilFinished));
            }

            public void onFinish() {
                notifyFinish();
            }
        };
        mCountDownTimer.start();
    }

    /**
     * 退出 / 关闭倒计时
     * <p>
     * 注意：主动调用退出方法时，不会触发 CountDownListener#finish() 回调
     */
    public synchronized void cancelCountdown() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    /**
     * 设置时间显示的格式，规则详情参考：
     *
     * @see DefaultStrategyDate
     */
    public void setDateFormatStrategy(DateFormatStrategy formatStrategy) {
        this.mDateFormatStrategy = formatStrategy;
    }

    /**
     * The synchronization mechanism is implemented by CopyOnWriteArrayList
     **/
    public boolean addCountDownListener(CountDownListener countDownListener) {
        return mCountDownListeners.add(countDownListener);
    }

    public boolean removeCountDownListener(CountDownListener countDownListener) {
        return mCountDownListeners.remove(countDownListener);
    }

    private String formatTime(long millis) {
        return mDateFormatStrategy.format(millis);
    }

    private void notifyFinish() {
        if (mCountDownListeners.isEmpty()) return;
        for (CountDownListener listener : mCountDownListeners) listener.onFinish();
    }

    private void notifyTickChange(long millis) {
        if (mCountDownListeners.isEmpty()) return;
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        for (CountDownListener listener : mCountDownListeners) listener.onTick(days, hours, minutes % 60, seconds % 60, millis % 1000);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mCountDownTimer != null) mCountDownTimer.cancel();
    }

    public static class DefaultStrategyDate implements DateFormatStrategy {

        // default format : yyyy-MM-dd HH:mm:ss

        @SuppressLint("DefaultLocale")
        @Override
        public String format(long millis) {
            long seconds = millis / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;
            if (days > 0) return String.format("%s 天", days);
            return String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60);
        }

    }

    public interface DateFormatStrategy {
        String format(long millis);

    }

    public interface CountDownListener {

        void onFinish();

        void onTick(long days, long hours, long minutes, long seconds, long millis);

    }

}