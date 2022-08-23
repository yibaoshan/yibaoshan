package com.android.blog.android_view.demo.v4;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.blog.R;

import java.util.ArrayList;

/**
 * Created on 2022/8/23
 */
public class ScrollViewUnSpecifiedTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrollview_unspectified_test);
        HelpWordView wordView = findViewById(R.id.word_view);
        ArrayList<HelpWordView.Word> arrayList = new ArrayList<>();
        arrayList.add(new HelpWordView.Word("鹅鹅鹅"));
        arrayList.add(new HelpWordView.Word("曲项向天歌"));
        arrayList.add(new HelpWordView.Word("白日依山尽"));
        arrayList.add(new HelpWordView.Word("低头思故乡"));
        wordView.setData(arrayList);
    }
}
