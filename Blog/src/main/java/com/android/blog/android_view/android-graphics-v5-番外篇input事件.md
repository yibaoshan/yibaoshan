
Android事件分发（上）：触摸事件的来源

Android事件分发（上）：从内核到应用

Android事件分发（下）：应用进程消费

Android图形系统（五）番外篇：触摸事件

自底向上循序渐进

### 系统进程传递事件

### 触摸事件的来源

触摸事件最初的来源是当我们触摸屏幕时屏幕驱动向 OS 发起的中断信号

#### 从硬件到内核

显示屏芯片驱动 触摸屏芯片驱动

当一个输入设备的驱动模块被首次载入内核的时候，会检测它应该管理的硬件设备

如果检测成功，驱动模块会调用include/linux/input.h中的input_register_device(…)函数设置一个/dev/input/eventX（X为整数）来代表这个输入设备

驱动模块同时也会通过include/linux/interrupt.h的request_irq(…)函数注册一个函数去处理这个硬件引发的中断

注册成功以后，设备有事件发生时，驱动程序就会交给对应的驱动模块进行处理，也就是向 /dev/input/eventX 文件写入消息

使用 cat /proc/bus/input/devices 指令查看注册input的设备，我的 Pixel 3 屏幕事件是 input2

使用 adb shell getevent 查看触摸事件发生时的原始值 /dev/input/eventX

#### 系统对触摸事件的处理

现在触摸屏驱动已经为我们收集好原始的触摸事件并写入到 event 文件中，下一步就轮到系统对这个 input 事件的处理

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

触发 ANR 分发

大胆猜一下路径，服务端

1. system_server 进程启动以后，创建 InputManagerService
2. InputManagerService 创建 InputReader 和 InputDispatcher
3. InputDispatcher 等待客户端连接


客户端

1. Activity 启动，ViewRootImpl 创建和 InputDispatcher 连接

#### Input事件在系统层面传递流程

触摸事件的来源与传递，下一篇将介绍应用进程是如何消费事件的

全文完

### 应用进程消费事件

View 事件分发

窗口与 InputManagerService 建立 socket 通信的过程还是挺复杂的，建议读者朋友再去搜索其他文章加深理解

总之，我们在 setContentView() 首次添加视图的过程中，InputChannel 就被创建好了，能够监听到用户触摸屏幕的事件

