
本篇是 MIT6.S081 操作系统课程 Lab2 的实验笔记

> - Lab1 地址：https://pdos.csail.mit.edu/6.828/2020/labs/syscall.html
> - 我的实验记录：https://github.com/yibaoshan/xv6-labs-2020/tree/syscall

In the last lab you used systems calls to write a few utilities. In this lab you will add some new system calls to xv6, which will help you understand how they work and will expose you to some of the internals of the xv6 kernel. You will add more system calls in later labs.

Lab1 写了几个调用 `syscall` 实现功能的程序，Lab2 将会为 xv6 增加两个新的 `syscall`，以帮助我们对内核的理解。

在开始实验之前，你需要先阅读 [《xv6 第二章 操作系统架构》](https://xv6.dgs.zone/tranlate_books/book-riscv-rev1/c2/s0.html)、[《xv6 第四章 陷入指令和系统调用 4.3 小节》](https://xv6.dgs.zone/tranlate_books/book-riscv-rev1/c4/s3.html)和[《xv6 第四章 陷入指令和系统调用 4.4 小节》](https://xv6.dgs.zone/tranlate_books/book-riscv-rev1/c4/s4.html)中的内容，了解什么是 `用户态`、`内核态`，以及调用了 `syscall` 以后发生了什么？

# System call tracing (moderate)

In this assignment you will add a system call tracing feature that may help you when debugging later labs. You'll create a new trace system call that will control tracing. It should take one argument, an integer "mask", whose bits specify which system calls to trace. For example, to trace the fork system call, a program calls trace(1 << SYS_fork), where SYS_fork is a syscall number from kernel/syscall.h. You have to modify the xv6 kernel to print out a line when each system call is about to return, if the system call's number is set in the mask. The line should contain the process id, the name of the system call and the return value; you don't need to print the system call arguments. The trace system call should enable tracing for the process that calls it and any children that it subsequently forks, but should not affect other processes.

增加一个系统调用跟踪功能 `trace` ，它接受一个 int 类型的参数 `mask`（掩码） ，`mask` 的每一位表示一个系统调用，如果为 1 则表示要跟踪该系统调用，如果为 0 则表示不跟踪该系统调用。

以第一个测试用例 `trace 32 grep hello README` 解释一下实验目的：

- `trace 32`，表示本次要跟踪系统调用 `read()`，因为 32 =  1 << `SYS_read`
- `grep hello README`，表示需要在 `README` 文件中搜索 `hello` 字符串。
- 两个命令拼成一行，表示我要执行 `grep hello README` 命令，你需要在 `grep` 执行过程中，打印出所有调用 `read()` 系统函数的次数和返回值（返回值在这里不是很重要，按要求输出就好了，可以忽略）。

这个实验的代码量不是很多，实验重点还是要弄清楚 `syscall` 的调用流程，以及参数的传递。

### 1、新增 trace 系统调用函数

第一步，我们需要模仿其他的系统函数，把 `trace` 系统调用添加到工程中。在内核文件 `kernel/syscall.h` 新增加 `trace` 的系统调用号，它是全局唯一标识符，要保证不和其他系统调用号冲突，我们这里选择了递增。

```c
// System call numbers
#define SYS_fork    1
...
#define SYS_close  21
#define SYS_trace  22 // here
```

接着，在 `kernel/syscall.c` 增加 `trace()` 的全局函数声明，并且，在 `syscalls` 数组增加 `sys_trace()` 和 `SYS_trace` 的映射关系。

这一步是为了确保，当系统调用发生时，能够通过 `SYS_trace` 的调用号执行 `sys_trace` 函数。

```c
extern uint64 sys_chdir(void);
... 
extern uint64 sys_uptime(void);
extern uint64 sys_trace(void); // here

static uint64 (*syscalls[])(void) = {
[SYS_fork]    sys_fork,
...
[SYS_close]   sys_close,
[SYS_trace]   sys_trace,       // here，SYS_trace = 22 = 调用 sys_trace() 函数
};
```

user/usys.pl` 中，新增 `trace()` 的跳板函数，`usys.pl` 是 Perl 脚本，编译期间执行脚本生成 `usys.S` 文件

```c
entry("fork");
...
entry("uptime");
entry("trace");
```

`user/user.h` 新增 `trace()` 的函数声明，这一步是将系统函数提供给用户程序调用，这样用户程序就可以像调用普通函数一样使用 `trace()`

```c
// system calls
int fork(void);
...
int uptime(void);
int trace(int);
```

### 2、proc 结构体新增 systrace 字段

实验提示我们可以通过在 `proc` 结构中引入一个新的变量来实现新的系统调用

接下来，打开 `kernel/proc.h` 文件，在 `proc` 结构体新增一个 `int` 类型的 `systrace` 字段，它用于记录当前进程是否需要跟踪系统调用。

```c
struct proc {
  struct spinlock lock;
  ...
  char name[16];               // Process name (debugging)
  uint64 systrace;             // System call tracing
};
```

### 3、实现 trace 功能

最后一步，我们来实现 `systrace` 系统调用的功能

打开 `kernel/sysproc.c` 文件，在底部追加 `sys_trace()` 函数。

```c
uint64
sys_trace(void)
{
    int mask;
    // 读取参数寄存器 a0 的值
    argint(0, &mask);
    myproc()->systrace = mask;
    return 0;
}
```

- `argint()` 函数用于获取调用 `syscall` 发生时的入参，支持 0~5 的数字，读取寄存器 `a0`~`a5` 的值。
- 参数寄存器共 8 个，`a0`~`a7`，其中 `a7` 比较特殊，保存的是 `系统调用号`，寄存器一般存的是调用 `syscall` 时传入的参数。
- 发生 `syscall` 时，CPU 会把入参写入参数寄存器，举几个例子
  - `open()`：`a0` = 文件路径的指针（用户空间地址）、`a1` = 打开的模式、`a2` = 权限标志。
  - `write()`：`a0` = 文件描述符、`a1` = 写入数据的指针（用户空间地址）、`a2` = 写入数据的长度。
  - `pipe()`：`a0` = 用户空间数组的地址（用于返回读/写端描述符）。

打开 `kernel/syscall.c` 文件，实现最重要的打印功能。

```c
// 系统调用号 对应的系统调用函数
static uint64 (*syscalls[])(void) = {
        [SYS_fork]    sys_fork,
        ...
        [SYS_trace]   sys_trace,
};
// 系统调用号 对应的名称
const char *syscall_names[] = {
        [SYS_fork]    "fork",
        [SYS_exit]    "exit",
        [SYS_wait]    "wait",
        [SYS_pipe]    "pipe",
        [SYS_read]    "read",
        [SYS_kill]    "kill",
        [SYS_exec]    "exec",
        [SYS_fstat]   "fstat",
        [SYS_chdir]   "chdir",
        [SYS_dup]     "dup",
        [SYS_getpid]  "getpid",
        [SYS_sbrk]    "sbrk",
        [SYS_sleep]   "sleep",
        [SYS_uptime]  "uptime",
        [SYS_open]    "open",
        [SYS_write]   "write",
        [SYS_mknod]   "mknod",
        [SYS_unlink]  "unlink",
        [SYS_link]    "link",
        [SYS_mkdir]   "mkdir",
        [SYS_close]   "close",
        [SYS_trace]   "trace",
};

void
syscall(void) { // 该函数是 syscall 统一的入口
    int num;
    struct proc *p = myproc();

    // 从 a7 寄存器中获取系统调用号，然后去 syscalls 数组找对应下标指向的系统调用函数。
    num = p->trapframe->a7;
    if (num > 0 && num < NELEM(syscalls) && syscalls[num]) {
        p->trapframe->a0 = syscalls[num]();
        // 检查 systrace 的第 num 位是否为1
        if ((p->systrace >> num) & 1) {
            printf("%d: syscall %s -> %d\n", p->pid, syscall_names[num], p->trapframe->a0);
        }
    }...
}
```

因为实验要求把系统调用号对应的名称也一起打印出来，所以我们需要再增加一个数组，用来映射 `系统调用号` 和 `系统调用名称`。

到这里，`trace` 的功能就完成了，但还有两个小细节需要补充

1. **每个进程结构体回收时，需要将 `systrace` 字段置为 0，防止脏数据污染。**
2. **调用 `fork` 创建子进程时，子进程的 `systrace` 字段也需要继承父进程的 `systrace` 值。**

细节补充 `kernel/proc.c`

```c
static void
freeproc(struct proc *p) {
    if (p->trapframe)
        kfree((void *) p->trapframe);
    ...
    p->state = UNUSED;
    p->systrace = 0;
}

int
fork(void) {
    int i, pid;
    ...
    np->systrace = p->systrace; // inherit systrace flag
    release(&np->lock);
    return pid;
}
```

最后在 `Makefile` 增加 `$U/_trace\`

```
UPROGS=\
	$U/_cat\
	...
	$U/_trace\ // here
```

`make qemu` 一起来看测试结果：

```shell
xv6 kernel is booting

hart 2 starting
hart 1 starting
init: starting sh
$ trace 32 grep hello README
3: syscall read -> 1023
3: syscall read -> 966
3: syscall read -> 70
3: syscall read -> 0
$ trace 2147483647 grep hello README
4: syscall trace -> 0
4: syscall exec -> 3
4: syscall open -> 3
4: syscall read -> 1023
4: syscall read -> 966
4: syscall read -> 70
4: syscall read -> 0
4: syscall close -> 0
```

从打印日志来看，是符合实验要求的，接着运行 `./grade-lab-syscall trace`

```shell
$ ./grade-lab-syscall trace
make: 'kernel/kernel' is up to date.
== Test trace 32 grep == trace 32 grep: OK (4.2s) 
== Test trace all grep == trace all grep: OK (2.9s) 
== Test trace nothing == trace nothing: OK (2.9s) 
== Test trace children == trace children: OK (54.0s) 
    (Old xv6.out.trace_children failure log removed)
$ 
```

自动测试也通过了，完整的代码在：https://github.com/yibaoshan/xv6-labs-2020/commit/347f74803ba687b3e2f935c7cea70ff191160202

> *注意：`gradelib.py` 文件中 `run_qemu_kw()` 函数规定，单个测试用例最大运行时长是 30s，如果超过这个时间会报 ‘`== Test trace children == Timeout! trace children: FAIL (31.3s) `’ 错误，解决方案是把 `timeout` 值调大。*

# Sysinfo (moderate)

In this assignment you will add a system call, sysinfo, that collects information about the running system. The system call takes one argument: a pointer to a struct sysinfo (see kernel/sysinfo.h). The kernel should fill out the fields of this struct: the freemem field should be set to the number of bytes of free memory, and the nproc field should be set to the number of processes whose state is not UNUSED. We provide a test program sysinfotest; you pass this assignment if it prints "sysinfotest: OK".

写一个系统调用函数 `sysinfo()`，能够获取系统当前的空闲内存和已经使用的进程数量。

### 1、获取空闲内存

在 `defs.h` 头文件声明 `kget_avl_mem()` 函数，用于获取空闲内存

```c
// kalloc.c
void*           kalloc(void);
...
void            kget_avl_mem(void); // here
```

实现统计功能，在 `kalloc()` 函数的基础上改就行了，打开 `kalloc.c`

```c
uint64
kget_avl_mem(void) {
    acquire(&kmem.lock);
    uint64 available_bytes = 0;
    struct run *r = kmem.freelist;
    while (r) {
        available_bytes += PGSIZE;
        r = r->next;
    }
    release(&kmem.lock);
    return available_bytes;
}
```

### 2、获取进程数量

同上，在 `defs.h` 声明 `cur_proc_cnt()` 函数

```c
// proc.c
int             cpuid(void);
...
void            procdump(void);
uint64			cur_proc_cnt(void); // here
```

函数实现部分丢在 `proc.c `文件

```
uint64
cur_proc_cnt(void) {
    struct proc *p;
    uint64 proc_cnt = 0;
    for (p = proc; p < &proc[NPROC]; p++) {
        proc_cnt += p->state != UNUSED ? 1 : 0;
    }
    return proc_cnt;
}
```

### 3、实现 sysinfo 功能

`获取空闲内存` 和 `已使用进程数量` 的函数都已经完成，接下来我们来实现 `sysinfo()` 系统调用功能, 在 kernel/sysproc.c 文件中新增 `sysinfo()` 函数。

```c
uint64
sys_sysinfo(void)
{
    uint64 addr;

    // 还是从 a0 寄存器读取用户程序传过来的地址，该地址用于把数据从内核内存复制到用户内存
    if(argaddr(0, &addr) < 0)
        return -1;

    struct sysinfo s;
    s.freemem = kget_avl_mem();
    s.nproc = cur_proc_cnt();

    // 这里使用了 copyout() 函数，把处于内核内存中的 sysinfo 数据，复制到用户程序的内存中
    if(copyout(myproc()->pagetable, addr, (char *)&s, sizeof(s)) < 0)
        return -1;
    return 0;
}
```

剩下要做的就是把新增的 `sysinfo()` 函数注册到为系统调用

kernel/syscall.h

```c
// System call numbers
#define SYS_fork    1
...
#define SYS_trace  22
#define SYS_sysinfo  23
```

kernel/syscall.c

```c

...
extern uint64 sys_trace(void);
extern uint64 sys_sysinfo(void); // append sys_sysinfo() to syscall.c

// 系统调用号 对应的系统调用函数
static uint64 (*syscalls[])(void) = {
        [SYS_fork]    sys_fork,
        ...
        [SYS_trace]   sys_trace,
        [SYS_sysinfo]   sys_sysinfo, // here
};

// 系统调用号 对应的名称
const char *syscall_names[] = {
        [SYS_fork]    "fork",
        ...
        [SYS_trace]   "trace",
        [SYS_sysinfo]   "sysinfo", // here
};

```

user/user.h

```c
// system calls
int fork(void);
...
int trace(int);
struct sysinfo;
int sysinfo(struct sysinfo *);
```

user/usys.pl

```c
entry("fork");
...
entry("trace");
entry("sysinfo");
```

编译运行 `sysinfotest` 查看结果：

```shell
$ make qemu

xv6 kernel is booting

hart 2 starting
hart 1 starting
init: starting sh
$ sysinfotest
sysinfotest: start
sysinfotest: OK
$ 
```

运行打分程序 `./grade-lab-syscall sysinfo`

```shell
$ ./grade-lab-syscall sysinfo
make: 'kernel/kernel' is up to date.
== Test sysinfotest == sysinfotest: OK (12.5s) 
$ 
```

搞定收工，完整代码在：https://github.com/yibaoshan/xv6-labs-2020/commit/1e4e5543d4ab18859899070c6b9cb1b8e3661edd

# 参考资料

- CS自学指南：https://csdiy.wiki/%E6%93%8D%E4%BD%9C%E7%B3%BB%E7%BB%9F/MIT6.S081/
- Miigon：https://blog.miigon.net/categories/mit6-s081/
- Wings：https://blog.wingszeng.top/series/learning-mit-6-s081/
- 知乎专栏《28天速通MIT 6.S081操作系统》：https://zhuanlan.zhihu.com/p/632281381