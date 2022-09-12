
Android图形系统（五）番外篇：触摸事件

自底向上循序渐进

#### 触摸事件的来源

触摸事件最初的来源是当我们触摸屏幕时屏幕驱动向 OS 发起的中断信号

##### 从硬件到内核

显示屏芯片驱动 触摸屏芯片驱动

当一个输入设备的驱动模块被首次载入内核的时候，会检测它应该管理的硬件设备

如果检测成功，驱动模块会调用include/linux/input.h中的input_register_device(…)函数设置一个/dev/input/eventX（X为整数）来代表这个输入设备

驱动模块同时也会通过include/linux/interrupt.h的request_irq(…)函数注册一个函数去处理这个硬件引发的中断

注册成功以后，设备有事件发生时，驱动程序就会交给对应的驱动模块进行处理，也就是向 /dev/input/eventX 文件写入消息

使用 cat /proc/bus/input/devices 指令查看注册input的设备，我的 Pixel 3 屏幕事件是 input2

使用 adb shell getevent 查看触摸事件发生时的原始值 /dev/input/eventX

##### 系统对触摸事件的处理

现在触摸屏驱动已经为我们收集好原始的触摸事件并写入到 event 文件中，下一步就轮到系统对这个 input 事件的处理

###### EventHub

文件在frameworks/native/services/inputflinger/EventHub.cpp

它的作用是监听、读取/dev/input目录下产生的新事件，并封装成RawEvent结构体供InputReader使用。

###### InputReader

文件在frameworks/native/services/inputflinger/InputReader.cpp

InputReader运行在一个单独的进程中，这个进程由InputManagerService的初始化而新建，具体内容请见：

http://gityuan.com/2016/12/10/input-manager/

它会在内部不断地循环调用loopOnce()方法来不断读取事件

###### InputDispatcher

文件在frameworks/native/services/inputflinger/InputDispatcher.cpp
