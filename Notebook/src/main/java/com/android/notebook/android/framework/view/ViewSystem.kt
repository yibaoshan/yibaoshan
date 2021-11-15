package com.android.notebook.android.framework.view

class ViewSystem {

    /**
     * Android View系统
     * 它们的职责以及它们能做到什么？
     * WMS：
     * */

    /**
     * Display显示系统
     *
     * 双缓存设计：解决显示器获取buffer时数据来自不同帧导致画面割裂的问题
     * GPU和display各自拥有自己的buffer便可解决
     *
     * VSync
     * 垂直同步信号发出时，两个buffer交换
     *
     * */

    /**
     * Vsync信号->SurfaceFlinger->WMS->Activity->View
     * SurfaceFlinger是干嘛的
     *
     * */

    class SurfaceFlinger {

        /**
         * 介绍：
         * 起源：由init进程解析init.rc文件拉起，单独运行的进程
         * 职责：
         * */

    }

}