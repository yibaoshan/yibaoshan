package com.android.notebook.android.frameworks.base.core.android.app

import com.android.notebook.android.frameworks.base.core.android.content.Intent
import com.android.notebook.android.frameworks.base.services.core.com.android.server.wm.ActivityTaskManagerService

/**
 * 连接aidl启动Activity
 * */
class Instrumentation {

    fun execStartActivity(contextThread: ActivityThread.ApplicationThread, intent: Intent) {
        /*这里会将启动意图intent和用于远程连接的aidl接口传给AMS/ATMS*/
        /*以我手里的Android11源码举例，调用这个方法后会执行以下方法*/
        //ActivityTaskManager.getService().startActivity()
        /*最后，通过远程调用，由系统服务ActivityTaskManager响应*/
        ActivityTaskManagerService.startActivity(contextThread, intent)
    }

}