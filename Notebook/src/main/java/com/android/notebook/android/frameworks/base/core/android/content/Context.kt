package com.android.notebook.android.frameworks.base.core.android.content

import com.android.notebook.android.frameworks.base.core.android.app.ActivityThread

abstract class Context {

    private lateinit var mMainThread: ActivityThread
    private lateinit var mPackageInfo: LoadedApk
    private lateinit var mClassLoader: ClassLoader

    abstract fun getPackageName(): String

    private class LoadedApk {}

}