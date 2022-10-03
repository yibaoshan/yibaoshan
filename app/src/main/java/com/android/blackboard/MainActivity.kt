package com.android.blackboard

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.*
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import java.lang.ref.WeakReference
import java.util.*

@ExperimentalStdlibApi
@SuppressLint("UseCompatLoadingForDrawables")
class MainActivity : AppCompatActivity() {

    companion object {

        @JvmStatic
        fun startActivity(activity: Activity, path: String? = null) {
            val intent = Intent(activity, MainActivity::class.java)
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
        parseAssets()
    }

    private fun initView() {

        fun createScrollView(): ScrollView {
            return ScrollView(this)
        }

        fun createLinearLayout(): LinearLayout {
            val linearLayout = LinearLayout(this)
            linearLayout.orientation = LinearLayout.VERTICAL
            linearLayout.setPadding(20, 20, 20, 20)
            return linearLayout
        }

        val rootView = createScrollView()
        val contentView = createLinearLayout()

        rootView.addView(contentView)
        setContentView(rootView)

        mContentViewReference = WeakReference(contentView)
    }

    private fun parseAssets() {

        fun generateItem(name: String): Item {
            if (name.contains(".")) return Item(name.substring(0, name.indexOf(".")), name);
            return return Item(name, name);
        }


        fun generateItemButtonView(path: String): Button {
            val button = AppCompatButton(this)
            val item = generateItem(path)
            button.text = item.path
            if (item.name == item.path) {// it's an folder
                button.setCompoundDrawables(resources.getDrawable(R.mipmap.ic_folder), null, null, null)
                button.setOnClickListener { startActivity(this, item.absolutePath(mPath)) }
            } else { // it's an file
                button.setOnClickListener { ContentActivity.startActivity(this, item.absolutePath(mPath)) }
            }
            return button
        }

        fun filter(path: String): Boolean {
            val value = path.toUpperCase(Locale.ROOT);
            if (value == "IMAGES") return false
            if (value == "WEBKIT") return false
            return true
        }

        for (path in resources.assets.list(mPath) ?: return) {
            if (!filter(path)) continue
            mContentViewReference.get()?.addView(generateItemButtonView(path))
        }
    }


    data class Item(val name: String, val path: String) {

        fun absolutePath(parent: String): String {
            if (parent.isEmpty()) return path;
            return "${parent}/" + path
        }

    }

}