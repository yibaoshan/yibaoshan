package cn.ybs.card.slide.anr

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import cn.ybs.card.slide.databinding.ActivityAnrBinding
import cn.ybs.core.base.BaseViewBindingActivity


class ANRActivity : BaseViewBindingActivity<ActivityAnrBinding>() {

    override fun initListenersAfterViewCreated() {
        super.initListenersAfterViewCreated()
        createNotificationChannel()
        requestNotificationPermission()
        viewBinding?.btnStartInput?.setOnClickListener {
//            Thread.sleep(4900)
            Thread.sleep(600000)
            Toast.makeText(this, "click", Toast.LENGTH_SHORT).show()
        }
        viewBinding?.btnStartForeground?.setOnClickListener {
            val intent = Intent(this, ForegroundService::class.java)
            ContextCompat.startForegroundService(this, intent)
        }
        viewBinding?.btnStartBackground?.setOnClickListener {
            val intent = Intent(this, BackgroundService::class.java)
            startService(intent)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("default", "Default",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }
    }


    private fun requestNotificationPermission() {
        val permission = "android.permission.POST_NOTIFICATIONS"
        if (Build.VERSION.SDK_INT >= 33) {
            if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, arrayOf(permission), 100)
            }
        }
    }


}