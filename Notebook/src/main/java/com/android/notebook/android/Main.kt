package com.android.notebook.android

class Main {

    /**
     * 借用邓凡平前辈的话：
     * Android分为几个层次，首先是Kernel层，它用得是Linux Kernel。
     * 然后是用户空间中的Framework层。最后是Application层。
     * Android的核心内容大部分集中在Framework层，我个人又把它划分成两个层次：
     * 1. 以Native语言编写的模块，包括负责显示的SurfaceFlinger、负责音频I/O的AudioFlinger、负责媒体播放的MediaPlayerService、负责Wi-Fi的wpa_supplicant、负责蓝牙的Bluez。
     * 我称之为Native Framework
     * 2. 以Java编写的模块，包括和App紧密交互的ActivityManagerService、WindowManagerService等。
     * 我称之为Java Framework
     * Android提供的开发工具有SDK和NDK之分。
     * 其实两个都是传统意义上的SDK，只不过SDK提供的是Java API，而NDK提供Native语言的API罢了。
     * NDK出现的目的是因为Android App是一个Java应用程序，它有时候需要调用Native库来做一些事情，而Native库就用Native语言的API来实现。
     *
     * 参考书籍：
     * - 深入理解Android：卷1 邓凡平
     * 以Native层Framework模块为分析对象
     * 知识点包括init、binder、zygote、jni、Message和Handler、audio系统、surface系统、vold、rild和mediascanner
     *
     * - 深入理解Android：卷2 邓凡平
     * 以Java层Framework模块为分析对象
     * PackageManagerService、ActivityManagerService、PowerManagerService、ContentService、ContentProvider等
     *
     * - 深入理解Android：卷3 张大伟
     * 以SystemUI为切入点，分析了WindowsManagerService、输入系统、控件系统等
     *
     * - Android开发艺术探索 任玉刚
     * Activity、IPC、View都有涉及，个人感觉章节较乱，不适合系统学习
     *
     * - Android进阶之光 刘望舒
     * 讲了5.0/7.0新特性、View事件分发、Java的多线程与阻塞队列、网络和Retrofit框架、部分涉及模式、元编程里面的依赖注入等
     * 个人感觉比较大杂烩，Android、Java、元编程、涉及模式、网络都有所涉及，不适合进阶
     * 作者著有Android进阶三部曲，还有两本Android进阶解密和Android进阶指北没看过，从目录上来说：
     * Android进阶解密偏framework，Android进阶指北偏Linux，质量和深度应该比进阶之光高一些，有机会可以看看
     *
     * Android系统可以简单理解为Linux系统，但在Linux基础上删除/修改/增加了部分代码
     * 根据维基百科的描述，Android以bionic取代Glibc、以Skia取代Cairo、再以OpenCORE取代FFmpeg等等
     * (维基百科-Android是什么:https://zh.wikipedia.org/wiki/Android)
     *
     * Android系统之博大精深，包括Linux内核、Native、虚拟机、Framework
     * 通过系统调用连通内核与用户空间
     * 通过JNI打通用户空间的Java层和Native层
     * 通过Binder、Socket、Handler等打通跨进程、跨线程的信息交换。
     *
     * 由于Android采用Linux内核开发，所以：
     * 1. 每个APP可以简单理解为一个进程
     * 2. 该进程由zygote进程fork而出
     * 3. zygote启动时会加载art虚拟机，并且初始化公共资源，比如framework类库
     * 4. APP启动时，由AMS发起进程间通信，通知zygote进程fork出新进程
     * 关于Android系统启动过程的详细信息，请查看：
     * @see com.android.notebook.android.docs.AndroidOSLaunchProcess
     *
     * 学习Android开发，首先需要了解的就是Google提供的framework库
     *
     * 其次，还需要掌握AndroidX提供的类库
     * 开发过程中所使用的到的Fragment、RecyclerView等都包含在内
     * 学习完framework和AndroidX两部分知识点后，您还可以尝试了解Android的运行时环境ART虚拟机
     * ART虚拟机部分被分到其他模块中(VirtualMachine)，如果您有兴趣，可以查看：
     * @see com.android.notebook.virtualMachine.Main
     *
     * 最后，工作中使用到的第三方库，也是需要掌握的部分
     *
     * 一、Framework
     * 1. 组件相关: Activity、Service、BroadcastReceiver、ContentProvider等
     *  1.1 Activity(包含了生命周期、启动模式等常见知识点)
     *  @see com.android.notebook.android.framework.component.Activity
     *  1.2 Service(Service启动方式、IntentService等)
     *  @see com.android.notebook.android.framework.component.Service
     *  1.3 BroadcastReceiver(有序、无序、粘性、本地广播)
     *  @see com.android.notebook.android.framework.component.BroadcastReceiver
     *  1.4 ContentProvider
     *  @see com.android.notebook.android.framework.component.ContentProvider
     *
     * 2. 基本通信方式：Binder、Handler、Socket
     *  2.1 Binder
     *  @see com.android.notebook.android.framework.communication.Binder
     *  2.2 Handler
     *  @see com.android.notebook.android.framework.communication.Handler
     *  2.3 Socket
     *  @see com.android.notebook.android.framework.communication.Socket
     *
     * 3. 系统服务：AMS、PMS、WMS、ServiceManager等 TODO
     *  3.1 ActivityManagerService 各个组件是如何进行管理与调度
     *  @see com.android.notebook.android.framework.service.ActivityManagerService
     *  3.2 InputManagerService
     *  @see com.android.notebook.android.framework.service.InputManagerService
     *  3.3 PackageManagerService Android安装策略
     *  @see com.android.notebook.android.framework.service.PackageManagerService
     *  3.4 WindowManagerService 窗口管理机制
     *  @see com.android.notebook.android.framework.service.WindowManagerService
     *
     * 4. Android View机制
     *  4.1 Android 事件分发机制的设计与实现
     *  @see com.android.notebook.android.framework.view.EventDispatch
     *  4.2 Android 事件拦截机制的设计与实现
     *  @see com.android.notebook.android.framework.view.EventIntercept
     *  4.3 Android View视图刷新&渲染 TODO
     *  @see com.android.notebook.android.framework.view.ViewSystem
     *  4.4 Android View机制设计与实现：布局流程
     *  @see com.android.notebook.android.framework.view.ViewLayout
     *  4.5 Android View机制设计与实现：测量流程
     *  @see com.android.notebook.android.framework.view.ViewMeasure
     *  4.6 Android LayoutInflater机制的设计与实现
     *  @see com.android.notebook.android.framework.view.LayoutInflater
     *
     * 5. 关键系统类
     *  5.1 Context
     *  @see com.android.notebook.android.framework.other.Context
     *
     * 二、Android X JetPack
     * @see com.android.notebook.android.jetpack.Fragment
     * 1. Fragment
     * 2. RecyclerView
     * 3. ViewPager
     * 4. Lifecycle
     * 5. FragmentActivity
     * 6. DataBinding
     *
     * 三、Third SDK
     * 1. Glide
     * 2. Retrofit
     * 3. ButterKnife
     * 4. Tinker
     *
     * 四、元编程与开发框架
     *
     * 五、其他
     *
     * 如何您想了解Android各个系统版本间差异，请查看：
     * @see com.android.notebook.android.docs.AndroidVersion
     *
     * Android Root权限相关
     * @see com.android.notebook.android.docs.AndroidRoot
     *
     * Android相关算法、签名等，请查看：
     * @see com.android.notebook.android.docs.AndroidEncrypt
     *
     * Android ADB常见命令：
     * @see com.android.notebook.android.docs.AndroidAdb
     *
     * */

    /**
     * Questions：
     * - 为什么adb命令可以执行发广播、启动Activity等操作
     *  答：个人猜想未验证：开机启动adbd进程，adbd通过binder(serviceManager)进程发消息给system_server进程
     * - c++层如何调用kernel层，Java层如何调用c++层
     *  答：sysCall、JNI
     * */

}