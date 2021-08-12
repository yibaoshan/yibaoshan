package com.android.notebook.android.frameworks.base.core.android.app

class ActivityThread {

    fun getApplicationThread(): ApplicationThread {
        return ApplicationThread()
    }

    /**
     * Server端 继承了IApplicationThread.Stub的aidl
     * 启动Activity时会把stub传给AMS/ATM
     * 注意：该aidl接口加了oneway关键字，也就是说所有方法都是异步执行，不需要等待方法返回
     * */
    class ApplicationThread {

    }

}