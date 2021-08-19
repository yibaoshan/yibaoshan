package com.android.notebook.android.frameworks.base.core.android.app

class Service {

    private lateinit var activityThread: ActivityThread

    fun attach() {

    }

    class IntentService {

        fun onHandleIntent() {
            /*执行任务的地方*/
        }

        fun onStartCommand(intent: Any, startId: Int) {
            onStart(intent, startId)
        }

        fun onStart(intent: Any, startId: Int) {
            /*提交任务给handler*/
        }

        fun handleMessage() {
            /*收到消息看开始执行*/
            onHandleIntent()
        }

        fun stopSelf(startId: Int) {
            /*干掉自己*/
        }
    }

}