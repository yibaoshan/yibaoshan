#### Overview

Android图形系统（三）系统篇：从渲染到合成

##### 一、前言

- 开场白
- 认识硬件，工业级瑞芯微芯片介绍
- 俗话说，驱动硬件不分家，在基于硬件的计基础上，有libui.so，引出fence同步机制、gralloc内存机制、hwc机制、OpenGL和GPU的关系机制
  - OpenGL ES是协议，在Android中具体实现为EGL，在向下的硬件实现是GPU，你调用的所有OpenGL指令都将有CPU发送给GPU执行
  - hwc是统称，具体有两个实现，一个是屏幕驱动，用来给将驱动产生的vsync同步给hwc，另一个就是2D图形引擎，用来合成layer
  - 硬件加速，Android 4.0以后应用程序2D UI绘制支持硬件加速，5.0以后主线程和渲染线程分离Render Thread，整个调用关系如下图，开启以后走GPU，也就是OpenGL，关闭以后走CPU

##### 二、VSync，系统的总指挥

介绍黄油计划之前的流程：接收到vsync信号后，app开始渲染，渲染完成之后显示

介绍黄油计划之后，ui线程接收到vsync信号后开始渲染到buffer，sf开始找渲染完成状态的buffer开始合成，屏幕当前使用的是显示的buffer，这就是threebuffer机制

黄油计划中另一个新增的Choreographer和handler的异步消息体现在图形系统中

Choreographer用于注册回调，接受vsync信号并处理，一旦vsync来临Choreographer就会向ui线程的messagequeue中提交一个屏障消息，然后将input，动画，渲染命令执行完成以后，移除这个屏障消息，在此过程中handler机制优先处理这些系统消息

介绍Android L5.0以后，加入了render线程，当app接受到vsync信号以后，在一个周期内，ui线程将绘制命令收集到DisplayList中（在draw方法中构建DisplayList）

接着ui线程调用syncAndDrawFrame方法将命令同步给render线程进行执行

再交由render调用GPU处理，此时ui线程可以处理来自于Choreographer其他回调，比如input事件，或者messagequeue中的其他msg

sf扫描所有layer中可用buffer，调用acquire方法去获取buffer，接着调用hwc去合成，以减轻GPU的压力，为什么说减轻GPU的压力呢？注意，此时app进程的渲染线程在调用GPU进行绘制

sf端合成完成以后，调用releaseBuffer()方法将buffer还给各自layer中的bufferqueue，hwc合成之后应该有两种选择

1. 调用gralloc模块的post方法，送显
2. 另一种方式是Overlay（hwcomposer的硬件合成）
3. 由hwc直接调用drm去送显，不经过sf
4. sf收到fence回调，由sf调用drm去送显，应该是这一种，因为sf要把buffer归队

###### 开场白

- 开场白，一口气讲完了硬件和系统服务，接下来我们来看看内存是如何流转的
- 介绍vsync信号到来时，sf一层做了什么，surface做了什么，由于surface是Java层的window，所以dialog和surfaceview也都是这时候进行渲染绘制的。
- 在此基础上，通过Choreographer将信号同步给view体系
- 本章目标是介绍当vsync信号到来时，每一层级都做了些什么？
  - 首先，介绍vsync信号的来源，在sf初始化时注册了回调，以及如何通知到Choreographer
    - 在Android12以前，有个类叫做DispSync，他是用来分发和控制没层级vsync信号到达时间，为什么要这么做呢？这样可以缓解，让厂商根据自己的硬件能力自行决定这个值设置为多少，你的GPU强一些，那就把渲染的间隔拉长一些，留给渲染的时间短一些
    - EventThread又是干嘛的
  - 接着，介绍各个层接接受到信号的反应
  - 应用阶段
    - view体系是如何更新的，这个应该是viewrootimpl和Choreographer，渲染线程应该在这聊吧
    - window/surface体系是如何更新的？
  - 渲染阶段
    - view体系应该是在这个阶段执行的渲染线程命令，调用硬件加速从而调用OpenGL也就是GPU
    - window/surface是否在这更新的？
  - 合成
    - sf做的事情，将layer的graphicbuffer状态为可用的丢给hwc，告诉hwc干活了
- 介绍一下view和activity和window的关系
  - 假设没有system_server这个进程，也就是wms、ams所在的进程，单单依靠sf其实已经可以在用户空间完成绘制以及送显了
  - Android官网介绍图形系统时把sf和wms并排，其实是要依靠ams一起完成工作的
  - 不把activity当组件，看做是视图的话，对应的就是viewrootimpl，window
  - surfaceview其实是一个没有view体系的一块surface，在sf一层中对应的layer
  - view体系是Google提供给开发者的一套工具，内部使用canvas，其内部是对skia的再封装

###### view和activity和surface的关系

Android为每个activity创建viewrootimpl

###### 番外篇-如何暂停接收vsync信号？

- 我们知道硬件是一直产生vsync信号的，那么当进入桌面缺什么都不动时？操作系统会傻傻的每次vsync来临时都去渲染吗？显然不会，那是怎么做到的呢？requestNextVsync()，不调用这个方法，就不会去渲染了。上面的流程是针对APP的，那sf呢？调用messageQueue::invalidate()
- 思考一个问题，打开APP后放置不动，系统还会走渲染流程吗？
- 当activity被覆盖时，后面的视图还会渲染吗？答案是不会，为什么呢？因为组件在执行stop周期的时候通知wms，当前的viewrootimpl的状态发生改变，不会接受vsync信号或者不处理，这也是为什么当启动一个透明主题的activity时，旧的页面只会执行pause周期，而不会去调用onstop方法的原因

##### 三、结语

- 开场白，忽略了某些模块以及具体实现细节，比如DRM/KMS是Linux主线显示框架，我们可以思考一个疑问，手机关机了为什么还能显示充电画面？这个问题的答案用DRM/KMS可以解释
- 总结部分，总的来说，依靠vsync信号驱动，像一个环一样操作bufferqueue，在这当中每一层出现了耗时过长，都会使APP发生卡顿
  - 当创建了Activity或者surface时，wms对应会创建window，在sf中对应创建layer

#### 一、Hardware/Software

##### GPU

##### FB to DRM/KMS

##### Fence机制

Android 4.0加入的同步机制

##### HWC

hwcomposition和hwcomposer

两层含义，当表示为vsync时，是用来监听回调屏幕驱动的垂直同步信号

表示为合成时，就是2D引擎，合成方式为DEVICE，否则为GPU合成

##### Vsync

vsync信号早在1.6版本就已经存在，去源码搜索eventhub.cpp可以看到

##### SurfaceFlinger

源码地址：http://www.aospxref.com/android-7.1.2_r39/xref/frameworks/native/services/surfaceflinger/SurfaceFlinger.cpp

**1、注册回调**

**2、接收回调**

- doComposition()

  > 执行正式的合成操作，合成完成以后将各个buffer置为free状态
  >
  > hwc present是什么？

- postComposition

  > 处理时间

来看handleMessageRefresh()方法做了些什么：

`void SurfaceFlinger::handleMessageRefresh() {`
   `...`
    `preComposition(); //合成前的准备`
    `rebuildLayerStacks();//重建layer堆栈`
    `setUpHWComposer();//hwcomposer的设定`
    `doComposition();//正式的合成处理`
    `postComposition(refreshStartTime);//合成后期的处理`
   `...`
`}`



sf的主要两个作用，一是接收/分发vsync信号

1. 接收来自hwc的vsync信号
2. 处理vsync信号，DispSync，在Android 12时已经被删除，可能Google觉得高刷是未来的趋势

二是合成layer

##### DispSync

> DispSync是Android 4.4才加入的类，位置在/frameworks/native/service/surfaceflinger/DispSync.cpp
>
> 引入 DispSync 的目的是为了通过 SF-VSYNC 来模拟 HW-VSYNC 的行为并且通过加入 offset 来让通知时机变得灵活。因此理解整个 DispSync 的流程就可以归结为下面几个部分：SF-VSYNC 通知周期 mPeriod 的计算；SF-VSYNC 的模拟方式以及 SF-VSYNC 传递流程
>
> 主要是偏移，引入理由是：
>
> 1. 降低触摸延迟
> 2. 最大化利用硬件性能，合理分配
> 3. 如果硬件足够牛逼，一个vsync周期内，可以完成绘制到合成到送显，不过各大厂商好像没怎么用
>
> 隶属于sf服务，主要为sf分配vsync信号
>
> **Offset**
>
> VSYNC_APP 和 VSYNC_SF 之间有一个 Offset
>
> Android 12删除了该类
>
> DispSync负责接受hw-vsync，接着发送sw-vsync
>
> - #### DispSyncSource
>
>   > DispSync的核心类
>
> - #### EventThread
>
>   > APP的EventThread
>   >
>   > sf的EventThread

##### Layer/Surface

```c++
void Layer::onFirstRef() {
    // Creates a custom BufferQueue for SurfaceFlingerConsumer to use
      sp<IGraphicBufferProducer> producer;
      sp<IGraphicBufferConsumer> consumer;
      BufferQueue::createBufferQueue(&producer, &consumer);
      mProducer = new MonitoredProducer(producer, mFlinger);
      mSurfaceFlingerConsumer = new SurfaceFlingerConsumer(consumer, mTextureName,
              this);
      mSurfaceFlingerConsumer->setConsumerUsageBits(getEffectiveUsage(0));
      mSurfaceFlingerConsumer->setContentsChangedListener(this);
      mSurfaceFlingerConsumer->setName(mName);
  
  #ifdef TARGET_DISABLE_TRIPLE_BUFFERING
  #warning "disabling triple buffering"
  #else
      mProducer->setMaxDequeuedBufferCount(2);
  #endif
  
      const sp<const DisplayDevice> hw(mFlinger->getDefaultDisplayDevice());
      updateTransformHint(hw);
  }
```

内部封装了一系列操作队列的方法，比如：

- dequeueBuffer获取buffer
- queueBuffer归队
- 

核心是bufferqueue，内部持有fq队列

##### BufferQueue

Android 4.1开始加入的机制，同期加入的还有Choreographer，用来分发vsync信号

至于vsync信号，则是在黄油计划之前就存在的时间，目前收录的Android源码最早可以追溯到1.6版本，可以去源码中搜索EventHub.cpp

黄油计划添加了哪些，draw with vsync

- 新增了Choreographer用于给APP同步vsync信号，Choreographer 的引入，主要是配合 Vsync ，给上层 App 的渲染提供一个稳定的 Message 处理的时机
- handler机制新增了同步屏障以支持优先处理vsync信号
- libgui新增了bufferqueue



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

##### View/ViewRootImpl

```
drawSoftware
```

放到第四篇：应用篇，闲聊View体系

- **SurfaceTexture**

  > SurfaceTexture看起来内部维护了一套bufferqueue，内部依旧持有Surface对象
  >
  > 通常的视图绘制到surface知乎就入队交给sf合成，而SurfaceTexture是入队自己管理了，可以去安排特效或者上屏
  >
  > 看起来surface接受到图形并调用post入列之后，SurfaceTexture会接收到onFrameAvaliable()回调，接着可以选择做其他操作
  >
  > 依旧可以在宿主内再进行其他操作，最终可以选择塞到sf中合成送显？

view体系是Google提供给开发者的一套工具，内部使用canvas，其内部是对skia的再封装

简单了看了一下flutter完全摒弃了view体系，使用skia的api来绘制内容，但在Android上依旧没有摆脱ams、pms构建的组件体系

##### SurfaceView/TextureView

SurfaceView具有独立的绘图Surface,但它仍然属于View树中的子节点，它依附在宿主窗口上，所以它自身的view也是需要绘制到宿主窗口的Surface上的

```java
//SurfaceView.java
@Override
public void draw(Canvas canvas) {
    if (mWindowType != WindowManager.LayoutParams.TYPE_APPLICATION_PANEL) {
        // draw() is not called when SKIP_DRAW is set
        if ((mPrivateFlags & PFLAG_SKIP_DRAW) == 0) {
            // punch a whole in the view-hierarchy below us
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        }
    }
    super.draw(canvas);
}

@Override
protected void dispatchDraw(Canvas canvas) {
    if (mWindowType != WindowManager.LayoutParams.TYPE_APPLICATION_PANEL) {
        // if SKIP_DRAW is cleared, draw() has already punched a hole
        if ((mPrivateFlags & PFLAG_SKIP_DRAW) == PFLAG_SKIP_DRAW) {
            // punch a whole in the view-hierarchy below us
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        }
    }
    super.dispatchDraw(canvas);
}
```

draw和dispatchDraw方法的参数canvas代表的是宿主窗口的绘图表面的画布

除了在宿主窗口上绘制ui，SurfaceView可以在自身的绘图表面上绘制内容，一般的绘制流程如下：

```java
SurfaceView sv = (SurfaceView )findViewById(R.id.surface_view);    
SurfaceHolder sh = sv.getHolder();  
Cavas canvas = sh.lockCanvas()  
 
//Draw something on canvas 
......  
 
sh.unlockCanvasAndPost(canvas);
```

获取canvas进行绘图，比如相机啥的可以直接绘制到bitmap上显示，2D的

在Activity resume的阶段会调用每个view的onAttachedToWindow方法

- onAttachedToWindow，两件事，监听生命周期和滑动事件

  > - 调用getViewRootImpl().addWindowStoppedCallback(this);添加Activity的onStop()监听，如果window已经被覆盖了，那么页面也就没有更新的必要了
  > - 调用observer.addOnScrollChangedListener(mScrollChangedListener);添加滑动事件的监听
  > - 调用updateSurface()更新surface，此方法创建了surface

- 更新方式

  > - lockCanvas，当要更新页面时，调用lockCanvas获取canvas对象，实际上在sf端是获取了一个可用buffer
  > - unlockCanvasAndPost，将buffer入列

总结SurfaceView，渲染自己管，合成依旧是sf，依附的组件归ams管理

##### 硬件加速

- 关闭硬件加速
  - 在ui线程中调用lockAsync获取buffer
  - 调用libSkia.so向buffer绘制渲染
  - 绘制完成调用unlock
  - 调用queueBuffer入队列，交给sf
- 开启硬件加速
  - 参考关闭的流程，绘制的过程交给renderThread执行，ui线程可以处理其他message

##### server之wms

应用程序负责修改绘制窗口中的内容，而WindowManager负责窗口的生命周期、几何属性、坐标变换信息、用户输入焦点、动画等功能。他还管理着窗口状态的变化，如窗口位置、大小、透明度以及Z-order（前后遮盖顺序）等一系列的逻辑判断。这些WindowManager功能由一系列接口或类构成，包括ViewManager、WindowManager、WindowManagerImpl、WindowManagerService等。

#### 三、Note About Process

##### APP to screen from fsp的计算

1. APP绘制到buffer，这个buffer是通过dequeueBuffer()申请的
2. 绘制完成之后调用queueBuffer()将buffer还给surfaceflinger区进程合成，sf回调用acquireBuffer()从bufferqueue拿到一个buffer去合成，fence机制会保证sf拿到的是GPU绘制好的
3. 合成完后调用releaseBuffer()将buffer还给bufferqueue
4. sf拿到合成后的Framebuffer去送显？

> 统计在一秒内该 App 往屏幕刷了多少帧，而在 Android 的世界里，每一帧显示到屏幕的标志是： present fence signal 了，因此计算 App 的 fps 就可以转换为：**一秒内 App 的 Layer 有多少个有效 present fence signal 了（这里有效 present fence 是指，在本次 VSYNC-sf 中该 Layer 有更新的 present fence）**

##### from Android Devloper Blog

1. 应用更新绘图命令列表
2. 应用向GPU发出绘制质量
3. GPU开始绘制
4. sf服务将绘制的buffer开始合成，合成完成后交给HAL显示
5. 屏幕显示

##### from 博客园

Android系统的UI从绘制到显示到屏幕是分两步进行的：

第一步是在Android应用程序进程这一侧进行的；

第二步是在SurfaceFlinger进程这一侧进行的。

前一步将UI绘制到一个图形缓冲区中，并且将该图形缓冲区交给后一步进行合成以及显示在屏幕中。

其中，后一步的UI合成一直都是以硬件加速方式完成的。

##### from 高爷

1. 每一帧处理的流程：接收到 Vsync 信号回调-> UI Thread –> RenderThread –> SurfaceFlinger(图中未显示)
2. UI Thread 和 RenderThread 就可以完成 App 一帧的渲染，渲染完的 Buffer 抛给 SurfaceFlinger 去合成，然后我们就可以在屏幕上看到这一帧了
3. 可以看到桌面滑动的每一帧耗时都很短（Ui Thread 耗时 + RenderThread 耗时），但是由于 Vsync 的存在，每一帧都会等到 Vsync 才会去做处理
4. sf职责

   > 当 VSYNC 信号到达时，SurfaceFlinger 会遍历它的层列表，以寻找新的缓冲区。如果找到新的缓冲区，它会获取该缓冲区；否则，它会继续使用以前获取的缓冲区。SurfaceFlinger 必须始终显示内容，因此它会保留一个缓冲区。如果在某个层上没有提交缓冲区，则该层会被忽略。SurfaceFlinger 在收集可见层的所有缓冲区之后，便会询问 Hardware Composer 应如何进行合成。

##### Porcess Overview from bob

前置条件

- Activity被创建后，Android会为APP进程创建两条线程
  - 主线程，ActivityThread，可以称为主线程，也可以称为UI线程。主线程运行这Handler机制，我们所有的触摸事件绘制命令等都是由handler处理
  - 渲染线程，RenderThread
  - 创建ViewRootImpl和Choreographer，Choreographer注册一系列回调，其中就包含TRAVERSAL回调
  - 在wms中创建window/surface
  - 在sf中创建layer，layer中包含BufferQueue，在Android S中发生变化

vsync到来时（接受与处理）

- APP端 vsync-app

  - surface端

    > 游戏或者flutter都是没有render线程的
    >
    > ui线程在游戏和flutter中，只是用来传递input事件，具体的渲染是自己控制的
    >
    > Flutter 的思路跟游戏开发的思路差不多，不依赖具体的平台，自建渲染管道，更新快，跨平台优势明显
    >
    > 在surface中可以监听vsync信号，调用lock获取一个可用的buffer，调用skia或者egl渲染完成后，调用unlockAndPost()/eglSwapBuffer()将buffer提交到sf，等待下一次vsync来临后合成
    >
    > 调用

  - view体系

    > render线程
    >
    > dequeueBuffer
    >
    > queueBuffer

- sf端 vsync-sf

请求vsync时机

- 在APP端
  -  invalidate 和 requestLayout最终都会调用到viewrootimpl.scheduleTraversals()方法，在此方法中会调用requestNextVsync()
  - 其他的诸如动画之类的也会触发请求vsync信号

Choreographer的作用

- 当收到 Vsync 信号时，去调用使用者通过 postCallback 设置的回调函数。比如**CALLBACK_TRAVERSAL**

在一次vsync的周期中，app和sf的交互有三点

1. Vsync 信号的接收和处理
2. RenderThread 的 dequeueBuffer
3. RenderThread 的 queueBuffer

1. 当我们在activity调用setContentView绑定xml时，经过跨进程通信后最终会向wms申请到一个window
1. 在应用接受到vsync时，调用dequeueBuffer()方法获取状态为FREE的buffer，一旦获取成功，buffer状态改变为DEQUEUED，表示被出列正在被生产者使用。绘制完成后调用queueBuffer()方法塞入队列，此时buffer状态为quque，表示该Buffer被生产者填充了数据，并且入队到BufferQueue了
1. 下一次vsync信号到来时，sf调用acquireBuffer()方法查找可合成的buffer调用hwc去合成，此时buffer状态为ACQUIRED，表示该Buffer被消费者获取了，该Buffer的所有权属于消费者
1. sf调用hwc模块合成，合成完成后hwc会完成送显

每个window内部都有一个bufferqueue队列，接收到vsync信号时

#### 四、Roast for Google

- 学习最大的难点之一在于，Android使用Linux内核，并在此基础上进行裁剪和添加功能，所以得去Android上游看看Linux的LTS主线版本，又因为Google提供了HAL层，所以驱动基本上都是下游实现。在消费级市场中高通无疑是最大的下游厂商，因为SOC是高通提供的，内核部分高通同样关注上游Linux主线版本，形成自己的内核caf。接着实现HAL层的接口，再接着给高通的下游厂商，也就是国内的小米OV等手机厂商，手机。以上流程就会导致，你想了解Android某个功能依赖Linux哪个API实现的你就得关注Linux的版本，要想知道某个硬件实现具体是什么东西，比如HWC是什么，就得关注高通一类的下游厂商的代码实现，接着才是Google的aosp源码，aosp和msm-aosp都得看

- 本文的目标是希望能够从头到尾的介绍一下整个显示流程，在执行的过程中发现话题过于庞大，从结果来看完成的不是很好，但个人在这个过程中还是有不少收获。人总是会进步的，相信过几年回头看自己写的文章时，大部分知识点我都已经理解了，那时

- 这些内容网上早已有前辈整理输出了，本篇文章只是做了整理，观点还是来自于各个前辈

- 忽略activity、service等组件的概念，在图形系统中，对应用户进程来说，Android系统提供的就只有window，activity是一个window，dialog也是一个window，surface同样是一个window

- 注1：bufferqueue在Android12已被更新

- 注2：dispsync在Android12已被删除

- 忽略的内容

  > - GPU之所以能够识别graphicbuffer因为父类是ANativeWindow
  > - GraphicBuffer的包装类是BufferSlot，状态则用BufferState
  > - BufferQueue具体实现是BufferBufferCore类
  > - BufferQueue生产者消费者接口以及具体实现
  >
  > APP忽略回调
  >
  > - 来自animation的回调
  > - 来自input的回调处理
  >
  > texture的介绍
  >
  > sf的启动，layer、bufferqueue的创建过程
  >
  > 各个层之间的调用过程、逻辑处理等
  >
  > 1是随着版本的更新，实现逻辑一直在变，实在没有精力去跟源码

#### 五、Note

- 创建一个activity之后，ams、wms、sf这些系统做了些什么
  - 创建activity之前，zygote进程
  - AMS为activity创建ViewRootImpl
  - WMS为ViewRootImpl创建Window/Surface
  - SF为Surface创建Layer
- vsync到来时，每一层都做了些什么
  - APP
    - 主线程处于 Sleep 状态，等待 Vsync 信号；我们知道主线程是消息驱动的，也就是handler机制，有消息就处理没消息就睡觉
    - sf向messageQueue添加同步屏障，通知vsync信号？
    - Vsync 信号到来，主线程被唤醒，Choreographer 回调 FrameDisplayEventReceiver.onVsync 开始一帧的绘制
    - 处理 App 这一帧的 Input/Animation/Traversal 事件(如果有的话)
    - 主线程与渲染线程同步渲染数据，同步结束后，主线程结束一帧的绘制，可以继续处理下一个 Message(如果有的话，IdleHandler 如果不为空，这时候也会触发处理)，或者进入 Sleep 状态等待下一个 Vsync
    - 渲染线程首先需要从 BufferQueue 里面取一个 Buffer(dequeueBuffer) , 进行数据处理之后，调用 OpenGL 相关的函数，真正地进行渲染操作，然后将这个渲染好的 Buffer 还给 BufferQueue (queueBuffer) ,
    - SurfaceFlinger 在 Vsync-SF 到了之后，将所有准备好的 Buffer 取出进行合成(这个流程在讲 SurfaceFlinger 的时候会提到)
    - 调用skia或者OpenGL渲染画面
  - SurfaceFlinger 
    - 当 VSYNC 信号到达时，SurfaceFlinger 会遍历它的层列表，以寻找新的缓冲区。如果找到新的缓冲区，它会获取该缓冲区；否则，它会继续使用以前获取的缓冲区。SurfaceFlinger 必须始终显示内容，因此它会保留一个缓冲区。如果在某个层上没有提交缓冲区，则该层会被忽略。
    - SurfaceFlinger 在收集可见层的所有缓冲区之后，便会询问 Hardware Composer 应如何进行合成。
    - 调用hwc合成画面，合成完成后也由hwc去送显
- DispSync是如何将事件分发给Choreographer的？EventThread是干嘛的
- sf调用hwc合成完成后谁去送显？release方法时谁调用的以及，合成完成后保存到哪里？
- surface和window，理论上window属于Java层归wms管理，surface是那一层的，native的吧，应该是对应关系吧
- view体系由Choreographer来指引，最终有viewrootimpl来执行draw系列，那么window/surface是如何监听vsync信号并且调用绘制的呢？
- surfaceview如何停止绘制？
- view体系中surface是如何创建的
- 书籍
  - [《深入理解Android内核设计思想》- 林学森](https://book.douban.com/subject/25921329/)
  
- 博主
  - [《Weishu's Notes》- 田维术（太极/两仪作者）](https://weishu.me/)
  - [《Android显示系列》- 努比亚团队](https://www.jianshu.com/c/3a4d92743e88)
  - [《Systrace系列》- 高爷（性能优化专家）](https://www.androidperformance.com/2019/05/28/Android-Systrace-About) 
  - [《Android 12 BlastBufferQueue系列》- 大天使之剑](https://www.jianshu.com/u/124e5f361305)
  - [《DRM与BufferQueue系列》- 何小龙](https://blog.csdn.net/hexiaolong2009?type=blog)
  - [《Android图形显示系列》- 夕阳叹](https://blog.csdn.net/jxt1234and2010/category_2826805.html)
  - [《王小二的Android站》-TCL王总](https://www.jianshu.com/u/fd0b722ce11f)
  - [《Silence.Slow.Simple专栏》- simowce](https://blog.simowce.com/archives/)
  - [《Android SurfaceFlinger 学习之路》- windrunnerlihuan](https://windrunnerlihuan.com/archives/page/2/)
  - [《Android 12(S) 图像显示系统》- 二的次方](https://www.cnblogs.com/roger-yu/p/15641545.html)
- 文章
  - [深入理解Flutter的图形图像绘制原理 - OPPO数智技术](https://segmentfault.com/a/1190000038827450)
  - [Android性能优化：定性和定位Android图形性能问题 - 飞起来_飞过来](https://juejin.cn/post/7096288511053004830)





