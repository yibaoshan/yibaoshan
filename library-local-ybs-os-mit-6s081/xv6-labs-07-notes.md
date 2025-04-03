
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