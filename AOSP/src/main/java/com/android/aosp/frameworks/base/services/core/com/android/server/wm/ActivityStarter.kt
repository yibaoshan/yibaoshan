package com.android.aosp.frameworks.base.services.core.com.android.server.wm

import com.android.aosp.frameworks.base.core.android.app.ActivityThread

/**
 * 拆解intent并启动Activity的控制器
 * 同样拥有ActivityTaskManagerService的引用
 * */
object ActivityStarter {

    private lateinit var mInTask: Task
    private lateinit var mTargetTask: Task

    private lateinit var mSourceStack: ActivityStack
    private lateinit var mTargetStack: ActivityStack

    fun init(service: ActivityTaskManagerService) {

    }

    fun setCaller(caller: ActivityThread.ApplicationThread): ActivityStarter {
        return this
    }

    /**
     * 最终解析并启动的方法
     * */
    fun execute() {
        resolveActivity()
        executeRequest()
    }

    private fun resolveActivity() {
        /*解析activityInfo*/
    }

    private fun executeRequest() {
        /*执行请求*/
        mTargetStack.startActivityLocked()
    }

}