package com.android.notebook.android

class Main {

    /**
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
     *
     * 学习Android开发，首先需要了解的就是Google提供的framework库
     *
     * 其次，还需要掌握AndroidX提供的类库
     * 平时开发过程中所使用的到的Fragment、RecyclerView等都包含在内
     * 学习完framework和AndroidX两部分知识点后，您还可以尝试了解Android的运行时环境ART虚拟机
     * ART虚拟机部分被分到其他模块中(VirtualMachine)，如果您有兴趣，可以查看：
     * @see com.android.notebook.virtualMachine.Main
     *
     * 最后，平时工作中使用到的第三方库，也是需要掌握的部分
     *
     * 一、Framework
     * 1. 组件相关: Activity、Service、BroadcastReceiver、ContentProvider等
     *  1.1 Activity(包含了生命周期、启动模式等常见知识点)
     *  @see com.android.notebook.android.app.Activity
     *  1.2 Service(Service启动方式、IntentService等)
     *  @see com.android.notebook.android.app.Service
     *  1.3 BroadcastReceiver(有序、无序、粘性、本地广播)
     *  @see com.android.notebook.android.content.BroadcastReceiver
     *  1.4 ContentProvider
     *  @see com.android.notebook.android.content.ContentProvider
     *
     * 2. 基本通信方式：Binder、Handler、Socket
     *  2.1 Binder
     *  @see com.android.notebook.android.binder.Binder
     *
     * 3. 系统服务：AMS、PMS、WMS、ServiceManager
     *
     * 4. 其他
     *  4.1 Context
     *  @see com.android.notebook.android.content.Context
     *
     * 二、Support Package And Android X
     *
     * 三、Third SDK
     *
     * 四、Android开发框架和JetPack
     *
     * 五、其他
     *
     * 如何您想了解Android各个系统版本间差异，请查看：
     * @see AndroidVersion
     *
     * Android Root权限相关
     * @see AndroidRoot
     *
     * Android相关算法、签名等，请查看：
     * @see AndroidEncrypt
     *
     * */

    /**
     * Questions：
     * - 为什么adb命令可以执行发广播、启动Activity等操作
     *  答：个人猜想未验证：开机启动adbd进程，adbd通过binder(serviceManager)进程发消息给system_server进程
     * */

}