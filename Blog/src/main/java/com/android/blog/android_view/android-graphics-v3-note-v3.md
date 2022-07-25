### Overview

可能要求读者对于Android系统的启动，系统中常年运行着进程，进程中的各大服务

### 一、开篇

#### 硬件介绍

##### 1、GPU

##### 2、DPU

#### 系统支持

##### 1、Fence机制

##### Gralloc

##### 2、什么是硬件加速？

总结如图，开启硬件加速对于APP来说导致的

1. 开启RenderThread，将会在systrace中体现
2. 由于厂商策略不同，GPU硬件可能并没能呈现预期效果

### 二、Vsync 系统的总指挥

#### 启动Activity伴随着

##### 1、创建surface_flinger进程

##### 2、创建system_server进程

##### 3、创建APP进程启动

###### 3.1 Activity的启动过程发生了什么？



##### 1、SF创建layer

##### 2、PWM创建Window/Surface

##### 3、创建ViewRootImpl

#### Vsync生产与处理

至此，APP进程创建完成，系统服务也时刻准备着

接下来，APP进程和系统进程都一同等待着Vsync信号的到来

##### 1、第一帧，APP进程绘制与渲染

##### 2、第二帧，SF合成

##### 3、第三帧，DRM/KMS显示

##### 4、新同学的加入：RenderThread

##### 5、如何暂停接收Vsync信号？

我们打开APP后没有进行任何操作，APP还会执行渲染流程吗？

答案显然是否定的



### 三、结语

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

Android图形子系列横跨硬件驱动、Linux内核、Framework框架三层，虽然每个模块设计的比较合理，但命名真的是过于混乱，以至于稍不留意就迷失在源码当中

这就导致想要从上到下理清它们之间的关系变成一件比较困难的事情，好在已经有各位前辈铺好了路

希望本文能够抛砖引玉，为好学的你提供一点点帮助

全文完

### 四、参考资料

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
