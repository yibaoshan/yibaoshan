package com.android.blackboard

import android.content.Context
import android.content.Intent
import android.os.*
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import java.lang.ref.WeakReference

@ExperimentalStdlibApi
class MenuActivity : AppCompatActivity() {

    companion object {

        fun startActivity(context: Context?, type: TypeEnum) {
            val intent = Intent(context, MenuActivity::class.java)
            intent.putExtra("name", type.name);
            context?.startActivity(intent)
        }
    }

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
        init()
    }

    private fun init() {
        val name = intent.getStringExtra("name")
        when {
            TypeEnum.Java.name == name -> {
                initJava()
            }
            TypeEnum.Android.name == name -> {
                initAndroid()
            }
            TypeEnum.Network.name == name -> {
                initNetwork()
            }
            TypeEnum.VM.name == name -> {
                initVM()
            }
        }
    }

    private fun initJava() {
        createButtonViewAddedToRootView("Java-语言基础.md")
        createButtonViewAddedToRootView("Java-并发编程.md")
        createButtonViewAddedToRootView("Java-集合容器.md")
    }

    private fun initAndroid() {

    }

    private fun initNetwork() {

    }

    private fun initVM() {

    }

    private fun createButtonViewAddedToRootView(text: String): Button {
        val button = AppCompatButton(this)
        button.text = text
        button.setOnClickListener { ContentActivity.startActivity(this, text) }
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