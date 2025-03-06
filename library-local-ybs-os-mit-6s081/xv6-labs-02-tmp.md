
本篇是 MIT6.S081 操作系统课程 Lab2 的实验笔记

> - Lab1 地址：https://pdos.csail.mit.edu/6.828/2020/labs/syscall.html
> - 我的实验记录：https://github.com/yibaoshan/xv6-labs-2020/tree/syscall

In the last lab you used systems calls to write a few utilities. In this lab you will add some new system calls to xv6, which will help you understand how they work and will expose you to some of the internals of the xv6 kernel. You will add more system calls in later labs.

Lab1 写了几个调用 `syscall` 实现功能的程序，Lab2 将会为 xv6 增加两个新的 `syscall`，以帮助我们对内核的理解。

在开始实验之前，你需要先阅读 [《xv6 第二章 操作系统架构》](https://xv6.dgs.zone/tranlate_books/book-riscv-rev1/c2/s0.html)、[《xv6 第四章 陷入指令和系统调用 4.3 小节》](https://xv6.dgs.zone/tranlate_books/book-riscv-rev1/c4/s3.html)和[《xv6 第四章 陷入指令和系统调用 4.4 小节》](https://xv6.dgs.zone/tranlate_books/book-riscv-rev1/c4/s4.html)中的内容，了解什么是 `用户态`、`内核态`，以及调用了 `syscall` 以后发生了什么？

简单聊聊 `用户态` 和 `内核态`

- 处于安全角度考虑，操作系统必须有隔离性 Isolation，用户程序必须要分隔开
  - CPU 管理，防止某个用户程序独占 CPU，其他程序无法运行，用户甚至做不到关机。
  - 内存管理，CPU 运行的代码和临时数据都保存在内存，用户程序不应该拥有随意删除/修改任一内存的权力，这是很危险的事情。

- CPU 的基本工作是三级流水线，取指、译码、执行，CPU 并不清楚自己在执行谁的程序，当然，这点对于 CPU 来说本来也不是很重要。
- 所有的程序其本质都是 `躺` 在物理内存中的一大段代码，不管是用户程序还是内核程序。
- OS-code 和 user-code 的区别之一是，加载到内存中的先后顺序不同。OS 代码一般由 BIOS/UEFI 等各种不同的 Bootloader 加载到内存中。
- 因为绝大多数的 CPU 初始上电时，都默认是最高特权级，所以先执行的代码就可以拿到 CPU 的控制权，为所欲为。
- 所谓控制权，指的是可以执行一些特权级指令。
  - 普通指令：算术（加、减、与、或）、跳转（相等跳转、不等跳转、函数跳转）、访存等
  - 特权级指令：CPU-Mode 切换、内存管理（页表相关读写）、中断与等待等。
- 为了保证 OS 的安全，几乎所有 CPU 架构都存在 `特权级` 的机制。

### 如何切换？

同样一行指令，

对于页表的支持（下一节会介绍）

trampoline（蹦床）

trapframe（陷阱帧）

更多关于进程切换的细节，我们会在后面的第四节课介绍。

对于 CPU 来说，并没有 用户 这个概念，你是 内核 代码

CPU 提供了寄存器 `sstatus` 和 `stvec`

那么，怎么

在物理内存地址 0x0000 处，

大家都是 `躺` 在物理内存中的一大段代码，不同的是

OS 为了自己的安全，在每次调用你的代码的之前，OS 都会执行 exit 让 CPU 降级

换个角度来看，你如果能躺在

用户程序是靠 os 调用来驱动，

怎么切换的？

切换的代价？

内核代码可以读写

CPU 的基本工作是三级流水线 取指、译码、执行，CPU 并不清楚自己在执行谁的程序，当然，这点对于 CPU 来说本来也不是很重要。

- 硬件最起码
- 操作系统的运行是靠 `中断` （包括 interrupt 和 trap）来驱动，操作系统是一段 `躺在` 内存中的代码，靠于 `中断` 来调用操作系统的代码，这个代码就是 `用户态`，而 `内核态` 则是操作系统本身。
  - 中断可能来自定时器、外设（键鼠网卡啥的）、磁盘等设备，也可能来自用户的 `trap`，比如 `open`、`exec`、`fork` 等 `syscall`。