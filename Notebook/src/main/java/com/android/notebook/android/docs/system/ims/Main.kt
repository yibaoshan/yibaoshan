package com.android.notebook.android.docs.system.ims

class Main {

    /**
     * InputManagerService简称ims，经常与Android输入系统SystemInput、View事件分发一起出现
     * EventHub：建立了Linux与输入设备之间的通信
     * InputReader：读取事件同时对输入事件进行拦截和转换，系统级别，如电源键、音量减、组合键等不交给分发系统
     * InputDispatcher：分发事件同时也是ANR处理者
     * ims和APP进程采用socket进行异步跨进程通信
     * */

}