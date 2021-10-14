package com.android.aosp.frameworks.base.services.core.com.android.server.wm

import com.android.aosp.frameworks.base.core.android.app.ActivityThread
import com.android.aosp.frameworks.base.core.android.content.Intent

object ActivityTaskManagerService {

    /**
     * Android10及以后版本拆分出来的service，单独处理Activity
     * */

    fun initialize() {
        ActivityStartController.init(this)
    }

    fun startActivity(caller: ActivityThread.ApplicationThread, intent: Intent) {
        startActivityAsUser(caller, intent)
    }

    private fun startActivityAsUser(caller: ActivityThread.ApplicationThread, intent: Intent) {
        /*这里调用启动Activity最终委托给ActivityStarter处理*/
        ActivityStarter.execute()
    }

}