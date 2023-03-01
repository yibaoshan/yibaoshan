
## 创建方式

- Executors#newCached/SingleThreadPool 等等，被阿里开发手册禁用
  - LinkedBlockingQueue 容量默认 MAX_VALUE，容易 OOM
- new ThreadPoolExecutor()
  - corePoolSize 为线程池的基本大小。通过 Runtime.getRuntime().availableProcessors() 获取 CPU 核心设置，效果不大。。
  - maximumPoolSize 为线程池最大线程大小。我觉得和内存相关，大内存可以设置的多点，核心数 x 10 都没问题
  - keepAliveTime 和 unit 则是线程空闲后的存活时间。
  - workQueue 用于存放任务的阻塞队列。通常是 LinkedBlockingQueue
  - threadFactory，设置创建线程方式，可以在这指定线程名称，优先级啥的，有默认值可以不传
    - defaultThreadFactory，优先级为 NORM_PRIORITY
  - handler 当队列和最大线程池都满了之后的饱和策略。系统实现了四个，可以不传
    - AbortPolicy，直接抛出异常，默认策略
    - CallerRunsPolicy，在当前线程执行
    - DiscardPolicy，抛弃不管
    - DiscardOldestPolicy，丢弃原先等待队列末尾任务，替换成自己

submit() 和 execute()的区别：submit()方法有返回值Future，而execute()方法没有返回值


## 线程状态

Linux 操作系统线程，有三种状态

- **ready** ：线程已创建，等待系统调度，分配 CPU 使用权。
- **running**：线程获得了 CPU 使用权，正在进行运算
- **waiting**：线程等待（或者说挂起），让出 CPU 资源给其他线程使用

Java 1.2 以后，在 Linux 平台的 JVM 是基于 pthread 实现的。因此，Java 线程 = 操作系统中的线程

不过，Java 增加了几种线程状态，都是和同步状态有关

- **new**: 线程还未 started 时的状态 即 new Thread().getState()
- **runnable**: 线程 start() 后的状态， 可在 run() 方法中 Thread.currentThread().getState() 获得
- **blocked**：等待锁的时的状态，在进入 synchronized 块
- **waiting**：正在等待另一个线程执行特定操作，比如调用了 Object#wait()，Thread#join()， LockSupport#park()
- **timed_waiting**: 具有指定等待时间的等待线程的线程状态,当调用 Thread#sleep(long)，Object#wait(long)，Thread.join(long)，LockSupport#parkNano(long) ,LockSupport#parkUntil(long)时进入
- **terminated**: 线程结束，对操作系统来说无意义

## 线程池设置

核心线程数量

- 大部分 APP 基本都是 I/O 密集型，因为网络请求比较多
- 根据网络文章，CPU 密集核心数应设为 N + 1，I / O 密集设为 2N + 1
- 实测一个普通的电商 APP 线程在 50 ~ 100 ，微信线程数量近 200
- 核心数
