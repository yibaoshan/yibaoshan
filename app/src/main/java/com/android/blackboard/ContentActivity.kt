package com.android.blackboard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import org.markdown4j.Markdown4jProcessor

class ContentActivity : AppCompatActivity() {

    companion object {

        @JvmStatic
        fun startActivity(activity: Activity, url: String? = null) {
            val intent = Intent(activity, ContentActivity::class.java)
            intent.putExtra("url", url)
            activity.startActivity(intent)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val contentUrl = intent.getStringExtra("url")
        if (contentUrl?.contains(".md") == true) {
            val open = assets.open(contentUrl);
            var contentMd = Markdown4jProcessor().process(open)
            findViewById<WebView>(R.id.webView).loadDataWithBaseURL(null, contentMd, "text/html", "utf-8", null)
            return
        }
        var baseUrl = "file:///android_asset/"
        baseUrl += if (contentUrl?.isNotEmpty() == true) contentUrl
        else "default.html"
        findViewById<WebView>(R.id.webView).loadUrl(baseUrl)
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