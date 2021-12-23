package com.android.designpattern.structural.proxy.binder.server;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.designpattern.R;
import com.android.designpattern.structural.proxy.binder.ILoginAidlInterface;

import java.util.HashMap;
import java.util.Map;


public class BinderServerActivity extends AppCompatActivity {

    private final Map<String, String> dp = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binder_server);
        TextView tvPid = findViewById(R.id.tv_pid);
        tvPid.setText(String.valueOf(Process.myPid()));
        init();
    }

    private void init() {
        dp.put("admin", "123qwe");
    }

    private final ILoginAidlInterface.Stub binder = new ILoginAidlInterface.Stub() {
        @Override
        public boolean login(String id, String pwd) {
            if (id == null || pwd == null) return false;
            if (dp.containsKey(id)) return pwd.equals(dp.get(id));
            return false;
        }
    };

}
