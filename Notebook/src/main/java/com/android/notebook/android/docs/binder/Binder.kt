package com.android.notebook.android.docs.binder

class Binder {

    /**
     * 前言
     * 我们知道，如果一个APP想要享受系统服务，就必须调用系统服务对外开放的接口
     * 那么APP进程如何知道所需要的系统服务在哪个进程呢？这时候，ServiceManager服务的作用就显现出来了。
     * 如何拿到ServiceManager呢？答案在IServiceManager.cpp中的defaultServiceManager()函数中
     * defaultServiceManager()会调用getContextObject()(相当于调用new BpBinder(0))得到SM的代理对象
     *
     * 我们来整理一下这个过程：
     * 1. APP进程调用defaultServiceManager()拿到ServiceManager的BpBinder
     *  具体实现方法：c++层通过IServiceManager.cpp->defaultServiceManager()获取
     *  Java层调用ServiceManager.java->getIServiceManager()获取
     *
     * 2. 通过ServiceManager查询得到目标系统服务的BpBinder，这样就可以发消息给系统服务所在的进程
     *
     * 3. 通过系统服务的BpBinder发送自己的BpBinder给系统服务进程，这样系统服务进程就可以回话了，至此，通信建立完成
     * */

    /**
     * Android整个跨进程通信都是基于binder，不光是底层使用，应用层也会使用
     * 所以必须有c++和Java两个层面的实现
     * 从实现的角度来说，Binder核心被实现成一个Linux驱动程序，并运行于内核态。这样它才能具有强大的跨进程访问能力。
     *
     * Looper:Epoll + eventfd
     * Input子系统:Socket + Epoll + Binder机制
     * Surface GUI:Binder + 匿名共享内存
     * 虚拟机的fork:Socket
     *
     * TODO ：
     * 1. 驱动层、C++层分别提供哪些接口供Java层调用？或者说，驱动层提供哪些接口给C++层，Java层包装了哪些调用？
     * 2. APP所在进程想要发起一个RPC请求，要经历哪几个步骤？如何得到对方进程的BpBinder
     * 3. Binder是什么？是驱动对应的某一块内存区域吗？
     * 4. Java层的aidl为什么要求实体类和代理类拥有同一套aidl文件？
     *  答：代理类拿到binder对象后不知道调用哪个方法，同一套aidl即可解决这个问题
     *
     * https://my.oschina.net/youranhongcha/blog/149575
     *
     * 首先，我们先从宏观感受下binder是如何设计的，这样可以帮助我们更好的理解binder
     * 1. Server
     * 2. Client
     * 3. ServiceManager：辅助管理
     * 4. Binder驱动：核心组件
     *
     * 接下来我们一起来看看Android是如何实现binder的
     * 1. 驱动层：
     * @see BinderDriverLayer
     * 2. C++层：
     * @see BinderCppLayer
     * 3. Java层：
     * @see BinderJavaLayer
     *
     * */

    private class BinderDriverLayer {
        /**
         * binder驱动层代码目录在
         * @see com.android.notebook.android.kernel.drivers.android
         * */
    }

    private class BinderCppLayer {
        /**
         * C++层代码目录在frameworks.native.libs.binder
         * @see com.android.notebook.android.frameworks.native.libs.binder
         * 注意：在Android11源码中，frameworks.native.include.binder和frameworks.native.libs.include.binder两个文件夹中的
         * IBinder.h、Binder.h等头文件内容是相同的，看哪一份都可以
         *
         * 其中：
         * 1. BpBinder和BBinder都负责传输的动作，前者为代理类，后者为实体类
         * @see com.android.notebook.android.frameworks.native.libs.binder.include.binder.Binder
         * @see com.android.notebook.android.frameworks.native.libs.binder.Binder
         * @see com.android.notebook.android.frameworks.native.libs.binder.include.binder.BpBinder
         * @see com.android.notebook.android.frameworks.native.libs.binder.BpBinder
         * 2. BpInterface和BnInterface负责传输的内容，前者为代理类接口，后者为实体类接口
         * @see com.android.notebook.android.frameworks.native.libs.binder.include.binder.IInterface
         *
         * */
    }

    private class BinderJavaLayer {
        /**
         * Java层代码目录在frameworks.base.core.java.android.os
         * @see com.android.notebook.android.frameworks.base.core.java.android.os
         *
         * 其中：
         * 1. IBinder对应的c层应该是BpBinder吧，binder的实体
         * @see com.android.notebook.android.frameworks.base.core.java.android.os.IBinder
         * 2. IInterface
         * @see com.android.notebook.android.frameworks.base.core.java.android.os.IInterface
         * */
    }

}