package com.android.designpattern.structural.proxy.binder.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.designpattern.R;
import com.android.designpattern.structural.proxy.binder.ILoginAidlInterface;

public class BinderClientActivity extends AppCompatActivity {

    private ILoginAidlInterface iLoginAidlInterface;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binder_client);

        TextView tvPid = findViewById(R.id.tv_pid);
        EditText editUserId = findViewById(R.id.edit_user_id);
        EditText editUserPwd = findViewById(R.id.edit_user_pwd);
        Button btnLogin = findViewById(R.id.btn_login);
        tvPid.setText(String.valueOf(Process.myPid()));

        btnLogin.setOnClickListener(v -> {
            if (iLoginAidlInterface != null) {
                try {
                    boolean loginResult = iLoginAidlInterface.login(editUserId.getText().toString(), editUserPwd.getText().toString());
                    showLoginResult(loginResult);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        Intent intent = new Intent();
        intent.setClassName("com.android.designpattern", "com.android.designpattern.structural.proxy.binder.server.BinderService");
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void showLoginResult(boolean result) {
        String message = "Login Successfully";
        if (!result) message = "Login failed,Please check id or password";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Login").setMessage(message).setNegativeButton("ok", null).create().show();
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Toast.makeText(BinderClientActivity.this, "service connected", Toast.LENGTH_SHORT).show();
            iLoginAidlInterface = ILoginAidlInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iLoginAidlInterface = null;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }
}
