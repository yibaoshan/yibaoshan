### Java J.U.C包

- juc-locks 锁框架
  - ReentrantLock
  - ReentrantReadWriteLock
  - StampedLock
- juc-atomic 原子类框架
  - 原子类：AtomicBoolean...
  - 原子数组：AtomicIntergerArray...
- juc-sync 同步器框架
  - CountDownLatch
  - Semaphore
- juc-collections 集合框架
  - Map：键值对
    - ConcurrentHashMap
    - ConcurrentSkipListMap
  - Set：无序集合
    - CopyOnWriteArraySet
    - ConcurrentSkipListSet
  - List：有序集合
    - CopyOnWriteArrayList
  - Queue：队列
    - 普通队列
      - LinkedBlockingQueue
      - ArrayBlockingQueue
      - PriorityBlockingQueue
      - SynchronousQueue
      - DelayQueue
      - LinkedTransferQueue
      - ConcurrentLinkedQueue
    - 双端队列
      - ConcurrentLikedDeque
      - LinkedBlockingDeque
- juc-executors 执行器框架
  - 线程池
  - Future
  - Fork/Join框架

**ConcurrentHashMap**

- JDK5：分段锁，必要时加锁
- JDK6：优化二次hash算法
- JDK7：段懒加载，volatile&cas
- JDK8：摒弃段，基于hashmap原理的并发实现

####Java线程8大原子操作

```
1.lock 将对象变成线程独占的状态
2.unlock 将线程独占状态的对象的锁释放出来
3.read 从主内存读数据
4.load 将从主内存读取的数据写入工作内存
5.use 工作内存使用对象
6.assign 对工作对象进行复制操作
7.store 在工作内存对象传送到主内存
8.write 将工作内存对象写入主内存，并覆盖旧值
```

####CPU缓存一致性 MESI

```
缓存的4种状态
1.modified（修改的） 是指当前缓存行数据有效，但是和主存数据不一致，当前数据只存在与本cache中
ps：个人理解，这个数据被其他线程修改了，并且其他人都知道，就TM我不知道？
2.exclusive（独家的） 是指缓存行数据有效，和主内存的也一样，但只存在于本cache中
ps：个人理解，这个数据被老子修改了，而且我TMD还更新到主存了，哈哈哈，volatile应该就是这个作用
3.shared（共享） 缓存行数据有效，和主存的也一致，数据存在于很多cache中
ps：个人理解，这个是有效数据，可以放心使用，但是有个疑问，存在于很多cache中是什么意思？不应该是没人更改这个数据吗？
4.invalid（无效）无效缓存行数据
```

####Volatile

```
保证线程完全的三个前提是：
1.原子性
2.可见性
3.有序性
volatile只能保证可见性和有序性
所以，volatile不可以做到线程安全
最后摘自《深入理解Java虚拟机》
在观察加入volatile的关键字的汇编代码发现，加入volatile会多出lock前缀指令
lock前缀指令实际上相当于内存屏障，它会提供3个功能
1.防止重排序，不会将其后面的指令排到内存屏障的前面，同样，也不会将内存屏障前面的指令排到后面
2.更改数据后强制刷新到主存
3.如果是写操作，那么其他CPU的缓存行失效
```

### Synchronized

在Java1.6之前，synchronize只是重量级锁，需要依赖Mutex Lock切换进程到和心态来完成

1.6之后引入了偏向锁和轻量级锁和自旋锁的实现

要理解synchronize的底层实现，首先要了解Java对象头和Monitor

Java对象由三个部分组成，对象头、实例数据、填充数据

作用在方法内部时，class字节码显示调用monitor进入和退出

作用在方法上时，该方法签名的flag会增加ACC_SYNCHRONIZED标识

### AQS

### Unsafe

### Java线程

**线程状态**

1. 新建状态：使用new关键字
2. 就绪状态：当线程对象调用start方法后
3. 运行状态：执行run方法
4. 阻塞状态：sleep或suspend
5. 死亡状态

####Java线程池

介绍：线程池属于开发中常见的一种**池化技术**，这类的池化技术的目的都是为了提高资源的利用率和提高效率，类似的HttpClient连接池，数据库连接池等。

四种常见的线程池：

~~~
1. newFixedThreadPool (固定数目线程的线程池)
2. newCachedThreadPool(可缓存线程的线程池)
3. newSingleThreadExecutor(单线程的线程池)
4. newScheduledThreadPool(定时及周期执行的线程池)
~~~

**线程池常用参数**

1. corePoolSize：核心数量，不会被回收，即使没有任务也会空闲
2. maximumPoolSize：最大数量，当队列拍满时会创建临时线程
3. keepLiveTime和unit：空闲线程的存活时间
4. workQueu：等待队列
5. handler：拒绝策略
   1. AbortPolicy：为线程池默认的拒绝策略，该策略直接抛异常处理。
   2. DiscardPolicy：直接抛弃不处理。
   3. DiscardOldestPolicy：丢弃队列中最老的任务。
   4. CallerRunsPolicy：将任务分配给当前执行execute方法线程来处理。

**为什么阿里开发手册不建议使用Executors创建线程池**

1. 内部使用的队列是LinkedBlockingQueu，默认大小时Int.max，会oom

#### ThreadLocal

#### 问题分析

- 如何停止一个线程？

  答：使用interrupt或者自定义Boolean

- 一个线程需要的内存？

  答：256k~1m

- Thread.interrupted和interrupt有什么区别？

  答：静态方法调用后会清空状态

- AtomicReference和AtomicReferenceFieldUpdater有何异同？

  - 功能一致，原理相同，都是基于unsafe的cas操作
  - AR通常作为对象的成员使用，开启指针压缩占16B，不开占24B
  - ARFU通常作为类的静态成员使用，对实例成员进行修改
  - AR使用更友好，ARFU适合实例比较多的场景

