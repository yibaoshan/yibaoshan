package com.android.designpattern.structural.proxy.binder.client;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.designpattern.R;
import com.android.designpattern.structural.proxy.binder.ILoginAidlInterface;

@RequiresApi(api = Build.VERSION_CODES.P)
@SuppressLint("UseSwitchCompatOrMaterialCode")
public class BinderClientActivity extends AppCompatActivity {

    private ILoginAidlInterface iLoginAidlInterface;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binder_client);
        init();
    }

    private void init() {
        bindingService();
        bindingClickListener();
    }

    private void bindingClickListener() {
        TextView tvPid = findViewById(R.id.tv_pid);
        TextView tvPName = findViewById(R.id.tv_process_name);
        tvPid.setText(String.valueOf(Process.myPid()));
        tvPName.setText(Application.getProcessName());

        EditText editUserId = findViewById(R.id.edit_user_id);
        EditText editUserPwd = findViewById(R.id.edit_user_pwd);

        Button btnLogin = findViewById(R.id.btn_login);
        Switch switchServer = findViewById(R.id.switch_server);
        switchServer.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (iLoginAidlInterface == null) return;
            try {
                if (isChecked) iLoginAidlInterface.connect();
                else iLoginAidlInterface.disconnect();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

        btnLogin.setOnClickListener(v -> {
            if (iLoginAidlInterface != null) {
                try {
                    int loginResult = iLoginAidlInterface.login(editUserId.getText().toString(), editUserPwd.getText().toString());
                    showLoginResult(loginResult);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void bindingService() {
        Intent intent = new Intent();
        intent.setClassName("com.android.designpattern", "com.android.designpattern.structural.proxy.binder.server.BinderService");
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void showLoginResult(int result) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (result < 0) {
            builder.setMessage("server not connected").setNegativeButton("ok", null).create().show();
            return;
        }
        if (result == 0) {
            builder.setMessage("login failed,please check id or password").setNegativeButton("ok", null).create().show();
            return;
        }
        builder.setMessage("login successfully").setNegativeButton("ok", null).create().show();
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
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
