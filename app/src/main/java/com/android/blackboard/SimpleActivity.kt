package com.android.blackboard

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.android.blackboard.ui.bitmap.BitmapActivity

class SimpleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fun createScrollView(): ScrollView {
            return ScrollView(this)
        }

        fun createLinearLayout(): LinearLayout {
            val linearLayout = LinearLayout(this)
            linearLayout.gravity = Gravity.CENTER
            linearLayout.orientation = LinearLayout.VERTICAL
            return linearLayout
        }

        val rootView = createScrollView()
        val contentView = createLinearLayout()

        rootView.addView(contentView)
        setContentView(rootView)

        contentView.addView(generateButtonView("bitmap") { startActivity(Intent(this@SimpleActivity, BitmapActivity::class.java)) })

    }

    private fun generateButtonView(text: String, onClickListener: View.OnClickListener): Button {
        val button = AppCompatButton(this)
        button.text = text
        button.setOnClickListener(onClickListener)
        return button
    }

}