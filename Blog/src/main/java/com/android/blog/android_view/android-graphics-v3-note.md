

标题：为什么关机了还能显示充电画面？

SurfaceFlinger图形系统

标题：View和ViewGroup，图像容器

#### 疑问区

> - Window和surface对应关系，最小表示单位是framebuffer吗？一个framebuffer对应的是什么？每个surface都有两个fb吗？
> - framebuffer是一堆数据吧，从哪来，到哪去
> - ，framebuffer的生成过程
> - 绘图方式、格栅化、合成
> - HWC到底是什么？GPU吗？HWC的KMS又是什么？
> - view和canvas之间的关系，view是一张画布，对应的是surface吗？
> - 一个Window上有好多个view吧，Window对应的是啥
> - Java Window和Native surface和Native layer的对应关系？
> - buffer是什么？是像素点数据吗？
> - 三重缓存和vsync和framebuffer对应Android里面的啥
> - 猜想：view不调用失效invalided方法，那么该view就不用重新绘制，调用合成就好了，举例来说，假设你的APP，为了性能考虑，当页面不可见时所有的动画都应该取消，不然一直调用
> - 自定义view中，谁来调用onDraw()方法ss
> - frameData是画面数据

- 底层组件支持

  > surface canvas
  >
  > - ANativeWindow支持GLES和Vulkan协议

- 高级别组件支持

  > - GLSurfaceView拥有GL环境的surface
  > - SurfaceView继承自View，并提供了一个独立的绘图层Surface，这个Surface在WMS中有自己对应的WindowState，在SF中也会有自己的Layer。你可以完全控制SurfaceView，比如说设定它的大小，所以SurfaceView可以嵌入到View结构树中，需要注意的是，但在Server端（WMS和SF）中，它的Surface与宿主窗口是分离的。这样的好处是对这个Surface的渲染可以放到单独线程去做，渲染时可以有自己的GL context。这对于一些游戏、视频等性能相关的应用非常有益，因为它不会影响主线程对事件的响应。但它也有缺点，因为这个Surface不在View hierachy中，它的显示也不受View的属性控制，所以不能进行Transition，Rotation，Scale等变换，也不能放在其它ViewGroup中，也不能进行Alpha透明度运算

#### 前言

> - 之前的两篇文章中分别介绍了屏幕的显示原理和屏幕的刷新原理，前两篇文章我们了解了屏幕实际上是由一个个像素点组成，本篇文章关注的是framebuffer的产生
>
> - 前面讲了画面撕裂的原因，framebuffer可以理解为屏幕每个像素点的值，包含颜色，深度等信息
>
> - 本章的重点是关心view是如何转变为屏幕像素点数据的？
>
> - 屏幕显示的元素很多，我们自己写的APP一个页面都有好几层视图，这几层是如何变成一副图像信息的呢？这一帧像素矩阵信息是怎么来的呢？
>
> - 众所周知，Google为了改进Android流畅度，在Android 4.1版本发布了project buffer黄油计划，希望Android系统能够像黄油一样丝滑
>
> - 在黄油计划中，Android的绘制被分成了三步，绘制、合成、显示，增加了，对应的vsync被分成了两份，一部分，今天我们来聊一聊
>
> - 发展到2022年，Android图形系统更加复杂，为了减少GPU的压力，设计了HWC的HAL抽象层进行合成，绘制端也加入了UI Renderer
>
> - 至此，文章的标题就有答案了，同理工厂模式也是直接
>
> - 图形系统真他妈复杂，全文完
>
> - 标题，Android  黄油计划
>   - 为了改进Android系统的流畅度，Google在Android 4.1版本发布了Android project butter黄有计划，希望Android系统能够像黄油一样丝滑
>   - 在黄油计划中新增了渲染线程，
>   - 发展到今天，Google为了减缓GPU压力加入hwc等，具体可以看官网以及高通的开发文档
>   
> - 结语：对于没有接触过framework开发的同学来说，理解hwc等概念还是有难度的，在文章的结尾，我们来聊一聊Android的hal层，理清hal的概念，对于我们理解Android系统架构有很大的帮助，这一切在知道hal是干嘛的之后就清晰了，不考虑硬件厂商驱动的情况下，Android图形系统是由sf作为中介，framebuffer作为媒介，通过binder传输，最终输出到显存由显示器驱动更新到屏幕
>
> - 注1：Android 7.0以上和图像引擎（高通a系列，armmali系列等）均已支持vulkan协议，本文未包含Vulkan相关内容
>
> - Andorid图形系统有surfaceflinger、wms等组成，内部命名也比较混乱，想要在短期内完全理清有些困难，本文也只是从下至上分析Android支持哪些绘图方式，以及他们是如何实现的
>
>   > 任何一节都可以单独，比如开发者选项中过度绘制的原理
>   >
>   > 为什么关机了还能显示充电画面？
>   >
>   > 为什么flutter可以跨平台
>   
> - 结语
>
>   > 本文只是从框架设计的角度聊聊Android系统的图形架构

#### 难点

> - Android图形系统框架一直在改进，由fb改为drm，加入了hwc，所以市面上也没有相关书籍可以从上到下的学习view的绘制流程

#### blog区

> - 加入了
> - 加入了Hardware Composer负责合成

#### 知识区

> - View使用skia，打开硬件加速使用hwui，也就是GPU
> - GPU厂家，除了骁龙使用自家的Adreno系列之外，华为的麒麟、三星的Exynos、联发科的天玑使用的都是ARM亲儿子：mali
> - PC中Linux系统在2012年就已经全部使用DRM框架了
> - PC端的显卡 = 移动端GPU+ Display Processor + Video Processor
> - Android的显示流程分为三个部分，绘制、合成、显示
> - surfacefingler是Android gui的核心，但对于OpenGL来说，sf可以看做应用
> - 抛开Java层不谈，在系统级别是由surfaceflinger和其内部的bufferqueue组成，他们都是c++程序
> - 底层应用我们可以来分析bootanimation，它的内部是借助surfaceflinger来完成的

#### 深入理解Android内核设计思想

> - 疑惑：
>   - 每个应用程序可以有几个bufferqueue，他们的关系是一对一还是多对一/一对多
>   - 应用程序绘制UI所需的内存控件时由谁来分配的
> - Surface对应一个本地窗口
> - bufferqueue是一个服务中心，生产者（应用程序/surface）需要使用一块buffer时，首先会去向中介bufferqueue发起dequeue申请，完成写入后调用queue接口将图形数据入列
> - 前面已经学习了bufferqueue的内部原理，那么应用程序又是如何与之配合的呢？解决这个疑惑的关键是了解应用程序是如何执行绘图流程的，这也是本节内容的重点

Android使用的图形框架

> 原子显示框架ADF：Atomic Display Framework

Andorid图形框架发展史

> Vulkan驱动

OpenGL ES代码在frameworks/native/opengl

Vulkan代码在frameworks/native/vulkan

SurfaceFlinger代码在framework/native/services/surfaceflinger

大致流程

> Android系统提供了三种绘图方式，分别是：Canvas、OpenGL ES和Vulkan
>
> 无论使用哪种，最终内容都会被渲染到surface上，然后交给surfaceflinger管理
>
> **图像生产者**
>
> 图像流生产方可以是生成图形缓冲区以供消耗的任何内容。例如 OpenGL ES、Canvas 2D 和 mediaserver 视频解码器。
>
> **图像消费者**
>
> 图像流的最常见消耗方是 SurfaceFlinger，该系统服务会消耗当前可见的 Surface，并使用窗口管理器中提供的信息将它们合成到屏幕
>
> SurfaceFlinger 是可以修改所显示部分内容的唯一服务。SurfaceFlinger 使用 OpenGL 和 Hardware Composer 来合成一组 Surface
>
> 其他 OpenGL ES 应用也可以消耗图像流，例如相机应用会消耗相机预览图像流。非 GL 应用也可以是使用方，例如 ImageReader 类

合成机制、显示机制

### 概述

#### 操作系统

Android系统是由三个部分组成，看gityuan的吧，这部分不用写了

曾经被问过一个问题，Activity和AndroidX包中的AppCompatActivity的类加载器是同一个吗？

我说是，然后面试官就让我回家等通知..

#### BufferQueue和Gralloc

- Gralloc 

  > Android系统在硬件抽象层中提供了一个Gralloc模块，封装了对帧缓冲区的所有访问操作。用户空间的应用程序在使用帧缓冲区之间，首先要加载Gralloc模块，并且获得一个gralloc设备和一个fb设备。有了gralloc设备之后，用户空间中的应用程序就可以申请分配一块图形缓冲区，并且将这块图形缓冲区映射到应用程序的地址空间来，以便可以向里面写入要绘制的画面的内容。最后，用户空间中的应用程序就通过fb设备来将已经准备好了的图形缓冲区渲染到帧缓冲区中去，即将图形缓冲区的内容绘制到显示屏中去。相应地，当用户空间中的应用程序不再需要使用一块图形缓冲区的时候，就可以通过gralloc设备来释放它，并且将它从地址空间中解除映射。

#### Surface和SufaceHolder

#### SurfaceFlinger和WindowManager

- SurfaceFlinger

  > SurfaceFlinger中需要显示的图层（layer）将通过DisplayDevice对象传递到OpenGLES中进行合成，合成之后的图像再通过HWComposer对象传递到Framebuffer中显示。DisplayDevice对象中的成员变量mVisibleLayersSortedByZ保存了所有需要显示在本显示设备中显示的Layer对象，同时DisplayDevice对象也保存了和显示设备相关的显示方向、显示区域坐标等信息。
  >
  > > ​    DisplayDevice是显示设备的抽象，定义了3中类型的显示设备。引用枚举类位于frameworks/native/services/surfaceflinger/DisplayDevice.h中，定义枚举位于hardware/libhardware/include/hardware/Hwcomposer_defs.h中：
  > >
  > > - DISPLAY_PRIMARY：主显示设备，通常是LCD屏
  > > - DISPLAY_EXTERNAL：扩展显示设备。通过HDMI输出的显示信号
  > > - DISPLAY_VIRTUAL：虚拟显示设备，通过WIFI输出信号

#### 硬件混合渲染器HAL（HWC）

### 渲染引擎

#### Skia库：Canvas 2D绘制

#### OpenGL ES/Vulkan：3D渲染

#### Mediaserver：视频解码器

Android 2D API，代码在/external/skia中，canvas调用的API底层就是由skia库来实现

正文

- 一、开篇

  - 应用开发者的工作中，想更新view很简单，只需要调用invalidate()方法，让当前view失效即可

  - 在Android 4.1黄油计划中，加入了choreographer，它的作用是将vsync信号同步给应用层，目的是把合成和显示分开，减轻GPU的计算量

  - 介绍当前图形框架的发展历史，最终引申到surfaceflinger

  - 对于应用开发者来说，我们只需要关surfaceflinger的工作流程即可

  - 图像流中最常见的消费者是surfaceflinger，同时他也是我们应用开发者需要关注的服务

    sufaceflinger消耗当前可见的surface

- 低级别组件之surfaceflinger

- Choreographer

Android GUI系统 SurfaceFlinger

- 文案

  > 在学习图形体系过程中，对系统中出现的dispaly/gralloc等等一系列陌生的模块感到混乱而无序，鉴于此，我们先从框架设计的角度来观察它们之间错综复杂的关系
  >
  > 程序设计就是这样，越到上层越复杂，因为需要支撑不同的业务
  >
  > Android提供了两种方式，一种是OpenGL ES，另一种是View

- 想法

  > 使用c写一个画面，修改开机画面文字

- OpenGL ES与EGL

  > SurfaceFlinger虽然是GUI系统的核心，但从OpenGL ES的角度来讲，SurfaceFlinger只能算是一个应用程序
  >
  > OpenGL ES是协议，开发商可以选择由软件实现（CPU）或者硬件实现（GPU），OpenGL在启动时会去读取egl.cfg这个配置文件，根据配置文件决定加载哪个so

- Android终端显示设备的化身，gralloc与Framebuffer

  > 所有的使用者需要借助gralloc来打开设备
  >
  > fb0表示“主屏幕”
  >
  > gpu0表示gralloc设备，负责图形缓冲区的分配和释放

- Android中的本地窗口

  > 本地窗口是OpenGL能否兼容多种系统的关键，在Android系统中，至少需要两种本地窗口
  >
  > - 对于管理者surfaceflinger来说，需要直接或间接的持有本地窗口，这个窗口就是FramebufferNativeWindow
  > - 对于应用程序来说，本地窗口是surface
  >
  > 在一个系统设备中只会有一个帧缓冲区

- 角色汇总

  - HAL层

    > HWComposer，有两个composer，一个用于合成layer，一个用于发送vsync信号
    >
    > Gralloc，单一职责，用于分配图像所需的内存

#### 资料

- 《深入理解Android内核设计思想-林学森》
- [Android-SurfaceFlinger图形系统](https://github.com/jeanboydev/Android-ReadTheFuckingSourceCode/blob/master/article/android/framework/Android-SurfaceFlinger%E5%9B%BE%E5%BD%A2%E7%B3%BB%E7%BB%9F.md)
- [针对移动端TBDR架构GPU特性的渲染优化](https://blog.csdn.net/leonwei/article/details/79298381)
- [深刻理解现代移动GPU](https://blog.csdn.net/qq_41028985/article/details/120828912)