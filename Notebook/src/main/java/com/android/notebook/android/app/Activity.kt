package com.android.notebook.android.app

class Activity {

    /**
     * 移动端和PC端的体验不同之处在于，不总是以固定位置开始
     * 比如，我想要写邮件，PC端是打开邮件应用，移动端则直接跳转到写邮件页面，这其中就是Activity的作用
     * 不需要启动整个应用，即可单独使用应用内的某个功能页
     *
     * Activity继承自ContextThemeWrapper
     * 此篇会介绍Activity生命周期和常见启动模式
     * 以及最重要的，Activity的启动过程
     *
     * Activity生命周期时
     * @see ActivityLifeCycle
     *
     * Activity启动模式
     * @see ActivityLaunchMode
     *
     * Activity启动过程
     * @see ActivityLaunchProcess
     *
     * Activity安全攻防
     * @see ActivitySafe
     *
     * Activity常见问题
     * @see AboutActivityQuestions
     *
     * @TODO
     * - Activity对象保存在哪里？生命周期是谁回调的？什么场景下会回调？
     * - Intent什么时候读取的flags
     * - 如果待启动的Activity所在进程没有启动，那么是什么时候启动的？启动后AMS如何知道并继续通知进程启动目标Activity
     * - 如何目标Activity所在的进程已经启动了，如何处理？
     * - 为什么Application启动Activity不加NEW_TASK会发生崩溃？
     * - 做实验：allowBackup允许备份如何攻击？export设为true会怎样，拿到全量类目启动任意Activity
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
         * 场景：
         * 1. 防止重复启动，比如从列表点击进入详情，除了view做防止双击处理外，还可以将商品详情页启动模式指定为singleTop
         *    当需要从商详再打开商详，不加singleTop的flag即默认指定为standard
         *
         * 三、singleTask
         * 在相同taskAffinity只有一个实例，一般主页会用这个模式
         *
         * 四、singleInstance
         * 在此模式下的activity会被放入其他task中，在RunningTaskInfo(ActivityManager.getRunningTasks)可以看到
         * 但如果不指定taskAffinity，会被默认放入当前包名下，这就会导致，命名activity存活在task中，但是最近任务中看不到，也无法通过返回键回退
         * 适合开放给其他APP使用的业务，比如登录页
         *
         * Intent：setFlags：
         *
         * 一、Intent.FLAG_ACTIVITY_NEW_TASK(比较关键)
         * 使用除Application和Service时需要加
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
         * Activity.startActivity()->Instrument.execStartActivity(rpc)
         * ->ActivityTaskManagerService.startActivityAsUser(ActivityStarter.execute())
         *
         * 最后，关于Activity启动过程中涉及到的类，您可以查看：
         * @see com.android.notebook.android.frameworks.base.core.android.app.Activity#startActivity()
         *
         * */

    }

    private class ActivitySafe {

        /**
         * Activity安全性一直被人诟病
         * 比如攻击者可以
         *
         * */

    }

    private class AboutActivityQuestions {

        /**
         * 1. 构造函数和onCreate哪个先执行？以及，私有化构造函数Activity还能启动吗？
         * 答：必然构造函数先执行，私有化构造函数会导致启动失败，newInstance失败
         *
         * */

    }

}