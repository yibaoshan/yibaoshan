package com.android.designpattern

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.android.designpattern.behavioral.cor.broadcast.OrderlyBroadcastActivity
import com.android.designpattern.structural.proxy.binder.client.BinderClientActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        findViewById<Button>(R.id.btn_launch_binder).setOnClickListener {
            startActivity(Intent(this, BinderClientActivity::class.java))
        }
        findViewById<Button>(R.id.btn_launch_orderly_broadcast).setOnClickListener {
            startActivity(Intent(this, OrderlyBroadcastActivity::class.java))
        }
    }

}