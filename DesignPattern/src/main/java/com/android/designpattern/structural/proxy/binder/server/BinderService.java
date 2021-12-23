package com.android.designpattern.structural.proxy.binder.server;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.android.designpattern.structural.proxy.binder.ILoginAidlInterface;

public class BinderService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
//        startActivity(new Intent(this, BinderServerActivity.class));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new ILoginAidlInterface.Stub() {
            @Override
            public boolean login(String id, String pwd) {
                return false;
            }
        }.asBinder();
    }

}
