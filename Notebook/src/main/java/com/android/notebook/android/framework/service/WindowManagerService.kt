package com.android.notebook.android.framework.service

class WindowManagerService {

    /**
     * frameworks/base/services/java/com/android/server/wm/WindowManagerService.java
     *
     * 窗口的概念：
     * Android系统中的窗口是屏幕上的一块用于绘制各种UI元素并可以响应用户输入的一个矩形区域。
     * 从原理上来讲，窗口的概念是独自占有一个Surface实例的显示区域。
     * 例如Dialog、Activity的界面、壁纸、状态栏以及Toast等都是窗口。
     *
     * Android显示系统的三个层次：
     * 1. UI框架层，其工作为在Surface上绘制UI元素以及响应输入事件。
     * 2. WMS，其主要工作是管理Surface的分配、层级顺序等。
     * 3. SurfaceFlinger，负责将多个Surface混合并输出。
     *
     * WMS的诞生：
     * 和其他的系统服务一样，WMS的启动位于SystemServer.java中ServerThread类的run（）函数内，创建分为三个阶段
     * 1. 创建WMS的实例
     * 2. 初始化显示信息
     * 3. 处理systemReady通知
     *
     * WMS的重要成员：
     * 1. mInputManager
     * InputManagerService（输入系统服务）的实例。用于管理每个窗口的输入事件通道（InputChannel）以及向通道上派发事件
     * 2. mChoreographer
     * 它拥有从显示子系统获取VSYNC同步事件的能力，从而可以在合适的时机通知渲染动作，避免在渲染的过程中因为发生屏幕重绘而导致的画面撕裂
     * 3. mAnimator
     * WindowAnimator的实例。它是所有窗口动画的总管（窗口动画是一个WindowStateAnimator对象）。在Choreographer的驱动下，逐个渲染所有的动画。
     * 4. mPolicy
     * WindowPolicyManager的一个实现，目前它只有PhoneWindowManager一个实现类
     * mPolicy定义了很多窗口相关的策略，例如，告诉WMS某一个类型的Window的ZOrder的值是多少，帮助WMS矫正不合理的窗口属性，会为WMS监听屏幕旋转的状态，还会预处理一些系统按键事件（例如HOME、BACK键等的默认行为就是在这里实现的）
     * 等等。所以，mPolicy可谓是WMS中最重要的一个成员了。
     * 5. mTokenMap
     * 一个HashMap，保存了所有的显示令牌（类型为WindowToken），用于窗口管理。
     * 6. mWindowMap
     * 也是一个HashMap，保存了所有窗口的状态信息（类型为WindowState），用于窗口管理
     * 7. mRotation
     * int型变量。它保存了当前手机的旋转状态。
     * */

    /**
     * 理解WindowToken
     * */

}