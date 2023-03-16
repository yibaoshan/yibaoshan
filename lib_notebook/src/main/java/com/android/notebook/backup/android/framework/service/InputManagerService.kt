package com.android.notebook.backup.android.framework.service

class InputManagerService {

    /**
     * InputManagerService简称ims，经常与Android输入系统SystemInput、View事件分发一起出现
     * EventHub：建立了Linux与输入设备之间的通信
     * 通过Linux内核的INotify与Epoll控制监听设备节点
     * 通过EventHub的getEvent函数读取设备节点的原始输入事件
     *
     * InputManager：
     * 创建InputReader和InputDispatcher
     * 其中InputReader会不断的循环读取EventHub中的原始输入事件
     * 将这些原始输入事件进行加工后交由InputDispatcher
     * InputDispatcher中保存了WMS中的所有的Window信息（WMS会将窗口的信息实时的更新到InputDispatcher中）
     * 这样InputDispatcher就可以将输入事件派发给合适的Window
     * 由于InputDispatcher和InputReader都是耗时的，因此单独创建了InputDispatcherThread和InputReaderThread。
     * InputReader：读取事件同时对输入事件进行拦截和转换，系统级别，如电源键、音量减、组合键等不交给分发系统
     * InputDispatcher：分发事件同时也是ANR处理者
     * ims和APP进程采用socket进行异步跨进程通信
     * */

}