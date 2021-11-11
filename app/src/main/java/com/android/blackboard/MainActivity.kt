package com.android.blackboard

import android.content.Intent
import android.os.*
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.blackboard.framework.FrameworkActivity
import com.android.blackboard.interview.InterviewMainActivity
import com.android.blackboard.interview.LookImageMainActivity

@ExperimentalStdlibApi
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onNotebookClick(view: View) {
        startActivity(Intent(this, InterviewMainActivity::class.java))
    }

    fun onLookImageClick(view: View) {
        startActivity(Intent(this, LookImageMainActivity::class.java))
    }

    fun onFrameworkClick(view: View) {
        startActivity(Intent(this, FrameworkActivity::class.java))
    }

}