package com.android.aosp.frameworks.base.services.core.com.android.server.wm

import com.android.aosp.frameworks.base.core.android.content.Intent

/**
 * 委托Activity启动的控制器
 * 拥有ActivityTaskManagerService的引用
 * ATMS在初始化时会将自身传递过来
 * */
object ActivityStartController {

    fun init(service: ActivityTaskManagerService) {
        ActivityStarter.init(service)
    }

    fun obtainStarter(intent: Intent, reason: String): ActivityStarter {
        return ActivityStarter
    }

}