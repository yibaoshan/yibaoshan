#### 一、Hardware

##### 1、GPU



#### 二、Linux & Android 

##### 1、FB to DRM/KMS

##### Fence机制

Android 4.0加入的同步机制

##### 2、HWC

两层含义，当表示为vsync时，是用来监听回调屏幕驱动的垂直同步信号

表示为合成时，就是2D引擎，合成方式为DEVICE，否则为GPU合成

###### vsync

vsync信号早在1.6版本就已经存在，去源码搜索eventhub.cpp可以看到

##### 3、SurfaceFlinger

sf的主要两个作用，一是接收/分发vsync信号

1. 接收来自hwc的vsync信号
2. 处理vsync信号，DispSync，在Android 12时已经被删除，可能Google觉得高刷是未来的趋势

二是合成layer

**DispSync**

> DispSync是Android 4.4才加入的类，位置在/frameworks/native/service/surfaceflinger/DispSync.cpp
>
> 隶属于sf服务，主要为sf分配vsync信号
>
> Android 12删除了该类

##### 4、layer

##### 5、BufferQueue

Android 4.1开始加入的机制

- **BufferQueueCore**

  > BufferQueueCore 的结构比较简单，它的最主要的功能就是管理 Graphic Buffer 申请/释放/状态维护，所以它的所有内容都是围绕这个核心功能，其中最核心的结构就是 `mSlots` 和 `mQueue`

- **GraphicBuffer**

  > 最重要的图形数据，借由 Gralloc 接口，最终通过 ION驱动来获得共享内存
  >
  > 该内存不但要跨进程还要跨硬件，APP进程、sf进程、gpu硬件、hwc硬件
  >
  > 有五种状态：FREE、DEQUEUED、QUEUED、ACQUIRED、SHARED

- **BufferSlot**

  > 理解为graphicbuffer的包装类，内部成员包含graphicbuffer本身和buffer状态
  >
  > `
  >
  > **struct** **BufferSlot** {   
  >
  > sp<GraphicBuffer> mGraphicBuffer;    
  >
  >  BufferState mBufferState; 
  >
  > }
  >
  > `

- **BLASTBufferQueue**

  > 上述流程的生产者消费者在，我们使用源码工具就会发现Android 11已经加入了blast，在Android 12中正式启用
  >
  > 在blast的逻辑中，生产者消费者的逻辑将被摒弃，buffer的申请释放都交由APP进程内完成，这部分逻辑可以看
  >
  > Android 11开始加入的新的类，Android 12开始正式启用
  >
  > 添加的目的是将对buffer的操作全部交由app进程，sf只负责合成，不再负责管理buffer

##### View

view体系是Google提供给开发者的一套工具，内部使用canvas，其内部是对skia的再封装

简单了看了一下flutter完全摒弃了view体系，使用skia的api来绘制内容，但在Android上依旧没有摆脱ams、pms构建的组件体系

#### 三、Note About Process

##### APP to screen from fsp的计算

1. APP绘制到buffer，这个buffer是通过dequeueBuffer()申请的
2. 绘制完成之后调用queueBuffer()将buffer还给surfaceflinger区进程合成，sf回调用acquireBuffer()从bufferqueue拿到一个buffer去合成，fence机制会保证sf拿到的是GPU绘制好的
3. 合成完后调用releaseBuffer()将buffer还给bufferqueue
4. sf拿到合成后的Framebuffer去送显？

> 统计在一秒内该 App 往屏幕刷了多少帧，而在 Android 的世界里，每一帧显示到屏幕的标志是： present fence signal 了，因此计算 App 的 fps 就可以转换为：**一秒内 App 的 Layer 有多少个有效 present fence signal 了（这里有效 present fence 是指，在本次 VSYNC-sf 中该 Layer 有更新的 present fence）**

##### Porcess Overview from bob

1. 当我们在activity调用setContentView绑定xml时，经过跨进程通信后最终会向wms申请到一个window

#### 四、Roast for Google

- 学习最大的难点之一在于，Android使用Linux内核，并在此基础上进行裁剪和添加功能，所以得去Android上游看看Linux的LTS主线版本，又因为Google提供了HAL层，所以驱动基本上都是下游实现。在消费级市场中高通无疑是最大的下游厂商，因为SOC是高通提供的，内核部分高通同样关注上游Linux主线版本，形成自己的内核caf。接着实现HAL层的接口，再接着给高通的下游厂商，也就是国内的小米OV等手机厂商，手机。以上流程就会导致，你想了解Android某个功能依赖Linux哪个API实现的你就得关注Linux的版本，要想知道某个硬件实现具体是什么东西，比如HWC是什么，就得关注高通一类的下游厂商的代码实现，接着才是Google的aosp源码，aosp和msm-aosp都得看
- 本文的目标是希望能够从头到尾的介绍一下整个显示流程，在执行的过程中发现话题过于庞大，从结果来看完成的不是很好，但个人在这个过程中还是有不少收获。人总是会进步的，相信过几年回头看自己写的文章时，大部分知识点我都已经理解了，那时
- 忽略activity、service等组件的概念，在图形系统中，对应用户进程来说，Android系统提供的就只有window，activity是一个window，dialog也是一个window，surface同样是一个window
- 注1：bufferqueue在Android12已被更新
- 注2：dispsync在Android12已被删除





