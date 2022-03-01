package com.android.blackboard

import android.os.*
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

@ExperimentalStdlibApi
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Overview"
        setContentView(R.layout.activity_main)
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

}