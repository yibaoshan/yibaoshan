package com.android.aosp.frameworks.base.core.android.app

import com.android.aosp.frameworks.base.core.android.content.Context

class ContextImpl : Context() {

    /*用于缓存获取的服务*/
    //val mServiceCache: Array<Any> = SystemServiceRegistry.createServiceCache()

    override fun getPackageName(): String {
        return ""
    }

}