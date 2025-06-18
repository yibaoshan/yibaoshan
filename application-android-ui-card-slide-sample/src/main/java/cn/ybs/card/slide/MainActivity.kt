package cn.ybs.card.slide

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import cn.ybs.card.slide.anr.ANRActivity
import cn.ybs.card.slide.recyclerview.SwipeCardActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.btn_anr_test).setOnClickListener {
            startActivity(Intent(this, ANRActivity::class.java))
        }
        findViewById<Button>(R.id.btn_preload_test).setOnClickListener {
            startActivity(Intent(this, GlidePreloadTestActivity::class.java))
        }
        findViewById<Button>(R.id.btn_swipe_card).setOnClickListener { startActivity(Intent(this, SwipeCardActivity::class.java)) }
    }

}