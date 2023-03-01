
Android 的 Dalvik / ART 虚拟机是为了给 APP 的 Java / Kotlin 代码提供运行环境，其中，编译、执行、垃圾回收(GC)这三个模块是虚拟机的重中之重

## JIT 与 AOT

- **解释执行**：JVM / Dalvik / ART 从磁盘加载 class / dex 到内存，将代码解释成目标平台指令集，等待 OS 调度执行
- **JIT (Just-in-time compilation)**：即时编译，实时收集热点代码，满足阈值动态编译为 machine code，等待 OS 调度执行
- **Ahead of time**：提前编译为本地机器码，格式为 oat（遵循 ELF 格式），缺点是增加安装耗时和占用更多本地存储

## Dalvik VS ART

- **指令集**：JVM 和 ART 都是基于栈的指令集实现（stack-based），而 Dalvik 是基于寄存器的指令集实现（register-based）
- 基于寄存器：执行速度更快（解释执行内存访问次数会减少），代价是更大的代码长度
- 基于栈：指令简单；
- Android 1.0，Dalvik 作为 Android 虚拟机运行环境
- Android 2.2，Dalvik 加入 JIT 动态编译
- Android 4.4，加入 ART 虚拟机
  - 此时的 ART 只支持 AOT 编译，没有 JIT
  - GC 优化：ART 比 Dalvik 拥有更快的 GC 速度，Dalvik 一次 GC 分为 4 个阶段（find root、mark1、mark2、collect），第一/三步会暂停两次程序，ART 仅需一次（标记 GC Root 阶段改为并发）
  - 分代管理：ART 引入了分代管理（新生代和老年代），同时也加入了针对分区的垃圾回收器
  - 内存分配：ART 使用新的内存分配算法（RosAlloc）来替代 Dalvik 使用的 dlmalloc 算法，性能提高约 4~5 倍
  - 内存整理：ART 加入了整理内存，在后台把不连续的内存使用 copy 为连续内存，来避免内存碎片化，比如有 20MB 剩余空间 (20个 1MB 的内存碎片)，尝试分配 2MB 空间抛出了 OOM 错误
- Android 5.0，Dalvik 退出舞台
- Android 7.0，ART 混合使用 AOT 编译，解释和 JIT 三种运行时
- Android 8.0 ~ Android 9.0 以后的 ART
    - 引入 CC（Concurrent Copy）回收器，通过 read-barrier（读屏障）减少暂停，类似 HotSpot 的 G1
    - 移除了分代管理，只用 CC 来回收堆内存
    - 修改内存分配方式，使用了 Bump Allocator 的机制来分配内存（region）
    - 内存分配修改后，新 GC 分为 Pause（STW）, Copying, Reclaim 三个阶段，以 Region 为单位进行 GC
- Android 10
  - Concurrent Copying (CC) GC 中又加入了分代管理，对应的新生代（Minor GC）与老年代（Major GC）也就回来了

## 方法执行

ART 对于 Java 方法实现了两种执行模式 ArtMethod#Invoke()

- 解释执行字节码，入口是 EnterInterpreterFromInvoke，该函数定义在 art/runtime/interpreter/interpreter.cc
- 快速执行，即直接调用通过 OAT 编译后的本地代码，在 ARM64 中的定义在 art/runtime/arch/arm64/quick_entrypoints_arm64.S

## 守护线程

- ReferenceQueueDaemon：引用队列守护线程，引用对象的关联的队列。当被引用对象引用的对象被GC回收的时候，被引用对象就会被加入到其创建时关联的队列去。这样应用程序就可以知道那些被引用对象引用的对象已经被回收了
- FinalizerDaemon：析构守护线程。重写了 finalize 的对象，它们被GC决定回收时，并没有马上被回收，而是被放入到一个队列中，等待FinalizerDaemon守护线程去调用它们的成员函数finalize，然后再被回收。
- FinalizerWatchdogDaemon：析构监护守护线程。用来监控FinalizerDaemon线程的执行。一旦检测那些重定了成员函数finalize的对象在执行成员函数finalize时超出一定的时候，那么就会退出VM。
- HeapTrimmerDaemon：堆裁剪守护线程。用来执行裁剪堆的操作，也就是用来将那些空闲的堆内存归还给系统。
- GCDaemon：并行GC线程。用来执行并行GC。



