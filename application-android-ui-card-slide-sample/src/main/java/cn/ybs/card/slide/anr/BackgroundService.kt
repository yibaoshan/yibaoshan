package cn.ybs.card.slide.anr

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class BackgroundService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        // 后台服务超 20s
        var cnt = 21

        while (cnt-- > 0) {
            Thread.sleep(1000)
            Log.d("BackgroundService", "time: $cnt")
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
}