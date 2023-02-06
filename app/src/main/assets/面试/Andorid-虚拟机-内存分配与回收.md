
## JIT 与 AOT

- **解释执行**：JVM / Dalvik / ART 从磁盘加载 class / dex 到内存，将代码解释成目标平台指令集，等待 OS 调度执行
- **JIT (Just-in-time compilation)**：即时编译，实时收集热点代码，满足阈值动态编译为 machine code，等待 OS 调度执行
- **Ahead of time**：提前编译为本地机器码，格式为 oat（遵循 ELF 格式），缺点是增加安装耗时和占用更多本地存储

## Dalvik VS ART

- Android 1.0，Dalvik 作为 Android 虚拟机运行环境
- Android 2.2，Dalvik 加入 JIT 动态编译
- Android 4.4，加入 ART 虚拟机，此时的 ART 只支持 AOT
- Android 5.0，Dalvik 退出舞台
- Android 7.0，ART 混合使用 AOT 编译，解释和 JIT 三种运行时
- **指令集**：JVM 和 ART 都是基于栈的指令集实现（stack-based），而 Dalvik 是基于寄存器的指令集实现（register-based）
- 基于栈：指令简单；
- 基于寄存器：执行速度更快（因为内存访问次数会减少），代价是更大的代码长度
- GC 优化：ART 比 Dalvik 拥有更快的 GC 速度，Dalvik 一次 GC 会暂停两次程序，ART 仅需一次（标记 GC Root 阶段改为并发处理）
- 分代管理：ART 引入了分代管理（新生代和老年代），同时也加入了针对分区的垃圾回收器
- 内存分配：ART 使用新的内存分配算法（RosAlloc）来替代 Dalvik 使用的 dlmalloc 算法，性能提高约 4~5 倍
- 内存整理：ART 加入了整理内存，在后台把不连续的内存使用 copy 为连续内存，来避免内存碎片化，比如有 20MB 剩余空间 (20个 1MB 的内存碎片)，尝试分配 2MB 空间抛出了 OOM 错误
- Android 8.0 以后的 ART
    - 移除了分代管理
    - 引入 CC（Concurrent Copy） 回收器，通过 read-barrier（读屏障）减少暂停，类似 HotSpot 的 G1 回收器
    - 修改内存分配方式，使用了 Bump Allocator 的机制来分配内存（region）
    - 内存分配修改后，新 GC 分为 Pause（STW）, Copying, Reclaim 三个阶段，以Region为单位进行GC

## 方法执行

ART 对于 Java 方法实现了两种执行模式 ArtMethod#Invoke()

- 解释执行字节码，入口是 EnterInterpreterFromInvoke，该函数定义在 art/runtime/interpreter/interpreter.cc
- 快速执行，即直接调用通过 OAT 编译后的本地代码，在 ARM64 中的定义在 art/runtime/arch/arm64/quick_entrypoints_arm64.S

## ART 内存分配

APP 运行时，虚拟机的内存分布：

- 256m ~ 512m 不等的 heap 空间，供

## ART 垃圾回收

art 支持的回收算法

```
enum CollectorType {
  kCollectorTypeMS,   mark-sweep 回收算法
  kCollectorTypeCMS, 并行mark-sweep 算法  
  kCollectorTypeSS,  // semi-space和mark-sweep 混合算法
  kCollectorTypeGSS,  分代kCollectorTypeSS.
  kCollectorTypeMC,  mark-compact算法
  kCollectorTypeCC,  concurrent-copying 算法
};
```

## 触发 GC

GC 触发条件在 art/runtime/gc/gc_cause.h 中，常见的 GcCause 有三种：

- GcCauseForAlloc：分配内存时，比如 new 新对象。堆中剩余空间不足以满足申请的内存大小，并发 GC
- GcCauseExplicit：手动调用 System.gc() 时，阻塞 GC
- GcCauseBackground：后台 GC，程序空闲时可能会触发，算法会并行

- 

## GC Roots 与 Stop-The-World

safePoint 是进入 的安全点，在 ART 中，由编译器（6.0 以前是 Quick 编译器，6.0 以后是 Optimizing 编译器）和解释器它俩完成添加 safePoint 的操作

