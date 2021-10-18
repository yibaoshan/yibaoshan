package com.android.blackboard

import android.os.*
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.lang.ref.WeakReference

/**
 *  author :Bob.
 *  date : 2021/1/26
 *  desc :
 */
@ExperimentalStdlibApi
class InterviewMainActivity : AppCompatActivity() {

    lateinit var weakReference: WeakReference<ViewGroup>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val scrollView = ScrollView(this)
        val contentView = LinearLayout(this)
        contentView.orientation = LinearLayout.VERTICAL
        contentView.gravity = Gravity.CENTER
        contentView.setPadding(20, 20, 20, 20)

        scrollView.addView(contentView)

        weakReference = WeakReference(contentView)
        setContentView(scrollView)
        init()
    }

    private fun init() {
        try {
            val list = assets.list("")
            for (i in list!!.indices) {
                val fileName = list[i]
                if (!fileName.lowercase().contains(".md")) continue
                addButtonView(fileName, View.OnClickListener { InterviewContentActivity.startContentActivity(this, fileName) })
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun addButtonView(text: String, onClickListener: View.OnClickListener): Button {
        val button = Button(this)
        button.text = text
        button.setOnClickListener(onClickListener)
        weakReference.get()?.addView(button)
        return button
    }
}