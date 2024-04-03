package com.android.blog

import android.app.Application
import android.content.Context
import me.weishu.reflection.Reflection

/**
 * Created on 2022/12/6
 */
class App :Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        Reflection.unseal(base)
    }
}