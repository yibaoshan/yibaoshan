package com.android.blackboard

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.*
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.concurrent.thread

@ExperimentalStdlibApi
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Overview"
        setContentView(R.layout.activity_main)
        val intentFilter = IntentFilter("hhh")
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter)
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter)
        thread { LocalBroadcastManager.getInstance(this).registerReceiver(object : BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                Log.e("TAG", "not main thred onReceive: " )
                title = "title"
                Toast.makeText(this@MainActivity,"not main thred onReceive:",Toast.LENGTH_SHORT).show();
            }

        }, intentFilter) }
    }

    private val receiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.e("TAG", "onReceive: " )
        }

    }

    fun onJavaClick(view: View) {
        MenuActivity.startActivity(this, TypeEnum.Java)
    }

    fun onAndroidClick(view: View) {
        MenuActivity.startActivity(this, TypeEnum.Android)
    }

    fun onNetworkClick(view: View) {
        MenuActivity.startActivity(this, TypeEnum.Network)
    }

    fun onVMClick(view: View) {
        MenuActivity.startActivity(this, TypeEnum.VM)
    }

    fun onOSClick(view: View) {
        MenuActivity.startActivity(this, TypeEnum.OS)
    }

    fun onBookClick(view: View) {
        MenuActivity.startActivity(this, TypeEnum.BOOK)
    }

}