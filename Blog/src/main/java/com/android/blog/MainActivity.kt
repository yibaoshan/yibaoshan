package com.android.blog

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import com.android.blog.android.graphics.demo.v4.LayoutTestActivity
import com.android.blog.android.graphics.demo.v4.MeasureTestActivity
import com.android.blog.android.jetpack.fragment.blog.demo.FragmentTestMainActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val rootView = findViewById<LinearLayout>(R.id.layout_root)
        rootView.addView(generateButton("Fragment") { startActivity(Intent(this, FragmentTestMainActivity::class.java)) })
        rootView.addView(generateButton("") { })
        rootView.addView(generateButton("[measure-cnt]FrameLayout") { MeasureTestActivity.startActivity(this, MeasureTestActivity.CNT_FRAME_LAYOUT) })
        rootView.addView(generateButton("[measure-cnt]LinearLayout") { MeasureTestActivity.startActivity(this, MeasureTestActivity.CNT_LINEAR_LAYOUT) })
        rootView.addView(generateButton("[measure-cnt]FrameDialog") { MeasureTestActivity.startActivity(this, MeasureTestActivity.CNT_FRAME_DIALOG) })
        rootView.addView(generateButton("") { })
        rootView.addView(generateButton("[measure-spec]FrameMatch") { MeasureTestActivity.startActivity(this, MeasureTestActivity.SPEC_FRAME_MATCH) })
        rootView.addView(generateButton("[measure-spec]FrameWrap") { MeasureTestActivity.startActivity(this, MeasureTestActivity.SPEC_FRAME_WRAP) })
        rootView.addView(generateButton("[measure-spec]FrameDP100") { MeasureTestActivity.startActivity(this, MeasureTestActivity.SPEC_FRAME_DP100) })
        rootView.addView(generateButton("") { })
        rootView.addView(generateButton("[measure-spec]LinearMatch") { MeasureTestActivity.startActivity(this, MeasureTestActivity.SPEC_LINEAR_MATCH) })
        rootView.addView(generateButton("[measure-spec]LinearWrap") { MeasureTestActivity.startActivity(this, MeasureTestActivity.SPEC_LINEAR_WRAP) })
        rootView.addView(generateButton("[measure-spec]LinearDP100") { MeasureTestActivity.startActivity(this, MeasureTestActivity.SPEC_LINEAR_DP100) })
        rootView.addView(generateButton("") { })
        rootView.addView(generateButton("[measure-spec]ScrollMatch") { MeasureTestActivity.startActivity(this, MeasureTestActivity.SPEC_SCROLL_MATCH) })
        rootView.addView(generateButton("[measure-spec]ScrollWrap") { MeasureTestActivity.startActivity(this, MeasureTestActivity.SPEC_SCROLL_WRAP) })
        rootView.addView(generateButton("[measure-spec]ScrollDP100") { MeasureTestActivity.startActivity(this, MeasureTestActivity.SPEC_SCROLL_DP100) })
        rootView.addView(generateButton("") { })
        rootView.addView(generateButton("[layout]斜着的ViewGroup") { startActivity(Intent(this, LayoutTestActivity::class.java)) })
        rootView.addView(generateButton("") { })
    }

    private fun generateButton(text: String, click: View.OnClickListener): Button {
        val button = AppCompatButton(this)
        if (text.isEmpty()) button.visibility = View.INVISIBLE
        button.text = text
        button.setOnClickListener(click)
        return button
    }

}