package com.android.designpattern.structural.proxy.binder.server;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.designpattern.structural.proxy.binder.ILoginAidlInterface;

public class BinderService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new ProxyBusinessILoginAidlInterface().asBinder();
    }

    private class BusinessILoginAidlInterface extends ILoginAidlInterface.Stub {

        @Override
        public void connect() {
            long start = System.currentTimeMillis();
            Intent intent = new Intent(BinderService.this, BinderServerActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            long end = System.currentTimeMillis();
            long connectTime = end - start;
        }

        @Override
        public void disconnect() {
            LocalBroadcastManager.getInstance(BinderService.this).sendBroadcast(new Intent("ACTION_BINDER_SERVICE_DISCONNECT"));
        }

        @Override
        public int login(String id, String pwd) throws RemoteException {
            if (Database.getInstance().size() == 0) return -1;
            return Database.getInstance().query(id, pwd) ? 1 : 0;
        }
    }

    private class ProxyBusinessILoginAidlInterface extends BusinessILoginAidlInterface {

        @Override
        public int login(String id, String pwd) throws RemoteException {
            //耗时统计
            return super.login(id, pwd);
        }
    }



}
