package com.android.designpattern

import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.android.designpattern.structural.proxy.binder.client.BinderClientActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        findViewById<Button>(R.id.btn_launch_binder).setOnClickListener {
            LocalBroadcastManager.getInstance(this).sendBroadcast(Intent("IntentFilter_001"))
            startActivity(Intent(this, BinderClientActivity::class.java))
        }
        val intentFilter1 = IntentFilter("IntentFilter_001");
        LocalBroadcastManager.getInstance(this).registerReceiver(object : BroadcastReceiver(){
            override fun onReceive(p0: Context?, p1: Intent?) {
                Log.e(TAG, "onReceive: 1")
                Thread.sleep(3000)
            }
        },intentFilter1)
        LocalBroadcastManager.getInstance(this).registerReceiver(object : BroadcastReceiver(){
            override fun onReceive(p0: Context?, p1: Intent?) {
                Log.e(TAG, "onReceive: 2")
            }
        },intentFilter1)
    }

}