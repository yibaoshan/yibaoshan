Android图形系统（三）系统篇：当我们点击“微信”这个应用后，它是怎么在屏幕上显示出来的？

对于应用开发工程师来说，虽然我们不需要写操作系统代码，但是了解View最终是如何显示到屏幕上还是非常有必要的

本篇是Android图形系列的第三篇文章，在之前的两篇文章中分别介绍了屏幕的“显示原理”和屏幕的“刷新原理”，今天我们来一起学习Android系统的图形架构设计，聊一聊输送到屏幕的画面数据是如何产生的

本文的目标是希望读者朋友建立一个Android图形子系统的框架，因此，文中不会包含太多的方法调用链以及代码逻辑，非Android开发工程师也可以放心食用

> *前排提醒：全文近2万字，建议阅读时长30分钟*

![android_graphic_v3_overview](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/v3/android_graphic_v3_overview.jpg)

# 一、开篇

**“当我们点开‘微信’这个应用后，它是怎么在屏幕上显示出来的？”**

这是一个非常复杂的问题，它的背后包含了由厂商驱动、Linux操作系统、HAL硬件抽象层和Android Framework框架层共同组建的一套非常庞大的Android图形子系统

想要给出这个问题的答案，就必须对Android图形子系统背后的运行流程有所了解

今天，我们从认识Android设备中的硬件开始，自下而上

一起来看看庞大的Android图形子系统是如何组建起来的

## 1、硬件驱动

雷军曾经说过，再复杂的系统设计，也离不开硬件的支持

在文章的开头，我们就先来了解一下：支撑应用程序绘图的硬件有哪些？

![f](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/v3/android_graphic_v3_mi10_dismantle.png)

*图片来源：https://www.ednchina.com/technews/12082_3.html*

上图是一张小米11的拆解图

从左到右分别是：屏幕、相机模组、主板IC以及手机外壳

小米11使用的是高通的”骁龙888“处理器，内存使用的是来自镁光的LPDDR5 8GB芯片（*下图中1/2部分，处理器和内存使用堆叠封装，俯视图它俩是叠在一起的*）

![android_graphic_v3_mi10_mainboard](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/v3/android_graphic_v3_mi10_mainboard.png)

*图片来源：https://www.laoyaoba.com/n/780219*

剩下按序号分别是：海力士128GB闪存芯片、高通的射频收发芯片、高通WiFi6/BT芯片、两颗高通的快充芯片、伏达的无线充电芯片等等，和本篇文章关系不大

回到小米11的SOC处理器部分（图1/2）

高通骁龙888（见下图）内部集成了`Kryo 680 CPU`、`Adreno 660 GPU`、`Spectra 580 ISP`、`X60 5G`等多个处理器芯片

![android_graphic_v3_snapdragon_888](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/v3/android_graphic_v3_snapdragon_888.jpeg)

图片来源：https://www.dpreview.com/news/2969199244/qualcomm-snapdragon-888-soc

在骁龙888封装的众多芯片中，`Adreno 660 GPU`模块（见下图）是我们需要关心的重点

因为它封装了我们在图形系统中经常使用到的两块芯片：

**GPU（Graphics Processing Unit）和DPU（Display Processing Unit）**

![android_graphic_v3_adreno660](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/v3/android_graphic_v3_adreno660.jpeg)

*图片来源：https://www.dpreview.com/news/2969199244/qualcomm-snapdragon-888-soc*

我们都知道，一幅图像的显示必须要经过`渲染`、`合成`、`送显`这三个阶段，才能展现给用户

GPU和DPU就是用于提供给应用程序渲染和合成能力的硬件设备，其中：

- **GPU芯片负责执行图形显示流程中的“渲染”工作**

- **DPU芯片负责执行图形显示流程中的“合成”工作**

接下来的章节中，我们从驱动程序的角度来聊聊什么是渲染，以及什么是合成？

### 图形渲染驱动

#### 1. 什么是渲染

众所周知，渲染工作是由GPU负责，那么在聊GPU之前首先得知道什么是“渲染”？我们来举个例子感受一下

在系统中申请一块`10*10`大小的图层内存，执行下列几条指令：

> **1、把图层背景这玩意染成绿的**
>
> **2、从左上（0,0）到右下（10,10）画一条宽度为1颜色为红色的直线**
>
> **3、以坐标点（5,5）为中心，画一个半径为3的实心圆，颜色要蓝色**

好了，接下来操作系统会把这些“绘图指令”同步给GPU去执行渲染任务

![android_graphic_v3_gpu_draw](/Users/bob/Desktop/Bob/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/v3/android_graphic_v3_gpu_draw.png)

*图片来源：自己画的*

GPU完成渲染工作后，我们将会得到一个大小为`10*10`二维数组，数组中的每个元素都保存着坐标点的颜色信息、深度信息

对应着将来要显示到屏幕上的一个个像素点

**把绘图指令转化为二维像素数组的过程，就叫做“渲染”**

本章节举了一个很简单的2D绘图的小例子，主要目的是让我们了解渲染工作是做什么的

> *图像渲染是一个非常复杂的话题，想要了解渲染实现原理可以点击[[这里]](https://zhuanlan.zhihu.com/p/101908082)*

#### 2. 什么是GPU

GPU全称是GraphicProcessing Unit，中文是图形处理器，其最大的作用就是进行各种绘制计算机图形所需的运算，包括顶点设置、光影、像素操作等

也就是用来执行我们开发者调用的一个个的“绘图指令”

所以，GPU实际上是多组图形函数API的集合，这些函数由硬件驱动实现

硬件厂商们遵循着不同协议的开发规范，比如OpenGL ES、Vulkan等等

![android_graphic_v3_adreno660_specifications](/Users/bob/Desktop/Bob/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/v3/android_graphic_v3_adreno660_specifications.png)

*图片来源：https://chiptechie.com/mobile-gpu/qualcomm-adreno-660/*

上图是[Adreno 660 GPU](https://chiptechie.com/mobile-gpu/qualcomm-adreno-660/)的介绍页面，可以看到小米11使用的这块芯片支持`OpenGL ES 3.2`版本、`OpenCL 2.0`版本和`Vulkan 1.1`版本

另外，660还支持了微软家的`DirectX`，这就意味着在安装了`Windows ARM版`的小米11中，应用程序也可以使用骁龙GPU来加速图形的渲染

> *更多关于GPU的介绍请点击[[这里]](https://cloud.tencent.com/developer/article/1756011)*

### 图形合成驱动

图形渲染阶段结束以后，接下来就到了图形的合成阶段

图形合成我们可以简单理解为Photoshop中的图层合成：

每个图层都有不同的内容，点击合并图层以后电脑会计算重叠区域的像素变化，最终把多个图层合并为一个图层

这么说可能还是有点抽象，接下来我们通过一个例子来帮助理解什么是“合成”？

#### 1. 什么是合成

这是一张launcher桌面的截屏，它是由“壁纸”、“顶部的状态栏”、“桌面的应用列表”以及“底部导航栏”这4个图层组成

![android_graphic_v3_hwc_finally](/Users/bob/Desktop/Bob/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/v3/android_graphic_v3_hwc_finally.png)

*图片来源：https://blog.zhoujinjian.cn/posts/20210810*

1、壁纸图层：

![android_graphic_v3_hwc_wallpaper](/Users/bob/Desktop/Bob/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/v3/android_graphic_v3_hwc_wallpaper.png)

*图片来源：https://blog.zhoujinjian.cn/posts/20210810*

2、顶部状态栏图层（很小的一个横条）：

![android_graphic_v3_hwc_statusbar](/Users/bob/Desktop/Bob/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/v3/android_graphic_v3_hwc_statusbar.png)

*图片来源：https://blog.zhoujinjian.cn/posts/20210810*

3、桌面应用列表：

![android_graphic_v3_hwc_launcher](/Users/bob/Desktop/Bob/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/v3/android_graphic_v3_hwc_launcher.png)

*图片来源：https://blog.zhoujinjian.cn/posts/20210810*

4、底部导航栏：

![android_graphic_v3_hwc_navigationbar](/Users/bob/Desktop/Bob/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/v3/android_graphic_v3_hwc_navigationbar.png)

*图片来源：https://blog.zhoujinjian.cn/posts/20210810*

这其中的每个图层，都是由上一步的GPU渲染出来的的成果

每个图层渲染完成以后，理论上可以直接送到屏幕上去显示了，但是

**大多数情况下屏幕上都不止一个图层**

**一旦图层与图层之间发生重叠（比如launcher的状态栏、应用列表和导航栏这3个图层都是叠加在壁纸图层的上面），重叠部分的像素颜色就需要重新计算**

**将多个图层合并成一个图层的过程，被称为“合成”**

#### 2. 什么是DPU

合成工作的本质是执行计算“脏区域”、格式转换、处理缩放等操作，这些任务我们也可以调用GPU的接口来完成

当我们认真观察合成的流程会发现，在执行图层合成的过程中是不需要3D操作的，因为早在“图层渲染”那一步GPU就完成了所有的3D处理的工作

这样的话，我们只需要为合成流程单独配置一块2D渲染引擎就OK了

目前在绝大多数Andorid设备中，承担这一责任的就是DPU芯片了

**DPU作为图形硬件的一部分，通常被封装在GPU模块当中，最主要的功能是将GPU渲染完成的图层输出到屏幕**

**兼任了合成功能以后，对于图层重叠的部分，DPU会自动计算出“脏区域”并更新像素颜色变化**

不过，DPU虽然可以执行合成工作，但它有合成数量的限制

如下图，Arm Mali-DP550这款DPU最多能够支持7层的合成任务

![android_graphic_v3_mali_dp550](/Users/bob/Desktop/Bob/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/v3/android_graphic_v3_mali_dp550.jpg)

*图片来源：https://community.arm.com/cn/f/discussions/6104/arm-mali-gpu*

#### 3. 什么是HWC

上一节聊DPU的时候我们提到了，如果想把合成流程独立出来，只需要单独配置一块2D渲染芯片就行了

厂商可以选择将合成工作放在`DPU`中，也可以选择在板子上加一块`2D渲染芯片`，将合成工作放在这块芯片中

抠门一点的厂商，可以选择不单独配置合成芯片，只使用GPU进行合成

这就需要一个规范把合成工作抽象成一个接口，由厂商自由选择合成方案

[Hardware Composer](https://source.android.com/devices/graphics/hwc)就是专门用来定义合成工作的抽象接口，它是[Android Hardware Abstraction Layer（HAL）](https://source.android.com/devices/architecture/hal-types?hl=zh-cn)硬件抽象层的成员之一

在HWC中，厂商使用的是DPU还是其他的2D渲染芯片不重要，只需要实现HWC的接口即可

我们来看Google官网对于HWC的定义：

![android_graphic_v3_developer_hwc](/Users/bob/Desktop/Bob/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/v3/android_graphic_v3_developer_hwc.png)

*图片来源：https://source.android.com/devices/graphics/hwc*

《每个字都认识就是不知道在说什么》系列，我把这段话按照标号翻译一下：

1. **用DPU做合成比GPU要高效**

2. **第二点比较重要，里面包含了hwc的执行逻辑，大致流程是这样：**

   > **sf进程：有6个渲染好的图层过来了，我全部塞给你，你自个儿处理完去送显？**
   >
   > **hwc：不行，我最多只能处理4个图层的合并工作，剩余两个图层我标记为GPU合成了，你处理一下**
   >
   > **sf进程：调用GPU完成另外2个图层的合成工作，并将合并后的图层交给hwc**

3. **第三点我不是很理解什么意思，因为理论上如果屏幕内容没有发生变化，sf不应该走合成流程，**

4. **超出hwc能力的图层会调用GPU合成，如果应用的图层太多会对性能产生影响，比如APP弹窗过多，每个Dialog都算一个图层**

关于第二点我们可以使用adb shell dumpsys SurfaceFlinger命令查看当前图层的合成方式，DEVICE表示HWC合成，CLIENT表示GPU合成

![android_graphic_v3_hwc_pixel3](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/v3/android_graphic_v3_hwc_pixel3.jpg)

*图片来源：自己截的*

另外，因为绝大多数情况下hwc的实现者是DPU，所以除了合成工作外，hwc模块还负责送显以及发送VSync信号

好了，hwc的部分到这里先告一段落，我们来总结一下“硬件驱动”小节的内容

本章节的重点是了解什么是渲染和合成以及渲染/合成使用的硬件设备是什么，渲染和合成之间的区别：

- **渲染，关注的是单个图层的内容，在当前图层的坐标系中，每个坐标点应该显示什么颜色**
- **合成，关注的是多个图层的内容，将多个图层重新计算后得到一个图层，参考ps的合并图层功能**

渲染、合成这两个阶段所需的硬件部分已经介绍完成，接下来我们一起看看Google为图形系统准备了哪些软件组件库

## 2、Google组件库

Andorid提供的低级别组件库更多是控制图形内存的流程以及内存结构的封装

比如CPU、GPU和HWC要共享同一块内存，那就需要一种格式让它们都能识别这块内存

另外，还需要一种机制保证数据的安全，防止发生某个硬件在使用过程中数据被其他硬件篡改的情况

图形系统中包含了[[libui.so]](http://www.aospxref.com/android-7.1.2_r39/xref/frameworks/native/libs/ui/)和[[libgui.so]](http://www.aospxref.com/android-7.1.2_r39/xref/frameworks/native/libs/gui/)两个低级别组件库，接下来一起来看看这两个库里面分别有什么

### libui组件库

#### 1. 什么是GraphicBuffer

GraphicBuffer是整个图形系统的核心，所以的渲染操作都将在此对象上进行，包括同步给GPU和HWC

每当应用有显示需求时，应用会向系统申请一块`GraphicBuffer`内存，这块内存将会共享给GPU用于执行渲染工作，接着会同步给HWC用于合成和显示

我们可以把每一个GraphicBuffer对象看做是一个个渲染完成的图层，对应`1.1.2.1`小节中Launcher的各个图层

> *更多关于GraphicBuffer的介绍请点击[[这里]](https://www.kancloud.cn/alex_wsc/android_depp/412982)*

#### 2. 什么是Fence机制

一个GraphicBuffer对象完整的生命周期大概是这样：

- **渲染阶段：应用有绘图需求了，由GPU分配一块内存给应用，应用调用GPU执行绘图，此时使用者是GPU**
- **合成阶段：GPU渲染完成后将图层传递给sf进程，sf进程决定由谁来合成，hwc或者GPU**
  - **如果使用GPU合成，那么此时buffer的使用者依旧是GPU**
  - **如果使用hwc合成，那么此时buffer的使用者是hwc**
- **显示阶段：所有的buffer在此阶段的使用者都是hwc，因为hwc控制着显示芯片**

从生命周期可以看出`GraphicBuffer`对象在流转的过程中，会被`GPU`、`CPU`、`DPU`三个不同的硬件访问

如果同一块内存能够被多个硬件设备访问，就需要一个同步机制

在Android图形系统中，Fence机制就是用来保证跨硬件访问时的数据安全

Fence的底层逻辑可以参考Java的`synchronized`互斥锁机制，我们也可以把Fence理解为一把硬件的互斥锁

每个需要访问GraphicBuffer的角色，在使用前都要检查这把锁是否signaled了才能进行安全的操作，否则就要等待

- **active状态，该GraphicBuffer正在被占用**
- **signaled状态，表明不再控制buffer**

> *更多关于Fence同步机制的介绍请点击[[这里]](https://blog.csdn.net/jinzhuojun/article/details/39698317)*

#### 3. 什么是Gralloc

移动设备的内部空间寸土寸金，不太可能像PC一样给GPU单独配个显存，因此移动端的"图形内存"都被分配在`运行内存`中

为了防止图形内存被滥用，Android抽象出Gralloc模块，规定了所有图形内存的申请与释放都需要通过该模块来操作，以此来规范图形内存的使用

> *更多关于Gralloc机制的介绍请点击[[这里]](https://windrunnerlihuan.com/2017/03/12/Android-SurfaceFlinger-%E5%AD%A6%E4%B9%A0%E4%B9%8B%E8%B7%AF-%E4%B8%80-Android%E5%9B%BE%E5%BD%A2%E6%98%BE%E7%A4%BA%E4%B9%8BHAL%E5%B1%82Gralloc%E6%A8%A1%E5%9D%97%E5%AE%9E%E7%8E%B0/)*
>
> *注意：Fence机制和Gralloc机制并不属于libui组件库，把它俩放到libui库展示是因为GraphicBuffer需要它们*

### libgui组件库

俗话说，越接近上层业务设计上就越复杂

libgui库虽说还没到业务层，但它里面的组件大多是对GraphicBuffer对象的封装，以满足不同的业务需求

#### 1. 什么是BufferQueue

在Android 4.1（Project Butter）中引入了BufferQueue，它是黄油计划中“Triple Buffer”的执行者

黄油计划的重点是“Drawing with VSync”，也就是把VSync信号同步给APP进程

这样做的目的是将渲染和合成分成两步来执行，减少一个VSync周期内的任务量，以降低丢帧发生的概率

回到本章节的主题：**BufferQueue**

从名称就可以看出来，它是一个封装了GraphicBuffer的队列，BufferQueue对外提供了GraphicBuffer对象出列/入列的接口

另外，BufferQueue还为每个GraphicBuffer对象包装了几种不同的状态，它们分别是：

> - **FREE：闲置状态，任何进程都可以获取该buffer进行操作，通常表示为APP进程可以申请使用的内存**
> - **DEQUEUED：出列状态，通常是APP进程在绘图，使用者是GPU**
> - **QUEUED：入列状态，表示APP绘图已经完成，等待从队列取出执行下一步合成，没有使用者**
> - **ACQUIRED：锁定状态，通常表示sf进程从队列取出，正在做合成工作，此时使用者可能是hwc也有可能是GPU**
> - **SHARED：共享状态，7.0版本加入的新状态，没找到相关介绍资料，合成工作完成以后共享给录屏软件？**

设计上，BufferQueue使用了[[生产者/消费者模式]](https://juejin.cn/post/7072263857015619621#heading-16)，绝大多数的情况下，APP作为GraphicBuffer的生产者，sf进程作为GraphicBuffer的消费者，它们俩共同操作一个buffer队列

简单描述一下生产者消费者操作队列时状态转换的过程：

> **生产者：APP进程**
>
> **1、producer->dequeueBuffer()**
>
> ​	 **从队列取出一个状态为“FREE”的buffer，此时该buffer状态变化为：FREE->DEQUEUED**
>
> **2、producer->queueBuffer()**
>
> ​	 **将渲染完成的buffer入列，此时该buffer状态变化为：DEQUEUED->QUEUED**
>
> **消费者：sf进程**
>
> **1、consumer->acquireBuffer()**
>
> ​	 **从队列中取出一个状态为“QUEUED”的渲染完的buffer准备去合成送显，此时该buffer的状态变化为：QUEUED->ACQUIRED**
>
> **2、consumer->releaseBuffer()**
>
> ​	 **buffer内容已经显示过了，可以重新入列给APP使用了，此时该buffer的状态变化为：ACQUIRED->FREE**

每个Buffer的一生，就是在不断地循环`FREE`->`DEQUEUED`->`QUEUED`->`ACQUIRED`->`FREE`这个过程，这中间有任何一个环节出现延迟，反应到屏幕上就是应用出现了卡顿

> *更多关于BufferQueue的介绍请点击[[这里]](https://zhuanlan.zhihu.com/p/62813895)*
>
> *说明：BufferQueue核心代码由BufferQueueCore、BufferQueueProducer、BufferQueueConsumer这3个类组成*
>
> *为了避免引入过多的角色导致文章的阅读体验下降，在后续的章节中，我会将这几个类统称为BufferQueue，包括接下来会出现的Surface、EventThread都是如此，只保留主干*

#### 2. 什么是Surface

在介绍libui库的时，我们提到了GraphicBuffer是整个图形系统的核心

但对于开发者来说，尤其是应用开发者，Surface才是我们看得见摸得着的核心类，应用中所有的绘图操作最终都是在Surface中执行的

Surface作为图像的生产者，持有BufferQueue的引用，并且封装了出列和入列两个方法

接下来我们通过一个例子来了解下日常开发中是如何调用到BufferQueue的：

我们都知道Android支持2D绘图，API使用的是[[Canvas]](https://developer.android.com/reference/android/graphics/Canvas)，也支持3D绘图，API使用的是[[OpenGL ES]](https://developer.android.com/guide/topics/graphics/opengl)

我们以2D绘图的流程来举例：

> **1、需要显示图形时，首先创建一个Surface对象**
>
> **2、调用Surface#lockCanvas()获取Canvas对象**
>
> **3、调用Canvas的draw开头的函数执行一系列的绘图操作**
>
> **4、调用Surface#unlockCanvasAndPost()将绘制完成的图层提交，等待下一步合成显示**

第二步的[[lockCanvas()]](http://www.aospxref.com/android-7.1.2_r39/xref/frameworks/base/core/java/android/view/Surface.java#299)方法返回了Canvas对象供开发者绘图使用，其内部就调用了BufferQueue#dequeueBuffer()申请一块图形buffer，后续所有的绘图结果都会写入这块内存中

第四步的[[unlockCanvasAndPost()]](http://www.aospxref.com/android-7.1.2_r39/xref/frameworks/base/core/java/android/view/Surface.java#321)方法内部调用了BufferQueue#queueBuffer()方法将绘制完成的Buffer入列，等待sf进程在下一次同步信号周期合成并完成送显

上面是使用Java开发的绘制流程，Surface除了给Java层提供绘图接口外，它还是`ANativeWindow`的实现类

`ANativeWindow`是用于提供给C/C++层开发的接口，和应用开发关系不大，点击[[这里]](https://www.cnblogs.com/yongdaimi/p/11244950.html)了解一下它们之间的区别即可

简单来说，`ANativeWindow`和`Surface`一样，都封装了Buffer的出列和入列方法

#### 3. 什么是DisplayEventReceiver

`DisplayEventReceiver`对象看起来有点面生，但提到`Choreographer`我相信大部分读者应该都知道是干什么的

`DisplayEventReceiver`和`Choreographer`都是在黄油计划加入的新成员，它俩是一一对应的关系（DisplayEventReceiver是Choreographer中的一个成员变量）

简单来说，`DisplayEventReceiver`让`Choreographer`对象拥有了感知VSync信号的能力，了解即可

> *关于DisplayEventReceiver更多细节请点击[这里](https://lishuaiqi.top/2018/07/15/Choreographer-1-choreographerAnalysize/)*

## 3、小结

呼~

终于把硬件驱动和Google组件库介绍完了，用一张图来总结本章节内容：

![android_graphic_v3_static_overview](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/v3/android_graphic_v3_static_overview.jpg)

*图片来源：自己画的*

最底层的硬件驱动由OEM厂商提供，也就是高通、ARM这些SOC厂商

如果设备中没有GPU/DPU这些硬件也没关系，Google为所有的驱动（包括HAL层）都提供了默认实现，也就是CPU实现，这也是为什么虚拟机能运行的原因

再往上是Google提供的组件库，里面都是一些常用的类，其中大部分成员我们都还是要了解它们各自是做什么的，因为只有把这些基础概念理解清楚，才能在后续阅读源码的过程中做到有的放矢

好了，Android图形系统的静态部分聊完了，接下来我们进入动态部分的内容

# 二、请求VSync信号

厂商驱动库和Google组件库作为Android图形系统的基石，为整个图形系统提供了强有力的支撑

在Android图形系统的动态部分，SurfaceFlinger作为系统的核心进程，在整个图形系统中起承上启下的作用

为图形系统服务的另外还有SystemServer进程，它和SurfaceFlinger进程一同支撑着整个系统的运转，其中：

- **SurfaceFlinger进程负责接受来自APP进程的图形数据，调用hwc进行合成并完成最终的送显**
- **SystemServer进程负责管理有哪些APP进程可以进行绘图操作以及各个图层的优先级**

SurfaceFlinger进程和SystemServer进程都是在VSync信号的驱使下进行工作，在接下来的章节中我们将会分析这两大系统进程分别在VSync信号到来时做了哪些事情

不过，在此之前，我们需要先知道它们是如何请求VSync信号的？

> *为了避免文章过于臃肿，图形系统的每个模块都删减了非常多的细节，如果您在阅读过程中有任何疑问，可以在评论区留言*
>
> *全文基于[Android 7.1.2](http://www.aospxref.com/android-7.1.2_r39/xref/)版本*

## 1、启动surface_flinger进程

Android 7.0以后对`init.rc`脚本进行了重构，sf进程的启动从[init.rc](http://androidxref.com/6.0.1_r10/xref/system/core/rootdir/init.rc)文件配置到了[surfaceflinger.rc](http://www.aospxref.com/android-7.1.2_r39/xref/frameworks/native/services/surfaceflinger/surfaceflinger.rc)文件，依旧由init进程拉起

先来看main_surfaceflinger.cpp的启动函数：

```c++
/frameworks/native/services/surfaceflinger/main_surfaceflinger.cpp
int main(int, char**) {

    //创建sf对象
    sp<SurfaceFlinger> flinger = new SurfaceFlinger();

    //调用init方法进行初始化
    flinger->init();

    //注册sf服务到servicemanager
    sp<IServiceManager> sm(defaultServiceManager());
    sm->addService(String16(SurfaceFlinger::getServiceName()), flinger, false);

    //注册gpu服务到servicemanager
    sp<GpuService> gpuservice = new GpuService();
    sm->addService(String16(GpuService::SERVICE_NAME), gpuservice, false);

    //调用run方法，进入休眠
    flinger->run();

    return 0;
}
```

`main_surfaceflinger`的入口函数的主要做了3件事：

1. **创建flinger对象并调用`init()`方法执行初始化工作**
1. **注册sf服务和gpu服务到servicemanager**
1. **调用`run()`方法进入休眠**

主要的工作是在flinger对象中的`init()`函数中完成的

我们继续向下跟`SurfaceFlinger#init()`函数：

```c++
/frameworks/native/services/surfaceflinger/SurfaceFlinger.cpp

//利用RefBase首次引用机制来做一些初始化工作，这里是初始化消息机制
//消息队列在sf进程中一共提供两个功能
//1. 执行sf进程请求VSync的工作
//2. VSync-sf信号到来后，执行合成工作
void SurfaceFlinger::onFirstRef()
{
    mEventQueue.init(this);
}

//初始化-只截取了和VSync信号和图形合成有关的部分代码
void SurfaceFlinger::init() {
    {

        // start the EventThread
        //启动事件分发线程，提供给APP进程注册事件回调
        //mPrimaryDispSync是用来控制
        sp<VSyncSource> VSyncSrc = new DispSyncSource(&mPrimaryDispSync,
                VSyncPhaseOffsetNs, true, "app");
        mEventThread = new EventThread(VSyncSrc, *this);

        //又启动一个事件分发线程，并将自己注册到hwc中，用于sf进程监听VSync信号
        sp<VSyncSource> sfVSyncSrc = new DispSyncSource(&mPrimaryDispSync,
                sfVSyncPhaseOffsetNs, true, "sf");
        mSFEventThread = new EventThread(sfVSyncSrc, *this);
        mEventQueue.setEventThread(mSFEventThread);
    }

    // Drop the state lock while we initialize the hardware composer. We drop
    // the lock because on creation, it will call back into SurfaceFlinger to
    // initialize the primary display.
    //初始化HWC对象，加载hwcomposer.so的动作在HWComposer的初始化函数中
    mHwc = new HWComposer(this);
    //将自己注册到hwc的回调函数中，其内部分别调用registerHotplugCallback、registerRefreshCallback、registerVSyncCallback三个回调方法
    mHwc->setEventHandler(static_cast<HWComposer::EventHandler*>(this));

    mEventControlThread = new EventControlThread(this);
    mEventControlThread->run("EventControl", PRIORITY_URGENT_DISPLAY);

}

//进入消息轮询
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

`init()`函数初始化流程稍微有点长，我们一步步拆开来看

### 初始化消息队列

```c++
//利用RefBase首次引用机制来做一些初始化工作，这里是初始化消息机制
//消息队列在sf进程中一共提供两个功能
//1. 执行sf进程请求VSync的工作
//2. VSync-sf信号到来后，执行合成工作
void SurfaceFlinger::onFirstRef()
{
    mEventQueue.init(this);
}
```

在SurfaceFlinger中，利用了RefBase首次引用机制来做一些初始化工作，这里是初始化消息队列

sf进程中的消息队列一共负责处理两种类型的消息：

- **INVALIDATE：处理Layer属性变化以及buffer的更新**
- **REFRESH：监听到VSync信号，执行合成工作**

在接下来的文章分析中我们会发现，sf进程的一生都将围绕着这两件事展开

### 启动事件分发线程

```c++
/frameworks/native/services/surfaceflinger/SurfaceFlinger.cpp
//初始化-只截取了和VSync信号和图形合成有关的部分代码
void SurfaceFlinger::init() {
    {

        // start the EventThread
        //启动事件分发线程，提供给APP进程注册事件回调
        //VSyncPhaseOffsetNs用来控制APP进程的偏移量
        sp<VSyncSource> VSyncSrc = new DispSyncSource(&mPrimaryDispSync,
                VSyncPhaseOffsetNs, true, "app");
        mEventThread = new EventThread(VSyncSrc, *this);
      
				//又启动事件分发线程，提供给sf进程注册事件回调
      	//sfVSyncPhaseOffsetNs用来控制sf进程的偏移量
        sp<VSyncSource> sfVSyncSrc = new DispSyncSource(&mPrimaryDispSync,
                sfVSyncPhaseOffsetNs, true, "sf");
        mSFEventThread = new EventThread(sfVSyncSrc, *this);
        mEventQueue.setEventThread(mSFEventThread);
    }
		//用于控制硬件VSync开关状态
    mEventControlThread = new EventControlThread(this);
    mEventControlThread->run("EventControl", PRIORITY_URGENT_DISPLAY);

}
```

`init()`函数中第二步是启动事件分发线程

一共启动了3个线程，其中：

- **`mEventThread`用于给APP进程提供VSync信号监听，通常称为VSYNC-app**
- **`mSFEventThread`用于给sf进提供VSync信号监听，通常称为VSYNC-sf**
- **`mEventControlThread`是总开关，用于控制硬件VSync信号的开启与关闭**

1、2线程分别是“APP进程”和“sf进程的”VSync事件分发线程

把APP进程和sf进程的VSync分开管理的好处是：降低操控延时

什么是“降低操控延时”？我们先来看看正常的显示流程：

> **VSync1：APP进程开始渲染，渲染完成后入列等待合成**
>
> **VSync2：sf查找所有渲染完成的图层，调用hwc合成，合成完成调用drm/fb显示框架送显，等待显示**
>
> **VSync3：为了防止画面撕裂，显示框架同样等待垂直同步信号到来时才切换framebuffer，此时用户看到更新的画面**

一幅画面最起码要经过2个VSync周期（渲染、合成），在第3个VSync信号到来后才能展示给用户

如果是60HZ的屏幕，用户从按下按钮到到看到画面更新，最快要**16.67ms*2 = 33.34ms**

现在假设有这么个情况：我自己造了个硬件非常非常牛逼，再复杂的画面渲染也只要**1ms**，合成也只要**1ms**

那系统层面有没有一种机制能让用户更快的看到画面更新呢？

**VSync offset：有**

#### 1. VSync offset

回头来看`init()`函数，在创建“APP进程”和“sf进程”的`DispSyncSource`对象时，分别传入了`VSyncPhaseOffsetNs`和`sfVSyncPhaseOffsetNs`两个变量

```c++
void SurfaceFlinger::init() {
 
  //VSyncPhaseOffsetNs - i'm here
  sp<VSyncSource> VSyncSrc = new DispSyncSource(&mPrimaryDispSync,
             VSyncPhaseOffsetNs, true, "app");
  
  //sfVSyncPhaseOffsetNs
  sp<VSyncSource> sfVSyncSrc = new DispSyncSource(&mPrimaryDispSync,
             sfVSyncPhaseOffsetNs, true, "sf");

}
```

- **`VSyncPhaseOffsetNs`用来控制APP进程的偏移量**

- **`sfVSyncPhaseOffsetNs`用来控制sf进程的偏移量**

我们知道硬件垂直同步信号的发送周期是固定的

既然大家都在自己的进程里等待着VSync信号的到来，然后各司其职做自己的工作

那我们通过更改偏移量的方式把“APP进程”和“sf进程”接收到VSync信号的时间错开

就可以实现：在一个硬件VSync信号周期内完成“渲染”和“合成”两件事，具体方案如下：

- **`VSyncPhaseOffsetNs = 0`，硬件VSync发生后，直接转发给app进程，让它开始绘制**
- **`sfVSyncPhaseOffsetNs ≥1`，硬件VSync发生后，延迟几毫秒再转发给sf进程，因为app已经渲染完成，sf合成刚刚渲染的图层**

好了，在一个硬件VSync周期16ms）内“渲染”和“合成”的工作都已经完成了，并且由于GPU性能过于牛逼，距离下次硬件VSync信号发送甚至还有**14ms**~

等下一次硬件VSync信号到来时，显示框架完成画面切换

和之前的方案比，同样是60HZ的屏幕

用户从按下按钮到到看到画面更新，只需要等待1个VSync信号周期，也就是**16.67ms**

#### 2. DispSync模型

还记得`init()`函数中启动的第3个线程吗？名称叫：`mEventControlThread`

```c++
/frameworks/native/services/surfaceflinger/SurfaceFlinger.cpp
class SurfaceFlinger {

    void SurfaceFlinger::init() {
        //用于控制硬件VSync开关状态
        mEventControlThread = new EventControlThread(this);
        mEventControlThread->run("EventControl", PRIORITY_URGENT_DISPLAY);
    }

    //接收来自hwc的硬件VSync信号
    void SurfaceFlinger::onVSyncReceived(int32_t type, nsecs_t timestamp) {
        bool needsHwVSync = false;
        needsHwVSync = mPrimaryDispSync.addResyncSample(timestamp);

        if (needsHwVSync) {
            enableHardwareVSync();
        } else {
            disableHardwareVSync(false);
        }
    }

    //启用硬件VSync信号
    void SurfaceFlinger::enableHardwareVSync() {
        mPrimaryDispSync.beginResync();
        mEventControlThread->setVSyncEnabled(true);
    }

    //关闭硬件VSync信号
    void SurfaceFlinger::disableHardwareVSync(bool makeUnavailable) {
       mEventControlThread->setVSyncEnabled(false);
       mPrimaryDispSync.endResync();
    }

}
```

`mEventControlThread`线程初始化之后被`DispSync`持有，用来启用和关闭硬件VSync的功能

在Android图形系统中，不管是硬件产生还是软件模拟的VSync信号，最终都交由DispSync来管理

VSync offset能够控制偏移量的背后就是DispSync模型

DispSync控制着这个系统的VSync信号出口，除了调整偏移量外，它的内部还有个预测机制

当接受到的硬件VSync信号量足够大时，DispSync会通过`mEventControlThread`关闭硬件VSync开关，通过预测的结果模拟VSync信号的产生，发送到app进程和sf进程

> *ps：我个人对DispSync模型仍然有疑问，这块理解的可能不太对，所以不敢妄下结论，建议阅读以下几篇文章进行学习：*
>
> *[《Analyze AOSP VSync model》](https://utzcoz.github.io/2020/05/02/Analyze-AOSP-VSync-model.html)、[《DispSync解析》](http://echuang54.blogspot.com/2015/01/dispsync.html)、[《Android DispSync 详解》](https://simowce.github.io/all-about-dispsync/)、[《Android R VSync相关梳理》](https://wizzie.top/Blog/2021/04/14/2021/210414_android_VSyncStudy)、[《Android SurfaceFlinger SW VSync模型》](https://www.jianshu.com/p/d3e4b1805c92)*

### 初始化HWComposer

```c++
/frameworks/native/services/surfaceflinger/SurfaceFlinger.cpp
void SurfaceFlinger::init() {
  	...
    //初始化HWC对象，加载hwcomposer.so的动作在HWComposer的初始化函数中
    mHwc = new HWComposer(this);
    //将自己注册到hwc的回调函数中，其内部分别调用registerHotplugCallback、registerRefreshCallback、registerVSyncCallback三个回调方法
    mHwc->setEventHandler(static_cast<HWComposer::EventHandler*>(this));
}
```

前面我们说sf进程的一生，就是在做“请求VSync”和“执行合成工作”这两件事

而HWComposer就是完成这两件事的关键，不管是接受硬件的VSync信号，还是完成图层合成工作，都和它有关

所以，HWComposer对象可以说是sf进程中的头号核心人物

sf进程在初始化HWComposer阶段一共做了两件事：

1. **创建HWC对象，保存到mHwc成员变量**

   > **注意，这里创建的是标准的HWComposer对象，在HWComposer构造函数中，调用了内部的[[loadHwcModule()]](http://www.aospxref.com/android-7.1.2_r39/xref/frameworks/native/services/surfaceflinger/DisplayHardware/HWComposer.cpp#79)方法来加载厂测提供的so库**

2. **注册VSync回调**

   > **HWC对象创建完以后，第二步就调用了setEventHandler将自己注册到VSync信号监听**
   >
   > **如果VSync信号发生变化，最终会调用到sf进程的[[onVSyncReceived()]](http://www.aospxref.com/android-7.1.2_r39/xref/frameworks/native/services/surfaceflinger/SurfaceFlinger.cpp#948)方法**

### 进入睡眠 等待唤醒

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
```

在完成所有初始化工作后，sf进程进入睡眠状态，等待消息唤醒

总结一下，sf进程在图形处理相关方面一共做了两件事：

1. **启动事件分发线程，内部由DispSync模型实现**
2. **初始化HWC对象，加载so库，注册VSync回调**

## 2、启动system_server进程

[[system_server](http://www.aospxref.com/android-7.1.2_r39/xref/frameworks/base/services/java/com/android/server/SystemServer.java)进程中运行着AMS、WMS等常见服务，这些服务都是由java代码实现的，需要一个Java的运行环境

所以SystemServer进程必须要等到Zygote进程创建完`DVM/ART`虚拟机以后，再由Zygote进程fork而来：

```java
/frameworks/base/services/java/com/android/server/SystemServer.java
class SystemServer {

    /**
    * The main entry point from zygote.
    */
    public static void main(String[] args) {
        new SystemServer().run();
    }

    private void run() {
        Looper.prepareMainLooper();
      	startBootstrapServices();
    		startOtherServices();
      	// Loop forever.
        Looper.loop();
    }

    //启动AMS
    private void startBootstrapServices() {
         mActivityManagerService = mSystemServiceManager.startService(ActivityManagerService.Lifecycle.class).getService();
         // Set up the Application instance for the system process and get started.
         mActivityManagerService.setSystemProcess();
    }

    //启动WMS
    private void startOtherServices() {
      	WindowManagerService wm = WindowManagerService.main();
      	//将wms注册到servicemanager
      	ServiceManager.addService(Context.WINDOW_SERVICE, wm);
      	mActivityManagerService.systemReady();//
    }

}

/frameworks/base/services/core/java/com/android/server/am/ActivityManagerService.java
class ActivityManagerService {

    //将自己注册到servicemanager中去
    public void setSystemProcess() {
        ServiceManager.addService(Context.ACTIVITY_SERVICE, this, true);
    }

    //启动launcher桌面
    public void systemReady() {
        //aosp版本不同代码也不同，在7.0中最终调用startHomeActivityLocked()方法唤起launcher
    }

}

/frameworks/base/services/core/java/com/android/server/wm/WindowManagerService.java
class WindowManagerService {

    public static WindowManagerService main(){
        return new WindowManagerService();
    }

}

```

Zygote进程是如何启动并拉起system_server进程这里不展开，我们来关注在SystemServer的`run()`函数中启动了两个服务：AMS和WMS

### 初始化ActivityManagerService

ActivityManagerService是Android系统最为核心的服务之一，负责组件（主要是Activity）的启动、切换、调度工作，来看AMS的启动流程

```java
/frameworks/base/services/core/java/com/android/server/am/ActivityManagerService.java
class ActivityManagerService {

    //将自己注册到servicemanager中去
    public void setSystemProcess() {
        ServiceManager.addService(Context.ACTIVITY_SERVICE, this, true);
    }

    //启动launcher桌面
    public void systemReady() {
        //aosp版本不同代码也不同，在7.0中最终调用startHomeActivityLocked()方法唤起launcher
    }

}
```

AMS在SystemServer进程启动过程中做了两件事：

1. **将自己注册到servicemanager中去**
2. **启动launcher桌面**

在Android图形系统中，每个Activity页面都可以看做是一个Window对象，所以Activity的生命周期也会影响Window对象的状态

**我们来思考一个问题：当我们启动一个透明背景的Activity页面或者是Dialog主题的Activity页面，原先的Activity会不会执行onStop()回调？**

**答案是：不会**

**如果AMS触发了Activity的onStop()回调方法，同时也会调用WindowManagerGlobal#setStoppedState()来通知Window对象，那么此Window会停止绘制**

**这并不是用户希望看到的结果**

### 初始化WindowManagerService

WindowManagerService的启动函数非常简单，在SystemServer中创建完成以后就直接注册到了servicemanager

```java
/frameworks/base/services/java/com/android/server/SystemServer.java
class SystemServer {

    //启动WMS
    private void startOtherServices() {
      	WindowManagerService wm = WindowManagerService.main();
      	//将wms注册到servicemanager
      	ServiceManager.addService(Context.WINDOW_SERVICE, wm);
    }

}

/frameworks/base/services/core/java/com/android/server/wm/WindowManagerService.java
class WindowManagerService {

    public static WindowManagerService main(){
        return new WindowManagerService();
    }

}
```

WMS启动完成以后，最重要的任务就是等待处理来自各个进程创建/更新/销毁Window的工作

在应用开发中，除了`Activity`对应一个Window窗口外，每个`Toast`、`Dialog`也都是一个Window窗口

Android中将窗口的类型分为三种：

- **Application Window（应用窗口）：对应的是Activity**
- **Sub Window（子窗口）：对应Dialog，特点是必须要依附父窗口才能显示**
- **System Window（系统窗口）：对应Toast、微信的视频通话、视频小窗播放等**

### Looper.loop()

将一系列服务启动结束以后，SystemServer调用`Looper#loop()`方法进入循环，保证system_server进程不退出

```java
/frameworks/base/services/java/com/android/server/SystemServer.java
class SystemServer {

    private void run() {
      	startBootstrapServices();
    		startOtherServices();
      	// Loop forever.
        Looper.loop();
    }

}

```

## 3、启动app进程

APP进程和SystemServer进程一样，都是从Zygote进程fork而来

启动APP进程的代码稍微有些长，有时间的同学建议点击[[这里]](http://www.aospxref.com/android-7.1.2_r39/xref/frameworks/base/core/java/android/app/ActivityThread.java)打开源码对照着看效果更佳

```java
/*

视图加载可以分为三个阶段：Activity对象的创建、视图对象的创建、ViewRootImpl的创建

在第一个阶段，ActivityThread.main()
ActivityThread，入口函数中初始化handler机制
ApplicationThread，ams传话筒
Activity，开发者的主战场
PhoneWindow，空壳子
WindowManager，wms的代理对象

在第二个阶段，Activity.setContentView
调用PhoneWindow设置视图
根据不同主题，为DecorView设置不同的子View（无论使用哪种主题视图，其中必然包含名为content的FrameLayout）
将开发者的布局文件添加到子View名为content的FrameLayout当中
为PhoneWindow设置DecorView

在第三个阶段，创建ViewRootImpl
1. Choreographer让它能够感知事件
2. 保存DecorView让它能够在事件来临时控制视图
3. Surface让它拥有绘图的能力
通过addToDisplay()方法推送到wms

**/

/frameworks/base/core/java/android/app/ActivityThread.java
class ActivityThread {

    //zygote进程fork成功后调用入口函数
    void main(){
        Looper.prepareMainLooper();
        attach();//attach方法和ams建立连接，提供给ams控制四大组件的句柄
        Looper.loop();
    }

    //分两步解释更容易理解
    //1. 不管是从桌面点击图标进入还是adb命令启动，最终都交由ams发送启动请求给zygote进程，接着zygote孵化出该APP进程调用main方法
    //2. APP进程启动将创建ApplicationThread对象，并发起IPC把此对象传递给ams，此后四大组件相关回到都将有ApplicationThread对象负责，最终转发给H类执行
    void attach() {
        //获取ams代理并将ApplicationThread将给ams，这个对象以后将是ams的传声筒
        IActivityManager mgr = ActivityManagerNative.getDefault();
        mgr.attachApplication(new ApplicationThread());
    }

    //ApplicationThreadNative封装一系列的关于四大组件回调方法的跨进程通信命令
    //ApplicationThread对象所有操作几乎都由AMS发起调用
    class ApplicationThread extends ApplicationThreadNative {

        void scheduleLaunchActivity(){
            handleMessage(LAUNCH_ACTIVITY);
        }

    }

    class H extends Handler {

        //转发来自ApplicationThread的消息
        void handleMessage(Message msg) {
            case LAUNCH_ACTIVITY::handleLaunchActivity();
            case RESUME_ACTIVITY::handleResumeActivity();
        }

        //转发来自handleMessage的消息
        void handleLaunchActivity(){
            performLaunchActivity();
            handleResumeActivity();
        }

        //转发来自handleMessage的消息
        void handleResumeActivity(){
            performResumeActivity()
            activity.makeVisible();//调用此方法说明第二阶段视图加载已经完成，准备提交到wms服务
        }

        //执行创建Activity对象并回调生命周期
        Activity performLaunchActivity(){
            Activity activity = new Activity();
            activity.attach();//回调attach
            activity.onCreate();//回调Activity
            return activity;
        }

        //执行回调生命周期
        void performResumeActivity(){
            activity.onResume();
        }

    }

}

/frameworks/base/core/java/android/app/Activity.java
class Activity {

    View mDecor;//用户设置的跟视图，通常会在ActivityThread中被赋值
    Window mWindow;//Activity首次被创建调用attach()方法时同步创建，创建动作在Activity
    WindowManager  mWindowManager;//在attach方法中被创建

    //1. 创建PhoneWindow保存到变量mWindow，此时的Window还没有View视图
    //2. 获取wms代理对象，塞到刚刚创建的window对象当中，同时保存到本地mWindowManager变量
    void attach(Window window){
        mWindow = new PhoneWindow(this, window);
        mWindow.setWindowManager(getSystemService(Context.WINDOW_SERVICE));
        mWindowManager = mWindow.getWindowManager();//获取WindowManager动作在Activity中，获取完成接着设置给自己的局部变量，这我是真的没想到，找的好辛苦
    }

    //至此，APP进程启动成功，第一阶段结束，准备进入第二阶段
    void onCreate(){
        setContentView();
    }

    //第二阶段开始：加载视图文件并绑定到DecorView
    void setContentView(View view) {
        mWindow.setContentView(view);
    }

    //第二阶段已经完成，准备进入第三阶段
    void onContentChanged(){
    }

    //第三阶段开始：将视图传递给wms
    //makeVisible()在ActivityThread.H.handleResumeActivity()方法中被调用
    //此阶段完成后会请求VSync信号，并在下一次VSync到来时绘制View树，在下下次sf进程合成，在下下下次展示给用户，整个流程如下：
    //VSync->view.draw()
    //     vysnc->sf.compose()
    //          VSync->drm.flip() 用户可以看到
    void makeVisible() {
        mWindowManager.addView(mDecor);
    }

}

/frameworks/base/core/java/com/android/internal/policy/PhoneWindow.java
class PhoneWindow extends Window {

    DecorView mDecor;
    ViewGroup mContentParent;

    //1. 创建DecorView对象
    //2. 将开发者设置的视图文件作为子View添加到mContentParent
    //3. 通知Activity中onContentChanged方法
    void setContentView(View view) {
        mDecor = generateDecor();
        mContentParent = generateLayout();//看generateLayout方法的注释
        mContentParent.addView(view);//将开发者设置的视图添加为子View
        getCallback().onContentChanged();//回调Activity中onContentChanged()方法
    }

    //创建一个空的DecorView，也就是FrameLayout，里面啥也没有
    void generateDecor() {
        return new DecorView(this);
    }

    //1. 根据不同主题设置不同布局文件，加载该布局文件并设置成DecorView的子View
    //2. 返回子View中id为content的ViewGroup，通常还是个FrameLayout
    //以上两步执行完成以后，DecorView的布局变成：
    //<FrameLayout>//DecorView的根布局
    //  <LinearLayout>//开发者设置带有ActionBar的主题，注意，这里的视图可变的，根据主题来选择不同的视图
    //      <ActionBar/>
    //      <FrameLayout
    //      android:id="@android:id/content"/>//这里的FrameLayout才是最终包含开发者在setContentView中设置的布局
    //  </LinearLayout>
    //</FrameLayout>
    void generateLayout() {
        //加载不同的theme主题的布局文件，比如我们在xml中指定android:theme=@style/NoActionBar
        View root = inflater.inflate(layoutResource);
        //将上一步解析的视图作为根布局添加到DecorView，常见的比如垂直方向的LinearLayout，这样布局DecorView
        mDecor.addView(root);
        //找到用来装用户视图的ViewGroup，通常还是个FrameLayout
        ViewGroup contentParent = mDecor.findViewById(R.id.content);
        return contentParent;
    }

}

//自身无逻辑，可以跳过
/frameworks/base/core/java/com/android/internal/policy/DecorView.java
class DecorView extends FrameLayout {

    //DecorView和PhoneWindow互相持有，这代码写的，啧啧啧
    PhoneWindow mWindow;

    DecorView(PhoneWindow window){
        mWindow = window;
    }

}

//定义View操作接口，顶级接口
/frameworks/base/core/java/android/view/ViewManager.java
public interface ViewManager
{
    public void addView();
    public void updateViewLayout();
    public void removeView();
}

//啥也不是
/frameworks/base/core/java/android/view/WindowManager.java
public interface WindowManager extends ViewManager {

}

//WindowManager的最终实现
/frameworks/base/core/java/android/view/WindowManagerImpl.java
public class WindowManagerImpl implements WindowManager {

    WindowManagerGlobal mGlobal = WindowManagerGlobal.getInstance();

    void addView(View decorView) {
        mGlobal.addView(decorView);
    }

}

//全局单例，和WMS建立连接通信，也是APP进程中，所有窗口实际的管理者
//内部mViews和mRoots变量保存着所有创建的Activity对应的View和ViewRootImpl
class WindowManagerGlobal {

   List<View> mViews;
   List<ViewRootImpl> mRoots;

   void addView(View decorView){
      ViewRootImpl root = new ViewRootImpl(decorView);
      mViews.add(decorView);
      mRoots.add(root);
      // do this last because it fires off messages to start doing things
      root.setView(view);
   }

}

//对应一个Activity，关于视图的事件触发都在此
//1. Choreographer让它能够感知事件
//2. 保存DecorView让它能够在事件来临时控制视图
//3. Surface让它拥有绘图的能力
class ViewRootImpl {

    Choreographer mChoreographer;//构造函数中被创建
    View mView;//保存DecorView

    final Surface mSurface = new Surface();

    public ViewRootImpl(){
        //可以感知VSync的原因可以追溯到libgui库中的DisplayEventReceiver类
        mChoreographer = Choreographer.getInstance();
    }

    //1. 请求VSync信号，等待VSync来临后绘图
    //2. 创建binder代理对象传递给wms，此后wms将通过此代理对象来通知APP进程应该做什么事
    void setView(View decorView){
        mView = decorView;//将DecorView保存到ViewRootImpl的成员变量mView中
        requestLayout();//请求VSync信号
        //通过binder向wms添加窗口
       res = mWindowSession.addToDisplay();
    }

}
```

APP整个启动过程大致可以分为三步：

1. **创建Activity并调用`setContentView()`绑定视图**
2. **请求VSync信号**
3. **进入睡眠 等待VSync信号唤醒**

我们先来看第一步：创建Activity并调用`setContentView()`绑定视图

### 创建Activity

在Android开发中，第一步永远都是设置视图，而设置视图不外乎于两种方式：xml文件和Java编码

不管使用哪种方式，都需要调用`setContentView()`方法将视图绑定到Activity当中

经过一系列的方法调用，最终会执行到`ViewRootImpl.setView()`方法将视图同步给WMS

创建Activity的过程中要依赖三个关键的对象：Window、DecorView、ViewRootImpl，我们可以按照这三个对象把创建Activity也分成了三个阶段：

> **1、Window对象的创建**
>
> **2、DecorView的创建**
>
> **3、ViewRootImpl的创建**

#### 1. Window的创建

```java
/frameworks/base/core/java/android/app/ActivityThread.java
class ActivityThread {

    //zygote进程fork成功后调用入口函数
    void main(){
        Looper.prepareMainLooper();
        attach();//attach方法和ams建立连接，提供给ams控制四大组件的句柄
        Looper.loop();
    }

    //分两步解释更容易理解
    //1. 不管是从桌面点击图标进入还是adb命令启动，最终都交由ams发送启动请求给zygote进程，接着zygote孵化出该APP进程调用main方法
    //2. APP进程启动将创建ApplicationThread对象，并发起IPC把此对象传递给ams，此后四大组件相关回到都将有ApplicationThread对象负责，最终转发给H类执行
    void attach() {
        //获取ams代理并将ApplicationThread将给ams，这个对象以后将是ams的传声筒
        IActivityManager mgr = ActivityManagerNative.getDefault();
        mgr.attachApplication(new ApplicationThread());
    }

    //ApplicationThreadNative封装一系列的关于四大组件回调方法的跨进程通信命令
    //ApplicationThread对象所有操作几乎都由AMS发起调用
    class ApplicationThread extends ApplicationThreadNative {

        void scheduleLaunchActivity(){
            handleMessage(LAUNCH_ACTIVITY);
        }

    }

    class H extends Handler {

        //转发来自ApplicationThread的消息
        void handleMessage(Message msg) {
            case LAUNCH_ACTIVITY::handleLaunchActivity();
            case RESUME_ACTIVITY::handleResumeActivity();
        }

        //转发来自handleMessage的消息
        void handleLaunchActivity(){
            performLaunchActivity();
            handleResumeActivity();
        }

        //执行创建Activity对象并回调生命周期
        Activity performLaunchActivity(){
            Activity activity = new Activity();
            activity.attach();//回调attach
            activity.onCreate();//回调Activity
            return activity;
        }

    }

}

/frameworks/base/core/java/android/app/Activity.java
class Activity {

    Window mWindow;//Activity首次被创建调用attach()方法时同步创建，创建动作在Activity
    WindowManager  mWindowManager;//在attach方法中被创建

    //1. 创建PhoneWindow保存到变量mWindow，此时的Window还没有View视图
    //2. 获取wms代理对象，塞到刚刚创建的window对象当中，同时保存到本地mWindowManager变量
    void attach(Window window){
        mWindow = new PhoneWindow(this, window);
        mWindow.setWindowManager(getSystemService(Context.WINDOW_SERVICE));
        mWindowManager = mWindow.getWindowManager();//获取WindowManager动作在Activity中，获取完成接着设置给自己的局部变量
    }

    //至此，APP进程启动成功，第一阶段结束，准备进入第二阶段
    void onCreate(){
        setContentView();
    }

}
```

`ApplicationThread`作为AMS控制手柄，接受到启动Activity的指令后会转发到`H#handleMessage()`方法，最终由`ActivityThread#handleMessage()`方法执行来创建Activity对象

Activity对象创建完成以后，紧接着就会调用`Activity的attach()`方法

在Activity的`attach()`方法完成的工作中，最重要的就是：创建了类型为`PhoneWindow`的Window实例对象

#### 2. DecorView的创建

在ActivityThread中，调用完`attach()`方法后紧接着就会调用`Activity#onCreate()`方法

开发者通常会在`onCreate()`方法中调用`setContentView()`来设置视图文件

这就进入到第二个阶段：DecorView的创建

```java
/frameworks/base/core/java/android/app/ActivityThread.java
class ActivityThread {

  //执行创建Activity对象并回调生命周期
  Activity performLaunchActivity(){
      Activity activity = new Activity();
      activity.attach();//回调attach
      activity.onCreate();//回调Activity
      return activity;
  }

}

/frameworks/base/core/java/android/app/Activity.java
class Activity {

    Window mWindow;//Activity首次被创建调用attach()方法时同步创建，创建动作在Activity
    WindowManager  mWindowManager;//在attach方法中被创建

    //第二阶段开始：加载视图文件并绑定到DecorView
    void setContentView(View view) {
        mWindow.setContentView(view);
    }

    //第二阶段已经完成，准备进入第三阶段
    void onContentChanged(){
    }

}

/frameworks/base/core/java/com/android/internal/policy/PhoneWindow.java
class PhoneWindow extends Window {

    DecorView mDecor;
    ViewGroup mContentParent;

    //1. 创建DecorView对象
    //2. 将开发者设置的视图文件作为子View添加到mContentParent
    //3. 通知Activity中onContentChanged方法
    void setContentView(View view) {
        mDecor = generateDecor();
        mContentParent = generateLayout();//看generateLayout方法的注释
        mContentParent.addView(view);//将开发者设置的视图添加为子View
        getCallback().onContentChanged();//回调Activity中onContentChanged()方法
    }

    //创建一个空的DecorView，也就是FrameLayout，里面啥也没有
    void generateDecor() {
        return new DecorView(this);
    }

    //1. 根据不同主题设置不同布局文件，加载该布局文件并设置成DecorView的子View
    //2. 返回子View中id为content的ViewGroup，通常还是个FrameLayout
    //以上两步执行完成以后，DecorView的布局变成：
    //<FrameLayout>//DecorView的根布局
    //  <LinearLayout>//开发者设置带有ActionBar的主题，注意，这里的视图可变的，根据主题来选择不同的视图
    //      <ActionBar/>
    //      <FrameLayout
    //      android:id="@android:id/content"/>//这里的FrameLayout才是最终包含开发者在setContentView中设置的布局
    //  </LinearLayout>
    //</FrameLayout>
    void generateLayout() {
        //加载不同的theme主题的布局文件，比如我们在xml中指定android:theme=@style/NoActionBar
        View root = inflater.inflate(layoutResource);
        //将上一步解析的视图作为根布局添加到DecorView，常见的比如垂直方向的LinearLayout，这样布局DecorView
        mDecor.addView(root);
        //找到用来装用户视图的ViewGroup，通常还是个FrameLayout
        ViewGroup contentParent = mDecor.findViewById(R.id.content);
        return contentParent;
    }

}

//自身无逻辑，可以跳过
/frameworks/base/core/java/com/android/internal/policy/DecorView.java
class DecorView extends FrameLayout {

    //DecorView和PhoneWindow互相持有
    PhoneWindow mWindow;

    DecorView(PhoneWindow window){
        mWindow = window;
    }

}

//定义View操作接口，顶级接口
/frameworks/base/core/java/android/view/ViewManager.java
public interface ViewManager
{
    public void addView();
    public void updateViewLayout();
    public void removeView();
}

//啥也不是
/frameworks/base/core/java/android/view/WindowManager.java
public interface WindowManager extends ViewManager {

}

//WindowManager的最终实现
/frameworks/base/core/java/android/view/WindowManagerImpl.java
public class WindowManagerImpl implements WindowManager {

    WindowManagerGlobal mGlobal = WindowManagerGlobal.getInstance();

    void addView(View decorView) {
        mGlobal.addView(decorView);
    }

}

//全局单例，和WMS建立连接通信，也是APP进程中，所有窗口实际的管理者
//内部mViews和mRoots变量保存着所有创建的Activity对应的View和ViewRootImpl
class WindowManagerGlobal {

   List<View> mViews;
   List<ViewRootImpl> mRoots;

   void addView(View decorView){
      ViewRootImpl root = new ViewRootImpl(decorView);
      mViews.add(decorView);
      mRoots.add(root);
      // do this last because it fires off messages to start doing things
      root.setView(view);
   }

}
```

我们都知道DecorView是顶级View，而且它自身是个`FrameLayout`，所以在`mWindow#setContentView()`中第一步就是将DecorView对象创建出来

创建完DecorView后，接着会加载Activity使用的主题文件，并且将该主题作为`子View`添加到DecorView，这个`子View`就是`mContentParent`

有了`mContentParent`，最后才是将我们设置的视图添加为`mContentParent`的`子View`

稍微有点绕，简单来说DecorView作为一个FrameLayout，通常内部会再包含一个子View：`mContentParent`

`mContentParent`中，会有一个名为`R.id.content`的FrameLayout，这个里面才是包含我们设置的视图

```java
void setContentView(View view) {
    //<FrameLayout>//DecorView的根布局
    //  <LinearLayout>//开发者设置带有ActionBar的主题，注意，这里的视图可变的，根据主题来选择不同的视图
    //      <ActionBar/>
    //      <FrameLayout
    //      android:id="@android:id/content"/>//这个FrameLayout才是最终包含开发者在setContentView中设置的布局
    //  </LinearLayout>
    //</FrameLayout>
    mDecor = generateDecor();
    mContentParent = generateLayout();//看generateLayout方法的注释
    mContentParent.addView(view);//将开发者设置的视图添加为子View
    getCallback().onContentChanged();//回调Activity中onContentChanged()方法
}
```

把开发者设置的视图添加为子View的下一步是回调Activity中`onContentChanged()`方法

当我们在Activity收到`onContentChanged()`回调的这一刻，说明DecorView已经创建完成

#### 3. ViewRootImpl的创建

第一阶段和第二阶段分别让我们拥有了一个`Window`对象和一个`DecorView`对象

Window对象中包含了DecorView对象，DecorView包含了我们设置的视图文件

接下来的任务就是把该Window对象传递给WMS

和前两个阶段不同的是，第三阶段是在Activity的`onResume()`回调中被触发的

在ActivityThread通知完`onResume()`的下一步调用了`makeVisible()`方法

`makeVisible()`方法中，将会调用`WindowManager#addView(mDecor)`将视图传递给WMS

```java
/frameworks/base/core/java/android/app/ActivityThread.java
class ActivityThread {
 
  void handleResumeActivity(){
      performResumeActivity()
      activity.makeVisible();//开始执行第三个阶段
  }

  //执行回调生命周期
  void performResumeActivity(){
      activity.onResume();
  }
  
}

/frameworks/base/core/java/android/app/Activity.java
class Activity {

    //第三阶段开始：将视图传递给wms
    //makeVisible()在ActivityThread.H.handleResumeActivity()方法中被调用
    void makeVisible() {
        mWindowManager.addView(mDecor);
    }

}

//WindowManager的最终实现
/frameworks/base/core/java/android/view/WindowManagerImpl.java
public class WindowManagerImpl implements WindowManager {

    WindowManagerGlobal mGlobal = WindowManagerGlobal.getInstance();

    void addView(View decorView) {
        mGlobal.addView(decorView);
    }

}

//全局单例，和WMS建立连接通信，也是APP进程中，所有窗口实际的管理者
//内部mViews和mRoots变量保存着所有创建的Activity对应的View和ViewRootImpl
/frameworks/base/core/java/android/view/WindowManagerGlobal.java
class WindowManagerGlobal {

	List<View> mViews;
	List<ViewRootImpl> mRoots;

	void addView(View decorView){
		ViewRootImpl root = new ViewRootImpl(decorView);
		mViews.add(decorView);
		mRoots.add(root);
		// do this last because it fires off messages to start doing things
		root.setView(view);
	}

}

//对应一个Activity，关于视图的事件触发都在此
//1. Choreographer让它能够感知事件
//2. 保存DecorView让它能够在事件来临时控制视图
//3. Surface让它拥有绘图的能力
/frameworks/base/core/java/android/view/ViewRootImpl.java
class ViewRootImpl {

    Choreographer mChoreographer;//构造函数中被创建
    View mView;//保存DecorView

    final Surface mSurface = new Surface();

    public ViewRootImpl(){
        //可以感知VSync的原因可以追溯到libgui库中的DisplayEventReceiver类
        mChoreographer = Choreographer.getInstance();
    }

    //1. 请求VSync信号，等待VSync来临后绘图
    //2. 创建binder代理对象传递给wms，此后wms将通过此代理对象来通知APP进程应该做什么事
    void setView(View decorView){
        mView = decorView;//将DecorView保存到ViewRootImpl的成员变量mView中
        requestLayout();//请求VSync信号
        //通过binder向wms添加窗口
      	res = mWindowSession.addToDisplay();
    }

}
```

WindowManagerImpl是WindowManager的最终实现类，它会调用到`WindowManagerGlobal#addView()`方法

而WindowManagerGlobal是全局单例，每个进程有且只有一个，也就是说

所有的Activity对应的Window都由WindowManagerGlobal进行管理

因此，WindowManagerGlobal会有两个关键集合：`mViews`和`mRoots`

**`mViews`是保管着的DecorView的集合，`mRoots`是保管着ViewRootImpl的集合**

保管着`ViewRootImpl`的集合？`DecorView`是每个Activity的跟视图，`ViewRootImpl`又是什么？

```java
//对应一个Activity，关于视图的事件触发都在此
//1. Choreographer让它能够感知事件
//2. 保存DecorView让它能够在事件来临时控制视图
//3. Surface让它拥有绘图的能力
/frameworks/base/core/java/android/view/ViewRootImpl.java
class ViewRootImpl {

    Choreographer mChoreographer;//构造函数中被创建
    View mView;//保存DecorView

    final Surface mSurface = new Surface();

    public ViewRootImpl(){
        //可以感知VSync的原因可以追溯到libgui库中的DisplayEventReceiver类
        mChoreographer = Choreographer.getInstance();
    }
  
}
```

看看ViewRootImpl类中的三个成员变量：

- **`mView`：DecorView让ViewRootImpl能够在事件来临时控制视图**
- **`mSurface`：让Activity拥有绘图的能力**
- **`mChoreographer`：让ViewRootImpl能够监听VSync信号**

**王炸！！！一个类几乎集齐了所有与视图相关的成员**

我们接着往下跟：

```java
/frameworks/base/core/java/android/view/WindowManagerGlobal.java
class WindowManagerGlobal {
  
  List<ViewRootImpl> mRoots;
  
	void addView(View decorView){
		ViewRootImpl root = new ViewRootImpl(decorView);
    mRoots.add(root);
    root.setView(view);
	}

}

/frameworks/base/core/java/android/view/ViewRootImpl.java
class ViewRootImpl {

    //1. 请求VSync信号，等待VSync来临后绘图
    //2. 创建binder代理对象传递给wms，此后wms将通过此代理对象来通知APP进程应该做什么事
    void setView(View decorView){
        mView = decorView;//将DecorView保存到ViewRootImpl的成员变量mView中
        requestLayout();//请求VSync信号
        //通过binder向wms添加窗口，这个方法背后又是一大堆方法调用，这里不展开讨论
       res = mWindowSession.addToDisplay();
    }

}
```

WindowManagerGlobal创建了`ViewRootImpl`对象后，把它保存到本地集合`mRoots`

接着调用`ViewRootImpl#setView()`方法添加视图，`setView()`中又调用`addToDisplay()`方法通过binder向WMS添加窗口

**至此，`Window`对象的创建、`DecorView`的创建和`ViewRootImpl`的创建这三大阶段全部完成**

我们来总结一下在创建Activity阶段发生的事情

![android_graphic_v3_create_activity](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/v3/android_graphic_v3_create_activity.jpg)

*图片来源：自己画的*

- **Activity中，创建PhoneWindow类型的Window对象**
- **Window中，创建DecorView对象，绑定setContentView传入的视图文件**
- **调用WindowManager添加视图，准备把视图绑定到WMS**
- **创建ViewRootImpl作为最终的执行者，将视图添加WMS**

### 请求VSync信号

在视图相关的对象创建就绪后，APP开始正式请求VSync信号

首次请求VSync信号的动作是在`ViewRootImpl#setView()`方法中触发的

```java
/frameworks/base/core/java/android/view/ViewRootImpl.java
class ViewRootImpl {

  void setView(View decorView){
    requestLayout();//请求VSync信号
  }
  
  void requestLayout() {
      scheduleTraversals();
  }

  //请求VSync，等待刷新
  void scheduleTraversals() {
      //1. 发送同步屏障消息的意义在于，保证VSync信号到来时，第一时间执行ViewRootImpl.doTraversal()方法
      mTraversalBarrier = mHandler.getLooper().getQueue().postSyncBarrier();//创建一个同步屏障（详见Android消息机制）
    	//2. 发送一条异步消息，mTraversalRunnable是处理这条消息的回调
      mChoreographer.postCallback(Choreographer.CALLBACK_TRAVERSAL, mTraversalRunnable, null);
    }
  
    final TraversalRunnable mTraversalRunnable = new TraversalRunnable();

    class TraversalRunnable implements Runnable {

        public void run() {
          //执行绘制流程
            doTraversal();
        }
    }
  
}
```

`setView()`方法中调用了`requestLayout()`方法，接着调用`scheduleTraversals()`方法进行VSync信号的请求

我们在开发过程中调用的`View#invalidate()/requestLayout()`这两个方法，最终也都会调用到`ViewRootImpl#scheduleTraversals()`方法请求VSync信号

我们来看看`scheduleTraversals()`里面都做了些什么？

> **1、向主线程发送一个同步屏障消息**
>
> **2、通过Choreographer提交类型为CALLBACK_TRAVERSAL的Runnable**

**一是向主线程中的消息队列发送一条同步消息，发送同步屏障消息的意义在于，保证VSync信号到来时，第一时间执行`ViewRootImpl#doTraversal()`方法**

**二是提交了`mTraversalRunnable`，如果VSync信号到来后将会被执行，`mTraversalRunnable`内部封装的是`doTraversal()`方法用于执行绘制流程**

### 进入睡眠 等待唤醒

当请求信号发出以后，APP进程就进入等待状态，等待VSync发生

```java
/frameworks/base/core/java/android/app/ActivityThread.java
class ActivityThread {

    //zygote进程fork成功后调用入口函数
    void main(){
        Looper.loop();
    }

}
```

Choreographer运行在主线程，也就是ActivityThread所在的线程，它们共用一个消息队列

所以，VSync请求最终阻塞在`ActivityThread#main()`方法中

# 三、处理VSync信号

万事俱备，只欠东风，APP进程等待着VSync信号的到来，SF进程则需要再等一段时间

## 1、APP进程：绘制三部曲

VSync以`异步消息`的身份被发送到主线程消息队列，该消息处理者为Choreographer中的`mHandler`对象

经过处理以后，最终会调用到`ViewRootImpl#doTraversal()`方法执行绘制，也就是View绘制三部曲：**measure、layout、draw**

```java
/frameworks/base/core/java/android/view/ViewRootImpl.java
class ViewRootImpl {
  
  	View mView;//保存DecorView

    //开始绘制
    void doTraversal() {
      mHandler.getLooper().getQueue().removeSyncBarrier(mTraversalBarrier);//移除同步屏障
            //由于同步屏障消息被移除，所以view的绘制工作和主线程的消息处理是一起在执行的
      performTraversals();//View的绘制起点
    }

    //绘图三部曲
    void performTraversals(){
        relayoutWindow();//向sf正式申请surface，在进入绘图之前为APP进程准备好一块surface内存
        mAttachInfo.mHardwareRenderer.initialize(mSurface);
        performMeasure();//Ask host how big it wants to be
        performLayout();
        performDraw();
    }

    void performMeasure(){
        mView.measure();
    }

    void performLayout(){
        mView.layout();
    }

    void performDraw(){
        mView.draw();
        mAttachInfo.mHardwareRenderer.draw(mView, mAttachInfo, this);
    }

    //创建surface
    //viewrootimpl持有的surface是Java对象，并没有在native创建对应的surface
    //不过这一些对于APP进程来说是无感的，APP->WMS->SF->WMS->APP，在这个过程中APP
    //在此方法中，调用wms为其创建native层的surface对象，在surface创建的过程中，会通知sf进程，sf进程为surface创建对应的layer，创建layer的过程中，又会初始化BufferQueue对象
    //surface中包含bufferqueue，所以sf进程除了为surface创建layer，还会为surface创建队列监听，当有新的视图变化，sf进程将会收到onFrameAvaliable()回调
    int relayoutWindow(){
    }

}
```

在开始绘图之前，ViewRootImpl还做了两件事

**一是移除同步屏障消息，View的绘制流程执行结束后，让我们开发者post的消息得以执行**

**二是调用了`relayoutWindow()`方法，向sf正式申请`Surface`，在进入绘图之前为APP进程准备好一块`Surface`内存**

好了，接下来正式执行绘图流程

### View#onMeasure()

`performMeasure()`方法中调用了[`View#measure()`](http://www.aospxref.com/android-7.1.2_r39/xref/frameworks/base/core/java/android/view/View.java#19820)

ViewRootImpl成员变量`mView`保存的是DecorView对象，所以，`measure()`方法将会从`跟视图`一层层向下遍历

```java
/frameworks/base/core/java/android/view/ViewRootImpl.java
class ViewRootImpl {
  
  	View mView;//保存DecorView

    void performMeasure(){
        mView.measure();
    }

}
```

在遍历整个View树的过程中，会出现多次遍历才能确定View大小的情况，尤其对于ViewGoup来说，取决于`测量模式`和`LayoutParams`配置等参数

在本章节我们需要了解：`measure()`方法是为了计算每一个View需要的大小，`measure()`方法执行完成以后，各个View的大小也都确定了

> *View的绘制我打算另起一篇文章介绍，所以关于`onMeasure()`细节部分，比如测试模式touchMode以及焦点的处理等，这里暂时不展开，之后的`layout`和`draw`也都会一笔带过*

### View#onLayout()

`performLayout()`方法中调用了[`View#layout()`](http://www.aospxref.com/android-7.1.2_r39/xref/frameworks/base/core/java/android/view/View.java#17622)

```java
/frameworks/base/core/java/android/view/ViewRootImpl.java
class ViewRootImpl {
  
  	View mView;//保存DecorView

    void performLayout(){
        mView.layout();
    }

}
```

上一小节`measure`执行完以后，确定了各个View的大小

在本章节中，`layout()`方法是为了计算每一个View的位置，`layout()`方法执行完成以后，各个View的位置也都确定了

### View#onDraw()

`performDraw()`方法中调用了`[View#draw()`](http://www.aospxref.com/android-7.1.2_r39/xref/frameworks/base/core/java/android/view/View.java#17154)

`draw`是最终绘制的阶段，在View体系中，所有的绘图操作都在`draw`阶段得到执行

```java
/frameworks/base/core/java/android/view/ViewRootImpl.java
class ViewRootImpl {
  
  	View mView;//保存DecorView

    void performDraw(){
        mView.draw();
    }

}
```

在执行V`iew#draw()`时，逻辑会和`measure`、`layout`过程有一点点不一样

`measure`过程和`layout`过程都是发生在CPU，`draw`不同，如果开启硬件加速，那么`draw`的过程发生在GPU

并且，Android 5.0版本加入了`RenderThread`，开启硬件加速后，在执行`draw`的过程中，Android将单独启动一个渲染线程来执行绘制任务

不过对于开发者来说，这一切是无感的

```java
/frameworks/base/core/java/android/view/View.java
class View {

    /**
     * This method is called by ViewGroup.drawChild() to have each child view draw itself.
     *
     * This is where the View specializes rendering behavior based on layer type,
     * and hardware acceleration.
     */
    boolean draw(Canvas canvas, ViewGroup parent, long drawingTime) {
        final boolean hardwareAcceleratedCanvas = canvas.isHardwareAccelerated();
        /* If an attached view draws to a HW canvas, it may use its RenderNode + DisplayList.
         *
         * If a view is dettached, its DisplayList shouldn't exist. If the canvas isn't
         * HW accelerated, it can't handle drawing RenderNodes.
         */

    }

}
```

关于`RenderThread`资料并不多，我们只能从源码注释中看看能否得到一些有用的信息，如上文

> **如果启用硬件加速，此时每一步绘图操作都将以`RenderNode`的形式保存到`DisplayList`**
>
> **然后同步给`RenderThread`执行实际的渲染工作**
>
> **如果关闭硬件加速，在`onDraw()`阶段View将会直接调用Canvas API画图，此时的渲染工作执行在UI线程**

启用渲染线程的好处网上倒是有现成的资料，我给大家念一念：

> 1. **显示列表可以根据需要绘制任意多次，无需进一步与业务逻辑交互**
> 2. **可以对整个列表进行某些操作（如平移、缩放等），而无需重新发出任何绘图操作**
> 3. **一旦知道所有的绘图操作，就可以对其进行优化：例如，所有文本在可能的情况下一次绘制在一起**
> 4. **显示列表的处理可能会被分发到另一个线程中执行**
>
> *来自于：[《Understanding the RenderThread》](https://medium.com/@workingkills/understanding-the-renderthread-4dc17bcaf979)*

我个人理解下来单独启用渲染线程有两大好处：

**一是去除重复绘图指令，比如多次setText只保留最后一次**

**二是减轻UI线程压力，一旦绘图指令收集完成，就可以同步给渲染线程执行，这样主线程剩余的时间就可以用来执行其他的消息，比如我们post的消息**

不过，不管有没有开启硬件加速，绘制工作执行在哪个线程，整个渲染流程还是要控制在一个VSync信号周期内完成，否则，该丢帧还是会丢帧

### 特殊情况：SurfaceView

在游戏开发或其他需要展示3D图形时，多数情况是使用SurfaceView来绘制

SurfaceView作为DecorView`跟视图`的成员之一，和普通View一样能够接受到Input事件（覆写onTouchEvent方法）

不过，SurfaceView有普通View没有的能力：“自主上帧”的权利

**什么“自主上帧”呢？**

我们都知道SurfaceView拥有单独的一块Surface，无论是使用Canvas进行2D开发还是OpenGL ES进行3D开发，最终的绘制结果都是保存在这块单独的Surface上

绘制完成以后，SurfaceView可以调用`unlockCanvasAndPost()`/`eglSwapBuffers()`将`GraphicBuffer`入列，提交给sf进程等待合成送显

SurfaceView让应用无需等待VSync信号的到来便可以执行绘制工作，这是它和普通View最大的区别

## 2、SF进程：合成五部曲

无论应用使用哪种API进行图形开发，在绘制流程结束后，APP作为图层的生产者总是会调用`BufferQueue#queueBuffer()`方法将`GraphicBuffer`入列

一旦有新的图层加入队列，就意味着作为图层消费者的SF进程可以开始工作了

### sf进程请求VSync

由于VSync是注册制，因此，sf进程在工作之前必须先请求VSync信号

```c++
/frameworks/native/services/surfaceflinger/Layer.cpp
class Layer {

    //当Surface发生变化以后，最终会调用onFrameAvailable()方法通知sf，让sf请求下一次VSync
    //这里需要注意，VSync信号是EventThread来分发的，APP和sf各自管理自己是否需要请求下一次VSync信号
    void Layer::onFrameAvailable() {
        mFlinger->signalLayerUpdate();
    }
}

/frameworks/native/services/surfaceflinger/SurfaceFlinger.cpp
class SurfaceFlinger {

    //queue内部调用了请求下一次VSync
    void SurfaceFlinger::signalLayerUpdate() {
        mEventQueue.invalidate();
    }

}

/frameworks/native/services/surfaceflinger/MessageQueue.cpp
class MessageQueue {

    //最终在MessageQueue类中执行了请求VSync信号的操作
    void MessageQueue::invalidate() {
        mEvents->requestNextVSync();
    }
}
```

当APP端的Surface发生变化以后，Layer的`onFrameAvailable()`方法会被调用，经过层层转发，最终由`MessageQueue#requestNextVSync()`执行VSync信号的请求

`Layer`这个类之前好像没有出现过，简单介绍一下：

APP进程中的每一个Surface对象，对应SF进程当中的一个Layer对象，它俩共享一个BufferQueue

**Surface作为图层的生产者，封装了出列入列的操作**

**Layer作为图层的消费者，封装了获取渲染图层和释放图层的操作**

### sf进程处理VSync

sf进程使用`MessageQueue`类执行了请求VSync信号的动作，所以，VSync信号到来后的处理同样也是在`MessageQueue`类中

```c++
/frameworks/native/services/surfaceflinger/MessageQueue.cpp
class MessageQueue {

    //接受来自DisplayEventReceiver的VSync信号
    int MessageQueue::eventReceiver(int /*fd*/, int /*events*/) {
        mHandler->dispatchInvalidate();
        return 1;
    }

    //收到VSync信号后，向sf进程中发送类型为"INVALIDATE"的消息
    void MessageQueue::Handler::dispatchInvalidate() {
        mQueue.mLooper->sendMessage(this, Message(MessageQueue::INVALIDATE));
    }

    //外部接口，用于向sf发送合成消息
    void MessageQueue::refresh() {
        mHandler->dispatchRefresh();
    }

    //给sf发送类型为REFRESH的消息，sf收到以后将会执行合成操作
    void MessageQueue::Handler::dispatchRefresh() {
        mQueue.mLooper->sendMessage(this, Message(MessageQueue::REFRESH));
    }

}

/frameworks/native/services/surfaceflinger/SurfaceFlinger.cpp
class SurfaceFlinger {

    void SurfaceFlinger::onMessageReceived(){

        //接收到VSync信号后，判断图层是否需要合成
        case MessageQueue::INVALIDATE: {
            bool refreshNeeded = false;
            refreshNeeded = handleMessageTransaction();
            refreshNeeded |= handleMessageInvalidate();
            //如果需要合成，通知MessageQueue发送一条REFRESH类型的消息
            if(refreshNeeded) signalRefresh();
        }
        //将会执行最终的合成操作
        case MessageQueue::REFRESH: {
            handleMessageRefresh();//合成并输出到屏幕
        }
    }

    //调用mEventQueue给sf自己发送一条Refresh类型的消息
    void SurfaceFlinger::signalRefresh() {
        mEventQueue.refresh();
    }

}
```

**`MessageQueue#eventReceiver()`收到VSync信号后发送`INVALIDATE`消息给sf进程**

**`SurfaceFlinger##onMessageReceived()`方法被触发**

在case为`INVALIDATE`的方法中，调用`handleMessageTransaction()`、`handleMessageInvalidate()`检查是否需要执行下一步合成

如果需要执行合成，最终会执行到`SurfaceFlinger#handleMessageRefresh()`方法

### sf进程执行合成

一旦调用到`handleMessageRefresh()`方法，意味着SF进程的合成工作正式开始

整个合成流程有五个步骤，所以可以取名叫做“合成五部曲”

接下来我们一起来看看“合成五部曲”都做了哪些事情：

```c++
/frameworks/native/services/surfaceflinger/SurfaceFlinger.cpp
class SurfaceFlinger {

    //合成五部曲
    void SurfaceFlinger::handleMessageRefresh(){
        //合成之前的与处理，检查是否有新的图层变化，如果有，执行请求下一次VSync信号
        preComposition();
        //若Layer的位置/先后顺序/可见性发生变化，重新计算Layer的目标合成区域和先后顺序
        rebuildLayerStacks();
        //调用hwc的prepare方法询问每个图层是否支持硬件合成
        setUpHWComposer();
        //当打开开发者选项中的“显示Surface刷新”时，额外为产生变化的图层绘制闪烁动画
        doDebugFlashRegions();
        //如果不支持硬件合成，在该方法中会调用GPU合成，接着提交buffer
        doComposition();
        //调Layer的onPostComposition方法，主要用于调试，可以忽略
        postComposition(refreshStartTime);
    }

    void SurfaceFlinger::preComposition(){
        bool needExtraInvalidate = false;
        const LayerVector& layers(mDrawingState.layersSortedByZ);
        const size_t count = layers.size();
        for (size_t i=0 ; i<count ; i++) {
            //因为在调用合成之前已经计算过脏区域，如果有图层在计算以后加入了队列，那么在预处理阶段要再次请求VSync信号
            if (layers[i]->onPreComposition()) {
                needExtraInvalidate = true;
            }
        }
        //存在未处理的layer，执行请求下一次VSync信号，避免这段时间内的帧数据丢掉了
        if (needExtraInvalidate) {
            signalLayerUpdate();
        }
    }

    void SurfaceFlinger::rebuildLayerStacks(){
        //获取当前应用程序所有按照z-order排列的layer
        const LayerVector& layers(mDrawingState.layersSortedByZ);
        //遍历每一个显示屏
        for (size_t dpy=0 ; dpy<mDisplays.size() ; dpy++) {
            //z-order排列的layer
            hw->setVisibleLayersSortedByZ(layersSortedByZ);
            //显示屏大小
            hw->undefinedRegion.set(bounds);
            //减去不透明区域
            hw->undefinedRegion.subtractSelf(tr.transform(opaqueRegion));
            //累加脏区域
            hw->dirtyRegion.orSelf(dirtyRegion);
        }
    }

    void SurfaceFlinger::setUpHWComposer() {
        //prepareFrame方法中调用了HWComposer::prepare方法
        for (size_t displayId = 0; displayId < mDisplays.size(); ++displayId) {
            auto& displayDevice = mDisplays[displayId];
            if (!displayDevice->isDisplayOn()) {
                continue;
            }
            status_t result = displayDevice->prepareFrame(*mHwc);
        }
    }

    void SurfaceFlinger::doComposition(){
        //遍历所有的DisplayDevice然后调用doDisplayComposition函数
        for (size_t dpy=0 ; dpy<mDisplays.size() ; dpy++) {
            const sp<DisplayDevice>& hw(mDisplays[dpy]);
            if (hw->isDisplayOn()) {
                //获得屏幕的脏区域，将脏区转换为该屏幕的座标空间
                const Region dirtyRegion(hw->getDirtyRegion(repaintEverything));
                //在此方法中将会调用到doComposeSurfaces()方法
                //在doComposeSurfaces方法中，将会为被标记为不支持硬件合成的图层调用Layer#draw()方法使用OpenGL ES合成
                doDisplayComposition(hw, dirtyRegion);
            }
        }
        postFramebuffer();
    }


    //第五步：更新DispSync机制，详情参见
    void SurfaceFlinger::postComposition(){
        //更新DispSync机制，详情参见
    }

}
```

#### 1. preComposition()

第一步是预处理阶段，调用每个layer`的onPreComposition()`方法询问是否需要合成

```c++
/frameworks/native/services/surfaceflinger/SurfaceFlinger.cpp
class SurfaceFlinger {

    void SurfaceFlinger::preComposition(){
        bool needExtraInvalidate = false;
        const LayerVector& layers(mDrawingState.layersSortedByZ);
        const size_t count = layers.size();
        for (size_t i=0 ; i<count ; i++) {
            //因为在调用合成之前已经计算过脏区域，如果有图层在计算以后加入了队列，那么在预处理阶段要再次请求VSync信号
            if (layers[i]->onPreComposition()) {
                needExtraInvalidate = true;
            }
        }
        //存在未处理的layer，执行请求下一次VSync信号，避免这段时间内的帧数据丢掉了
        if (needExtraInvalidate) {
            signalLayerUpdate();
        }
    }

}
```

第一步执行完以后，根据`needExtraInvalidate`来确定是否有遗漏的图层，如果有就再次请求VSync信号，避免丢失帧数据

#### 2. rebuildLayerStacks()

第二步：计算各个Layer的目标合成区域和先后顺序

```c++
/frameworks/native/services/surfaceflinger/SurfaceFlinger.cpp
class SurfaceFlinger {

    void SurfaceFlinger::rebuildLayerStacks(){
        //获取当前应用程序所有按照z-order排列的layer
        const LayerVector& layers(mDrawingState.layersSortedByZ);
        //遍历每一个显示屏
        for (size_t dpy=0 ; dpy<mDisplays.size() ; dpy++) {
            //z-order排列的layer
            hw->setVisibleLayersSortedByZ(layersSortedByZ);
            //显示屏大小
            hw->undefinedRegion.set(bounds);
            //减去不透明区域
            hw->undefinedRegion.subtractSelf(tr.transform(opaqueRegion));
            //累加脏区域
            hw->dirtyRegion.orSelf(dirtyRegion);
        }
    }

}
```

第二步执行完以后，确定了每个图层的可见区域和跟其他图层发生重叠部分的脏区域

#### 3. setUpHWComposer()

第三步：更新HWComposer对象中图层对象列表以及图层属性

`prepareFrame()`方法中调用了`HWComposer#prepare`方法

在HWC的`prepare()`方法中，将会确定每一个图层使用哪种合成方式

```c++
/frameworks/native/services/surfaceflinger/SurfaceFlinger.cpp
class SurfaceFlinger {

    void SurfaceFlinger::setUpHWComposer() {
        //prepareFrame方法中调用了HWComposer::prepare方法
      	//在HWC的prepare方法中，将会确定每一个图层使用哪种合成方式
        for (size_t displayId = 0; displayId < mDisplays.size(); ++displayId) {
            auto& displayDevice = mDisplays[displayId];
            if (!displayDevice->isDisplayOn()) {
                continue;
            }
            status_t result = displayDevice->prepareFrame(*mHwc);
        }
    }

}
```

第三步执行完以后，确定了每个图层的合成方式

#### 4. doComposition()

第四步：执行真正的合成工作

```c++
/frameworks/native/services/surfaceflinger/SurfaceFlinger.cpp
class SurfaceFlinger {

    void SurfaceFlinger::doComposition(){
        //遍历所有的DisplayDevice然后调用doDisplayComposition函数
        for (size_t dpy=0 ; dpy<mDisplays.size() ; dpy++) {
            const sp<DisplayDevice>& hw(mDisplays[dpy]);
            if (hw->isDisplayOn()) {
                //获得屏幕的脏区域，将脏区转换为该屏幕的座标空间
                const Region dirtyRegion(hw->getDirtyRegion(repaintEverything));
                //在此方法中将会调用到doComposeSurfaces()方法
                //在doComposeSurfaces方法中，将会为被标记为不支持硬件合成的图层调用Layer#draw()方法使用OpenGL ES合成
                doDisplayComposition(hw, dirtyRegion);
            }
        }
        postFramebuffer();
    }

}
```

第四步执行完以后，完成了两件事：

1. **将不支持硬件合成的图层进行GPU合成**

   > **在`doComposeSurfaces()`方法中，将会为被标记为不支持硬件合成的图层调用`Layer#draw()方`法使用OpenGL ES合成**

2. **调用`postFramebuffer()`进行送显**

   >  **`postFramebuffer()`方法会将GPU合成后的图层和需要HWC合成的图层一起打包提交给HWC**
   >
   > **HWC最终会调用DRM框架进行送显，当下一次硬件VSync信号发生时交换Framebuffer**

#### 5. postComposition()

第五步：送显之后的善后工作

在Android 7.0版本中，`postComposition()`方法用来执行更正DispSync模型，可以忽略

```c++
/frameworks/native/services/surfaceflinger/SurfaceFlinger.cpp
class SurfaceFlinger {

    //第五步：更新DispSync机制，详情参见
    void SurfaceFlinger::postComposition(){
        //更新DispSync，详情参见DispSync模型一节
    }

}
```

> *ps：关于SF进程合成部分的源码不方便调试，所以这里的结论没有得到验证不一定对*
>
> *另外，合成部分大多数内容都来自于[[这里](https://windrunnerlihuan.com/archives/page/2/)，读者朋友可以去查找其他资料学习*

## 3、小结

至此，APP绘制工作和SF合成工作已经全部完成，我们来画张图总结一下本章节内容

![android_graphic_v3_dynamic_overview](/Users/bob/Desktop/Bob/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/v3/android_graphic_v3_dynamic_overview.png)

*图片来源：自己画的*

一个APP完整的显示流程大致分为三个阶段

1. **app-请求**

   > **APP页面元素一旦发生变化，调用`invalidate()`/`requestLayout()`方法请求下一次VSync信号，此时sf什么都不做**

2. **app-VSync & sf-请求**

   > **app-VSync信号到来后，APP进程执行绘图三部曲，**
   >
   > **绘图流程结束后，sf收到`onFrameAvailable()`，sf进程请求VSync**

3. **sf-VSync**

   > **sf-VSync信号到来，sf进程执行合成五部曲，接着将结果提交给hwc**
   >
   > **等待下次硬件VSync信号发生，切换Framebuffer展示给用户**

# 四、结语

本文把Android图形子系统分为两个部分：静态部分和动态部分

在静态部分中，硬件驱动和Google组件库为应用提供了绘图的能力

在动态部分中，介绍了VSync信号是如何把系统打理的井井有条的，着重分析了各个进程是如何请求和处理VSync信号

有了这两部分的内容，我们来尝试解答一下文章的标题：**当我们点击微信这个应用后，它是如何在屏幕上显示出来的？**

> - **Launcher进程拉起微信的默认启动页：WeChatSplashActivity**
>
> - **WeChatSplashActivity加载视图文件，AMS和WMS为Activity创建DecorView、Window、ViewRoot**
>
> - **视图文件准备就绪，请求VSync信号**
>
> - **app-VSync处理，执行绘制三部曲，绘制结束以后，通知sf进程**
>
> - **sf进程请求VSync信号**
>
> - **sf-VSync处理，执行合成五部曲，提交到hwc**
>
> - **hw-VSync原始信号处理，显示框架执行交换图层数据，用户看到微信的启动图**

**最后，本篇文章之所以能够顺利产生，得益于各位前辈的无私分享（见参考资料）**

**希望本文能够起到抛砖引玉的效果，给各位读者朋友带来一点点帮助**

**全文完**

# 五、参考资料

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
