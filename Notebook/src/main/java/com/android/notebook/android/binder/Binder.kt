package com.android.notebook.android.binder

class Binder {

    /**
     * Android整个跨进程通信都是基于binder，不光是底层使用，应用层也会使用
     * 所以必须有c++和Java两个层面的实现
     *
     * Looper:Epoll + eventfd
     * Input子系统:Socket + Epoll + Binder机制
     * Surface GUI:Binder + 匿名共享内存
     * 虚拟机的fork:Socket
     *
     * https://my.oschina.net/youranhongcha/blog/149575
     *
     * 首先，我们先从宏观感受下binder是如何设计的，这样可以帮助我们更好的理解binder
     *
     * binder组成：
     * 1. Server
     * 2. Client
     * 3. ServiceManager：辅助管理
     * 4. Binder驱动：核心组件
     *
     * C++层
     * 1.IBinder
     *  -> BpBinder
     *  -> BBinder
     *      -> BnInterface
     *          -> BnXXX
     * 2.BpInterface
     *  -> BpXXX
     *
     * Java层
     * 1.IBinder
     *  -> Binder
     *      -> xxxNative
     *      -> xxxService
     * 2.IInterface
     *  -> Ixxx
     *      -> xxxProxy
     *
     * */

}