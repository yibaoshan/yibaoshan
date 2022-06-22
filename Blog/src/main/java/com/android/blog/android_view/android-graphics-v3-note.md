

标题：什么是低级别组件？

Canvas能不能画3D图形？

开机画面一共显示几次？

#### 前言

> - 前面讲了画面撕裂的原因，framebuffer可以理解为屏幕每个像素点的值，包含颜色，深度等信息
> - 屏幕显示的元素很多，我们自己写的APP一个页面都有好几层视图，这几层是如何变成一副图像信息的呢？这一帧像素矩阵信息是怎么来的呢？

#### 疑问区

> - Android绝大多数SOC都是高通吧，那显卡驱动不也是高通写的吗
> - HWC到底是什么？GPU吗？HWC的KMS又是什么？

#### 知识区

> - GPU厂家，除了骁龙使用自家的Adreno系列之外，华为的麒麟、三星的Exynos、联发科的天玑使用的都是ARM亲儿子：mali
> - PC中Linux系统在2012年就已经全部使用DRM框架了
> - PC端的显卡 = 移动端GPU+ Display Processor + Video Processor
> - Android的显示流程分为三个部分，绘制、合成、显示

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