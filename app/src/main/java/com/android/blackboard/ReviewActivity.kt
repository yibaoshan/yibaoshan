package com.android.blackboard

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.android.blackboard.ui.review.ContentActivity
import java.lang.ref.WeakReference
import java.util.*

@ExperimentalStdlibApi
@SuppressLint("UseCompatLoadingForDrawables")
class ReviewActivity : AppCompatActivity() {

    companion object {

        @JvmStatic
        fun startActivity(activity: Activity, path: String? = null) {
            val intent = Intent(activity, ReviewActivity::class.java)
            intent.putExtra("path", path)
            activity.startActivity(intent)
        }

    }

    private lateinit var mPath: String

    private lateinit var mContentViewReference: WeakReference<ViewGroup>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPath = intent.getStringExtra("path") ?: ""
        initView()
        initData()
    }

    private fun initView() {

        if (mPath.isNotEmpty()) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            title = mPath
        }

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

        mContentViewReference = WeakReference(contentView)
    }

    private fun initData() {
        if (mPath.isEmpty()) {
            val list = arrayListOf<String>()
            list.add("JVM")
            list.add("Java")
            list.add("Linux")
            list.add("Android")
            list.add("设计模式")
            for (path in list) mContentViewReference.get()?.addView(generateButtonView(path))
            return
        }
        parseAssets()
    }

    private fun parseAssets() {

        fun filter(path: String): Boolean {
            val value = path.toUpperCase(Locale.ROOT)
            if (value == "IMAGES") return false
            if (value == "WEBKIT") return false
            return true
        }

        val folders = arrayListOf<String>()
        val files = arrayListOf<String>()

        for (path in resources.assets.list(mPath) ?: return) {
            if (!filter(path)) continue
            if (path.contains(".")) files.add(path)
            else folders.add(path)
        }
        folders.addAll(files)
        for (path in folders) mContentViewReference.get()?.addView(generateButtonView(path))
    }

    private fun generateButtonView(path: String): Button {

        fun createItem(name: String): Item {
            if (name.contains(".")) return Item(name.substring(0, name.indexOf(".")), name);
            return return Item(name, name)
        }

        val button = AppCompatButton(this)
        val item = createItem(path)
        button.text = item.path
        if (item.name == item.path) {// it's an folder
            button.setCompoundDrawablesWithIntrinsicBounds(resources.getDrawable(R.mipmap.ic_folder), null, null, null)
            button.setOnClickListener { startActivity(this, item.absolutePath(mPath)) }
        } else { // it's an file
            button.setCompoundDrawablesWithIntrinsicBounds(resources.getDrawable(R.mipmap.ic_file_markdown), null, null, null)
            button.setOnClickListener { ContentActivity.startActivity(this, item.absolutePath(mPath)) }
        }
        return button
    }

    data class Item(val name: String, val path: String) {

        fun absolutePath(parent: String): String {
            if (parent.isEmpty()) return path;
            return "${parent}/" + path
        }

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