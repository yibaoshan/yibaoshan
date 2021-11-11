package com.android.blackboard.interview

import android.os.*
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import com.android.blackboard.R
import java.io.IOException
import java.lang.ref.WeakReference

@ExperimentalStdlibApi
class LookImageMainActivity : AppCompatActivity() {

    private lateinit var weakReference: WeakReference<ViewGroup>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val scrollView = ScrollView(this)
        val contentView = LinearLayout(this)
        contentView.orientation = LinearLayout.VERTICAL
        contentView.gravity = Gravity.CENTER
        contentView.setPadding(20, 20, 20, 20)

        scrollView.addView(contentView)

        weakReference = WeakReference(contentView)
        setContentView(scrollView)
        addView("Android启动过程", R.mipmap.ic_android_start_process)
    }

    private fun addView(title: String, resId: Int) {
        addButtonView(title) { LookImageContentActivity.startContentActivity(this, title, resId) }
    }

    private fun addButtonView(text: String, onClickListener: View.OnClickListener): Button {
        val button = Button(this)
        button.text = text
        button.setOnClickListener(onClickListener)
        weakReference.get()?.addView(button)
        return button
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}