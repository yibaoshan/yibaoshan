package com.android.jetpack.fragment.usage.activity

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import com.android.jetpack.BaseActivity
import com.android.jetpack.R

class UsageActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_main)
        title = "Fragment-使用方式"
        init()
    }

    private fun init() {
        val rootView = findViewById<LinearLayout>(R.id.layout_main)
        rootView.addView(createButtonView("XML<fragment>标签") { startActivity(Intent(this, UsageExample1Activity::class.java)) })
        rootView.addView(createButtonView("ViewGroup+FM(add)") { startActivity(Intent(this, UsageExample2Activity::class.java)) })
        rootView.addView(createButtonView("ViewGroup+FM(replace)") { startActivity(Intent(this, UsageExample3Activity::class.java)) })
        rootView.addView(createButtonView("ViewPager") { startActivity(Intent(this, UsageExample4Activity::class.java)) })
        rootView.addView(createButtonView("Navigation") { startActivity(Intent(this, UsageExample5Activity::class.java)) })
    }

}