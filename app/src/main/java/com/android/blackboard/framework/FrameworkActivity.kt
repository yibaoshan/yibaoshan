package com.android.blackboard.framework

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.blackboard.R

/**
 * Created by yibaoshan@foxmail.com on 2021/10/27
 */
class FrameworkActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_framework)
    }

    fun onViewClick(view: View) {
        startActivity(Intent(this, FrameworkViewActivity::class.java))
    }

}