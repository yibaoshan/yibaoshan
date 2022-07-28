### Waiting

> - Surface/Layer相关
>
>   > 

Android图形系统（三）系统篇：闲聊View显示流程

对于应用开发工程师来说，虽然我们不需要写操作系统代码，但是了解View最终是如何显示到屏幕上还是非常有必要的

本篇是Android图形系列的第三篇文章，在之前的两篇文章中我们分别学习了屏幕的“显示原理”和屏幕的“刷新原理”，今天我们来一起学习Android系统的图形架构设计，聊一聊输送到屏幕的画面数据是如何诞生的

本文的目标是希望读者朋友能从宏观上构建一个Android图形子系统的清晰框架与认知

因此，文中不会包含太多的方法调用链以及代码逻辑，非Android开发工程师也可以放心食用

以下，enjoy：

我是概览图

### 一、开篇

Android图形子系统由Linux操作系统层、HAL硬件驱动层、Android Framework框架层几个部分组成，整个系统非常庞大，各个模块之间错综复杂，让人无从下手

不过，再复杂的系统设计，也离不开硬件的支持

今天，让我们化繁为简，从最基础的硬件组成开始学习，自下而上，看看Android图形子系统是如何一步步建立起来的

#### Android设备由哪些硬件组成？

这里需要重点关注的是GPU

我们常常听到的OpenGL ES就是由GPU驱动来实现，包括Android 7.0宣布支持Vulkan协议，同样也是由GPU驱动实现

我们可以去RK3889的用户手册

图片来源：datasheet

瑞芯微这块开发板使用的是Arm的亲儿子mali，同时支持OpenGL 12Vulkan

也可以说，OpenGL ES一定程度上指导了电路板的设计

#### 什么是Android HAL层？

##### 1、HWC

瑞芯微开发板关于HWC部分实现点[[这里]](https://gitlab.com/TeeFirefly/firenow-oreo-rk3399/-/tree/master/hardware/rockchip/hwcomposer)



#### Framework层库支持

消费级的联发科/骁龙的用户手册都要花钱买，我们就找个工业级的，演示一下

我们知道Android的HAL，在图形系统中，另一个技术名词也会经常被体积：OpenGL ES

#### 什么是HAL？

实现HAL和OpenGL ES/Vulkan协议，是嵌入式驱动工程师需要完成的工作

CPU内存可以组建出一台计算机，

Android图形子系统，

从HAL入手，看看是什么支撑起Android图形系统

尽量的

本篇文章我们将尝试把图形系统化繁为简，

当我们打开Android开发者



HAL和OpenGL ES一样，指导驱动层的设计，驱动又建立在硬件之上，在一定程度上也可以说是指导硬件的电路设计

电路怎么设计我不管，驱动怎么写我也不管

Andorid设备基于硬件，驱动层

在文章的开头我们先来聊一聊一块普通的开发板/手机主板上都有哪些

2D图形是3D图形的子集



本篇文章大部分源码都来自7.0版本，选择7.0的原因很简单，因为它包含了4.1黄油计划和5.0的RenderThread又没有高版本的复杂逻辑，非常适合我们简单了解图形系统的设计

#### 硬件介绍

##### 1、GPU

##### 2、DPU

#### Android层级设计

##### 1、libui.so

###### 1.1 Fence机制

###### 1.2 Gralloc机制

######1.3 GraphicBuffer

GraphicBuffer是整个图形系统的核心，所以的渲染操作都将在此对象上进行，包括同步给GPU以及HWC

在流转的过程中，GraphicBuffer不但要跨进程传递，还涉及到跨硬件，因此，GraphicBuffer中也会保存fence的状态

拥有了libui库以后，我们就可以通过操作系统来调用屏幕驱动，从而直接去显示画面

现在大多数开发板都实现了FB框架，所以我们可以打开fb0设备进行写入，感兴趣的可以点击[这里](https://www.youtube.com/watch?v=BUPPyR6VasI)查看视频

Google在2018年发布的Pixel 3首次使用了DRM/KMS框架，

我自己的pixel用的是DRM框架，我尝试了一下没有成功，懒得折腾了

关于DRM可以查看何小龙的视频

##### 2、libgui.so

###### 2.1 BufferQueue

从名字就可以看出来，GraphicBuffer是一个GraphicBuffer队列

在7.0中用BufferItem进行二次封装，其中的mslot表示buffer的状态，bufferitem在不同版本命名可能不一样，注意区分

在BufferQueue包装中，赋予了GraphicBuffer的几种状态，它们分别是

###### 2.2 Surface

Surface中持有BufferQueue的引用，并且封装了出列、入列等一系列的操作

###### 2.3 DisplayEventReceiver



##### 3、SurfaceFlinger进程

###### 3.1 Layer

Layer通过GraphicBuffer的包装类BufferItem持有GraphicBuffer的队列

layer有两个，一个是hwui包下的，通常我们说的layer值得是sf包下的

日常吐槽Google工程师命名及其混乱

##### 4、什么是硬件加速？

总结如图，开启硬件加速对于APP来说导致的

1. 开启RenderThread，将会在systrace中体现
2. 由于厂商策略不同，GPU硬件可能并没能呈现预期效果

认识硬件设计和层级设计非常重要，建议读者在阅读本文时同时打开以下网页对比着看，在阅读过程中不知道回过头看看在系统设计的哪一层

- libui.so：Fence、Gralloc、GraphicBuffer
- libgui.so：BufferQueue和Surface
- surfaceflinger进程：Layer和DispSync
- SystemServer进程：ams、wms等常用服务

### 二、系统启动流程

#### 启动surface_flinger进程

##### 1、创建HWComposer对象

#### 启动system_server进程

#### 启动app进程

### 三、Vsync：系统的总指挥

#### 第一次Vsync：View的绘制与渲染

在vsync发生之前，系统早已经做好了准备

本章我们一起来看一看，HAL层面的驱动实现我们先不管

#### Vsync到来前的准备工作

我们假设内核和驱动部分都已经启动好了

##### 1、创建surface_flinger进程



http://www.aospxref.com/android-7.1.2_r39/xref/frameworks/native/services/surfaceflinger/

```c++

frameworks/native/services/surfaceflinger/Client.cpp
// protected by mLock
DefaultKeyedVector< wp<IBinder>, wp<Layer> > mLayers;//保存客户端的layer

frameworks/native/services/surfaceflinger/SurfaceFlinger.cpp
//sf的初始化方法
void SurfaceFlinger::init() {
  // initialize EGL for the default display
  mEGLDisplay = eglGetDisplay(EGL_DEFAULT_DISPLAY);
  eglInitialize(mEGLDisplay, NULL, NULL);
  // start the EventThread
sp<VSyncSource> vsyncSrc = new DispSyncSource(&mPrimaryDispSync,
        vsyncPhaseOffsetNs, true, "app");
mEventThread = new EventThread(vsyncSrc, *this);
sp<VSyncSource> sfVsyncSrc = new DispSyncSource(&mPrimaryDispSync,
        sfVsyncPhaseOffsetNs, true, "sf");
mSFEventThread = new EventThread(sfVsyncSrc, *this);
mEventQueue.setEventThread(mSFEventThread);
  //初始化hwc函数
  run();
}

void SurfaceFlinger::run() {
    do {
        waitForEvent();//睡觉，等待事件发生
    } while (true);
}

//APP进程发起连接
sp<ISurfaceComposerClient> SurfaceFlinger::createConnection()
{
    sp<ISurfaceComposerClient> bclient;
    sp<Client> client(new Client(this));
    status_t err = client->initCheck();
    if (err == NO_ERROR) {
        bclient = client;
    }
    return bclient;
}

//介绍消息并处理
void SurfaceFlinger::onMessageReceived(int32_t what, int64_t vsyncId, nsecs_t expectedVSyncTime) {
    switch (what) {
        case MessageQueue::INVALIDATE: {
            onMessageInvalidate(vsyncId, expectedVSyncTime);
            break;
        }
    }
}

frameworks/native/services/surfaceflinger/EventThread.cpp
void EventThread::enableVSyncLocked() {
     if (!mUseSoftwareVSync) {
         // never enable h/w VSYNC when screen is off
         if (!mVsyncEnabled) {
             mVsyncEnabled = true;
             mVSyncSource->setCallback((this));
             mVSyncSource->setVSyncEnabled(true);
         }
     }
     mDebugVsyncEnabled = true;
     sendVsyncHintOnLocked();
 }
```

总的来说，sf进程在图形处理相关方面一共做了三件事

1. 注册vsync信号回调，如果硬件不支持，启用VSyncThread线程模拟
2. 启动vsync信号线程（如果硬件支持的话）
3. 初始化HWComposer对象，并且注册HWC回调
4. 提供链接方法，等待APP端跨进程调用
5. 睡觉，等待消息

关于第4点要着重强调一遍，APP进程申请Surface成功后，经过一系列的方法调用，最终会在sf进程中创建对应的Layer，这个Layer会保存在mLayers中

注意，每个版本的surfaceflinger代码都在变，对不上的话可以检查源码版本

##### 2、创建system_server进程

Loop()

##### 3、创建APP进程

从zygote进程中fork出APP进程后，WindowManagerGlobal

Activity的创建过程的调用链有点长，这里先忽略掉，在AMS和WMS的通力合作下创建出Activity实例对象

这个实例在AMS保存为ActivityRecord对象，在WMS中保存为WindowState对象

ViewRootImpl中持有两个非常重要的对象：Choreographer和Surface

Choreographer中也有一个非常重要的对象：DisplayEventReceiver

DisplayEventReceiver完成对gui.so中的DisplayEventReceiver封装，

还记得DisplayEventReceiver吗？

这就是为什么Choreographer也能接收到vsync信号的关键

```c++
frameworks/native/libs/gui/DisplayEventReceiver.cpp
DisplayEventReceiver::DisplayEventReceiver() {
    sp<ISurfaceComposer> sf(ComposerService::getComposerService());
    if (sf != NULL) {
        mEventConnection = sf->createDisplayEventConnection();
        if (mEventConnection != NULL) {
            mDataChannel = mEventConnection->getDataChannel();
        }
    }
}
```

ViewRootImpl本身由WMS管理，一个Activity对应一个ViewRootImpl

从这里我们也可以两位

AMS负责管理组件状态，WMS负责管理视图状态

我们这里一笔带过，简单来说是通过AMS创建了

###### 3.1 AMS创建Activity对象

###### 3.2 WMS创建Window对象

###### Choreographer回调注册

###### ViewRootImpl

在ViewRootImpl中会创建mSurface对象，这个对象对应的类是Surface.java

surface.java封装了对gui的操作，jni类在android_view_Surface.cpp中

呼~

到这里，一个surface对象终于创建完成

###### ActivityThread

Looper.loop()进入睡眠等待唤醒

sf准备好接收vsync信号

#### Vsync生产与处理

sf在初始化时注册了hwc的回调，hwc是由屏幕驱动来定时调用的，由~~DispSync~~来分发

很多分析文章都提到了DispSync，所以我们简单介绍一下：

> 是什么

假设hwc信号直接到达sf，再有sf分发给各个

至此，APP进程创建完成，系统服务也时刻准备着

接下来，APP进程和系统进程都一同等待着Vsync信号的到来

Drawing with VSync

##### 1、第一帧，APP进程绘制与渲染

- 创建Surface，创建BufferQueue，SF对应创建Layer，每一个Surface创建成功后，经过一系列的方法调用，最终会被同步到sf进程，并创建Layer，就将会被把书翻到第一章第二节的，surface
- 

前面我们提到了eventthread，

- ViewRootImpl.requestLayout()
- ViewRootImpl.scheduleTraversals()
- ViewRootImpl.doTraversal()
- ViewRootImpl.performTraversals()

> perfromDraw()
>
> ​	->draw()
>
> ​		->drawSoftware()	



##### 2、第二帧，SF合成

##### 3、第三帧，DRM/KMS显示

##### 4、新同学的加入：RenderThread

Android 5.0以后的View体系中加入了RenderThread，也就是渲染线程

支持硬件加速的情况下，渲染过程和UI线程分离了，UI线程负责将onDraw中的绘制命令（被称为RenderNode）收集到DisplayList，接着调用syncAndDrawFrame()方法将命令同步给RenderThread，随后执行渲染任务

引入渲染线程的好处有两个：

一是可以防止重复绘制，比如

二是留给UI线程更多的时间来处理messagequeue中的消息，

##### 5、如何暂停接收Vsync信号？

我们打开APP后没有进行任何操作，APP还会执行渲染流程吗？

答案显然是否定的

sf和app都需要调用requestNextVsync方法请求下一次同步信号

vsync信号由DispSyncSource和EventThread来分发

比如录屏软件就会调用获取当前的buffer

sf的两个回调：

**MessageQueue::invalidate**

当layer有变化时，messagequeue会收到invalidate的消息

在invalidate回调中，sf回去请求请求一次vsync callback回调

没有layer请求，就永远不会有vsync回调

而invalidate回调，可能是app进程画面有更新，要去合成

也可能画面没更新，虚拟屏幕或者录屏软件在发消息

**MessageQueue::vsyncCallback**

走合成流程

### 三、结语

再次强调一遍，文章中源码版本是7.0

了解了GraphicBuffer的流转过程，也就明白了Android系统的显示流程

总结一下，SurfaceFlinger是Android图形系统的核心进程，在整个图形系统中起承上启下的作用

在Android系统中，sf是关键，通过sf可以申请到一个layer

server进程中管理组件的状态，没有

总结一下vsync到来之前和到来之后发生的事情

Android启动时会创建两大进程，其中常见的ams和wms运行在server进程

当APP启动以后，不管是因为input事件还是fling或者主动发起更新请求，最终都会调用到requestNextVsync()方法中

以大部分应用开发来举例：

从主要角色延伸出来

为了过于臃肿，文章删减了许多辅助角色

本文更多的是以进程的视角，以分层的架构来解释Android图形子系统的全貌，为此删减了许多辅助角色

这些辅助角色都是由

为了避免引入过多的角色导致阅读体验不佳，尽可能用最简单的来解释，我在各个层级中删减掉许多辅助类/文件，如果对于大致不是很熟悉的情况下去跟源码中可能会感到困惑

但这样的做法会给想要阅读源码的小伙伴感到困惑，造成不小的麻烦

本文从进程的角度分析了Android的显示流程，文章中省略了许多重要的知识点，方法的调用链，比如：

> - 合并到SurfaceFlinger.cpp
> - 合并到gui/Surface.cpp
>
>   > GraphicBuffer这块内存需要在几个不同的硬件中流转，被不同的硬件识别并使用需要实现不同的协议
>   >
>   > ANativeWindow简单来说是公共协议OpenGL ES的具体实现类
>   >
>   > Surface继承自ANativeWindow，这样Surface中的GraphicBuffer这块内存就能被GPU所识别并使用
> - SurfaceControl用于管理Surface的创建与sf进程的链接
> - 合并EventThread到sf中
> - 合并SurfaceControl.cpp到Surface.cpp中
> - 合并DisplayEventReceiver.cpp到Surface.cpp
> - HWC、EGL等系统关键模块初始化的时机以及创建过程
> - GraphicBuffer的流转中包含了Fence的状态
> - BufferQueue的核心在BufferQueueCore类中，也无法直接操作BufferQueue，GraphicBuffer被封装为Solt
> - Android S更改了显示流程
>   - 删除ION驱动改为dma_buf直接驱动
>   - 删除DisySync类，笔者暂时没找到新的替换类
>   - GraphicBuffer队列改为App控制

在这其中的任何一个环节都可以写篇文章来介绍，作者能力有限，无法将以上细节全部解释清楚

Android图形子系列横跨硬件驱动、Linux内核、Framework框架三层，虽然每个模块设计的比较合理，命名比较混乱，但命名真的是过于混乱，以至于稍不留意就迷失在源码当中

这就导致想要理清它们之间的关系变成一件比较困难的事情，好在已经有各位前辈铺好了路

希望本文能够抛砖引玉，为好学的你提供一点点帮助

全文完

### 五、参考资料

- [《深入理解Android内核设计思想》- 林学森](https://book.douban.com/subject/25921329/)
- [《Weishu's Notes》- 田维术（太极/两仪作者）](https://weishu.me/)
- [《Android显示系列》- 努比亚团队](https://www.jianshu.com/c/3a4d92743e88)
- [《Systrace系列》- 高爷（性能优化专家）](https://www.androidperformance.com/2019/05/28/Android-Systrace-About) 
- [《Android 12 BlastBufferQueue系列》- 大天使之剑](https://www.jianshu.com/u/124e5f361305)
- [《DRM与BufferQueue系列》- 何小龙](https://blog.csdn.net/hexiaolong2009?type=blog)
- [《Android图形显示系列》- 夕阳叹](https://blog.csdn.net/jxt1234and2010/category_2826805.html)
- [《王小二的Android站》- 王小二（TCL王总）](https://www.jianshu.com/u/fd0b722ce11f)
- [《Silence.Slow.Simple专栏》- simowce](https://blog.simowce.com/archives/)
- [《Android SurfaceFlinger 学习之路》- windrunnerlihuan](https://windrunnerlihuan.com/archives/page/2/)
- [《Android 12(S) 图像显示系统》- 二的次方](https://www.cnblogs.com/roger-yu/p/15641545.html)
- [深入理解Flutter的图形图像绘制原理 - OPPO数智技术](https://segmentfault.com/a/1190000038827450)
- [Android性能优化：定性和定位Android图形性能问题 - 飞起来_飞过来](https://juejin.cn/post/7096288511053004830)
