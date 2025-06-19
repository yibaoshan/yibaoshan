
并发安全问题的本质，是共享资源的竞争和操作的原子性，在操作系统的层面上

- OS 基于硬件提供的中断机制、Trap 机制，触发任务切换，任务切换是并发执行的基础。
- 除了内存操作，OS 提供了文件、设备、网络等资源给应用程序使用，所以，对 OS 来说，共享资源不仅限于主存，还包括文件、设备、网络等系统资源，这些由操作系统提供的同步机制来保证访问安全。
- 同步机制方面，操作系统基于硬件的原子操作指令，封装同步原语（互斥锁、信号量、条件变量）给应用程序使用，这也保证了 OS 的操作原子性。

# OS 同步原语

基础能力，互斥、自旋、信号量

### 操作指令封装

原子操作封装 ：

```c
// arch/x86/include/asm/atomic.h
static inline int atomic_cmpxchg(atomic_t *v, int old, int new) // 比较并交换
{
    return cmpxchg(&v->counter, old, new);
}
```

内存屏障封装 ：

```c
// include/linux/compiler.h
#define barrier() __asm__ __volatile__("": : :"memory")
#define mb()  asm volatile("mfence":::"memory") // 全屏障
#define rmb() asm volatile("lfence":::"memory") // 读屏障
#define wmb() asm volatile("sfence":::"memory") // 写屏障
```

### 基础同步原语

自旋锁（Spinlock），基于原子操作实现忙等待

```c
typedef struct {
    atomic_t lock;
} spinlock_t;

void spin_lock(spinlock_t *lock) {
    while (atomic_cmpxchg(&lock->lock, 0, 1) != 0)
        cpu_relax();
}
```

信号量（Semaphore），同样依赖原子操作

```c
struct semaphore {
    raw_spinlock_t lock;
    unsigned int count;
    struct list_head wait_list;
};
```

互斥锁（Mutex），结合自旋实现

```c
struct mutex {
    atomic_t count;
    spinlock_t wait_lock;
    struct list_head wait_list;
};
```

### 高级/衍生同步原语

- 读写锁（RW Lock），基于自旋锁和计数器实现读写分离
- 条件变量（Condition Variable），基于互斥锁和等待队列实现等待/通知机制
- RCU（Read-Copy Update），基于自旋锁和内存屏障实现读多写少场景的同步
- ...
