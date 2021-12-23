package com.android.designpattern

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.android.designpattern.structural.proxy.binder.client.BinderClientActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.btn_launch_binder).setOnClickListener {
//            startActivity(Intent(this, BinderServerActivity::class.java))
            startActivity(Intent(this, BinderClientActivity::class.java))
        }
    }
}