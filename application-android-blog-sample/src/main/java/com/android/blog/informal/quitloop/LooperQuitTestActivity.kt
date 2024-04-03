package com.android.blog.informal.quitloop

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.android.blog.R

/**
 * Created on 2022/11/24
 */
class LooperQuitTestActivity : AppCompatActivity() {

    private val TAG = "LooperQuitTestActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(TAG, "onCreate: ")
        setContentView(R.layout.activity_looper_quit_loop)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun onQuitLoopClick(view: View){
        Quit.setQuitAllowed(Handler().looper.queue)
        Handler().looper.quit()
    }

    override fun onStop() {
        super.onStop()
        Log.e(TAG, "onStop: " )
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "onDestroy: " )
    }
}