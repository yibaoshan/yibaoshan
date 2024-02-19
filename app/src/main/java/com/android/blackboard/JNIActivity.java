package com.android.blackboard;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * author : <a href="https://yibs.space"/>
 * e-mail : yibaoshan@foxmail.com
 * time   : 2024/02/19
 */
public class JNIActivity extends AppCompatActivity {

    static {
        System.loadLibrary("native-lib");
    }

    public native long allocateMemory();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Button button = new Button(this);
        button.setText("申请内存");
        button.setOnClickListener(v -> {
                    long memory = allocateMemory();
                    Toast.makeText(this, "ret: " + (memory / 1024 / 1024) + "M", Toast.LENGTH_SHORT).show();
                }
        );

        TextView textView = new TextView(this);
        setContentView(button);
    }
}
