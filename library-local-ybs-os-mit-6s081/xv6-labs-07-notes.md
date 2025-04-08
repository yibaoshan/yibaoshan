
# chapter 6 of book xv6 os book

睡眠锁 - xv6 为长时间持锁的场景设计一种既能阻塞等待又能让出 CPU 的锁机制。

例如，文件系统在读写磁盘时要持有文件锁，磁盘 I/O 可能耗时几十毫秒。

如果用自旋锁，其他进程在等待时会一直忙等（浪费 CPU），并且自旋锁持有期间不能让出 CPU，也不能开中断，容易导致死锁或响应性下降。

# chapter 7 of book xv6 os book

- 任何时候，一个 CPU 核心只能运行一个线程：用户线程，内核线程，调度线程。
- 一个进程执行用户级指令，或者在内核中执行指令，或者不执行，状态被保存在 trapframe 和上下文中。
- 每个进程有两个线程：一个是用户级线程，一个是内核级线程，进程要么在用户空间执行，要么在内核空间执行。

通过下面四个步骤，让 xv6 在多核 CPU 上既保证了每个进程在用户空间拥有“自己的 CPU 感觉”，也能高效、安全地在内核中来回切换，对于某一个 CPU 来说：

1. 用户态 → 内核态（Trap） 
   - 用户进程执行 ecall（系统调用）或被硬件中断打断，跳转到位于 trampoline 的 uservec。 
   - uservec 将所有用户寄存器（包括通用寄存器、程序计数器 sepc、段寄存器等）保存到该进程的 trapframe（位于内核栈底部），切换到内核页表后跳到 usertrap() 继续处理。
2. 内核态旧进程 → 调度线程（swtch） 
   - 如果在中断处理或系统调用中决定要让出 CPU（比如定时器中断触发 yield()，或进程调用 sleep()/exit()），会进入 sched()。 
   - sched() 要求持有 p->lock，然后调用 swtch(&p->context, mycpu()->context)： 
     - 把当前内核线程的 callee‑saved 寄存器（s0–s11、ra、sp 等）存入 p->context。 
     - 切换到当前 CPU 专用的调度上下文 cpu->context，继续执行调度函数。
3. 调度线程 → 新进程内核线程（swtch） 
   - 调度函数遍历进程表，找到一个 RUNNABLE 的 p，设置 p->state = RUNNING、cpu->proc = p，并持有 p->lock。 
   - 调用 swtch(&cpu->context, &p->context)： 
     - 把调度线程的寄存器保存到 cpu->context， 
     - 从 p->context 恢复寄存器，切换到该进程的内核栈和内核执行点。
4. 内核态 → 用户态（trap return）
   - 新进程的内核线程执行到 usertrapret()（在 usertrap() 尾部设置好），它： 
     - 用 trapframe 中保存的寄存器恢复用户寄存器状态， 
     - 切回到该进程的用户页表
     - 执行 RISC‑V 的 sret 指令，跳回用户态的 sepc。

重点

- 两次 swtch： 
  - 从“要让出的进程”→“调度线程” 
  - 从“调度线程”→“要运行的进程”
- 专用调度栈：每个 CPU 有独立的 cpu->context 和调度栈，避免多个核在同一内核栈上执行。
- 锁的配合：切换前后都要持有 p->lock，保证在修改 p->state、p->context、cpu->proc 等关键字段时不会竞态。

# Lecture 9: interrupts

与 trap 不同的点是

1. 中断来自 CPU 外，随时可能会发生，并且，中断的事件，可能和当前正常运行的程序一点关系都没有
2. 并发性，外部设备，例如网卡，是和 CPU 同时运行的
3. 外部设备需要被编程

- 中断那会停止当前运行的程序，对于用户程序而言这不是个问题，但对内核来说，事情就不一样了。这意味着，即使是内核代码，也不是直接串行的。

# Lecture 10： Multi-threading

为了保证能成功 “上锁”，CPU 提供了原子操作指令。

死锁，线程 A 持有锁 A，需要锁 B，线程 B 持有锁 B，需要锁 A

# Lecture 11：Thread Switching

yield - 自愿让出 CPU。

pre-emptive - 定时器中断将CPU控制权给到内核，内核再自愿的出让CPU。