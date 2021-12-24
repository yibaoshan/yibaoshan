package com.android.designpattern.structural.proxy.binder.server;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.designpattern.structural.proxy.binder.ILoginAidlInterface;

public class BinderService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new ILoginAidlInterface.Stub() {

            @Override
            public void connect() {
                Intent intent = new Intent(BinderService.this, BinderServerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            @Override
            public void disconnect() {
                LocalBroadcastManager.getInstance(BinderService.this).sendBroadcast(new Intent("ACTION_BINDER_SERVICE_DISCONNECT"));
            }

            @Override
            public int login(String id, String pwd) {
                if (Database.getInstance().size() == 0) return -1;
                return Database.getInstance().query(id, pwd) ? 1 : 0;
            }
        }.asBinder();
    }

}
