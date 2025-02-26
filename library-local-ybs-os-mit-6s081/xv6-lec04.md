
# 视频笔记

- satp 寄存器保存的地址是物理地址。
- mmu 转换时，是去内存里面物理地址。

虚拟地址转为物理地址

- 内存页大小为 4KB = 4 * 1025Byte * 8Bit = 4096 个比特位，一般绝大多数 CPU 都使用或者支持 4KB 分页。
- 在实际的物理内存中，内存页是最小颗粒度，它绝对是连续的一段物理内存。
- risc-v 寄存器都是 64 位，但是，mmu 仅用了其中的 39 位，前 25 位是空着的没用，后面的 39 位又分为两个部分
  - 前 27 位表示这个物理地址具体在哪一页（4KB/页）
  - 后 12 位是偏移量，表示在这一页的哪个 Bit。
- 基于以上，问大家一个问题：SV 39 的寻址方式支持的最大的虚拟内存是多大？
- 答案是 2 的 27 次方再乘上页大小 2^27 * 4KB = 512GB。
- risc-v 支持的物理内存大小是 2^56 = 32PB
- MMU of CPU or Mainboard
  - 虚拟内存地址 39 位，物理地址 56 位。
  - 抛开最后低 12 位的 offset，虚拟地址 27 位，物理地址 44 位。
  - 也就是说，MMU 需要把 27 位的地址，转换成 44 位。怎么做呢？
- 页表
  - 一些基本概念，页表是一块内存，规定大小是 8kb
  - MMU 三级页表，需要找三次。
  - 通过第一级找到第二级
  - satp 寄存器保存的是第一级的物理地址，当需要切换进程时，操作系统会写入该进程对应的第一级列表的物理地址到 satp 寄存器
  - 写入 satp 寄存器是受保护的特权级指令，用户程序无法更改 satp 寄存器的值。

# 疑问❓

该应用违反了开发者政策，使用了第三方支付方式，将用户引导至 Google Play 结算系统以外的系统。

触发方式：

1. 注册男用户并登入，和内部的 AI 聊天后触发购买会员或者购买聊天次数。
2. 再连续付款两次以后，可能会触发第三方支付系统。
3. 第三方支付系统使用 WebView 页面，也可以跳出应用到浏览器结算。

- mmu 转换过程？三级目录
- 三级页表，每个页表的大小是多少？

# 课前要求

1. 阅读 book-riscv-rev1 第三章，page tables
2. 阅读部分 kernel 源码：
   1. https://github.com/mit-pdos/xv6-riscv/blob/riscv/kernel/memlayout.h
   2. https://github.com/mit-pdos/xv6-riscv/blob/riscv/kernel/vm.c
   3. https://github.com/mit-pdos/xv6-riscv/blob/riscv/kernel/kalloc.c
   4. https://github.com/mit-pdos/xv6-riscv/blob/riscv/kernel/riscv.h
   5. https://github.com/mit-pdos/xv6-riscv/blob/riscv/kernel/exec.c

### page tables of book-riscv-rev1

让人头疼的硬件分页机制

- user/kernel model 已经解决了隔离性，为什么还需要分页？
  - User/Kernel Mode：解决“代码能做什么”
  - 分页：解决“代码能访问哪些内存”
  - 二者缺一不可：即使程序在用户态，若没有分页，也可能通过野指针破坏其他进程或内核。
- 访问内存会鉴权，避免非法访问，为什么需要分页？
- 涉及到的硬件 or 寄存器有哪些？分别有哪些作用？
  - satp（Supervisor Address Translation and Protection）寄存器是控制页表的核心寄存器。

Sv39 RISC-V 的分页机制（三级页表），虚拟地址结构（共 39 位，支持 512GB 虚拟空间），页表项（PTE, Page Table Entry）占 8 字节

| 一级目录号（9位） | 二级目录号（9位） | 三级目录号（9位） | 页内偏移（12位） |

每一级目录号用来索引页表，最后 12 位是页内偏移（因为每页大小是 4KB = 2^12）。

举个栗子 🌰：

假设程序要访问虚拟地址 0x12345678，硬件会这样查 

- 一级页表：用前 9 位（比如 0x123）找到二级页表的位置。 
- 二级页表：用中间 9 位（比如 0x456）找到三级页表的位置。 
- 三级页表：用最后 9 位（比如 0x78）找到最终的物理页号。 
- 拼出物理地址：物理页号 + 页内偏移（最后 12 位）。

### memlayout.h

https://github.com/mit-pdos/xv6-riscv/blob/riscv/kernel/memlayout.h

定义了 xv6 内存布局的相关常量和宏，帮助操作系统管理内存

- KERNBASE：这是内核内存的起始地址，通常它在 32-bit 系统中为 0x80000000，意味着内核内存从这个地址开始。
- PHYSTOP：这是物理内存的结束地址，表示操作系统能够访问的最大物理内存地址。

### vm.c

关键函数包括： 

- walk：遍历页表，返回虚拟地址对应的 PTE。 
- mappages：将一段虚拟地址映射到物理地址。 
- uvmcreate：为用户进程创建空页表。 
- kvminit：初始化内核页表。

内核页表初始化（kvminit）

```c
void kvminit() {
kernel_pagetable = (pagetable_t) kalloc();
memset(kernel_pagetable, 0, PGSIZE);
kvmmap(UART0, UART0, PGSIZE, PTE_R | PTE_W); // 映射 UART 设备
kvmmap(VIRTIO0, VIRTIO0, PGSIZE, PTE_R | PTE_W); // 映射 VirtIO 设备
// ...
}
```

每个进程（struct proc）有自己的页表（p->pagetable），在上下文切换时，操作系统会更新 satp 寄存器：

```c
void scheduler() {
// ...
w_satp(MAKE_SATP(p->pagetable)); // 设置 satp 寄存器
sfence_vma(); // 刷新 TLB
// ...
}
```