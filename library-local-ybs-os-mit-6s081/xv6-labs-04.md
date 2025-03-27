
本篇是 MIT6.S081 操作系统课程 Lab4 的实验笔记，Lab4 的两道题难度并不高，更重要的是 Lec5 和 Lec6 课程中，对 xv6 在 RISC-V 上的 Trap 流程进行学习。

> - Lab3 地址：https://pdos.csail.mit.edu/6.828/2020/labs/traps.html
> - 我的实验记录：https://github.com/yibaoshan/xv6-labs-2020/tree/traps

在开始实验之前，你需要：

1. 观看 Lecture 5 课程录播视频：**RISC-V Calling Convention and Stack Frames**
    - YouTube 原版：https://www.youtube.com/watch?v=s-Z5t_yTyTM
    - 哔哩哔哩中译版：https://www.bilibili.com/video/BV19k4y1C7kA?vd_source=6bce9c6d7d453b39efb8a96f5c8ebb7f&p=4
2. 阅读 [《xv6 book》](https://xv6.dgs.zone/tranlate_books/book-riscv-rev1/c3/s0.html) 第四章：**陷阱指令和系统调用**
    - 英文原版：https://pdos.csail.mit.edu/6.828/2020/xv6/book-riscv-rev1.pdf
    - 中译版：https://xv6.dgs.zone/tranlate_books/book-riscv-rev1/c4/s0.html
3. 观看 Lecture 6 课程录播视频：**Isolation & System Call Entry/Exit**
    - YouTube 原版：https://www.youtube.com/watch?v=T26UuauaxWA
    - 哔哩哔哩中译版：https://www.bilibili.com/video/BV19k4y1C7kA?vd_source=6bce9c6d7d453b39efb8a96f5c8ebb7f&p=5

# RISC-V assembly (easy)

代码切换到 `traps` 分支，仓库中有个 user/call.c 文件

```c
int g(int x) {
  return x+3;
}

int f(int x) {
  return g(x);
}

void main(void) {
  printf("%d %d\n", f(8)+1, 13);
  exit(0);
}
```

执行 `make fs.img` 以后，它会被编译为 call.asm 汇编文件。

```asm
user/_call:     file format elf64-littleriscv

Disassembly of section .text:

0000000000000000 <g>:

int g(int x) {
   0:	1141                	addi	sp,sp,-16
   2:	e422                	sd	s0,8(sp)
   4:	0800                	addi	s0,sp,16
  return x+3;
}
   6:	250d                	addiw	a0,a0,3
   8:	6422                	ld	s0,8(sp)
   a:	0141                	addi	sp,sp,16
   c:	8082                	ret

000000000000000e <f>:

int f(int x) {
   e:	1141                	addi	sp,sp,-16
  10:	e422                	sd	s0,8(sp)
  12:	0800                	addi	s0,sp,16
  return g(x);
}
  14:	250d                	addiw	a0,a0,3
  16:	6422                	ld	s0,8(sp)
  18:	0141                	addi	sp,sp,16
  1a:	8082                	ret

000000000000001c <main>:

void main(void) {
  1c:	1141                	addi	sp,sp,-16
  1e:	e406                	sd	ra,8(sp)
  20:	e022                	sd	s0,0(sp)
  22:	0800                	addi	s0,sp,16
  printf("%d %d\n", f(8)+1, 13);
  24:	4635                	li	a2,13
  26:	45b1                	li	a1,12
  28:	00000517          	auipc	a0,0x0
  2c:	7b050513          	addi	a0,a0,1968 # 7d8 <malloc+0xea>
  30:	00000097          	auipc	ra,0x0
  34:	600080e7          	jalr	1536(ra) # 630 <printf>
  exit(0);
  38:	4501                	li	a0,0
  3a:	00000097          	auipc	ra,0x0
  3e:	27e080e7          	jalr	638(ra) # 2b8 <exit>
```

阅读 call.asm 中的 `g()` 、`f()` 和 `main()` 三个函数代码的，回答以下问题。

**Q：哪些寄存器包含传递给函数的参数？例如，在 `main()` 调用 `printf()` 时，哪个寄存器中包含 13？**

> `a0` ~ `a7` 共 8 个参数寄存器，13 保存在 `a2`
> RISC-V 函数参数优先使用寄存器传递，返回值可以放在 `a0` 和 `a1` 寄存器，如果函数参数超过寄存器梳理，额外的参数会被存放在栈上。

**Q：在 `main()` 函数的汇编代码中， `f()` 函数的调用在哪里？ `g()` 函数的调用又在哪里？（提示：编译器可能会内联函数。）**

> 正如提示所言，汇编里面没有对 `f()` 和 `g()` 的调用（没有看到 `call` 或 `jalr` 指令跳转到这些函数）
> 由于 `f()` 和 `g()` 函数都很简单（ `f()` 调用 `g()` ， `g()` 只是返回参数 +3），编译器进行了函数内联优化，直接把计算结果 `f(8)+1` 优化成了常量 12。

**Q：函数 `printf()` 位于什么地址？**

> `printf()` 函数位于地址 0x630
> 1. `auipc` ra,0x0 - 将当前 PC（程序计数器）的值加上立即数（这里是0）存入 `ra` 寄存器
> 2. `jalr` 1536(ra) - 跳转到 `ra` + 1536 的地址，并将返回地址保存在 `ra` 中

**Q：在 `jalr` 到 `printf()` 在 `main()` 之后，寄存器 `ra` 中的值是什么？**

> `ra` 寄存器中保存的值是 0x38 ，也就是 `jalr` 指令后面那条指令的地址（ exit(0) 的第一条指令的地址）

**Q：运行以下代码，输出是什么？如果 RISC-V 是大端序，你需要将 `i` 设置为什么值才能得到相同的输出？ 57616 的值是否需要改变？**

```c
   unsigned int i = 0x00646c72;
   printf("H%x Wo%s", 57616, &i);

   unsigned int i = 0x00646c72;
   printf("H%x Wo%s", 57616, &i);
```

> 输出是： **HE110 World**。在大端序下， `i` 应该设置为 0x726c6400，57616 的值不需要改变，因为： 
> 1. 这个数字是直接用于十六进制打印（ %x ） 
> 2. 数字的表示方式不受字节序的影响，输出仍然是 E110

**Q：在下面的代码中，在 'y=' 之后将打印什么？（注意：答案不是一个具体的值。）为什么会这样？**

```c
   	printf("x=%d y=%d", 3);
```

> - x= 后会打印 3，y= 后会打印一个随机值，具体是什么值取决于上一次 `a2` 寄存器保存的是什么

# Backtrace (moderate) 

Implement a backtrace() function in kernel/printf.c. Insert a call to this function in sys_sleep, and then run bttest, which calls sys_sleep. Your output should be as follows:

实现一个 `backtrace()` 函数并在 kernel/printf.c 中插入对该函数的调用，然后运行 `sys_sleep` ， `sys_sleep` 会调用 bttest 。你的输出应如下所示：

```
backtrace:
0x0000000080002cda
0x0000000080002bb6
0x0000000080002898
```

标的中等难度，实际做起来挺简单的，思路：

- 每个函数调用都会在栈上创建一个栈帧（stack frame）
- 栈帧中包含：
  - 返回地址（return address）：在帧指针-8的位置
  - 上一个栈帧的帧指针（frame pointer）：在帧指针-16的位置

首先打开 kernel/riscv.h 文件，在底部追加获取帧指针的函数

```c
static inline uint64
r_fp()
{
  uint64 x;
  asm volatile("mv %0, s0" : "=r" (x) );
  return x;
}
```

在 kernel/defs.h 中添加函数声明

```c
// printf.c
...
void            printfinit(void);
void            backtrace(void);  // 新增
```

接着，kernel/printf.c 实现 `backtrace()` 函数

```c
void
backtrace(void)
{
  printf("backtrace:\n");
  
  uint64 fp = r_fp();
  uint64 top = PGROUNDUP(fp);
  uint64 bottom = PGROUNDDOWN(fp);
  
  // 遍历栈帧
  while(fp >= bottom && fp < top) {
    uint64 ra = *(uint64*)(fp - 8);  // 返回地址在 fp-8
    printf("%p\n", ra);
    fp = *(uint64*)(fp - 16);  // 上一个帧指针在 fp-16
  }
}
```

最后一步， kernel/sysproc.c 的 `sys_sleep` 中增加调用

```
uint64
sys_sleep(void)
{
  int n;
  uint ticks0;
  
  backtrace();  // 添加调用
  
  ...
}
```

搞定，主要工作在 `backtrace()` 函数中，挺简单的，运行 bttest 查看结果

```
$ bttest
backtrace:
0x0000000080002ce4
0x0000000080002bbe
0x00000000800028a8
```

测试通过，完整的代码在：https://github.com/yibaoshan/xv6-labs-2020/commit/78fd795840d77baf3a0f3e61d9b5d85c55085e2f

# Alarm (hard) 

In this exercise you'll add a feature to xv6 that periodically alerts a process as it uses CPU time. This might be useful for compute-bound processes that want to limit how much CPU time they chew up, or for processes that want to compute but also want to take some periodic action. More generally, you'll be implementing a primitive form of user-level interrupt/fault handlers; you could use something similar to handle page faults in the application, for example. Your solution is correct if it passes alarmtest and usertests.

写个定时器程序，实现两个系统调用:

1. `sigalarm(interval, handler)` - 设置定时器,每经过 interval 个时钟周期就调用一次 `handler()` 函数
2. `sigreturn()` - `handler()` 函数执行完后返回原来的执行位置

这道题也不是很难，代码量比上一题多一点，主要考察**系统调用的实现**（Lab2已经做过了）、**用户态和内核态的切换**、**如何保存和回复进程的上下文**、以及 **处理时钟中断** 这几点。

首先，按照实验要求，在 user/user.h 中添加两个系统调用

```c
// system calls
int fork(void);
...
int uptime(void);
int sigalarm(int ticks, void (*handler)());
int sigreturn(void);                       
```

在 kernel/sysproc.c 底部实现系统调用

```c
uint64
sys_sigalarm(void)
{
  int interval; // 定时器的间隔
  uint64 handler; // 处理函数的地址

  if(argint(0, &interval) < 0)
    return -1;
  if(argaddr(1, &handler) < 0)
    return -1;
    
  struct proc *p = myproc();

  // 初始化定时器
  p->alarm_interval = interval;
  p->alarm_handler = (void(*)())handler;
  p->ticks_count = 0;
  p->alarm_on = 0;
  
  return 0;
}

uint64
sys_sigreturn(void)
{
  struct proc *p = myproc();
  *p->trapframe = *p->alarm_trapframe; // 恢复保存的上下文!!!
  p->alarm_on = 0; // 清除执行标志，允许下一次定时器触发
  return 0;
}
```

接着需要为 xv6 增加两个系统调用，参考 lab2 的过程，我这里写到一起了

```c
user/usys.pl 添加系统调用入口
entry("fork");
...
entry("sigalarm"); 
entry("sigreturn");

kernel/syscall.h 中添加系统调用号
#define SYS_sigalarm  22 
#define SYS_sigreturn  23

kernel/syscall.c
...
extern uint64 sys_uptime(void);
extern uint64 sys_sigalarm(void); 
extern uint64 sys_sigreturn(void);

static uint64 (*syscalls[])(void) = {
[SYS_fork]    sys_fork,
...
[SYS_close]   sys_close,
[SYS_sigalarm]  sys_sigalarm, 
[SYS_sigreturn] sys_sigreturn,
};
```

然后我们来修改调用过程，先为进程增加定时器相关字段， kernel/proc.h

```c
struct proc {
  struct spinlock lock;
  ...
  char name[16];               // Process name (debugging)

  int alarm_interval;         // 记录定时器触发间隔（以时钟周期为单位）
  void (*alarm_handler)();    // 定时器处理函数的函数指针
  int ticks_count;           // 当前已经过的时钟周期计数器
  struct trapframe *alarm_trapframe; // 用于保存中断前进程的上下文信息
  int alarm_on;              // 定时器是否已经在运行了
};
```

字段的含义看注释

接着来处理时钟中断逻辑 kernel/trap.c

```
void
usertrap(void)
{
   ...
  // give up the CPU if this is a timer interrupt.
  if(which_dev == 2) {
    struct proc *p = myproc();

    // 如果设置了定时器
    if(p->alarm_interval > 0) {
      p->ticks_count++;

      // 是否达到触发条件：计数达到了设定的间隔 + 处理函数当前未在执行
      if(p->ticks_count >= p->alarm_interval && !p->alarm_on) {
        p->ticks_count = 0;
        p->alarm_on = 1;
        
        // 首次调用时分配保存上下文的空间
        if(p->alarm_trapframe == 0)
          p->alarm_trapframe = kalloc();
        // 保存当前进程的完整上下文
        *p->alarm_trapframe = *p->trapframe;
        
        // 修改程序计数器指向处理函数
        // 当中断返回时会跳转到 handler 函数执行
        p->trapframe->epc = (uint64)p->alarm_handler;
      }
    }
    yield();
  }

  usertrapret();
}
```

最后，修改 Makefile，添加 `alarmtest` 测试程序

```makefile
UPROGS=\
	...
	$U/_zombie\
	$U/_alarmtest\    # 新增
```

执行 `alarmtest` 运行测试程序

```
$ alarmtest
test0 start
.....alarm!
test0 passed
test1 start
.alarm!
alarm!
alarm!
.alarm!
alarm!
alarm!
.alarm!
.alarm!
alarm!
alarm!
test1 passed
test2 start
..alarm!
test2 passed
```

测试通过，完整代码在：https://github.com/yibaoshan/xv6-labs-2020/commit/50da5c39ceee8ec76a6b038e1f4ca3e2fed018a9

# 参考资料

- CS自学指南：https://csdiy.wiki/%E6%93%8D%E4%BD%9C%E7%B3%BB%E7%BB%9F/MIT6.S081/
- Miigon：https://blog.miigon.net/categories/mit6-s081/
- 知乎专栏《28天速通MIT 6.S081操作系统》：https://zhuanlan.zhihu.com/p/632281381