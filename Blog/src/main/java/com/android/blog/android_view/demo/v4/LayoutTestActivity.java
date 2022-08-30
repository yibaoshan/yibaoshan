package com.android.blog.android_view.demo.v4;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.blog.R;

/**
 * Created on 2022/8/30
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class LayoutTestActivity extends AppCompatActivity {

    SlantLinearLayout slantLinearLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_test);
        slantLinearLayout = findViewById(R.id.slant_layout);
    }

    public void onEnableSlant(View view) {
        slantLinearLayout.enableSlant(true);
    }

    public void onDisableSlant(View view) {
        slantLinearLayout.enableSlant(false);
    }
}
