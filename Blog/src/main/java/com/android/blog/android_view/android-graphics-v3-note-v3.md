Android图形系统（三）系统篇：闲聊SurfaceFlinger

### Overview

可能要求读者对于Android系统的启动，系统中常年运行着进程，进程中的各大服务

### 一、开篇

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
- 

### 二、Vsync：系统的总指挥

在vsync发生之前，系统早已经做好了准备

本章我们一起来看一看，HAL层面的驱动实现我们先不管

#### Vsync到来前的准备工作

##### 1、创建surface_flinger进程

http://www.aospxref.com/android-7.1.2_r39/xref/frameworks/native/services/surfaceflinger/

Loop()

```c++
frameworks/native/services/surfaceflinger/SurfaceFlinger.cpp
//sf的初始化方法
void SurfaceFlinger::init() {
  //初始化hwc函数
  run();
}

void SurfaceFlinger::run() {
    do {
        waitForEvent();//等待事件发生
    } while (true);
}
```

总的来说，sf进程一共做了三件事

1. 注册vsync信号回调，如果硬件不支持，启用VSyncThread线程模拟
2. 启动vsync信号线程（如果硬件支持的话）
3. 初始化HWComposer对象

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



#### Vsync生产与处理

sf在初始化时注册了hwc的回调，hwc是由屏幕驱动来定时调用的，由~~DispSync~~来分发

很多分析文章都提到了DispSync，所以我们简单介绍一下：

> 是什么

假设hwc信号直接到达sf，再有sf分发给各个

至此，APP进程创建完成，系统服务也时刻准备着

接下来，APP进程和系统进程都一同等待着Vsync信号的到来

##### 1、第一帧，APP进程绘制与渲染

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

### 三、结语

总结一下，SurfaceFlinger是Android图形系统的核心进程，在整个图形系统中起承上启下的作用

在Android系统中，sf是关键，通过sf可以申请到一个layer

server进程中管理组件的状态，没有

总结一下vsync到来之前和到来之后发生的事情

Android启动时会创建两大进程，其中常见的ams和wms运行在server进程

当APP启动以后，不管是因为input事件还是fling或者主动发起更新请求，最终都会调用到requestNextVsync()方法中

以大部分应用开发来举例：

本文从进程的角度分析了Android的显示流程，文章中省略了许多重要的知识点，比如：

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
