package com.android.blog.android_view.demo.v4;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.blog.R;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created on 2022/8/23
 */
public class MeasureSpecTestActivity extends AppCompatActivity {

    public enum TYPE implements Serializable {
        AT_MOST, EXACTLY, UNSPECIFIED
    }

    public static void startActivity(Context context, TYPE type) {
        if (context == null) return;
        Intent intent = new Intent(context, MeasureSpecTestActivity.class);
        int intType = 0;
        if (type == TYPE.EXACTLY) intType = 1;
        else if (type == TYPE.UNSPECIFIED) intType = 2;
        intent.putExtra("type", intType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switch (getIntent().getIntExtra("type", 0)) {
            case 0:
                performAT_MOST();
                break;
            case 1:
                performEXACTLY();
                break;
            case 2:
                performUNSPECIFIED();
                break;
        }
    }

    private void performAT_MOST() {
        setContentView(R.layout.activity_measure_spec_test_at_most);
        HelpWordView wordView = findViewById(R.id.word_view);
        ArrayList<HelpWordView.Word> arrayList = new ArrayList<>();
        arrayList.add(new HelpWordView.Word("鹅鹅鹅"));
        arrayList.add(new HelpWordView.Word("曲项向天歌"));
        arrayList.add(new HelpWordView.Word("白日依山尽"));
        arrayList.add(new HelpWordView.Word("低头思故乡"));
        wordView.setData(arrayList);
    }

    private void performEXACTLY() {
        setContentView(R.layout.activity_measure_spec_test_exactly);
    }

    private void performUNSPECIFIED() {
        setContentView(R.layout.activity_measure_spec_test_unspecified);
    }

}
