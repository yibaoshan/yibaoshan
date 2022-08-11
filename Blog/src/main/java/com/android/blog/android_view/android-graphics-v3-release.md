Android图形系统（三）系统篇：当我们点击“微信”这个应用后，它是怎么在屏幕上显示出来的？

对于应用开发工程师来说，虽然我们不需要写操作系统代码，但是了解View最终是如何显示到屏幕上还是非常有必要的

本篇是Android图形系列的第三篇文章，在之前的两篇文章中分别介绍了屏幕的“显示原理”和屏幕的“刷新原理”，今天我们来一起学习Android系统的图形架构设计，聊一聊输送到屏幕的画面数据是如何产生的

本文的目标是希望读者朋友建立一个Android图形子系统的框架，因此，文中不会包含太多的方法调用链以及代码逻辑，非Android开发工程师也可以放心食用

以下，enjoy：

我是概览图

### 一、开篇

当我们点开“微信”这个应用后，它是怎么在屏幕上显示出来的？

这是一个非常复杂的问题，它的背后包含了由厂商驱动、Linux操作系统、HAL硬件抽象层和Android Framework框架层共同组建的一套非常庞大的Android图形子系统

想要给出这个问题的答案，需要对Android图形子系统背后的运行流程有所了解

今天，我们从认识Android设备的硬件开始，自下而上，一起来看看庞大的图形系统是如何一步步建立起来的

#### 1、硬件驱动

再复杂的系统设计，也离不开硬件的支持，在文章的开头，我们先来了解一下，Android设备里支撑应用程序绘图的硬件有哪些

![f](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/v3/android_graphic_v3_mi10_dismantle.png)

*图片来源：https://www.ednchina.com/technews/12082_3.html*

这是一张小米11的拆解图

从左到右分别是屏幕、相机模组、主板IC以及手机外壳

![android_graphic_v3_mi10_mainboard](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/v3/android_graphic_v3_mi10_mainboard.png)

*图片来源：https://www.laoyaoba.com/n/780219*

小米11使用的是高通的骁龙888处理器，内存使用的是来自镁光的LPDDR5 8GB芯片（*图1/2部分，处理器和内存使用堆叠封装，俯视图它俩是叠在一起的*）

剩下按序号分别是：海力士128GB闪存芯片、高通的射频收发芯片、高通WiFi6/BT芯片、两颗高通的快充芯片、伏达的无线充电芯片等等

高通骁龙888 SOC内部集成了Kryo 680 CPU、Adreno 660 GPU、Spectra 580 ISP、X60 5G等多个处理器芯片

![android_graphic_v3_snapdragon_888](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/v3/android_graphic_v3_snapdragon_888.jpeg)

图片来源：https://www.dpreview.com/news/2969199244/qualcomm-snapdragon-888-soc

其中，Adreno 660 GPU是我们需要关心的重点，因为它封装了图形系统中经常要使用到的两块芯片：GPU（Graphics Processing Unit）和DPU（Display Processing Unit）

![android_graphic_v3_adreno660](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/v3/android_graphic_v3_adreno660.jpeg)

*图片来源：https://www.dpreview.com/news/2969199244/qualcomm-snapdragon-888-soc*

##### 图形渲染驱动

###### 1. 什么是渲染

渲染是计算机图形学中的最重要的研究课题之一，也是图形系统中必不可少的一部分

一幅图像的显示必须要经过渲染、合成、送显这三个阶段

其工作原理非常复杂，我们通过一个例子来感受什么是渲染：

我们知道，那么什么是渲染？

举个例子来解释GPU的渲染工作：

我向系统申请了一块10*10大小的图层内存，单位是像素，接着想在图层上画点东西

> 1. **把图层染成绿的**
> 2. **从左上（0,0）到右下（10,10）画一条宽度为1颜色为红色的直线**
> 3. **以坐标点（5,5）为中心，画一个半径为3的实心圆，颜色要蓝色**

好了，接下来CPU会把我的这些绘图指令同步给GPU去执行渲染任务，一起同步过去的还有图层的内存描述符

GPU渲染工作完成后，我就能得到10*10大小的二维数组（还是原先那块内存），数组中的每个元素保存着坐标点的颜色信息、深度信息，对应着将来要显示到屏幕上的一个个像素点

![android_graphic_v3_gpu_draw](/Users/bob/Desktop/Bob/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/v3/android_graphic_v3_gpu_draw.png)

*图片来源：自己画的*

在一块固定大小的图层中，执行一系列的绘图指令，执行完成以后我能将这块内存交给屏幕去显示，这个就叫做渲染

图像渲染是一个非常复杂的话题

渲染和绘制是同一个意思，对应单词都是Render

具体的渲染过程和实现原理可以看[《渲染管线的三大阶段》](https://zhuanlan.zhihu.com/p/101908082)这篇文章，本小节我们主要理解GPU的渲染工作是做什么的

###### 2. 什么是GPU

OpenGL ES

OpenGL ES是一个由[Khronos组织](http://www.khronos.org/)制定并维护的开发规范，类似的协议还有Vulkan、DirectX

它规定了每个函数该如何执行，以及它们的输出值，至于具体每个函数是如何实现，由OpenGL库的开发者自行决定，实际的OpenGL库的开发者通常是显卡的生产商

比如我们打开[Adreno 660 GPU](https://chiptechie.com/mobile-gpu/qualcomm-adreno-660/)的介绍页面，可以看到这块GPU芯片支持OpenGL ES 3.2版本、OpenCL 2.0版本和Vulkan 1.1版本

另外，它还支持了微软家的DirectX，这就意味着在安装了Windows ARM版的手机中，应用程序也可以使用骁龙GPU来加速图形的渲染

更多关于GPU和OpenGL ES的介绍请点击[[这里]](https://cloud.tencent.com/developer/article/1756011)

###### 3. 什么是硬件加速

Android硬件加速的一些问题和错误：https://blog.csdn.net/icyfox_bupt/article/details/18732001

对于应用开发者是无感的

早些年对硬件加速都是又爱又恨的，好的方面是开启硬件加速后页面的确会流畅许多，坏的方面是在某些机型上页面显示可能会有些问题

游戏开发，普通的View或者自定义控件都是使用Canvas来完成绘图工作，

##### 图形合成驱动

聊完了图形的渲染，下一步就到图像的合成阶段

虽然GPU也可以用来做合成工作，但现阶段绝大多数的移动设备中，执行合成任务的都是DPU

###### 1. 什么是合成

在介绍DPU之前，我们需要先来了解什么是合成

![android_graphic_v3_hwc_finally](/Users/bob/Desktop/Bob/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/v3/android_graphic_v3_hwc_finally.png)

*图片来源：https://blog.zhoujinjian.cn/posts/20210810*

这是一张launcher桌面的截屏，它是由“壁纸”、“顶部的状态栏”、“桌面的应用列表”以及“底部导航栏”这4个图层组成

壁纸图层：

![android_graphic_v3_hwc_wallpaper](/Users/bob/Desktop/Bob/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/v3/android_graphic_v3_hwc_wallpaper.png)

*图片来源：https://blog.zhoujinjian.cn/posts/20210810*

顶部状态栏图层（很小的一个横条）：

![android_graphic_v3_hwc_statusbar](/Users/bob/Desktop/Bob/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/v3/android_graphic_v3_hwc_statusbar.png)

*图片来源：https://blog.zhoujinjian.cn/posts/20210810*

桌面应用列表：

![android_graphic_v3_hwc_launcher](/Users/bob/Desktop/Bob/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/v3/android_graphic_v3_hwc_launcher.png)

*图片来源：https://blog.zhoujinjian.cn/posts/20210810*

底部导航栏：

![android_graphic_v3_hwc_navigationbar](/Users/bob/Desktop/Bob/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/v3/android_graphic_v3_hwc_navigationbar.png)

*图片来源：https://blog.zhoujinjian.cn/posts/20210810*

在上一小节我们了解了GPU的职责是“图层渲染”，上面这4个图层就是GPU渲染出来的成果

**每个图层渲染完成以后，理论上可以直接送到屏幕上去显示了**

**但是，除了全屏播放视频的场景外，大多数情况下屏幕上都不止一个图层**

**一旦图层与图层之间发生重叠（比如launcher的状态栏、应用列表和导航栏这3个图层都是叠加在壁纸图层的上面）**

**重叠部分的像素颜色就需要重新计算**

**将多个图层合并成一个图层的过程，被称为“合成”**

###### 2. 什么是DPU

合成的工作本质上还是渲染，所以这原先其实是GPU的活

但是，图层合成的过程中是不需要3D渲染的，因为早在“图层渲染”那一步GPU就完成了所有的3D渲染工作

这样的话为合成流程单独配置一颗2D渲染引擎就OK了，目前承担这一责任的就是DPU了

DPU的全称是Display Processor Unit，通常被封装在GPU模块当中

其最主要的功能是将GPU渲染完成的图层合并输出到屏幕，对于图层重叠的部分，DPU会自动计算出“脏矩形”并更新像素颜色变化

如下图，Arm Mali-DP550这款DPU最多能够支持7层的合成任务

![android_graphic_v3_mali_dp550](/Users/bob/Desktop/Bob/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/v3/android_graphic_v3_mali_dp550.jpg)

*图片来源：https://community.arm.com/cn/f/discussions/6104/arm-mali-gpu*

###### 3. 什么是HWC

当然，合成的工作也可以不放在DPU中，厂商可以选择在板子上加一块2D渲染芯片，专门用来执行合成任务

[Hardware Composer](https://source.android.com/devices/graphics/hwc)就是专门用来定义合成工作的接口对象，它是[Android Hardware Abstraction Layer（HAL）](https://source.android.com/devices/architecture/hal-types?hl=zh-cn)硬件抽象层的成员之一

HWC不在乎厂商使用的是DPU还是其他的2D渲染芯片，厂商只需要实现HWC的接口定义即可

我们来看Google官网对于HWC的定义：

![android_graphic_v3_developer_hwc](/Users/bob/Desktop/Bob/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/v3/android_graphic_v3_developer_hwc.png)

*图片来源：https://source.android.com/devices/graphics/hwc*

“每个字都认识就是不知道在说什么”系列，我把这段话按照标号翻译一下：

1. 用DPU做合成比GPU要高效

2. 第二点比较重要，里面包含了hwc的执行逻辑，大致流程是这样：

   > sf进程：有6个渲染好的图层过来了，我全部塞给你，你自个儿处理完去送显？
   >
   > hwc：不行，我最多只能处理4个图层，我把其中最简单的3个图层标记为GPU合成了，你把这3个合并成一个图层再给我吧
   >
   > sf进程：调用GPU完成另外3个图层的合成工作，并将合并后的图层交给hwc

3. 理论上如果屏幕内容没有发生变化，sf不应该走合成流程，第三点我不是很理解想要表达什么意思

4. 超出hwc能力的图层会调用GPU合成，如果应用的图层太多会对性能产生影响，比如APP弹窗过多，每个Dialog都算一个图层

关于第二点我们可以使用adb shell dumpsys SurfaceFlinger命令查看当前图层的合成方式，DEVICE表示HWC合成，CLIENT表示GPU合成

我是图片

另外，除了合成工作，hwc还负责送显以及发送Vsync信号，绝大多数情况下hwc的实现者是DPU，而DPU控制着屏幕的显示刷新

关于送显部分可以看何小龙的[[DRM系列]](https://blog.csdn.net/hexiaolong2009/article/details/83720940)进行学习

Android中各子系统通常不会直接基于Linux驱动来实现，而是由HAL层间接引用底层架构，在显示系统中也同样如此

好了，hwc的部分到这里先告一段落，我们来总结一下“硬件驱动”小节的内容

本章节的重点是了解什么是渲染和合成以及渲染/合成使用的硬件设备是什么，渲染和合成之间的区别：

- 渲染，关注的是单个图层的内容，在当前图层的坐标系中，每个坐标点应该显示什么颜色
- 合成，关注的是多个图层的内容，将多个图层重新计算后得到一个图层，参考ps的合并图层功能

还有一点要说明，渲染和合成对应的GPU和HWC驱动都由OEM厂商提供，也就是高通、ARM这些SOC厂商

如果你的设备中没有GPU和DPU也没关系，Google为所有的驱动都提供了默认实现，也就是CPU模拟，这也是为什么虚拟机能运行的原因

至此，渲染、合成、送显三个阶段所需的硬件部分已经介绍完成，接下来我们一起看看Google为图形系统准备了哪些软件组件库

#### 2、Google组件库

所谓的组件库更多是对数据结构的封装，比如CPU、GPU和HWC要共享同一块内存，那就需要一种格式让它们都能识别这块内存

另外还需要一种机制来约束它们的行为，防止某个硬件在使用过程中内存数据被其他硬件更改，保证数据安全

Google为图形系统提供了[[libui.so]](http://www.aospxref.com/android-7.1.2_r39/xref/frameworks/native/libs/ui/)和[[libgui.so]](http://www.aospxref.com/android-7.1.2_r39/xref/frameworks/native/libs/gui/)两个库，接下来一起来看看这两个库里面分别有什么

##### libui组件库

###### 1. 什么是GraphicBuffer

GraphicBuffer是整个图形系统的核心，所以的渲染操作都将在此对象上进行，包括同步给GPU和HWC

每当应用有显示需求时，应用会向系统申请一块GraphicBuffer内存，这块内存将会共享给GPU用于执行渲染工作，接着会同步给HWC用于合成和显示

我们可以把GraphicBuffer对象看做是一个个渲染完成以后的图层，对应1.1.2.1小节中launcher的各个图层

更多关于GraphicBuffer的介绍请点击[[这里]](https://www.kancloud.cn/alex_wsc/android_depp/412982)

###### 2. 什么是Fence机制

GraphicBuffer对象的一个完整的生命周期大概是这样：

- 渲染阶段：应用有绘图需求了，由GPU分配一块内存给应用，应用调用GPU执行绘图，此时使用者是GPU
- 合成阶段：GPU渲染完成后将图层传递给sf进程，sf进程决定由谁来合成，hwc或者GPU
  - 如果使用GPU合成，那么此时buffer的使用者依旧是GPU
  - 如果使用hwc合成，那么此时buffer的使用者是hwc
- 显示阶段：所有的buffer在此阶段的使用者都是hwc，因为hwc控制着显示芯片

从生命周期可以看出GraphicBuffer对象在流转的过程中，会被GPU、CPU、DPU三个不同的硬件访问

如果同一块内存能够被多个硬件设备访问，就需要一个同步机制，在Android图形系统中，Fence机制就是用来保证跨硬件访问时的数据安全

Fence的实现原理可以类比Java的synchronized互斥锁机制，我们也可以把Fence理解为一把硬件的互斥锁

每个需要访问GraphicBuffer的角色，在使用前都要检查这把锁是否signaled了才能进行安全的操作，否则就要等待

- active状态，该GraphicBuffer正在被占用
- signaled状态，表明不再控制buffer

更多关于Fence同步机制的介绍请点击[[这里]](https://blog.csdn.net/jinzhuojun/article/details/39698317)

###### 3. 什么是Gralloc

Gralloc是Android中负责申请和释放GraphicBuffer的HAL层模块

移动设备的内部空间寸土寸金，显然不大可能像PC一样给GPU单独配个显存，移动端的"图形内存"都被分配在运行内存中

为了防止图形内存被滥用，Google抽象出Gralloc模块，规定了所有图形内存的申请与释放都需要通过该模块，以此来规范图形内存的使用

> *注意：Fence机制和Gralloc机制并不属于libui组件库，把它俩分到libui库展示是因为GraphicBuffer需要它们*

更多关于Gralloc机制的介绍请点击[[这里]](https://windrunnerlihuan.com/2017/03/12/Android-SurfaceFlinger-%E5%AD%A6%E4%B9%A0%E4%B9%8B%E8%B7%AF-%E4%B8%80-Android%E5%9B%BE%E5%BD%A2%E6%98%BE%E7%A4%BA%E4%B9%8BHAL%E5%B1%82Gralloc%E6%A8%A1%E5%9D%97%E5%AE%9E%E7%8E%B0/)

##### libgui组件库

越接近上层业务设计上就越复杂，libgui库虽说还没到业务层，但它里面的组件大多是对GraphicBuffer对象的封装以满足不同的业务需求

###### 1. 什么是BufferQueue

在Android 4.1（Project Butter）中引入了BufferQueue，它是黄油计划中“Triple Buffer”的执行者

黄油计划的重点是“Drawing with VSync”，也就是把Vsync信号同步给APP进程

这样做的目的是将渲染和合成分成两步来执行，减少一个Vsync周期内的任务量，以降低丢帧发生的概率

回到本章的主题BufferQueue，从名称就可以看出来，它是一个封装了GraphicBuffer的队列

BufferQueue对外提供了出列/入列的接口，还为GraphicBuffer包装了几种不同的状态，它们分别是：

> - FREE：闲置状态，任何进程都可以获取该buffer进行操作，通常表示为APP进程可以申请使用的内存
> - DEQUEUED：出列状态，通常是APP进程在绘图，使用者是GPU
> - QUEUED：入列状态，表示APP绘图已经完成，等待从队列取出执行下一步合成，没有使用者
> - ACQUIRED：锁定状态，通常表示sf进程从队列取出，正在做合成工作，此时使用者可能是hwc也有可能是GPU
> - SHARED：共享状态，7.0版本加入的新状态，没找到相关介绍资料，合成工作完成以后共享给录屏软件？

**BufferQueue使用了[[生产者/消费者模式]](https://juejin.cn/post/7072263857015619621#heading-16)，在绝大多数的情况下，APP作为生产者，sf进程作为消费者，它们俩共同操作一个buffer队列**

**简单描述一下生产者消费者操作队列时状态转换的过程：**

> 生产者：APP进程
>
> 1、producer->dequeueBuffer()
>
> ​	 从队列取出一个状态为“FREE”的buffer，此时该buffer状态变化为：FREE->DEQUEUED
>
> 2、producer->queueBuffer()
>
> ​	 将渲染完成的buffer入列，此时该buffer状态变化为：DEQUEUED->QUEUED
>
> 消费者：sf进程
>
> 1、consumer->acquireBuffer()
>
> ​	 从队列中取出一个状态为“QUEUED”的渲染完的buffer准备去合成送显，此时该buffer的状态变化为：QUEUED->ACQUIRED
>
> 2、consumer->releaseBuffer()
>
> ​	 buffer内容已经显示过了，可以重新入列给APP使用了，此时该buffer的状态变化为：ACQUIRED->FREE

**一个buffer的一生，就是在不断地循环FREE->DEQUEUED->QUEUED->ACQUIRED->FREE这个过程，中间有任何一个环节出现延迟，反应到屏幕上就是应用出现了卡顿**

> *BufferQueue核心代码由BufferQueueCore、BufferQueueProducer、BufferQueueConsumer这3个类组成*
>
> *为了避免引入过多的角色导致文章的阅读体验下降，在后续的章节中，我会将这几个类统称为BufferQueue，包括接下来会出现的Surface、EventThread都是如此，只保留主干*

更多关于BufferQueue的介绍请点击[[这里]](https://zhuanlan.zhihu.com/p/62813895)

###### 2. 什么是Surface

Surface是离应用开发者最近的一个类，在Android Framework窗口实现里

每一个Window其实都会对应一个Surface，我们日常使用的Activity、Dialog、Toast这些都是Window

Surface中持有BufferQueue的引用，因为Surface通常作为buffer的生产者，所以它只封装了出列和入列两个方法

每个egl对象

作为surface

Surface是ANativeWindow的子类，也是ANativeWindow的具体实现

对于用户空间来说，一个Surface意味着图层，每个图层都会拥有一个BufferQueue



###### 3. 什么是DisplayEventReceiver

DisplayEventReceiver本来不想加入到文章中，写到后面发现这哥们不介绍的话没法向下进行了

DisplayEventReceiver成员看起来有点面生，但提到Choreographer我相信大部分读者应该都认识

简单来说，DisplayEventReceiver让Choreographer对象拥有了感知Vsync信号的能力，DisplayEventReceiver是Choreographer中的一个成员变量

关于DisplayEventReceiver更多细节请点击[这里](https://lishuaiqi.top/2018/07/15/Choreographer-1-choreographerAnalysize/)

#### 分割线

呼~

至此，libui、libgui两个Google组件库基本介绍完了，我们来回顾一下本章节内容：

在硬件驱动中我们认识了GPU和DPU，在Google低级别组件库中我们认识了GraphicBuffer、BufferQueue和Surface

本章节的目的是认识硬件以及低级别组件库，了解提供了什么功能，理解它们在系统中的位置，

厂商驱动库和Google组件库作为Android图形系统基石，共同组建了庞大的图形子系统，为图形系统强有力的支撑

接下来我们开始分析系统各个关键进程的启动流程，看看系统在开机到App请求Vsync信号之间都做了哪些工作

当我们把硬件驱动的功能以及组件库这些基础概念理解清楚，才能在后续阅读源码的过程中做到有的放矢

只有把这些基础概念理解清楚，才能再阅读源码的过程中更加有的放矢

我是图片

### 二、请求Vsync信号

厂商驱动库和Google组件库作为Android图形系统基石，共同组建了庞大的图形子系统，为整个图形系统强有力的支撑

对于操作系统来说，得让这一切动起来才有意义

本章节运行

在Android图形系统中，surface_flinger进程和system_server进程管理着整个系统的运转，其中：

- surface_flinger进程负责接受来自APP进程的图形数据，调用hwc进行合成并完成最终的送显
- system_server进程负责管理有哪些APP进程可以进行绘图操作以及管理各个图层的优先级

它们都是在Vsync信号的驱使下进行工作，在接下来的章节中我们将会分析这两大系统进程在Vsync信号到来时做了哪些事情

在此之前，我们需要先了解系统进程是如何请求并最终接受到Vsync信号的？

*全文基于[Android 7.1.2](http://www.aospxref.com/android-7.1.2_r39/xref/)版本*

#### 1、启动surface_flinger进程

Android 7.0以后对init.rc脚本进行了重构，sf进程的启动从[init.rc](http://androidxref.com/6.0.1_r10/xref/system/core/rootdir/init.rc)文件配置到了[surfaceflinger.rc](http://www.aospxref.com/android-7.1.2_r39/xref/frameworks/native/services/surfaceflinger/surfaceflinger.rc)文件，依旧由init进程拉起

main_surfaceflinger的入口函数：

```c++
/frameworks/native/services/surfaceflinger/main_surfaceflinger.cpp
  
int main(int, char**) {

    // instantiate surfaceflinger
    sp<SurfaceFlinger> flinger = new SurfaceFlinger();

    // initialize before clients can connect
    flinger->init();

    // publish surface flinger
    sp<IServiceManager> sm(defaultServiceManager());
    sm->addService(String16(SurfaceFlinger::getServiceName()), flinger, false);

    // publish GpuService
    sp<GpuService> gpuservice = new GpuService();
    sm->addService(String16(GpuService::SERVICE_NAME), gpuservice, false);

    // run surface flinger in this thread
    flinger->run();

    return 0;
}
```

main()函数的主要作用是创建SurfaceFlinger对象并初始化，要完成的工作都在SurfaceFlinger对象中的init()函数中：

```c++
/frameworks/native/services/surfaceflinger/SurfaceFlinger.cpp
  
//利用RefBase首次引用机制来做一些初始化工作，这里是初始化Handler机制
void SurfaceFlinger::onFirstRef()
{
    mEventQueue.init(this);
}

//初始化
void SurfaceFlinger::init() {
    {
        // initialize EGL for the default display
      	//初始化OpenGL 图形库相关配置
        mEGLDisplay = eglGetDisplay(EGL_DEFAULT_DISPLAY);
        eglInitialize(mEGLDisplay, NULL, NULL);

        // start the EventThread
        //启动事件分发线程，提供给APP进程注册事件回调
        sp<VSyncSource> vsyncSrc = new DispSyncSource(&mPrimaryDispSync,
                vsyncPhaseOffsetNs, true, "app");
        mEventThread = new EventThread(vsyncSrc, *this);
        //又启动一个事件分发线程，并将自己注册到hwc中，用于sf进程监听vsync信号
        sp<VSyncSource> sfVsyncSrc = new DispSyncSource(&mPrimaryDispSync,
                sfVsyncPhaseOffsetNs, true, "sf");
        mSFEventThread = new EventThread(sfVsyncSrc, *this);
        mEventQueue.setEventThread(mSFEventThread);

        // Get a RenderEngine for the given display / config (can't fail)
        mRenderEngine = RenderEngine::create(mEGLDisplay,
                HAL_PIXEL_FORMAT_RGBA_8888);
    }

    // Drop the state lock while we initialize the hardware composer. We drop
    // the lock because on creation, it will call back into SurfaceFlinger to
    // initialize the primary display.
    //初始化HWC对象，加载hwcomposer.so的动作在HWComposer的初始化函数中
    mHwc = new HWComposer(this);
    //将自己注册到hwc的回调函数中，其内部分别调用registerHotplugCallback、registerRefreshCallback、registerVsyncCallback三个回调方法
    mHwc->setEventHandler(static_cast<HWComposer::EventHandler*>(this));

    // retrieve the EGL context that was selected/created
    mEGLContext = mRenderEngine->getEGLContext();

    // make the GLContext current so that we can create textures when creating
    // Layers (which may happens before we render something)
    getDefaultDisplayDevice()->makeCurrent(mEGLDisplay, mEGLContext);

    mEventControlThread = new EventControlThread(this);
    mEventControlThread->run("EventControl", PRIORITY_URGENT_DISPLAY);

    // set initial conditions (e.g. unblank default device)
    initializeDisplays();

}

void SurfaceFlinger::run() {
    do {
        waitForEvent();
    } while (true);
}

//等待消息唤醒
void SurfaceFlinger::waitForEvent() {
    do {
        IPCThreadState::self()->flushCommands();
        int32_t ret = mLooper->pollOnce(-1);
        } while (true);
}
```

SurfaceFlinger初始化流程稍微有点长，我们一步步拆开来看

##### 初始化消息队列

```c++
/frameworks/native/services/surfaceflinger/SurfaceFlinger.cpp
//利用RefBase首次引用机制来做一些初始化工作，这里是初始化Handler机制
void SurfaceFlinger::onFirstRef()
{
    mEventQueue.init(this);
}

/frameworks/native/services/surfaceflinger/MessageQueue.cpp
void MessageQueue::init(const sp<SurfaceFlinger>& flinger)
{
    mFlinger = flinger;
    mLooper = new Looper(true);
    mHandler = new Handler(*this);
}
```

在SurfaceFlinger中，利用了RefBase首次引用机制来做一些初始化工作，这里是初始化Handler机制

sf进程在Android图形系统中，作为图层的消费者（不考虑截屏/录屏），负责调用hwc进行图层合成

这里的消息队列负责处理两件事：

Android是消息驱动的操作系统，APP的UI线程的设计如此，native的系统进程也是如此

在sf进程中，消息队列主要处理两件事：提供给APP进程发送“我要刷新”的消息以及接受来自HWC的Vsync信号开始执行合成工作

##### 初始化EGL环境

```c++
/frameworks/native/services/surfaceflinger/SurfaceFlinger.cpp
void SurfaceFlinger::init() {
    {
        // initialize EGL for the default display
        mEGLDisplay = eglGetDisplay(EGL_DEFAULT_DISPLAY);
        eglInitialize(mEGLDisplay, NULL, NULL);
      	...
        // Get a RenderEngine for the given display / config (can't fail)
        mRenderEngine = RenderEngine::create(mEGLDisplay,
                HAL_PIXEL_FORMAT_RGBA_8888);
    }

    // retrieve the EGL context that was selected/created
    mEGLContext = mRenderEngine->getEGLContext();

}
```

初始化工作的第二步是配置EGL环境，eglGetDisplay()方法最终会调用到位于/frameworks/native/opengl/libs/EGL/Loader.cpp的load_driver()方法来加载libEGL.so

[EGL](https://www.khronos.org/registry/EGL/)是OpenGL ES在每一部Android设备中的具体实现，这一步执行完成以后用户空间就可以调用egl进行绘图，接着调用eglSwapBuffers()方法将其送到屏幕显示

##### 启动事件分发线程

```c++
/frameworks/native/services/surfaceflinger/SurfaceFlinger.cpp
void SurfaceFlinger::init() {
    {
      	...
        // start the EventThread
        //启动事件分发线程，提供给APP进程注册事件回调
        sp<VSyncSource> vsyncSrc = new DispSyncSource(&mPrimaryDispSync,
                vsyncPhaseOffsetNs, true, "app");
        mEventThread = new EventThread(vsyncSrc, *this);
        //又启动一个事件分发线程，并将自己注册到hwc中，用于sf进程监听vsync信号
        sp<VSyncSource> sfVsyncSrc = new DispSyncSource(&mPrimaryDispSync,
                sfVsyncPhaseOffsetNs, true, "sf");
        mSFEventThread = new EventThread(sfVsyncSrc, *this);
        mEventQueue.setEventThread(mSFEventThread);

    }

    mEventControlThread = new EventControlThread(this);
    mEventControlThread->run("EventControl", PRIORITY_URGENT_DISPLAY);

}
```

###### 1. 如何暂停接收vsync信号？

理解这两个线程的作用非常重要，我们来思考一个问题：当页面没有发生任何变化时，APP进程会走渲染流程吗？同理，sf进程会走合成流程吗？

答案是不会，为什么？

APP进程由渲染需求或者sf进程由合成需求才会打开接收vsync信号的开关

###### 2. 理解DispSync模型

在Android图形系统中，Vsync信号不管是硬件产生还是软件模拟，最终都交由DispSync来管理

本章节了解hardware的信号经由DispSync转发后才会到达sf进程和app进程即可，想要完全理解DispSync模型，建议阅读这几篇文章

[《Analyze AOSP vsync model》](https://utzcoz.github.io/2020/05/02/Analyze-AOSP-vsync-model.html)

[《DispSync解析》](http://echuang54.blogspot.com/2015/01/dispsync.html)

[《Android DispSync 详解》](https://simowce.github.io/all-about-dispsync/)

[《Android R Vsync相关梳理》](https://wizzie.top/Blog/2021/04/14/2021/210414_android_VsyncStudy)

[《Android SurfaceFlinger SW Vsync模型》](https://www.jianshu.com/p/d3e4b1805c92)

vsyncPhaseOffsetNs和sfVsyncPhaseOffsetNs

DispSync管理着vsync信号的出口，不管是

DispSync训练完成以后，

##### 初始化HWComposer

```c++
/frameworks/native/services/surfaceflinger/SurfaceFlinger.cpp
void SurfaceFlinger::init() {
  	...
    // Drop the state lock while we initialize the hardware composer. We drop
    // the lock because on creation, it will call back into SurfaceFlinger to
    // initialize the primary display.
    //初始化HWC对象，加载hwcomposer.so的动作在HWComposer的初始化函数中
    mHwc = new HWComposer(this);
    //将自己注册到hwc的回调函数中，其内部分别调用registerHotplugCallback、registerRefreshCallback、registerVsyncCallback三个回调方法
    mHwc->setEventHandler(static_cast<HWComposer::EventHandler*>(this));
}
```

mHwc可以说是sf进程中的核心人物了，不管是接受硬件的vsync信号，还是完成图层合成工作以及最终的送显

##### 进入睡眠 等待唤醒

```c++
/frameworks/native/services/surfaceflinger/main_surfaceflinger.cpp
int main(int, char**) {
  	...
    // run surface flinger in this thread
    flinger->run();
    return 0;
}  

/frameworks/native/services/surfaceflinger/SurfaceFlinger.cpp  
void SurfaceFlinger::run() {
    do {
        waitForEvent();
    } while (true);
}

//等待消息唤醒
void SurfaceFlinger::waitForEvent() {
    do {
        IPCThreadState::self()->flushCommands();
        int32_t ret = mLooper->pollOnce(-1);
        } while (true);
}
```

在完成所有初始化工作后，sf进程进入睡眠状态，等待唤醒

整个调用流程可以简化为

我是图片

> 之前写的，总的来说，sf进程在图形处理相关方面一共做了三件事
>
> 1. 注册vsync信号回调，如果硬件不支持，启用VSyncThread线程模拟
> 2. 启动vsync信号线程（如果硬件支持的话）
> 3. 初始化HWComposer对象，并且注册HWC回调
> 4. 提供链接方法，等待APP端跨进程调用
> 5. 睡觉，等待消息
>
> 关于第4点要着重强调一遍，APP进程申请Surface成功后，经过一系列的方法调用，最终会在sf进程中创建对应的Layer，这个Layer会保存在mLayers中
>
> 注意，每个版本的surfaceflinger代码都在变，对不上的话可以检查源码版本

#### 2、启动system_server进程

[system_server](http://www.aospxref.com/android-7.1.2_r39/xref/frameworks/base/services/java/com/android/server/SystemServer.java)进程中运行着AMS、WMS等常见服务，这些服务都是由java代码实现的，需要一个jvm的运行环境

因此，system_server进程必须要等到zygote进程创建DVM/ART虚拟机以后，再由zygote进程fork而来：

```java
/frameworks/base/services/java/com/android/server/SystemServer.java
  
/**
 * The main entry point from zygote.
 */
public static void main(String[] args) {
    new SystemServer().run();
}

private void run() {
  	...
    Looper.prepareMainLooper();
  	startBootstrapServices();
		startOtherServices();
  	// Loop forever.
    Looper.loop();
}  

//启动AMS
private void startBootstrapServices() {
  	// Activity manager runs the show.
 	 mActivityManagerService = mSystemServiceManager.startService().getService();
}  

//启动WMS
private void startOtherServices() {
  	WindowManagerService.main();//ps：同样是系统服务，待遇差距为什么这么大？我wms差哪了？不服.jpg
}  

```

zygote进程是如何启动并最终拉起system_server进程这里不展开，我们重点关注SystemServer的run函数

##### 初始化ActivityManagerService

AMS是Android系统最为核心的服务之一，其职责包括四大核心组件与进程的管理



从服务的名称也可以看出来，主要是管理Activity

所有的页面都是以Activity作为页面的承载，四大组件是Android系统为应用开发者提供

提供给上层业务开发，也就是为应用开发提供接口

在图形系统中AMS有什么作用呢？管理页面是否可见，举个例子：

当Activity完全不可见时，页面有个无限循环动画在执行，操作系统会绘制它吗？

这也是为什么当我们启动一个透明态的Activity时，原本的Activity只会执行onPause()而不执行onStop()回调方法的原因

##### 初始化WindowManagerService

理论上，可以不通过AMS实现页面展示

在本小节中聊聊WMS在图形系统当中的作用

activity、dialog、toast等等

我们可以跳过AMS直接向WMS添加一个View，这个View能够

思考一个问题，在activity中显示一个dialog弹窗，虽然启动dialog需要activity的上下文，这个弹窗显然并不归activity管理

用户点击返回键时，接收事件的肯定是最上层的弹窗，接着dismiss()

##### 进入睡眠 等待唤醒

进入循环，保证system_server进程不退出

详细的启动流程这里不展开，我们着重关注system_server进程中的AMS和WMS这两个服务



> - AMS负责组件（主要是Activity）的启动、切换、调度工作，简单来说就是管理组件是否有绘制权限，如果应用被切换到后台，就没必要绘制图形了
> - PMS负责为APP分配图层，并确定每个图层的深度，除此以外，PMS还负责分发触摸信号、垂直同步信号等工作



#### 3、启动app进程

代码已经非常精简了，乱不乱，我觉得很乱

接下来上一道硬菜，从ActivityThread.main()到Activity.onCreate()的过程

虽然删减了许多但代码非常长，建议跳过，接下来会分片段分析

有(*在*)时(*摸*)间(*鱼*)的朋友可以通篇阅读，几乎每个方法我都写了注释

没办法越接近上层业务设计就越复杂

> 创建流程
>
> 启动Activity

APP进程和system_server进程一样，都是从zygote进程fork而来

创建过程中的IPC通信，最终会回调到Activity的onCreate()方法

我们直接快进到setContentView以后，初始化以后

onCreate中解析xml文件

关注onCreate以后发生的事情

onResume中可见，显然

APP的启动过程这里同样不展开，对于Activity启动流程不熟悉的同学可以去网上搜索文章看完再回来（不必拘泥于细节，了解大致流程即可）

在APP创建完成以后，会启动AndroidManifest中配置的默认Activity，拉起Activity过程中，一共完成了三件事：

1. 调用setContentView加载视图，创建ViewRootImpl
2. 调用ViewRootImpl.requestLayout()方法请求Vsync信号
3. 主线程Looper.loop()进入休眠，等待Vsync到来

##### 创建ViewRootImpl

Android开发中设置视图不外乎于两种方式：xml文件和Java编码

不管是使用xml还是View对象，都需要调用setContentView()方法将视图绑定到Activity当中：

```java
\frameworks\base\core\java\android\app\Activity.java

public void setContentView(View view) {
    getWindow().setContentView(view);
}

\frameworks\base\core\java\com\android\internal\policy\PhoneWindow.java
@Override
public void setContentView(View view) {
  ...
}

\frameworks\base\core\java\android\view\WindowManagerGlobal.java

public void addView() {
  ...
  root = new ViewRootImpl()//创建了关键的viewrootimpl
  root.setView();
}

frameworks\base\core\java\android\view\ViewRootImpl.java

public void setView(View view) {
    mView = view;//将DecorView保存到ViewRootImpl的成员变量mView中
    requestLayout();//请求vsync信号
  	res = mWindowSession.addToDisplay();//背后又是老长一串调用链，就不展开跟了，大致流程是在wms服务中创建了WindowState对象
}
```

如代码所示，经过一系列的方法调用后，最终会执行ViewRootImpl.setView()方法，将DecorView保存到ViewRootImpl的成员变量mView中；同时，setView()方法会将视图同步给WMS，在WMS中对应创建了一个WindowState对象

除此以外，ViewRootImpl还包含Choreographer对象，它是ViewRootImpl几个关键成员变量之一：

```java
\frameworks\base\core\java\android\view\ViewRootImpl.java
  
public ViewRootImpl(){
  //获取Choreographer单例对象
  mChoreographer = Choreographer.getInstance();
}

/frameworks/base/core/java/android/view/Choreographer.java
  
private Choreographer(Looper looper) {
  mHandler = new FrameHandler(looper);
  //初始化之后通过jni调用到DisplayEventDispatcher初始化，接着创建libgui.so中DisplayEventReciver对象
  //再往下的代码就不跟了，感兴趣的同学可以自己去看，大致流程是创建了一个与sf进程的连接并注册到EventThread线程中，从而获得vsync感知能力
  mDisplayEventReceiver =  new FrameDisplayEventReceiver(looper);
}  
```

ViewRootImpl在构造函数中获取了Choreographer实例对象，紧接着在Choreographer的构造函数中又创建了FrameDisplayEventReceiver对象

FrameDisplayEventReceiver对应的实现是开篇中提到的libgui.so中DisplayEventReciver对象，到这里我们就不向下继续追踪，以免引入过多的角色导致文章的阅读体验下降

总之，FrameDisplayEventReceiver让Choreographer对象拥有了感知Vsync信号的能力

ViewRootImpl另一个需要关注的成员变量是mSurface，它是View能显示一切的基础

```java
\frameworks\base\core\java\android\view\ViewRootImpl.java
  
public final Surface mSurface = new Surface();
```

总结一下在setContentView阶段发生的事情：

- 创建ViewRootImpl对象并将DecorView绑定到mView成员变量中
- 创建Choreographer对象并注册一系列回调方法
- 创建了Surface对象交给ViewRootImpl成员变量mSurface

##### 请求Vsync信号

在ViewRootImpl首次加载视图的时候，需要注意一个细节

ViewRootImpl.requestLayout()

在ViewRootImpl.setView()方法中，

```java
View.invalidate()/requestLayout()
	->
```

invalidate 和 requestLayout最终都会调用到viewrootimpl.scheduleTraversals()方法，在此方法中会调用requestNextVsync()

ViewRootImpl#performTraversals

scheduleTraversals() 



当queue buffer到BufferQueue时终于会触发layer的onFrameAvailable()函数，而该函数会触发一次surfaceflinger的vsync事件。

##### 进入睡眠 等待唤醒

> 之前写的，从zygote进程中fork出APP进程后，WindowManagerGlobal
>
> Activity的创建过程的调用链有点长，这里先忽略掉，在AMS和WMS的通力合作下创建出Activity实例对象
>
> 这个实例在AMS保存为ActivityRecord对象，在WMS中保存为WindowState对象
>
> ViewRootImpl中持有两个非常重要的对象：Choreographer和Surface
>
> Choreographer中也有一个非常重要的对象：DisplayEventReceiver
>
> DisplayEventReceiver完成对gui.so中的DisplayEventReceiver封装，
>
> 还记得DisplayEventReceiver吗？
>
> 这就是为什么Choreographer也能接收到vsync信号的关键
>
> ```c++
> frameworks/native/libs/gui/DisplayEventReceiver.cpp
> DisplayEventReceiver::DisplayEventReceiver() {
>     sp<ISurfaceComposer> sf(ComposerService::getComposerService());
>     if (sf != NULL) {
>         mEventConnection = sf->createDisplayEventConnection();
>         if (mEventConnection != NULL) {
>             mDataChannel = mEventConnection->getDataChannel();
>         }
>     }
> }
> ```
>
> ViewRootImpl本身由WMS管理，一个Activity对应一个ViewRootImpl
>
> 从这里我们也可以两位
>
> AMS负责管理组件状态，WMS负责管理视图状态
>
> 我们这里一笔带过，简单来说是通过AMS创建了

AMS创建了Record

### 三、接收Vsync信号

好了，万事俱备，只欠东风，APP进程和SF进程都一同等待着Vsync信号的到来

Drawing with VSync

#### 1、APP进程执行绘制

创建Surface，创建BufferQueue，SF对应创建Layer，每一个Surface创建成功后，经过一系列的方法调用，最终会被同步到sf进程，并创建Layer，就将会被把书翻到第一章第二节的，surface

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

unlockCanvasAndPost()`or `eglSwapBuffers()（取决于开发者使用2D绘图API或者3D绘图API）

最后，如果使用2D绘图API，调用unlockCanvasAndPost()方法将graphicbuffer入列

如果使用3D绘图API，调用eglSwapBuffers()方法入列

对于大部分应用开发工程师来说，最终调用的都是unlockCanvasAndPost()方法

##### 发送同步消息屏障

为了避免屏幕发生撕裂，vsync信号早在1.6版本就已经存在，去源码搜索eventhub.cpp可以看到

Google在Android 4.1发布的黄油计划之所以广为人知，是因为vsync同步给了APP进程，通过代码逻辑将渲染、合成、送显

渲染合成显示各占一个buffer，这也是Triple buffering的由来

除此以外，为了配合chro，handler机制加入了异步消息和同步屏障

Google在Android 5.0加入了renderthread，更进一步优化了图形，ui线程负责onlayout/onmeausre，在ondraw阶段记录下渲染命令，接着同步给RenderThread



##### 执行绘制工作

Android 5.0以后的View体系中加入了RenderThread，也就是渲染线程

支持硬件加速的情况下，渲染过程和UI线程分离了，UI线程负责将onDraw中的绘制命令（被称为RenderNode）收集到DisplayList，接着调用syncAndDrawFrame()方法将命令同步给RenderThread，随后执行渲染任务

引入渲染线程的好处有两个：

一是可以防止重复绘制，比如

二是留给UI线程更多的时间来处理messagequeue中的消息，

##### 取消同步消息屏障

##### 特殊情况：SurfaceView

在游戏开发或其他需要展示3D图形时，多数情况是使用SurfaceView来绘制

SurfaceView和普通View最大的区别是拥有“自主上帧”的权利，什么意思呢？

我们都知道SurfaceView拥有单独的一块Surface，

在有绘图需求时，我们可以调用lockCanvas()/eglCreateWindowSurface()获取一块surface

绘制完成以后，调用unlockCanvasAndPost()/eglSwapBuffers()将graphicbuffer入列，提交给sf进程，等待下一次vsync信号到来

我们可以选择使用Canvas在这块单独的Surface进行绘制，

使用他们的好处是可以，什么意思呢

2D场景使用canvas，3D场景使用egl

自行调用eglSwapBuffers进行入列

比如王者荣耀早期最高只有30帧，也就是说王者荣耀每一帧画面绘制时间需要2个vsync周期，之后才会提交给sf进行合成

gl是拥有egl环境的surfaceview，同样适用于

texture暂时没研究，不敢妄下结论

同理，适用于glsurfaceview拥有自己的surface的视图组件

注意，无论如何，因为sf进程接受vsync的指导的原因，APP的输出帧率永远小于等于屏幕的刷新率，APP进程提交的画面总是在下一次vsync信号到来时才能被输送到屏幕显示

#### 2、SurfaceFlinger进程执行合成

mLayers对象保存着所有的图层，APP进程中申请的graphicbuffer也是驻留在SurfaceFlinger这边的进程中

sf负责合成工作，大致的流程是询问

关键是两个回调，具体细节这里就不展开

##### invalidate

##### vsyncCallback

##### 闲聊DispSync设计

offset的设计理念是，用户能够更早的看到画面，绘制工作和合成工作将在一个vsync周期内完成

至此，整个vsync周期发生的都已经完成，我们来梳理显示流程

下面几个问题是我个人在学习图形系统中比较疑惑的点，在此分享希望能够帮助到其他人

如何暂停接收Vsync信号？

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



### 四、结语

作者是应用开发工程师，没有硬件经验，文中难免遗漏甚至错误

并通过EventThread将vsync信号公开给其他进程使用

在一次次的vsync信号驱使下

APP进程和sf进程日复日重复自己的工作，

以sf进程为界限将图形系统一分为二

sf以上，关注的是surface的创建、深度管理等，此时的surface是一块画布

sf以下，关注的是graphicbuffer的流转，此时的surface

最后两张图总结本文的内容

一张是静态图，展示Android图形架构的设计

动态图，表示每次vsync到来时，不同进程的处理方式，以及最重要的，graphicbuffer的流程过程

从显示流程来看，每个层级关注的事情是不一样的

对于低级别组件库来说，提供给上层使用，不涉及任何逻辑，同样也不关心vsync信号

对于sf进程来说，关心的是GraphicBuffer的流转过程，在vsync的指导下，控制者graphicbuffer的消费与释放，更关注我应该什么时候去合成图层

对于system进程来说，服务于开发者，关注的是surface的创建与使用，由于surface封装了graphicbuffer出列/入列的方法，所以本质上也是system进程在管理队列的生产

对于app进程来说，关注的是如何实现蓝湖上的ui

AMS/WMS在图形系统中的作用

WMS管理的窗口类型可以分为三种，应用窗口、子窗口（需要父窗口）和系统窗口

我们的应用属于应用窗口，Dialog则是子窗口，Toast属于系统窗口

创建更新删除工作，还管理者每一个Window的深度Z-Order

Activity/Dialog/Toast/Window之间的区别？

Surface/Layer/GraphicBuffer/ButterQueue之间的联系？



总结一下View的显示流程，分三步走：

- 第一步，系统启动阶段
  1. 启动sf进程，初始化hwc、egl环境等，启动vsync线程并注册回调
  2. 启动zygote进程，初始化jvm环境加载常用jni，最后拉起system进程启动常用的ams、wms服务等
  3. 启动launch，加载
- 第二部，请求vsync信号阶段
  1. 通过chro讲触摸事件同步给APP进程，如果

创建APP进程并加载xml文件

1. wms为activity创建viewrootimpl
2. viewrootimpl持有surface，

时序图就不给大家画了，画了也记不住，每个版本还都不一样

友情提醒，在分析调用链的过程中，时刻谨记当前的方法运行在哪个进程，发送的指令是哪个芯片执行的，这对我们理解图形系统有着非常大的帮助

再次强调一遍，文章中源码版本是7.0

隐藏了非常多的细节

了解了GraphicBuffer的流转过程，也就明白了Android系统的显示流程

总结一下，SurfaceFlinger是Android图形系统的核心进程，在整个图形系统中起承上启下的作用

在Android系统中，sf是关键，通过sf可以申请到一个layer

server进程中管理组件的状态，没有

总结一下vsync到来之前和到来之后发生的事情

Android启动时会创建两大进程，其中常见的ams和wms运行在server进程

当APP启动以后，不管是因为input事件还是fling或者主动发起更新请求，最终都会调用到requestNextVsync()方法中

以大部分应用开发来举例：

从主要角色延伸出来

为了不让文章过于臃肿，文章删减了许多辅助角色

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

Android图形子系统横跨硬件驱动、Linux内核、Framework框架三层，虽然每个模块设计的比较合理，命名比较混乱，但命名真的是过于混乱，以至于稍不留意就迷失在源码当中

这就导致想要理清它们之间的关系变成一件比较困难的事情，好在已经有各位前辈铺好了路

Android图形子系统是最复杂的子系统，没有之一

内容

站在前人的肩膀上，结合着自己的理解，聊一聊对View的显示流程，不当之处多多指正

希望本文能够抛砖引玉，为屏幕前的读者朋友提供一点点帮助

全文完

### 五、参考资料

- [《深入理解Android内核设计思想》- 林学森](https://book.douban.com/subject/25921329/)
- [《深入理解Android 卷III》- 张大伟](https://book.douban.com/subject/26598458/)
- [《Weishu's Notes》- 田维术（太极/两仪作者）](https://weishu.me/)
- [《Android显示系列》- 努比亚团队](https://www.jianshu.com/c/3a4d92743e88)
- [《Systrace系列》- 高爷](https://www.androidperformance.com/2019/05/28/Android-Systrace-About) 
- [《Android 10 Display System源码分析》- ZHOUJINJIAN](https://blog.zhoujinjian.cn/archives/page/2/)
- [《DRM与BufferQueue系列》- 何小龙](https://blog.csdn.net/hexiaolong2009?type=blog)
- [《Android图形显示系列》- 夕阳叹](https://blog.csdn.net/jxt1234and2010/category_2826805.html)
- [《王小二的Android站》- 王小二（TCL王总）](https://www.jianshu.com/u/fd0b722ce11f)
- [《Silence.Slow.Simple专栏》- simowce](https://blog.simowce.com/archives/)
- [《Android SurfaceFlinger 学习之路》- windrunnerlihuan](https://windrunnerlihuan.com/archives/page/2/)
- [《Android 12(S) 图像显示系统》- 二的次方](https://www.cnblogs.com/roger-yu/p/15641545.html)
- [深入理解Flutter的图形图像绘制原理 - OPPO数智技术](https://segmentfault.com/a/1190000038827450)
- [Android性能优化：定性和定位Android图形性能问题 - 飞起来_飞过来](https://juejin.cn/post/7096288511053004830)
