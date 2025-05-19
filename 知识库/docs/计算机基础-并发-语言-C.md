
简单罗列 POSIX 线程库（pthread）的并发支持

# 同步原语

互斥锁（Mutex）

```c
pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;

// 基本操作
pthread_mutex_lock();      // 加锁
pthread_mutex_trylock();   // 尝试加锁
pthread_mutex_unlock();    // 解锁

// 属性设置
pthread_mutexattr_t attr;
pthread_mutexattr_init();
pthread_mutexattr_settype();  // 设置类型（普通、递归、检错等）
```

条件变量（Condition Variable）

```c
pthread_cond_t cond = PTHREAD_COND_INITIALIZER;

pthread_cond_wait();       // 等待条件
pthread_cond_signal();     // 唤醒一个线程
pthread_cond_broadcast();  // 唤醒所有线程
```

自旋锁（Spinlock）

```c
pthread_spinlock_t spinlock;

pthread_spin_lock();       // 获取自旋锁
pthread_spin_unlock();     // 释放自旋锁
```

...