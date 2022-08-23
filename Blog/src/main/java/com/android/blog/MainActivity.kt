package com.android.blog

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import com.android.blog.android_view.demo.v4.ScrollViewUnSpecifiedTestActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val rootView = findViewById<LinearLayout>(R.id.layout_root);
        rootView.addView(generateButton("父布局为UNSPECIFIED") { startActivity(Intent(this, ScrollViewUnSpecifiedTestActivity::class.java)) })
    }

    private fun generateButton(text: String, click: View.OnClickListener): Button {
        val button = AppCompatButton(this)
        button.text = text
        button.setOnClickListener(click)
        return button
    }

}