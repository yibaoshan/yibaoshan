package com.android.blackboard.ui.review

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import br.tiagohm.markdownview.MarkdownView
import br.tiagohm.markdownview.css.styles.Github
import com.android.blackboard.R


class ContentActivity : AppCompatActivity() {

    companion object {

        @JvmStatic
        fun startActivity(activity: Activity, url: String? = null) {
            val intent = Intent(activity, ContentActivity::class.java)
            intent.putExtra("url", url)
            activity.startActivity(intent)
        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val contentUrl = intent.getStringExtra("url")
        title = contentUrl

        val mMarkdownView = findViewById<MarkdownView>(R.id.markdown_view)
        mMarkdownView.addStyleSheet(Github())
        mMarkdownView.loadMarkdownFromAsset(contentUrl)
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