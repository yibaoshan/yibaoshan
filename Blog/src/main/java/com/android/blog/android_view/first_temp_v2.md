Android图形架构概述(硬件层)：什么是PWM调光？

Android图形架构概述(驱动层)：vsync是如何解决画面撕裂问题的？

> 双缓冲机制
>
> 现在我们跳出Android手机，看看PC的设计
>
> 屏幕板子上有块内存，用于记录像素buffer，屏幕驱动按照刷新率去内存读取，所谓双缓冲，指的
>
> 多重缓冲也是可以的，代价是更高的延迟
>
> 作者：TC130
> 链接：https://zhuanlan.zhihu.com/p/385642198
> 来源：知乎
> 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
>
> 
>
> 假设现在我们只有一个buffer，表示当前需要显示到显示器上的内容。当一帧中的三角形被绘制时，会随着显示器的刷新，逐渐一点点出现，这种效果是很奇怪的。即使我们的帧率和显示器刷新率相等，single buffer 还是会有问题。如果我们决定清除 buffer 然后绘制一个较大的三角形，因为VDC 会将正在绘制的color buffer区域输出，我们就会短暂地看到 color buffer的变化。这种现象叫做**撕裂/tearing**，画面显示看起来被分割成了两部分。在一些像Amiga 的古老系统中，你可以通过检测 Beam来防止画面撕裂，这样single buffering就是可行的。现在只有在一些 VR系统中才会用到这种 single bugger渲染架构，会使用 beam 方式来尽可能降低延迟。
>
> 目前最常用的消除撕裂的方式是使用 double buffering。一个渲染完成的图像在 **front buffer** 中显示，同时不可见的的 **back buffer** 在被绘制。当back buffer 中的图像被传输到显示器后，图形驱动会 swap front buffer 和 back buffer，来避免撕裂。Swap的过程通常都是简单地交换两个 color buffer的指针。对于 CRT 显示器，这个事件叫做 vertical retrace，整个过程叫做 **vertical synchronization / Vsync / 垂直同步**。虽然LCD 显示器没有 beam 的 retace，但是我们使用相同的术语来表示显示器中的交换过程。
>
> 如果渲染过程完成后，立即交换 back buffer 和 front buffer，可以最大化帧率，这样可以用来测试渲染系统的性能。但是这样其实是没有跟随 vsync 来进行更新的，同样会造成撕裂。不过因为两个buffer 都是渲染好的完整图像，撕裂效果不会像 single buffer 中那样糟糕。
>
> 对 double buffering 进行改进，添加上 pending buffer， 就形成了 triple buffering。pending buffer 和 back buffer 类似，都是不可见的，不同的是 pending buffer 是可以被修改的。pending buffer 在交换之后，会变成 back buffer。再次 swap 后，成为 front buffer。这样，三种buffer 构成了循环，如上图所示。
>
> 使用 doule buffering 时，等待垂直刷新及swap时，front buffer 需要被显示，而 back buffer 因为是已经渲染好的图像，所以需要保持不变，等待被显示。相对 double buffering，triple buffering的优势在于，当等待垂直刷新的时候，系统可以访问 pending buffer。这样 triple buffering 避免了等待的时间，从而增加帧率。不过相应的缺点就是，会增加整整一帧的延迟，会给用户从手柄鼠标键盘的输入增加延迟，甚至产生卡顿。
>
> 理论上来说，超过三个的buffer 也是可行的。如果每帧渲染的时间波动很大，使用更多的buffer，就能使结果更平滑，帧率更高，当然代价就是更大的延迟。

Android图形架构概述(系统层)：Flutter没有继承View为什么能在屏幕上显示？

> 在Android4.1之前是不支持vsync信号的，哪怕硬件驱动支持
>
> 在驱动层我们知道，VSync信号是由GPU驱动或者屏幕驱动产生的，假设手机驱动不支持怎么办？
>
> Android4.1中提供了*VSyncThread*来软件模拟
>
> 引入了Vsync(Vertical Syncronization)用于渲染同步，使得App UI和SurfaceFlinger可以按硬件产生的VSync节奏来进行工作
>
> 画图：你使用canvas api 还是OpenGL es都可以
>
> 合成：surfaceFligler只管合成图像
>
> 疑问❓：三重缓冲

Android图形架构概述(番外篇)：Input事件先到达DecorView还是Activity？

View系列开篇：什么是刷新率

事情是这样的，

屏幕显示原理：    

LCD屏幕：DC调光

OLED屏幕：PWM调光

逐行刷新

屏幕撕裂

vsync信号

引申出屏幕gif动图

市面上手机屏幕分为两种

一种是LCD屏幕，使用DC调光

屏幕其实是由一个个像素点组成的，每个像素点

当人看到一幅画面快速闪过时，这幅画面产生的视觉刺激会在大脑中停留几十到几百毫秒时间，亮度越亮，停留的时间越长

这一特征我们称为视觉残留



范围：  
屏幕的显示原理  
    逐列刷新、逐行刷新
    屏幕最终要的数据类型是每一行的RGB值，

    调光方式，DC调光和PWM调光
    有了上述理论知识以后，我们来看一个现象，这有什么用呢？我们可以看到很有趣的现象
    
    以我手里的pixel3举例，PWM调光频率为245HZ，https://www.notebookcheck.net/Google-Pixel-3-Smartphone-Review.366326.0.html#toc-9
    
    对自己手机屏幕参数感兴趣的同学可以去这个网站查询：www.notebookcheck.net
    
    快门速度：
    简单来说就是曝光时间，快门帘打开让光线进入传感器的时间
    
    占空比：
    PWM一个周期计算公式：
    T = 1/f
    周期 = 
    
    我们看到的是什么？


屏幕撕裂

垂直同步信号

讨论一下，高刷屏对于工程师的挑战

对于硬件工程师来说，大部分主板上CPU和GPU共享一块内存，所以屏幕色深值越高，描述每个像素点需要内存就越大，内存的大小和内存的访问速度绝不能拉胯
屏幕刷新率高了，CPU和GPU需要在更短的时间处理数据，在提升硬件性能的同时还需要平衡功耗控制

对于应用开发工程师来说，在View绘制，做好性能优化这块展开可以聊很久