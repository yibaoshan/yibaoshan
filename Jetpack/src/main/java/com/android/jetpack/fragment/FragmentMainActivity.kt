package com.android.jetpack.fragment

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import com.android.jetpack.fragment.result.activity.ResultAPIActivity
import com.android.jetpack.fragment.usage.activity.UsageActivity
import com.android.jetpack.BaseActivity
import com.android.jetpack.R

class FragmentMainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_main)
        init()
    }

    private fun init() {
        val rootView = findViewById<LinearLayout>(R.id.layout_main)
        rootView.addView(createButtonView("Fragment使用") { startActivity(Intent(this, UsageActivity::class.java)) })
        rootView.addView(createButtonView("Fragment通信") { startActivity(Intent(this, ResultAPIActivity::class.java)) })
    }

}