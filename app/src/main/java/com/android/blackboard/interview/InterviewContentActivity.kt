package com.android.blackboard.interview

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.android.blackboard.R
import org.markdown4j.Markdown4jProcessor

/**
 *  author :Bob.
 *  date : 2021/1/13
 *  desc :
 */
class InterviewContentActivity : AppCompatActivity() {

    companion object {

        @JvmStatic
        fun startContentActivity(activity: Activity, url: String? = null) {
            val intent = Intent(activity, InterviewContentActivity::class.java)
            intent.putExtra("url", url)
            activity.startActivity(intent)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content)
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

}