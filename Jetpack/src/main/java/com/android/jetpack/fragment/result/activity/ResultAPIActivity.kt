package com.android.jetpack.fragment.result.activity

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import com.android.jetpack.BaseActivity
import com.android.jetpack.R

class ResultAPIActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_main)
        title = "Fragment-通信方式"
        init()
    }

    private fun init() {
        val rootView = findViewById<LinearLayout>(R.id.layout_main)
        rootView.addView(createButtonView("接口&方法调用") { startActivity(Intent(this, ResultExample1Activity::class.java)) })
        rootView.addView(createButtonView("ViewModel") { startActivity(Intent(this, ResultExample2Activity::class.java)) })
        rootView.addView(createButtonView("ResultAPI") { startActivity(Intent(this, ResultExample3Activity::class.java)) })
    }

}