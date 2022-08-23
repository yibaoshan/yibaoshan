package com.android.blog.android_view.demo.v1

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.blog.R

/**
 * Created by yibs.space on 2022/4/21
 */
class FPSTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when (intent.action) {
            "view" -> setContentView(R.layout.activity_fps_test_view)
            "surface" -> setContentView(R.layout.activity_fps_test_surface)
            "texture" -> setContentView(R.layout.activity_fps_test_texture)
            else -> setContentView(R.layout.activity_fps_test)
        }
    }

    fun onViewClick(view: View) {
        val intent = Intent(this, FPSTestActivity::class.java)
        intent.action = "view"
        startActivity(intent)
    }

    fun onSurfaceClick(view: View) {
        val intent = Intent(this, FPSTestActivity::class.java)
        intent.action = "surface"
        startActivity(intent)
    }

    fun onTextureClick(view: View) {
        val intent = Intent(this, FPSTestActivity::class.java)
        intent.action = "texture"
        startActivity(intent)
    }

}