## Note V2

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



## Note

#### 疑问区❓

- BufferQueue在哪个进程？貌似是一个单独的service，但是否是单独进程还需确认

- 创建一个activity或者surfaceview会申请一个graphicbuffer吗？graphicbuffer

- 既然graphicbuffer贯穿全文，sf和GPU都可以用，那么native从哪一层开始创建/拥有了它，surface还是layer

- HWC1和HWC2区别

- APP通过Choreographer感知vsync信号，进而去更新Render List，那么和渲染线程里面渲染动作是同一个vsync周期吗？

- 每个app进程都会持有Window对象，Window对象又等同于surface等同于layer，那么，每一层layer都有一个graphicbuffer吗？

  > 答：都有好几个graphicbuffer，从surface.cpp源码可以发现surface持有bufferqueue对象

- 每日更新

  > 图形显示流程大致分为三个阶段，bufferqueue和graphicbuffer
  >
  > - 绘制/渲染阶段
  >
  >   > 此阶段由上层应用，比如OpenGL/Vulkan/Skia等引擎绘制到graphicbuffer
  >   >
  >   > 对应应该是从wms获取一个Surface
  >
  > - 合成阶段
  >
  >   > 此阶段由surfaceflinger调用OpenGL或者HWC来合成layer
  >   >
  >   > 此阶段对应数据应该是layer
  >
  > - 送显阶段
  >
  >   > 此阶段调用Android的显示框架
  >   >
  >   > Android显示框架进化过程，从Framebuffer架构进化到Google自己设计的ADF框架
  >   >
  >   > 再从ADF框架进化到DRM框架
  >
  > 通过gralloc管理图形内存
  >
  > 剩下还有fence同步机制和传递数据的bufferqueue

  > 当你创建了View或者将XML添加到Activity时，

- GPU

  > GPU的职责是根据一个三维场景中的顶点、纹理等信息，转换成一张二维图像
  >
  > 通常把一个渲染流程分3个阶段：
  >
  > - 应用阶段
  >
  >   > 应用阶段由CPU完成，几何阶段 和 光栅化阶段 由GPU完成。
  >
  > - 几何阶段
  >
  >   > - 顶点数据
  >   >
  >   > OpenGL规定图形是由点，线，三角形组成
  >   >
  >   > 对应1个顶点，2个顶点，3个顶点。这三个又称为**图元**。点和线一般在2D场景使用，在3D场景，基本是由N多个三角形，来拼接成一个物体模型
  >   >
  >   > 通常表示为顶点数组**VertexArray**和顶点缓冲区**VertexBuffer**
  >   >
  >   > 在3D开发中，一组顶点数据，一般会包含顶点坐标 + 纹理坐标
  >   >
  >   > - 顶点着色器
  >   >
  >   > 顶点着色器是GPU内部流水线的第一步，每个顶点，都会调用一次顶点着色器
  >   > 它的主要工作：坐标转换和逐顶点光照，以及准备后续阶段（如片元着色器）的数据。
  >
  > - 光栅化阶段
  >
  >   > 计算每个图元都覆盖了哪些像素，然后给这些像素着色
  >   >
  >   > - 光栅化就是把顶点数据转换为片元的过程。具有将图转化为一个个栅格组成的图像的作用。片元中的每一个元素对应于帧缓冲区的一个像素
  >   > - 光栅化其实是一种将几何图元变为二维图像的过程。该过程包含了两部分的工作。第一部分工作：决定窗口坐标中的哪些整型栅格区域被基本图元占用；第二部分工作：分配一个颜色值和一个深度值到各个区域。光栅化过程产生的是片元
  >   > - 3d 坐标如何转换成 2D的屏幕坐标

- HWC（Hardware Composer）

  > Google的提供的协议标准，对手机SOC来说，通常指的是DPU（Display Processing Unit）
  >
  > 如果SOC没有DPU，那么就调用GPU来合成
  >
  > 如果连GPU也没有，那也不要紧，libGLES_android库可以支持上层调用OpenGL ES的函数，不过执行者是CPU而已
  >
  > 如果连CPU也没有，那..那系统也启动不了
  >
  > 

- OpenGL ES

  > 规定GPU实现标准协议，掌握了OpenGL ES的语法
  >
  > - OpenGL是一个跨编程语言、跨平台的编程图形程序接口，它将计算机的资源抽象称为一个个OpenGL的对象，对这些资源的操作抽象为一个个的OpenGL指令
  >
  > - OpenGL ES是OpenGL三维图形API的子集，针对手机、PDA和游戏主机等嵌入式设备而设计的，去除了许多不必要和性能较低的API接口
  >
  > - [EGL](https://www.khronos.org/registry/EGL/)
  >
  >   > [api地址](https://www.khronos.org/registry/EGL/sdk/docs/man/html/)
  >   >
  >   > eglCreateClientImage对象是CPU和GPU共享内存对象
  >
  > - 在Android中使用ES的方式
  >
  >   > 需要使用EGL，也就是Android版本的OpenGL ES具体实现
  >   >
  >   > - Java层
  >   >
  >   >   > - 自定义类直接继承GLSurfaceView，然后我们重点就主要写Render方法就行
  >   >   > - 自己搭建EGL（是OpenGL ES和本地窗口系统的接口，不同平台上EGL配置是不一样的，而OpenGL的调用方式是一致的，就是说：OpenGL跨平台就是依赖于EGL接口）环境，然后开启自己的EGL线程，后续使用和继承GLSurfaceView基本是一致的
  >   >
  >   > - native层

- Skia

  > 一套2D渲染的api协议，类似的还有cairo，Skia的核心是canvas
  >
  > Skia依赖的第三方库众多，包括字体解析库freeType,图片编解码库libjpeg-turbo、libpng、libgifocode、libwebp和pdf文档处理库fpdfemb等。Skia支持多种软硬件平台，既支持ARM和x86指令集，也支持OpenGL、Metal和Vulkan低级图形接口。
  >
  > https://skia.googlesource.com/skia/

- SurfaceFlinger

  > Fence同步机制，APP进程、surfaceflinger进程、hwc、GPU驱动，四个方面的同步机制
  >
  > 在文章开始之前，有必要提醒了解什么是生产者消费者模式
  >
  > 了解几个概念，生产者消费者，绘制渲染合成送显
  >
  > 摒弃几个概念，hal，hwc，GPU
  >
  > 本篇文章将会从屏幕的角度展开，
  >
  > 文章中隐藏了大量的代码细节，在绘制过程中任何一个环节都可以单独写一篇文章来介绍
  >
  > 为后续的源码阅读提供支撑
  >
  > 以架构设计来看
  >
  > 以服务进程来看
  >
  > - 绘制图层
  >
  >   > APP通过OpenGL指令把图形结果绘制到graphicbuffer对象
  >   >
  >   > layer最终绘制
  >
  > - 合成图层
  >
  >   > surfaceflinger会调用GPU或者hwc进行合成
  >   >
  >   > framebuffersurface最终合成
  >
  > - 送显
  >
  >   > 交由framebuffer或者drm

- Fence同步机制

  > **这就需要一种不仅是跨进程的，也是跨硬件的同步机制: Fence 机制**

- **Graphic Buffer**

  > 硬件共享内存，由Gralloc分配，由bufferqueue管理
  >
  > 在板子上，CPU和GPU和DPU属于不同的硬件，graphic buffer这块内存被这几个硬件共享，这样就能减少复制，提高效率
  >
  > - **Server端**：**Allocate**
  >
  >   > 分配Buffer，由 SurfaceFlinger负责（也可以用单独的服务，这个可以配置，后面默认就用SF）；**只有两个接口：alloc与free**
  >   >
  >   > 为了减小SF的负载，Android S开始强制**Client端分配buffer，**而linux上早已如此处理。
  >
  > - **Client端**：**Mapper**
  >
  >   > GraphicBufferMapper ：应用使用Buffer，由GPU填东西了；**接口主要为：importBuffer（之前是registerBuffer），lock**，导入当前进程地址空间。
  >
  > - **BufferQueue**
  >
  >   > **BufferQueue对App端的接口为IGraphicBufferProducer，实现类为Surface**，对SurfaceFlinger端的接口为IGraphicBufferConsumer，实现类为SurfaceFlingerConsumer（最新版本改名了，但不影响讨论）。
  >   >
  >   > BufferQueue中对每一个GraphiBuffer都有BufferState标记着它的状态，
  >   >
  >   > > 比如new Surface是不会真正分配的，只有在**dequeuBuffer的时候才会请求分配，此时会调用**new GraphicBuffer则会真正分配。
  >   > > 在状态分配时，对于Client端有dequeueBuffer(请求), queueBuffer（绘制结束，发送至服务端） ；

- Android显示框架演变

  > FB框架->[ADF](https://lwn.net/Articles/565422/)->DRM
  >
  > linux给userspace提供的屏幕操作的接口，通过这组接口来向屏幕来提交我们所绘制的画面
  >
  > Android的display架构中是谁在使用这组api呢？
  >
  > - DRM
  >
  >   > DRM是linux内核的一个子系统，它提供一组API，用户空间程序可以通过它发送画面数据到GPU或者专用图形处理硬件（如高通的MDP）
  >   >
  >   > drm 是一个管理 GPU 的显示框架
  >   > 在内核级别提供内存管理，中断处理， DMA控制
  >
  > - ADF
  >
  >   > ADF(Atomic Display Framework)是Google新推出的一个关于Display驱动的框架
  >
  > | **SurfaceView**      | **Java**                                                     | **/[frameworks](http://aospxref.com/android-12.0.0_r3/xref/frameworks/)/[base](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/)/[core](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/)/[java](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/java/)/[android](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/java/android/)/[view](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/java/android/view/)/[SurfaceView.java](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/java/android/view/SurfaceView.java)** |
  > | -------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
  > | **JNI**              | **无**                                                       |                                                              |
  > | **SurfaceControl**   | **Java**                                                     | **/[frameworks](http://aospxref.com/android-12.0.0_r3/xref/frameworks/)/[base](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/)/[core](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/)/[java](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/java/)/[android](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/java/android/)/[view](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/java/android/view/)/[SurfaceControl.java](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/java/android/view/SurfaceControl.java)** |
  > | **JNI**              | **/[frameworks](http://aospxref.com/android-12.0.0_r3/xref/frameworks/)/[base](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/)/[core](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/)/[jni](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/jni/)/[android_view_SurfaceControl.cpp](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/jni/android_view_SurfaceControl.cpp)** |                                                              |
  > | **Surface**          | **Java**                                                     | **/[frameworks](http://aospxref.com/android-12.0.0_r3/xref/frameworks/)/[base](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/)/[core](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/)/[java](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/java/)/[android](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/java/android/)/[view](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/java/android/view/)/[Surface.java](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/java/android/view/Surface.java)** |
  > | **JNI**              | **/[frameworks](http://aospxref.com/android-12.0.0_r3/xref/frameworks/)/[base](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/)/[core](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/)/[jni](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/jni/)/[android_view_Surface.cpp](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/jni/android_view_Surface.cpp)** |                                                              |
  > | **BLASTBufferQueue** | **Java**                                                     | **/[frameworks](http://aospxref.com/android-12.0.0_r3/xref/frameworks/)/[base](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/)/[graphics](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/graphics/)/[java](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/graphics/java/)/[android](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/graphics/java/android/)/[graphics](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/graphics/java/android/graphics/)/[BLASTBufferQueue.java](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/graphics/java/android/graphics/BLASTBufferQueue.java)** |
  > | **JNI**              | **/[frameworks](http://aospxref.com/android-12.0.0_r3/xref/frameworks/)/[base](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/)/[core](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/)/[jni](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/jni/)/[android_graphics_BLASTBufferQueue.cpp](http://aospxref.com/android-12.0.0_r3/xref/frameworks/base/core/jni/android_graphics_BLASTBufferQueue.cpp)** |                                                              |

- 沧浪之水-**概念**

  > **在这里各种Surface，Window，View、Layer实在是太乱了，下面就区分一下这些概念**
  >
  > Window -> DecorView-> ViewRootImpl -> WindowState -> Surface -> Layer 是一一对应的。
  > 一般的Activity包括的多个View会组成View hierachy的树形结构，只有最顶层的DecorView，也就是根结点视图，才是对WMS可见的，即有对应的Window和WindowState。
  >
  > 一个应用程序窗口分别位于应用程序进程和WMS服务中的**两个Surface对象**有什么区别呢？
  > 虽然它们都是用来操作位于SurfaceFlinger服务中的同一个Layer对象的，不过，它们的操作方式却不一样。具体来说，就是位于应用程序进程这一侧的Surface对象负责绘制应用程序窗口的UI，即往应用程序窗口的图形缓冲区填充UI数据，而位于WMS服务这一侧的Surface对象负责设置应用程序窗口的属性，例如位置、大小等属性。
  > 这两种不同的操作方式分别是通过C++层的Surface对象和SurfaceControl对象来完成的，因此，位于应用程序进程和WMS服务中的两个Surface对象的用法是有区别的。之所以会有这样的区别，是因为**绘制应用程序窗口是独立的，由应用程序进程来完即可，而设置应用程序窗口的属性却需要全局考虑，即需要由WMS服务来统筹安排**。

- 沧浪之水-绘制（**客户端，对应于DRM**）

  > 
  >
  > 绘制完成之后，Android应用程序进程再调用前面获得的Canvas的成员函数unlockAndPost请求显示在屏幕中，其本质上是向SurfaceFlinger服务queue一个Graphic Buffer，以便SurfaceFlinger服务可以对Graphic Buffer的内容进行合成，以及显示到屏幕上去
  >
  > - **软件绘制**
  >
  >   > 软件绘制流程比较简单
  >   >
  >   > 在Android应用程序进程这一侧，每一个窗口都关联有一个Surface。每当窗口需要绘制UI时，就会调用其关联的Surface的成员函数lock获得一个Canvas，其本质上是向SurfaceFlinger服务dequeue一个Graphic Buffer。
  >   > Canvas封装了由Skia提供的2D UI绘制接口，并且都是在前面获得的Graphic Buffer上面进行绘制的，这个Canvas的底层是一个bitmap，也就是说，绘制都发生在这个Bitmap上。绘制完成之后，Android应用程序进程再调用前面获得的Canvas的成员函数unlockAndPost请求显示在屏幕中，其本质上是向SurfaceFlinger服务queue一个Graphic Buffer，以便SurfaceFlinger服务可以对Graphic Buffer的内容进行合成，以及显示到屏幕上去。
  >
  > - **硬件绘制**
  >
  >   > 硬件加速渲染和软件渲染一样，在开始渲染之前，都是要先向SurfaceFlinger服务dequeue一个Graphic Buffer。不过对硬件加速渲染来说，这个Graphic Buffer会被封装成一个ANativeWindow，并且传递给Open GL进行硬件加速渲染环境初始化。
  >   > 在Android系统中，ANativeWindow和Surface可以是认为等价的，只不过是ANativeWindow常用于Native层中，而Surface常用于Java层中。 Open GL获得了一个ANativeWindow，并且进行了硬件加速渲染环境初始化工作之后，Android应用程序就可以调用Open GL提供的API进行UI绘制了，绘制出来内容就保存在前面获得的Graphic Buffer中。
  >   > 当绘制完毕，Android应用程序再调用libegl库（一般由第三方提供）的eglSwapBuffer接口请求将绘制好的UI显示到屏幕中，其本质上与软件渲染过程是一样的。
  >
  > - **MainThread和RenderThread的分离**
  >
  >   > 在Android 5.0之前，Android应用程序的Main Thread不仅负责用户输入，同时也是一个OpenGL线程，也负责渲染UI。通过引进Render Thread，我们就可以将UI渲染工作从Main Thread释放出来，交由Render Thread来处理，从而也使得Main Thread可以更专注高效地处理用户输入，这样使得在提高UI绘制效率的同时，也使得UI具有更高的响应性。
  >   >
  >   > 
  >   >
  >   > 对于上层应用而言，UI thread仍然是Main Thread，它并不清楚Render Thread的存在，而**对于SurfaceView，UI thread不是Main Thread，而是重新启用了一个新的线程**。
  >
  > - **Render Node**
  >
  >   > Android 5之后，在Android应用程序窗口中，每一个View都抽象为一个[Render Node](https://developer.android.com/reference/android/graphics/RenderNode)
  >   >
  >   > 如果一个View设置有Background，这个Background也被抽象为一个Render Node
  >   >
  >   > app在onDraw方法中是创建displayList
  >   >
  >   > 
  >   >
  >   > 每一个Render Node都关联有一个Display List Renderer。这里又涉及到另外一个概念——**Display List**。Display List是一个绘制命令缓冲区。也就是说，当View的成员函数onDraw被调用时，我们调用通过参数传递进来的Canvas的drawXXX成员函数绘制图形时，我们实际上只是将对应的绘制命令以及参数保存在一个Display List中。接下来再通过Display List Renderer执行这个Display List的命令，这个过程称为Display List Replay。
  >   > 引进Display List的概念有什么好处呢？主要是两个好处。
  >   > 第一个好处是在下一帧绘制中，如果一个View的内容不需要更新，那么就不用重建它的Display List，也就是不需要调用它的onDraw成员函数。
  >   > 第二个好处是在下一帧中，如果一个View仅仅是一些简单的属性发生变化，例如位置和Alpha值发生变化，那么也无需要重建它的Display List，只需要在上一次建立的Display List中修改一下对应的属性就可以了，这也意味着不需要调用它的onDraw成员函数。这两个好处使用在绘制应用程序窗口的一帧时，省去很多应用程序代码的执行，也就是大大地节省了CPU的执行时间。
  >   >
  >   > 
  >   >
  >   > 注意，只有使用硬件加速渲染的View，才会关联有Render Node，也就才会使用到Display List。对于使用了软件方式来渲染的View，具体的做法是创建一个新的Canvas，这个Canvas的底层是一个Bitmap，也就是说，绘制都发生在这个Bitmap上。绘制完成之后，这个Bitmap再被记录在其Parent View的Display List中。而当Parent View的Display List的命令被执行时，记录在里面的Bitmap再通过Open GL命令来绘制。

- 沧浪之水-合成（**服务端，对应于KMS**）

  > FB框架到DRM框架是整个框架的改变，对于上层用户来说，’/dev/fb0’是找不到了，但在drm框架下兼容FB接口还是非常简单的，主要看OEM厂商的意愿了。
  >
  > *还需要兼容这些接口吗？那就看谁在使用FB/ADF/DRM的接口呢？*
  >
  > **其一，Android启动后，hwcomposer就是唯一的使用者；**
  >
  > **其二，Android启动前，大约有下面3个情况：**
  >
  > - ***Recovery——显示调用比较简单，可以通过这个了解drm的基本显示流程；***
  > - ***关机充电；***
  > - ***开机从kernel到android的启动前这段时间；基本不会主动绘制（通常是一个logo，由bootloader传过来），这是在kernel完成的***
  >
  > **可以看出来使用这些接口都是系统完成的，由谷歌自己完成了升级，第三方是没有权限调这样的接口的，兼容是没必要了。**

- 努比亚团队

  > 在Android系统上应用要绘制一个画面，首先要向SurfaceFlinger申请一个画布，这个画布所使用的buffer是SurfaceFlinger通过allocator service（vendor.qti.hardware.display.allocator-service）来分配出来的，allocator service是通过ION从kernel开辟出来的一块共享内存，这里申请的都是每个图层所拥有独立buffer, 这个buffer会共享到HWC Service里，由SurfaceFlinger来作为中枢控制这块buffer的所有权，其所有权会随状态不同在App, SurfaceFlinger, HWC Service间来回流转。【有误】
  >
  > - App到SurfaceFlinger
  >
  >   > 应用首先通过Surface的接口向SurfaceFlinger申请一块buffer,  需要留意的是Surface刚创建时是不会有buffer被alloc出来的，只有应用第一次要绘制画面时dequeueBuffer会让SurfaceFlinger去alloc buffer, 在应用侧会通过importBuffer把这块内存映射到应用的进程空间来
  >   >
  >   > 
  >   >
  >   > 之后App通过dequeueBuffer拿到画布， 通过queueBuffer来提交绘制好的数据
  >   >
  >   > 
  >   >
  >   > HWC Service负责将SurfaceFlinger送来的图层做合成，形成最终的画面，然后通过drm的接口更新到屏幕上去（注意：在DRM一章中给出的使用DRM的例子子demo的是通过page flip方式提交数据的，但hwc service使用的是另一api atomic commit的方式提交数据的，drm本身并不只有一种方式提交画面）
  >
  > - SurfaceFlinger到HWC Service
  >
  >   > HWC Service的代码位置在 hardware/qcom/display, HWC Service使用libdrm提交帧数据的地方我们可以在systrace上观察到：
  >
  > - HWC Service到kernel
  >
  >   > hwc service通过drmModeAtomicCommit接口向kernel提交合成数据：
  >   >
  >   > drmModeAtomicCommit通过ioctl调用到kernel：
  >   >
  >   > kernel/msm-5.4/techpack/display/msm/msm_atomic.c
  >
  > 在本章中我们了解了APP绘画的画布是由SurfaceFlinger提供的，而画布是一块共享内存，APP向SurfaceFlinger申请到画布，是将共享内存的地址映射到自身进程空间。 App负责在画布上作画，画完的作品提交给SurfaceFlinger， 这个提交操作并不是把内存复制一份给SurfaceFlinger，而是把共享内存的控制权交还给SurfaceFlinger，  SurfaceFlinger把拿来的多个应用的共享内存再送给HWC Service去合成，   HWC Service把合成的数据交给DRM去输出完成app画面显示到屏幕上的过程。为了更有效地利用时间这样的共享内存不止一份，可能有两份或三份，即常说的double buffering, triple buffering.
  >
  > 那么我们就需要设计一个机制可以管理buffer的控制权，这个就是BufferQueue.
  >
  > - BufferQueue
  >
  >   > 在BufferQueue的设计中，一个buffer的状态有以下几种：
  >   >
  >   > **FREE** ：表示该buffer可以给到应用程序，由应用程序来绘画
  >   > **DEQUEUED**:表示该buffer的控制权已经给到应用程序侧，这个状态下应用程序可以在上面绘画了
  >   > **QUEUED**: 表示该buffer已经由应用程序绘画完成，buffer的控制权已经回到SurfaceFlinger手上了
  >   > **ACQUIRED**:表示该buffer已经交由HWC Service去合成了，这时控制权已给到HWC Service了
  >
  > - 画布的申请
  >
  >   > 在Android系统中每个Activity都有一个独立的画布（在应用侧称为Surface,在SurfaceFlinger侧称为Layer）， 无论这个Activity安排了多么复杂的view结构，它们最终都是被画在了所属Activity的这块画布上，当然也有一个例外，SurfaceView是有自已独立的画布的，但此处我们先只讨论Activity画布的建立过程。
  >
  > - 绘制的流程
  >
  >   > 首先App每次开始绘画都是收到一个vsync信号才会开始绘图（这里暂不讨论SurfaceView自主上帧的情况），应用是通过Choreographer来感知vsync信号, 在ViewRootImpl里向Choreographer注册一个callback, 每当有vsync信号来时会执行mTraversalRunnable:
  >   >
  >   > 































标题：为什么关机了还能显示充电画面？

SurfaceFlinger图形系统

标题：View和ViewGroup，图像容器

#### **概念**

> **在这里各种Surface，Window，View、Layer实在是太乱了，下面就区分一下这些概念**
>
> Window -> DecorView-> ViewRootImpl -> WindowState -> Surface -> Layer 是一一对应的。
> 一般的Activity包括的多个View会组成View hierachy的树形结构，只有最顶层的DecorView，也就是根结点视图，才是对WMS可见的，即有对应的Window和WindowState。
>
> 一个应用程序窗口分别位于应用程序进程和WMS服务中的**两个Surface对象**有什么区别呢？
> 虽然它们都是用来操作位于SurfaceFlinger服务中的同一个Layer对象的，不过，它们的操作方式却不一样。具体来说，就是位于应用程序进程这一侧的Surface对象负责绘制应用程序窗口的UI，即往应用程序窗口的图形缓冲区填充UI数据，而位于WMS服务这一侧的Surface对象负责设置应用程序窗口的属性，例如位置、大小等属性。
> 这两种不同的操作方式分别是通过C++层的Surface对象和SurfaceControl对象来完成的，因此，位于应用程序进程和WMS服务中的两个Surface对象的用法是有区别的。之所以会有这样的区别，是因为**绘制应用程序窗口是独立的，由应用程序进程来完即可，而设置应用程序窗口的属性却需要全局考虑，即需要由WMS服务来统筹安排**。

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

> - 理解Android图形系统并不是一件轻松的事情，Google发布的是Android版本，内核部分拉取的是上游Linux最新的LTS版本，对其做裁剪和增加新功能，下游是以高通为首的芯片供应商，Google定义的HWC都是高通在实现，接着往下才是手机厂商，高通就把google的aosp的代码同步回去，经过修改（硬件抽象层、系统框架、通讯层），形成自己的版本，也就是msm-aosp，手机厂商拿到的通常是msm-aosp，所以我们在学习的时候就很痛苦，你不但要看Google的AOSP，还要看下游供应商是如何实现的，要看如何实现的还得去查高通display框架设计，不同的版本可能还会有变化，就很痛苦
>
> - Android显示框架几经演变，从Linux FB框架到自研的ADF框架，再到现在的Linux DRM/KMS框架，HWC也从1.0进化到2.0版本
>
> - 我写文章的目的首先是自己了解，接着才是，本着文章的是让读懂的原则
>
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
>
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

## TEMP

图形渲染要经过哪几个阶段？

#### what

- 硬件

  > - GPU 3D图形引擎
  >
  >   > 职责：根据一个三维场景中的顶点、纹理等信息，转换成一张二维图像
  >   >
  >   > 三个阶段：应用阶段、几何阶段、光栅化阶段
  >   >
  >   > 在操作系统中表现为OpenGL ES，具体API为[EGL](https://www.khronos.org/registry/EGL/)
  >
  > - 2D图形引擎
  >
  >   > Google抽象的合成组件，在硬件实现中，通常是2D图形引擎，以瑞芯开发板举例
  >   >
  >   > 2D引擎，在操作系统中表现为HWC
  >
  > - CPU
  >
  >   > CPU也被列进来的原因是它是兜底的执行者，一部Android设备中可以没有GPU，使用CPU进行图形渲染，也可以没有HWC，CPU可以处理所有的事情，脏活累活都是它的

- 系统

  > - GraphicBuffer流程过程

- 框架策略

#### test

- 一、开篇

  > 写引言部分
  >
  > 绘制、合成、送显
  >
  > 前两篇关注的是送显之后的显示方式，
  >
  > 实际上，图形显示流程大致分为三个阶段，一帧图像数据的诞生，才是图形系统中最复杂的部分
  >
  > 绘制
  >
  > 严格来说，送显方案不止两种，驱动直连才是最基本的方式，刚刚探讨的两种方案都是在此基础上调用API。不过这是硬件工程师考虑的事情，离普通应用开发工程师实在是太远，所以并没有放在文中讨论
  >
  > fb是如何被生产出来的
  >
  > **一块板子上有哪些硬件？介绍大致的流程以及硬件功能**
  >
  > 先来认识一下硬件有哪些
  >
  > 有CPU、GPU、2d引擎等
  >
  > Linux内核将它们连接在一起，通过fence方式
  >
  > 用户空间的
  >
  > 软件是基于硬件，在讨论fb流传过程之前，我们要先对认识一下硬件
  >
  > 由于消费级芯片（麒麟/天玑/骁龙）公开资料比较少，我们随便找个工业级瑞芯[RK3399](http://opensource.rock-chips.com/images/d/d7/Rockchip_RK3399_Datasheet_V2.1-20200323.pdf)来举例
  >
  > 我是图片
  >
  > 从硬件的角度来说，无非是共享一块内存，CPU把指令发送给GPU，GPU处理完了还给CPU，接着调用HWC进行合成，合成完去送显
  >
  > 我们这里简单点把CPU等同于操作系统
  >
  > 
  >
  > 这几个硬件通过操作系统OS连接在一起，这种跨硬件的同步方式就是大名鼎鼎的Fence机制
  >
  > 由于消费级芯片（麒麟/天玑/骁龙）公开资料比较少，我们随便找个工业级瑞芯[RK3399](http://opensource.rock-chips.com/images/d/d7/Rockchip_RK3399_Datasheet_V2.1-20200323.pdf)来举例，
  >
  > **什么是HWC？**
  >
  > 如果我们去看官网的图形架构设计图，会发现列的有hwc，
  >
  > 得去高通的msm-aosp找对应的实现，
  >
  > 这里分享一下平时如何找代码的，我们都知道Google是Android定义者，最大的下游厂家应该非高通莫属，其次是联发科、以及其他工业消费厂家，他们拿到
  >
  > 对于一个功能要不要实现，怎么实现，由厂商自行决定
  >
  > 手机厂商通常拿到的是高通或联发科的平台代码，在此基础上进行魔改
  >
  > 所以我们这里也以高通为例，打开高通的[开发者社区](https://www.codeaurora.org/)
  >
  > 
  >
  > HWC的具体实现是2D图形引擎，我们知道Android 4.1之后发布了黄油计划
  >
  > 为了避免发生拷贝，一旦发生数据拷贝就意味着数据要发生传输，传输就需要耗能，移动设备最重要的就是能耗
  >
  > 在冯诺伊曼架构的指导下，只要有CPU和内存就可以运行操作系统，把RK3399设计图中多媒体处理中所有硬件全部丢弃依旧可以正常运行，Android系统提供了lib库，通过CPU来执行指令
  >
  > 把渲染和合成分开执行在不同的vsync周期，
  >
  > 在更高的刷新率设备上，每个阶段的耗时也要求越短，所以流水线也会越长，通常会设计成更多的
  >
  > 开发板和普通的手机还是有些区别的，比如mip接口
  >
  > Android开发工程师通常启动activity就可以在屏幕上看到画面，
  >
  > 好了，这下我们知道图像内存是如何流转的，也知道操作系统拥有bufferqueue来管理graphicbuffer

  > 总结一下HWC是流水线上的一环，加入HWC为了解决
  >
  > 还有另外一个hwc用来接受屏幕驱动产生的vsync信号，
  >
  > 好了，现在硬件和系统的关键模块我们都已经了解了，接下来就是让整个流程run起来
  >
  > **1、认识硬件**
  >
  > **2、认识操作系统**
  >
  > 那么接下来我们从
  >
  > 

- 二、视图的加载与graphicbuffer的分配，vsync，系统的总指挥！

  > 咋一看标题很奇怪，视图的加载是在APP用户进程，graphicbuffer分配是在系统进程，中间
  >
  > 每次vsync的到来，app进程，surfaceflinger进程等都要开始工作
  >
  > 每次到来就像领导来视察一样，app开始检查有没有新的变化，sf检查有没有需要合成的layer
  >
  > 1、APP进程
  >
  > 2、sf进程
  >
  > 3、hwc服务

- 结语

  > 在深入了解图形子系统之后，我才发觉这是多么庞大的话题
  >
  > 图形数据本质是二维数组，数组中每个元素描述了像素点的状态
  >
  > 在此之前，
  >
  > 本文把重点放在了介绍GraphicBuffer流转过程，事实上，图形子系统涉及到非常多的模块
  >
  > 
  >
  > 生产一篇文章的过程中，我的习惯是将某个领域的知识点全部查清楚，接着看看能否找到一个切入点引出一条故事线，让这条线尽可能包含整个知识介绍
  >
  > 在二次创作中为了文章不显得太臃肿往往会选择丢弃一部分信息，如果不小心将某个重要的知识点剔除了，我自己在审稿时是发现不了的，因为每个点的上下文我脑子里都有，这部分丢弃信息并不会给我造成理解上的问题
  >
  > 在读者看来非常致命，所以，在这里真诚的请求大家，如果您在阅读文章的过程有任何疑问，请在评论区提出来，我会尽力解答，感谢您的阅读
  >
  > 文章的最后一节列出了，相较于本篇文章，参考资料对Android工程师的帮助显然会更大，它们能够帮助你构建一个完整的图形框架，限于篇幅和个人精力，本篇包含的内容只能说是沧海一粟
  >
  > 全文完
  >
  > 图形子系统涉及到非常多的知识点，
  >
  > 每个环节都非常重要，可以写出一篇文章来介绍，忽略了GPU是如何根据一个三维场景中的顶点、纹理等信息，转换成一张二维图像的，忽略了g是如何申请并管理内存的，忽略了sf是，
  >
  > 最重要的是忽略了DRM架构，这和有关系，比如我们可以来思考一个问题：为什么手机关机还能显示充电画面？单单为了介绍内存数据的流程流程已经耗费了，没有时间和精力去搞懂全部的实现原理

前两篇文章我们分别介绍了屏幕的"显示原理"和屏幕的"刷新原理"，本篇文章我们将要一起来了解Android图形架构是如何设计的，显示流程，在学习刷新原理的文章中，

#### 一、开篇

#### 二、图形送显方案

##### DRM直连

关机充电动画/闹钟画面

surfaceflinger未启动时的开机动画

##### OpenGL ES Native



实际上，framebuffer已经是绘制流程的，的生产和管理，才是图形系统当中最为重要的部分

本篇的重点是关注framebuffer的生成过程，渲染流程

GPU图像渲染流程，内存如何分配，窗口管理等等

每一步又可以继续拆分。比如GPU图形渲染又分为应用阶段、几何阶段、光栅化阶段

其中，几何阶段又可以分为顶点着色器等等

在深入了解OpenGL图形开发一段时间后，我放弃了理解实现原理的想法，太他吗复杂了

所以在此关于渲染流程可能会一笔带过，对于图形开发的当做黑盒处理，塞入数据出来的是framebuffer

理解sf在系统框架中的位置，有助于我们构建Android体系架构

参考资料

- [GPU 渲染管线和硬件架构浅谈-腾讯技术工程](https://mp.weixin.qq.com/s/-ueKhxbsJOnUtV1SC5eyBQ)







