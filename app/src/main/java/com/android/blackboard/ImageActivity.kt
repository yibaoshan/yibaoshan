package com.android.blackboard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity

class ImageActivity : AppCompatActivity() {

    companion object {

        @JvmStatic
        fun startContentActivity(activity: Activity, title: String, resId: Int) {
            val intent = Intent(activity, ImageActivity::class.java)
            intent.putExtra("title", title)
            intent.putExtra("resId", resId)
            activity.startActivity(intent)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = intent.getStringExtra("title")
        val image = PinchImageView(this)
        image.setImageResource(intent.getIntExtra("resId", 0))
        setContentView(image)
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