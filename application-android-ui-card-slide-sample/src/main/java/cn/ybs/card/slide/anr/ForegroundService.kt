package cn.ybs.card.slide.anr

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import cn.ybs.card.slide.R

class ForegroundService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        // 启动前台服务，20s 内未发送前台通知，触发 ANR
        //Thread.sleep(19_000)
        Thread.sleep(21_000)

        val notification = NotificationCompat.Builder(this, "default")
            .setContentTitle("前台服务")
            .setContentText("测试触发 ANR")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        startForeground(1, notification)

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
