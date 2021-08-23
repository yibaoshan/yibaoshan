package com.android.notebook.android.app

class Service {

    /**
     * Service继承自ContextWrapper，由AMS管理和管理，ActivityThread执行
     *
     * 本文围绕Service生命周期、启动方式展开
     * 什么是Service？
     * Android提供运行在后台的服务，木得界面，可以脱离activity哦
     * 下载通知栏显示，播放音视频的通知栏菜单，都是service
     * 什么业务适合服务呢？有重复性，生命周期长，和页面无关等都可以用
     * 比如下载文件就可以单独启动一个服务，这样任何页面需要下载就可以通知service，下载完成可以notify通知栏，单击通知可以调整到下载完成页
     * 这样的业务就很适合用服务
     * 再比如电商里面的商详视频，社区视频，用户如果想要下载，就可以用上面的方法
     * 还有就是提供远程服务供其他APP使用，跨进程IPC通信使用aidl即可
     *
     * 场景：
     * 比如我们的H5资源包就是在单独服务
     * 下载视频在intentService服务
     *
     * Service生命周期和启动方式可以看这里：
     * @see ServiceLifecycle
     *
     * IntentService看这里：
     * @see IntentService
     *
     *
     * 注意点：
     * 1. stopSelf()/stopSelf(startId)/stopSelfResult(startId)和stopService()的区别？
     *  stopSelf(startId)若没有相同startId，则不会执行停止服务操作，stopService会关闭整个服务
     *
     * 2. 远程服务不可使用startService方式启动，只允许bindService。多个APP调用bindService时只会有一个service实例，且service只会响应最后一个client的动作
     *
     * 3. Service允许使用Toast，但不允许弹窗Dialog，否则会收到以下错误：Unable to add window -- token null is not valid; is your activity running?
     *
     * Android O(8.0 API26)及之后版本调用启动前台服务后5s内需要显示Notification，否则会受到Exception:
     * android.app.RemoteServiceException: Context.startForegroundService() did not then call Service.startForeground()
     * 前台服务加权：<uses-permission android:name="android.permission.FOREGROUND_SERVICE"/>
     *
     * */

    private class IntentService {

        /**
         * IntentService是Android为了后台线程任务设计的service
         * 不需要开发者自己维护线程池，当所有任务完成后，Service会自动结束
         *
         * 继承IntentService需要实现两个方法：
         * 1. 构造函数super(name)，name为线程名称
         * 2. onHandleIntent(intent) 相当于提交任务，有多个任务提交时，IntentService会按顺序依次执行，不会并行
         *
         * 执行流程:startService()->onStartCommand()->onStart()->Handler.sendMessage()->onHandleIntent()->stopSelf()
         *
         * @see com.android.notebook.android.frameworks.base.core.android.app.Service.IntentService
         *
         * */

    }

    private class ServiceLifecycle {

        /**
         * 随着启动方式的不同，Service生命周期也不同
         *
         * 调用startService()
         * @see StartService
         *
         * 调用bindService()
         * @see BindService
         *
         * */

        private class StartService {

            /**
             * 1. onCreate()
             *
             * 2. onStartCommand(intent,flag,startId):int
             * flag参数有三种情况：
             *  2.1.1 0
             *      正常创建时的默认值
             *  2.1.2 START_FLAG_REDELIVERY
             *      系统重建并且返回值START_REDELIVER_INTENT为时的带参
             *  2.1.3 START_FLAG_RETRY
             *      重试启动时带参，搞不懂什么场景下会发生？
             *
             * 默认情况下返回值有两种：
             *  2.2.1 START_STICKY
             *      如果Service启动后被清理，系统重启服务时，intent为空
             *  2.2.2 START_STICKY_COMPATIBILITY
             *      START_STICKY兼容版本，而且还不保证onStartCommand会被调用
             *
             * 重写onStarCommand时还有另外两种返回值可选：
             *  2.3.1 START_NOT_STICKY
             *      如果Service被清理，系统将不会尝试重新启动
             *  2.3.2 START_REDELIVER_INTENT
             *      如果Service被清理，系统会尝试重启，并带入把最后一次的intent带过来
             *
             * 3. onDestroy()
             *
             * */

            /**
             * 启动流程：
             * 1. 继承Service
             * 2. xml注册
             * 3. context.startService()
             *
             * 关闭流程：
             * 1. context.stopService()/service.stopSelf()
             *
             * 特点：可单独运行在后台
             * */

        }

        private class BindService {

            /**
             * onCreate()
             *
             * onBind()
             *
             * onUnbind()
             *
             * onDestroy()
             *
             * 此外，还有个onRebind()方法，该方法在onUnbind返回true时执行
             *
             * */

            /**
             * 启动流程：
             * 1. 继承Service
             * 2. 重写onBind返回Binder对象
             * 3. Activity创建ServiceConnection
             * 4. context.bindService(intent,serviceConnection,flag)
             *
             * 关闭流程：
             * 1. context.unbindService(serviceConnection)
             *
             * bindService时flag含义：
             * 1. Context.BIND_AUTO_CREATE
             * 说明：表示收到绑定请求的时候，如果服务尚未创建，则即刻创建，在系统内存不足需要先摧毁优先级组件来释放内存，且只有驻留该服务的进程成为被摧毁对象时，服务才被摧毁
             * 2. Context.BIND_DEBUG_UNBIND
             * 说明：通常用于调试场景中判断绑定的服务是否正确，但容易引起内存泄漏，因此非调试目的的时候不建议使用
             * 3. Context.BIND_NOT_FOREGROUND
             * 说明：表示系统将阻止驻留该服务的进程具有前台优先级，仅在后台运行，该标志位位于Froyo中引入。
             *
             * */

        }

    }

}