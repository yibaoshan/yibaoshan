package com.android.blackboard;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class TestService extends Service {

    public TestService(){
//        super("mmp");
    }

    public TestService(String name) {
//        super(name);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new SampleServiceBinder();
    }

//    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Toast.makeText(this,"I am a Service",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        builder.setNegativeButton("R.string.cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        AlertDialog dialog = builder.create();
//        dialog.show();
        Toast.makeText(this,"I am a Service",Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private class SampleServiceBinder extends Binder {

        public TestService getService() {
            return TestService.this;
        }

    }

}
