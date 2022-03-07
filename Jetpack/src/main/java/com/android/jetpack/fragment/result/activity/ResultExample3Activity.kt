package com.android.jetpack.fragment.result.activity

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.jetpack.R

class ResultExample3Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "通信-ResultAPI"
        setContentView(R.layout.activity_result_example3)
        init()
    }

    private fun init() {
        supportFragmentManager.setFragmentResultListener("requestKey", this, { requestKey, bundle ->
            // 监听key为“requestKey”的结果， 并通过bundle获取
            val result = bundle.getString("bundleKey")
            findViewById<TextView>(R.id.tv_content).text = result
        })
    }

}