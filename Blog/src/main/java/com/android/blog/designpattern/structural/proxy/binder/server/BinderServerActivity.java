package com.android.blog.designpattern.structural.proxy.binder.server;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.designpattern.R;

@RequiresApi(api = Build.VERSION_CODES.P)
public class BinderServerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binder_server);

        TextView tvPid = findViewById(R.id.tv_pid);
        TextView tvPName = findViewById(R.id.tv_process_name);

        tvPid.setText(String.valueOf(Process.myPid()));
        tvPName.setText(Application.getProcessName());

        initDatabase();
    }

    private void initDatabase() {
        Database.getInstance().put("admin", "123456");
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("ACTION_BINDER_SERVICE_DISCONNECT"));
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Database.getInstance().clear();
            finish();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }
}
