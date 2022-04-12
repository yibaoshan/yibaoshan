package com.android.blog.android_view;

public class Main {

    /**
     * OpenGL ES 是什么
     * Surface是什么？SurfaceHolder、SurfaceControl
     * SurfaceView，TextureView
     *
     * SurfaceFlinger的职责？
     *
     * 视频播放、游戏等是直接操作Surface，根据什么协议呢？
     *
     * */

    /**
     *
     * 屏幕相关
     *
     * 1. 屏幕刷新率和FPS关系？
     * 2. 屏幕的像素大小
     * 3. 屏幕的色深
     * 4. vsync信号：通知绘制下一帧？交换buffer缓冲数据？节省CPU性能？
     * 5. 逐行扫描，从上到下，从左到右的原则
     *
     * 一块屏幕是由很多个像素点组成的大的LED矩阵，
     *
     * 屏幕刷新率是固定的，比如60GHZ，或者90、120
     * FPS是显卡在每秒钟里面能渲染出多少帧的画面，值的大小通常由画面的复杂度以及GPU的算力和贷款来决定
     *
     * 也就是说，显示器的刷新率代表了显示器从帧缓存中读取数据的速度
     *
     * 显卡的渲染帧率代表了显卡向帧缓存中写入数据的速度，
     *
     * 屏幕的刷新率
     * 假设为60HZ也就是1秒刷新60帧，大约16.67毫秒刷新1帧，代表没隔16ms屏幕回去读取帧缓冲中的数据
     *
     * 对于高配电脑（指显卡渲染速度大于显示器刷新率），开垂直同步有助于增加画面稳定性，同时降低功耗，实际帧率与显示器刷新率相同或者是刷新率的倍数。
     * 对于低配电脑（指显卡渲染速度小于显示器刷新率），开启垂直同步后由于还要等待同步信号，加上垂直同步本身消耗，造成实际FPS降低。
     *
     * https://blog.csdn.net/weixin_42352178/article/details/115403092
     *
     * GPU显卡垂直同步和屏幕之间的关系
     * 假设屏幕60GHZ，GPU显卡比较流弊，1秒能渲染100帧，那么就有40帧被浪费了，这时候开启垂直同步信号
     *
     * 屏幕撕裂
     *
     * 在单缓存时代，若FPS帧率大于显示器帧率，便会发送撕裂
     * */

    /**
     * 渲染引擎，方式
     * 2D: Skia Canvas
     * 2D: OpenGL ES
     * 3D: Vulkan
     *
     * Android View是不是使用Canvas画布来绘制的呢？
     * */

    /**
     * 操作系统 Android OS
     *
     * 图形组件
     * 无论开发者使用什么渲染 API，一切内容都会渲染到 Surface 上
     * surface表示生产者，SurfaceFlinger表示消费者
     * 它们之间传递的数据就是平时所说的buffer
     *
     * SurfaceView
     * 结合了 Surface 和 View。SurfaceView 的 View 组件由 SurfaceFlinger（而不是应用）合成，从而可以通过单独的线程/进程渲染，并与应用界面渲染隔离
     *
     * SurfaceTexture
     * 将 Surface 和 GLES 纹理相结合来创建 BufferQueue
     *
     * TextureView
     * 结合了 View 和 SurfaceTexture。TextureView 对 SurfaceTexture 进行包装，并负责响应回调以及获取新的缓冲区
     *
     * SurfaceFligler
     * 负责合成多个Surface，将多个Surface合成成一帧图，交给缓冲区，发送给显示屏
     * 在图形框架中是一个承上启下的角色，对于屏幕来说，它是buffer的生产者，对于应用来说，它是消费者
     *
     *
     * 双重缓存、三重缓存
     * */

    /**
     * APP开发
     * View
     * ViewGroup
     * ViewRootImpl
     * Window
     *
     * 自定义View、ViewGroup
     * */

    /**
     * 应用层与native层对应关系
     * 上层Window创建之后，native层会创建对应的Surface，以及SurfaceFlinger进程会创建对应Layer，所以应用层的窗口对应到SurfaceFlinger进程其实就是Layer。
     * */

    /**
     * BufferQueue的生产者/消费者模型
     * 在进入讨论这些扩展之前，先简单回顾下Andriod BufferQueue的运行机制。
     *
     * 在Android (3.0之后)，上到application,下到surfaceflinger, 所有的绘制过程都采用OpenGL ES来完成。对于每个绘制者（生产者，内容产生者）来说，步骤大致都是一样的。
     *
     * (1)获得一个Surface（一般是通过SurfaceControl）
     *
     * (2)以这个Surface为参数，得到egl draw surface 和 read surface. 通常这俩是同一个对象
     *
     * (3)配置egl config,然后调用eglMakeCurrent()配置当前的绘图上下文。
     *
     * (4)开始画内容，一般通过glDrawArray()或者glDrawElemens()进行绘制
     *
     * (5)调用eglSwapBuffers() swap back buffer和front buffer。
     *
     * (6)回到第4步，画下一帧。
     *
     * 我们知道，所有的绘制，最后的结果无非是一块像素内存，内部存放了RGB，或者YUV的值；这块内存，也就是Surface的backend。在Android中，为了让内容生产者和消费者可以并行工作，每个Surface背后都采用了三倍的内存缓冲(MTK是四倍缓冲)，这样，当绘制者在绘制的时候，不会影响当前屏幕的显示，而且，绘制者画完一帧之后，一般立即就可以再获得一块新的Buffer进行下一帧的绘制，而无需等待。
     * */

    /**
     * 双缓冲
     * 三缓冲
     * 绘制一个buffer
     * 合成一个buffer
     * 显示一个buffer
     * */

}
