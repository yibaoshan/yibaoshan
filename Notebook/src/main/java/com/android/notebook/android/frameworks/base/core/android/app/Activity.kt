package com.android.notebook.android.frameworks.base.core.android.app

import com.android.notebook.android.frameworks.base.core.android.content.Intent

object Activity {

    private lateinit var mMainThread: ActivityThread
    private lateinit var mInstrumentation: Instrumentation

    fun attach(aThread: ActivityThread, instr: Instrumentation) {
        mMainThread = aThread
        mInstrumentation = instr
    }

    fun startActivity(intent: Intent) {
        startActivityForResult(intent)
    }

    fun startActivityForResult(intent: Intent) {
        mInstrumentation.execStartActivity(mMainThread.getApplicationThread(), intent)
    }

}