
其实我很早之前就看完了，一直懒得写文章。原本打算十一假期写的，也搁置了

1. datasheet
2. 电路设计
3. 程序仿真

## 前言

先来交代一下这本书的背景，王爽老师的《汇编语言》是基于 Intel 80X86 CPU 编著的

80X86 CPU 是怎么工作的？

我看这本书的目的并不是为了学习汇编语言，只是想搞懂一个问题：在硬件侧，CPU 提供了哪些能力给应用程序使用？

了解了 CPU 提供了哪些功能后，我就可以区分

当 APP 在运行时，哪些能力是由 CPU 硬件测提供的，哪些是 Linux OS 和 JVM（Dalvik、ART、HotSpot）提供的，这对我后续了解技术原理很重要。

比如，我们都知道 Java 语言的 volatile 关键字，是用来，那么，它是怎么实现的？依托于 OS 还是 CPU 的能力？

再比如，我们常说的 Dalvik 是基于寄存器实现，ART 是基于栈实现，这两者有什么区别？

本文其实和汇编语言没啥关系，更多的是答疑解惑

废话不多说，接下来开始进入正文，ps：以下内容都是我个人理解，仅作参考。

去了解计算机科学，程序是怎么运行的？

更准确的说，我是想了解当我们的程序在运行时，哪些能力是由 CPU 硬件测提供的，

我就可以区分，当我们的程序在运行时，以及，了解

举个例子，当提到 " CPU 虚拟化" 技术的时候，课本上说的是：

- CPU的虚拟化技术可以单CPU模拟多CPU并行，允许一个平台同时运行多个操作系统，并且应用程序都可以在相互独立的空间内运行而互不影响，从而显著提高计算机的工作效率；
- 虚拟化技术与多任务以及超线程技术是完全不同的。多任务是指在一个操作系统中多个程序同时并行运行，而在虚拟化技术中，则可以同时运行多个操作系统，而且每一个操作系统中都有多个程序运行，每一个操作系统都运行在一个虚拟的CPU或者是虚拟主机上；而超线程技术只是单CPU模拟双CPU来平衡程序运行性能，这两个模拟出来的CPU是不能分离的，只能协同工作；
- 虚拟化技术也与目前VMware Workstation等同样能达到虚拟效果的软件不同，是一个巨大的技术进步，具体表现在减少软件虚拟机相关开销和支持更广泛的操作系统方面；
- 纯软件虚拟化解决方案存在很多限制。“客户”操作系统很多情况下是通过VMM(Virtual Machine Monitor，虚拟机监视器)来与硬件进行通信，由VMM来决定其对系统上所有虚拟机的访问。(注意，大多数处理器和内存访问独立于VMM，只在发生特定事件时才会涉及VMM，如页面错误。)在纯软件虚拟化解决方案中，VMM在软件套件中的位置是传统意义上操作系统所处的位置，而操作系统的位置是传统意义上应用程序所处的位置。这一额外的通信层需要进行二进制转换，以通过提供到物理资源(如处理器、内存、存储、显卡和网卡等)的接口，模拟硬件环境。这种转换必然会增加系统的复杂性。此外，客户操作系统的支持受到虚拟机环境的能力限制，这会阻碍特定技术的部署，如64位客户操作系统。在纯软件解决方案中，软件堆栈增加的复杂性意味着，这些环境难于管理，因而会加大确保系统可靠性和安全性的困难；

这些是结论，是 虚拟化 技术能做那些事情，不能做哪些事情，我们当然不能说它写错了。但是，对于新接触的一个名词，我更希望去了解这项技术的来龙去脉，是怎么一步一步发展成现在这个样子的。

## CPU 是如何运行的

最小执行单元：取指（指令 + 操作数） -> 解析 -> 执行

## 贯穿全文的基础知识

汇编语言是用来控制/使用 CPU 的语言，在阅读文章前，

记住，这些能力，是 CPU 硬件侧的能力，在设计 CPU 画电路板的时候，这些逻辑就存在了，打开开关，电子必定向固定的方向传输。

地址总线、数据总线、控制总线

## 8086的寄存器（方法是如何执行的）

先来学两个简单的汇编指令

```
mov 
```

SP 的内容不需要我们管，在代码编译阶段，编译器会帮助我们处理好。

## CPU 提供的栈机制（栈和栈帧）

## jvm 虚拟机和 OS

首先说明，"虚拟化" 和 "虚拟机" 是两个不同的概念

## 补充

关于 Dalvik 是基于寄存器实现，ART 是基于栈实现 这个问题，玩安卓和知乎上都有相关帖子

## 参考资料

https://book.douban.com/annotation/23343580/
https://cloud.tencent.com/developer/article/1657345
https://www.wanandroid.com/wenda/show/13383
https://www.zhihu.com/question/266211661
https://book.douban.com/subject/25726019/
https://zhuanlan.zhihu.com/p/77663680
https://gitbook.coder.cat/function-call-principle/content/function-stack-frame.html
https://www.0xffffff.org/2013/10/22/21-x86-asm-1/

https://www.reddit.com/r/linux/comments/cbd9h/linux_or_bsd_and_similar_for_intel_8086/?rdt=45146
https://elks.sourceforge.net/download.html

Intel 8086仿真模拟器
https://github.com/ZubinGou/8086-emulator

Online 8086 EMULATOR
https://yjdoc2.github.io/8086-emulator-web/

在线汇编
https://www.eecso.com/test/asm/

在线汇编语言第四版 PDF 
https://pdf.wenjie.store/pdf/%E7%B3%BB%E7%BB%9F/%E6%B1%87%E7%BC%96%E8%AF%AD%E8%A8%80_%E7%AC%AC%E5%9B%9B%E7%89%88%20.pdf

MAC下安装汇编环境的2种方式
https://wenjie.store/archives/mac-xia-an-zhuang-hui-bian-huan-jing-de-2-zhong-fang-shi

《汇编语言（第二版王爽）》读书笔记
https://bbs.kanxue.com/thread-154158.htm
http://8.129.108.223/post/book-notes/020-Assembly-Language/p1-p3/
http://www.luzexi.com/2020/10/18/%E8%AF%BB%E4%B9%A6%E7%AC%94%E8%AE%B012

维基百科
https://zh.wikipedia.org/wiki/Intel_8086

8086 datasheet
https://www.inf.pucrs.br/~calazans/undergrad/orgcomp_EC/mat_microproc/intel-8086_datasheet.pdf

80386与8086区别以及保护模式
https://www.cnblogs.com/xuehongyang/p/5457413.html
