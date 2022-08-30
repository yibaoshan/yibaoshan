package com.android.blog

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import com.android.blog.android_view.demo.v4.LayoutTestActivity
import com.android.blog.android_view.demo.v4.MeasureSpecTestActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val rootView = findViewById<LinearLayout>(R.id.layout_root);
        rootView.addView(generateButton("[measure]AT_MOST") { MeasureSpecTestActivity.startActivity(this, MeasureSpecTestActivity.TYPE.AT_MOST) })
        rootView.addView(generateButton("[measure]EXACTLY") { MeasureSpecTestActivity.startActivity(this, MeasureSpecTestActivity.TYPE.EXACTLY) })
        rootView.addView(generateButton("[measure]UNSPECIFIED") { MeasureSpecTestActivity.startActivity(this, MeasureSpecTestActivity.TYPE.UNSPECIFIED) })
        rootView.addView(generateButton("[layout]斜着的ViewGroup") { startActivity(Intent(this, LayoutTestActivity::class.java)) })
    }

    private fun generateButton(text: String, click: View.OnClickListener): Button {
        val button = AppCompatButton(this)
        button.text = text
        button.setOnClickListener(click)
        return button
    }

}