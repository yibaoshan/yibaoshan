package com.android.blackboard

import android.app.Activity
import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.android.blackboard.TestContentProvider.CourseProviders
import java.lang.reflect.Field
import java.lang.reflect.Method


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<TextView>(R.id.text).setOnClickListener {
            val intent = Intent(this@MainActivity, MainActivity2::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            application.startActivity(intent)
            startForegroundService(Intent(this@MainActivity, TestService::class.java))
        }
        val intentFilter = IntentFilter("")
        intentFilter.priority = 1000
        registerReceiver(broadcastReceiver, intentFilter)

        sendOrderedBroadcast(Intent(), "")
        LocalBroadcastManager.getInstance(this).sendBroadcast(Intent())
    }

    override fun onResume() {
        super.onResume()
        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningTaskInfoList = am.getRunningTasks(10)
        for (runningTaskInfo in runningTaskInfoList) {
            Log.e(TAG, "id: " + runningTaskInfo.id)
            Log.e(TAG, "description: " + runningTaskInfo.description)
            Log.e(TAG, "number of activities: " + runningTaskInfo.numActivities)
            Log.e(TAG, "topActivity: " + runningTaskInfo.topActivity)
            Log.e(TAG, "baseActivity: " + runningTaskInfo.baseActivity.toString())
        }
    }

    fun getAllActivitys(): List<Activity>? {
        val list: MutableList<Activity> = ArrayList()
        try {
            val activityThread = Class.forName("android.app.ActivityThread")
            val currentActivityThread: Method = activityThread.getDeclaredMethod("currentActivityThread")
            currentActivityThread.isAccessible = true
            //获取主线程对象
            val activityThreadObject: Any = currentActivityThread.invoke(null)
            val mActivitiesField: Field = activityThread.getDeclaredField("mActivities")
            mActivitiesField.isAccessible = true
            val mActivities = mActivitiesField.get(activityThreadObject) as Map<Any, Any>
            for ((_, value) in mActivities) {
                val activityClientRecordClass: Class<*> = value.javaClass
                val activityField: Field = activityClientRecordClass.getDeclaredField("activity")
                activityField.setAccessible(true)
                val o: Any = activityField.get(value)
                list.add(o as Activity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    private val broadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            resultCode = 200
            resultData = "叫爸爸"
        }

    }

    private fun useTestContentProvider() {
        val cursor: Cursor = contentResolver.query(CourseProviders.COURSE_URI, null, null, null, null) ?: return
//        contentResolver.registerContentObserver()
//        contentResolver.notifyChange()
        cursor.close()
    }

}