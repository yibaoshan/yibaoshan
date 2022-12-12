

现代操作系统主要包含一下几部分：

1、进程管理子系统（SCHED）：又大致包含调度模块；任务管理模块；同步模块；CPU模块。

2、内存管理子系统（MM）：又大致包含虚拟内存模块；内存映射模块；页表模块；物理内存模块。

3、文件子系统（Virtual File System,VFS）：又大致包含VFS模块；缓存模块；文件系统模块。

4、网络子系统（NET）：又大致包含套接字模块；协议栈模块；网络设备模块。

5、设备子系统：又大致包含字符设备模块；块设备模块。

### 概述

首先我们要知道，Linux 只是内核，通常我们称之为 Linux 操作系统更应该被称为 GNU + Linux，所有被叫做 “Linux” 的发行版实际上都是 GNU/Linux发行版，包括 Red Hat 、 Ubuntu 、 Debian 、CentOS 等

哦对，还有 Android 系统，不管其他人怎么看，在我认为，Android 就是 Linux 的发行版之一

内核，对于主板来说，也是个程序，是为你运行的其他程序分配计算机资源的程序，是操作系统的基本部分

Linux 内核的作用是将应用层序的请求传递给硬件，并充当底层驱动程序，比如程序想要访问内存、访问相机、得到键鼠事件等等

### 相关链接

- [Linux 官网](https://www.linux.org/)
- [GNU 官网](https://www.gnu.org/)
- [Linux 和 GNU 系统 - GNU 官网](https://www.gnu.org/gnu/linux-and-gnu.html)