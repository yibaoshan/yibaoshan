package com.android.blog.android.graphics.demo.v5

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.blog.R

/**
 * Created on 2022/11/24
 */
class TouchTestActivity : AppCompatActivity() {

    private val TAG = "TouchTestActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(TAG, "onCreate: "+Thread.currentThread().toString() )
        setContentView(R.layout.activity_input_touch_test)
    }
}