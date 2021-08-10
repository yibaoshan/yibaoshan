package com.android.notebook.android.app

class Activity {

    /**
     * 此篇会介绍Activity生命周期和常见启动模式
     * 以及最重要的，Activity的启动过程
     *
     * Activity生命周期时
     * @see ActivityLifeCycle
     *
     * Activity启动模式
     * @see ActivityLaunchMode
     *
     * @TODO
     * 1. AMS通知其他进程启动时，已经建立连接了吗？不然，如何通信呢？
     * 猜想：因为是从zygote进程孵化出去的，所以已经有连接了？
     * 2. ActivityStarter启动时，如何确定Activity所在的Task和如何压入ActivityStack的？
     * 3. Activity启动另一个Activity时，其自身和another的生命周期如何变化？
     * 4. Activity的构造函数和onCreate()哪个先执行？为什么？
     * 5. 如何目标Activity所在的进程已经启动了，如何处理？
     * 6. ActivityThread、ActivityRecord、Instrument、ActivityStarter、ActivityStartController分别是什么？在整个启动过程中担任什么样的角色？
     * 7. ActivityTaskManager和ActivityTaskManagerService的区别是什么？他们分别运行在哪个进程？
     *
     * */

    private class ActivityLifeCycle {

        /**
         * 介绍Activity常见生命周期方法和各个方法的意义
         * 以及，在什么样的场景去使用它
         *
         * Activity涉及到状态切换的生命周期
         *
         * 一、onCreate()
         * 此方法在整个生命周期仅会调用一次
         *
         * 二、onStart()
         * Activity准备进入前台时调用
         *
         * 三、onResume()
         * 当Activity准备好与用户交互时调用
         *
         * 四、onPause()
         * Activity失去焦点时调用，此时Activity依旧处于可见状态
         * 此时，可以清理一些不需要的资源，如轮播图，退出动画等
         *
         * 五、onStop()
         * Activity进入后台时调用，若是跳转到其他页面，那么其他页面的onResume执行后，会回调该方法
         *
         * 六、onDestroy()
         * Activity被销毁前调用，可以在此方法中释放资源，如：
         * 销毁广播、中断网络请求，退出动画、释放相机、释放大内存
         *
         * 七、onRestart()
         *
         * */

        /**
         * Activity其他生命周期方法
         *
         * 一、onActivityResult()
         * 该方法一般用于期待来自由我启动的其他页面的返回值，比如：权限申请
         *
         * 二、onSaveInstanceState()/onRestoreInstanceState()
         * 当Activity被意外杀死：LMK低内存杀死、系统配置更改(比如旋转屏幕)
         * 或者应用退出到后台时会调用
         * 正常返回键和主动调用finish()和用户多任务删除不会调用
         * 注意：在onSaveInstanceState中不能做重量级存储
         *
         * 三、onPostCreate()/onPostResume()/onContentChange()
         * onPostCreate()会在onRestoreInstanceState之后，onResume之前被调用
         * onPostResume()在onResume后被调用
         * onContentChange()是在setContentView之后被调用
         *
         * 四、onNewIntent()
         * 在使用singleInstance、singleTop、singleTask时，需要保证Activity的单例状态
         * Activity会被重复启动，这时候，onNewIntent方法会被调用
         *
         * */

    }

    private class ActivityLaunchMode {

        /**
         * Activity启动模式
         * 在开发Android过程中，每个Activity都可以单独指定启动模式
         * 在XML中设置android:launchMode的值
         * 在代码中，调用Intent.setFlags(int flags)
         * (代码设置的优先级大于xml中指定的值)
         *
         * XML指定：
         *
         * 一、standard
         *
         * 二、singleTop
         *
         * 三、singleTask
         * 在相同taskAffinity只有一个实例
         *
         * 四、singleInstance
         *
         * Intent：setFlags：
         *
         * 一、Intent.FLAG_ACTIVITY_NEW_TASK(比较关键)
         *
         * 二、Intent.FLAG_ACTIVITY_CLEAR_TASK
         * 只能与#FLAG_ACTIVITY_NEW_TASK配合使用，启动时清空当前栈，新启动的Activity变成这个空栈的根Activity并将这个栈移动到最前面。
         * Intent.FLAG_ACTIVITY_CLEAR_TASK的优先级最高，基本可以无视所有的配置，包括启动模式及Intent Flag，哪怕是singleInstance也会被finish并重建。
         *
         * 三、Intent.FLAG_ACTIVITY_CLEAR_TOP
         * 如果栈中存在相同的Activity，则把这个Activity上面的Activity都干掉。
         * 如果同时设置了FLAG_ACTIVITY_SINGLE_TOP ，则回调onNewIntent方法；否则先finish后create这个实例
         *
         * 四、Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
         * 创建并启动Activity并提到栈顶，如果栈中存在此Activity，则不创建Activity实例并把栈中的实例移动到栈顶
         *
         * 五、Intent.FLAG_ACTIVITY_NO_ANIMATION
         * 这个标志将阻止系统进入下一个Activity时应用Activity迁移动画
         *
         * 六、Intent.FLAG_ACTIVITY_NO_HISTORY
         * Activity正常启动，只是不被压入栈中
         *
         * 七、Intent.FLAG_ACTIVITY_TASK_ON_HOME
         * 需要和FLAG_ACTIVITY_NEW_TASK配合使用。添加这个Flag后，启动的Activity会把系统桌面Activity放在LauncherAct上面(单独的Task)
         * 然后启动目标Activity(新Task)在最上面，如果按返回按键，会返回到桌面Activity
         *
         * 八、Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENT
         * 新的Activity不会在最近启动的Activity的列表中保存。
         *
         * */

    }

    private class ActivityLaunchProcess {

        /**
         * 谈到Activity的启动过程，就绕不过AMS(ActivityManagerService)(Android 10及以上版本拆分出ActivityTaskManager管理Activity)
         * 谈到AMS，就绕不过进程通信Binder
         *
         * 好了，我们先来大概介绍一下，当你调用startActivity之后，会发生些什么
         *
         * Activity.startActivity()->Instrument.execStartActivity()->AMS/ATM.startActivity(aidl)
         * ->ActivityTaskManagerService.startActivityAsUser()->ActivityStarter.execute()
         *
         * */

    }

}