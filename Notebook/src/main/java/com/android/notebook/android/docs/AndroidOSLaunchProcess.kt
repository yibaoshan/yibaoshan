package com.android.notebook.android.docs

class AndroidOSLaunchProcess {

    /**
     * 1. 主板上电，BootLoader拉起Linux内核代码
     * 2. Kernel内核代码初始化内存、磁盘等之后退化成0号进程idle，然后启动1号进程init进程和2号进程kthreadd进程
     *  @see IdleProcess
     *  @see InitProcess
     *  @see KthreaddProcess
     * 3. init进程读取/解析init.rc文件，启动ServiceManager进程、Zygote进程
     * 4. Zygote进程启动SystemServer进程
     *
     * */

    private class IdleProcess {

    }

    private class InitProcess {

    }

    private class KthreaddProcess {

    }

}