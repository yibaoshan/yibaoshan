
# chapter 2 of book xv6 os book

略

# chapter 3 of book xv6 os book

假设内存空间为 0 ~ 1024，

出于安全考虑，内存里面的数据应该是受保护的，其他程序不应该有权限随意读、写我的程序数据

页式硬件

1. RISCV 内存指令使用的都是虚拟地址，页式硬件有三级查找
2. satp 寄存器用来设置是否启用分页模式，如果启用，设置根页表物理地址

# page tables

内存管理是操作系统的重要组成部分，而 MMU 则是操作系统能实现内存管理的重要部分。

从硬件和软件两个方面理解页表

首先，硬件部分，CPU 的 MMU 模块职责：

- 地址转换：MMU 根据页表逐级查找虚拟地址对应的物理页。CPU 执行需要读写内存指令时，比如 lb 
  - lb 指令(load byte)：从内存中读取一个字节，将结果写入到通用寄存器中。
- 权限检查：验证页表项中的权限标志（如读/写/执行权限）。
- 异常触发：若页表无效或权限不足，触发缺页异常（Page Fault）或访问违规。

OS 职责：

- 页表构建：操作系统需在内存中创建三级页表，并维护其结构。
- 配置寄存器：通过 satp 寄存器设置分页模式（Sv39）及根页表物理地址。
- 异常处理：响应硬件触发的异常，动态分配物理页或调整权限。

页表是 MMU 的数据库，它存储了虚拟地址和物理地址的映射关系，以及页的权限信息。

这个数据库只有 OS 有写入权限，其他程序 read only

地址瞎写也没关系，因为页表里没有这个地址，会触发异常，