package com.android.notebook.android.docs.content

class BroadcastReceiver {

    /**
     * 广播(Broadcast)是Android系统中用于组件直接传输数据(Intent)的一种通信方式
     *
     * 广播的基本使用：
     * @see BroadcastReceiverUse
     *
     * tips：
     * 1. 为什么有了binder和handler还需要设计广播？
     *  广播基于观察者模式设计，方便程序解耦，发送者和接受者无需事先知道对方是否存在
     *
     * 本文的目标：
     * 什么是broadcastReceiver，有哪些种类，它能够做什么
     * 底层实现是什么？binder吗？
     * 安全性如何？会不会导致anr？
     * 广播发送的流程
     * 广播注册的流程
     *
     * TODO 疑问点：
     * 1. xml声明的广播是谁调用注册的？
     *  答：罗升阳博客下讨论结果好像是Android开机会扫描安装目录下xml，并自动注册？
     * 2. 本地广播既然不涉及到跨进程，那么它还需要AMS来处理吗？它的通信方式用的是什么？
     *
     * 涉及到的设计模式：
     * 1. 观察者模式
     * 2. 代理模式
     * 3. 适配器模式
     * 4. 订阅者模式
     * */

    /**
     * 广播注册
     * 1. 静态注册
     * 2. 动态注册
     * 3. LocalBroadcastManager
     *
     * 广播发送
     * 1. 有序广播
     * 2. 无序广播
     * 3. 粘性广播
     *
     * */

    private class BroadcastReceiverUse {

        /**
         * 静态广播使用流程
         *
         * 接收者：
         * 1. 继承BroadcastReceiver类，重写onReceive方法
         * 2. AndroidManifest.xml中使用<receiver>标签注册，action-name为广播接受标识
         *
         * 发送者：
         * 1. context.sendBroadcast(intent(action))
         *
         * */

        /**
         * 动态广播使用流程
         *
         * 接收者：
         * 1. 继承BroadcastReceiver类，重写onReceive方法
         * 2. context.registerReceiver(receiver,intentFilter(action))
         *
         * 发送者：
         * 1. context.sendBroadcast(intent(action))
         *
         * 注意点：
         * 1. 记得调用unregisterReceiver()解除注册
         *
         * */

        /**
         * 有序广播使用流程
         *
         * 参考静态广播和动态广播的使用流程，唯一有差别的地方在于：
         *
         * 接收者：
         * 1. 静态广播：xml中intent-filter设置priority的值
         * 2. 动态广播：intentFilter设置priority的值
         * 注意：priority值越大优先级越高
         *
         * 发送者：
         * 1. context.sendOrderedBroadcast(intent,receiverPermission)
         * 注意：receiverPermission和Activity的permission一样，在跨进程中，拥有相同权限AMS才会处理，防止其他APP攻击。同一进程无视
         *
         * 拦截广播：abortBroadcast()
         *
         * */

        /**
         * 本地广播使用流程
         *
         * 参考静态广播和动态广播的使用流程，唯一有差别的地方在于：
         *
         * 1. 不支持发送有序广播
         *
         * */

        /**
         * 粘性广播使用流程
         *
         * */

    }

}