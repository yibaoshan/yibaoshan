
Android事件分发（上）：触摸事件的起源与传递

Android事件分发（下）：触摸事件的消费与拦截

Android事件分发（下）：应用进程消费

Android图形系统（五）番外篇：浅析触摸事件分发

自底向上循序渐进

概览图，介绍 input 子系统的架构设计

### 一、触摸事件的起源与传递

Android 事件分发分为上下两篇，

#### 从硬件到内核

系统开机的过程中，触摸驱动使用 input#input_register_device() 函数将自己注册到内核，注册成功后，/dev/input 目录下会多出 eventXX 的设备文件

这时候我们点击屏幕，设备驱动会把触摸事件通过 input_event() 函数，写入到刚刚创建的 /dev/input/event 设备文件中

注册成功后，我们可以使用xx命令查看

Linux 内核在启动过程中会加载触摸屏驱动，

内核系统收到硬件中断信号后，会把触摸事件的原始信息保存到input节点中

注册成功以后，设备有事件发生时，驱动程序就会交给对应的驱动模块进行处理，也就是向 /dev/input/eventX 文件写入消息

使用 cat /proc/bus/input/devices 指令查看注册input的设备，我的 Pixel 3 屏幕事件是 input2

使用 adb shell getevent 查看触摸事件发生时的原始值 /dev/input/eventX

#### 内核解析原始事件

现在触摸屏驱动已经为我们收集好原始的触摸事件并写入到 event 文件中，下一步就轮到系统对这个 input 事件的处理

在内核空间，input 子系统可分为驱动层、核心层和事件层

- 驱动层：输入设备的具体驱动程序，向核心层报告输入内容
- 核心层：为驱动层提供输入设备注册和操作接口，通知事件层对输入事件进行处理
- 事件层：主要和用户空间进行交互

驱动层是上一小节当中的触摸驱动，通常由屏幕厂家提供

核心层是 Linux 开源程序，代码目录在

驱动代码注册和上报事件的接口由 input.c 来提供

#### 系统对触摸事件的转发

eventhub，是sf中所有，为什么在sf中呢？

仔细想想，framework中谁拥有所有窗口的集合？wms算一个，再往下就是sf，因为

#### APP进程注册事件监听

### 二、APP进程消费事件

#### Activity处理触摸事件

#### ViewGroup分发与拦截

#### View消费事件

#### 特殊情况：触摸区域是SurfaceView的处理

### 三、结语

##### EventHub：读event事件

文件在frameworks/native/services/inputflinger/EventHub.cpp

它的作用是监听、读取/dev/input目录下产生的新事件，并封装成RawEvent结构体等待InputReader使用。

#### Android Framework分发

##### InputReader

1. 负责从EventHub读取出元事件，不断地循环调用loopOnce()方法来不断读取事件
2. 对元事件进行处理，封装成inputEvent事件
3. 把inputEvent事件发送给事件监听器QueueInputListener，通过该监听器将事件传递给InputDispatcher

##### InputDispatcher

1. 找到能够被触摸的窗口
2. 通过 InputChannel 发送跨进程通信给目标窗口


大胆猜一下路径，服务端

1. system_server 进程启动以后，创建 InputManagerService
2. InputManagerService 创建 InputReader 和 InputDispatcher
3. InputDispatcher 等待客户端连接

客户端

1. Activity 启动，ViewRootImpl 创建和 InputDispatcher 连接

#### Input事件在系统层面传递流程

触摸事件的来源与传递，下一篇将介绍应用进程是如何消费事件的

全文完

### 补充

本文涉及到的源码版本为 Android 7.1，对应内核版本为 Linux 4.4.1

Linux input ：

### 参考资料

和本文相比，参考资料列表的文章更值得阅读

- [Linux驱动开发|input子系统 - 安迪西](https://blog.csdn.net/Chuangke_Andy/article/details/122181549)
- [Linux驱动开发|电容触摸屏 - 安迪西](https://blog.csdn.net/Chuangke_Andy/article/details/122454299)
- [Android(Linux) 输入子系统解析 - Andy Lee](http://huaqianlee.github.io/2017/11/23/Android/Android-Linux-input-system-analysis/)
- [Android 如何上报 Touchevent 给应用层 - 董刚](https://dqdongg.com/c/touch/android/2014/07/10/Touch-inputevent.html)

