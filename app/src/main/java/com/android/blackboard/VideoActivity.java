package com.android.blackboard;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.blackboard.ui.widget.CountDownTextView;

public class VideoActivity extends AppCompatActivity {

    private static final String TAG = "VideoActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        CountDownTextView countDownTextView = findViewById(R.id.tv_countdown);
        countDownTextView.startCountdown(24*60*60*1000+3*1000);
        countDownTextView.addCountDownListener(new CountDownTextView.CountDownListener() {
            @Override
            public void onFinish() {

            }

            @Override
            public void onTick(long days, long hours, long minutes, long seconds, long millis) {
                Log.e(TAG, "onTick: " +days + ", " + hours+" , "+minutes + " ,  "+seconds+", "+millis );
            }
        });
    }

}
