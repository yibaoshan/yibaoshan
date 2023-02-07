
Dalvik 已过时，暂不讨论

## ART 内存分配

ART 将 heap 划分为 spaces 进行管理（CardTable），系统版本不同 spaces 划分也不同，不过一般可以分为：

- RegionSpace：应用堆区对象几乎都在这，GC 活跃区。由一个个的 Region 组成，单个 region 256KB
- ImageSpace：系统 Framework 类都在这，比如 Activity，无需 GC
- ZygoteSpace：系统 Framework 类都在这，比如 Activity，无需 GC
- LargeObjectSpace：大对象分配区，比如 7.0 之前的 Java Bitmap，算应用占用内存

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

## GC Roots

- System Class：系统类加载器加载的类，比如 Activity
- Thread：未停止的线程
- Java Stack Frame：方法栈引用的对象
- Finalizable：FinalizerDaemon 线程维护的等待重写 finalize() 方法的对象

Stop-The-World

safePoint 是进入 的安全点，在 ART 中，由编译器（6.0 以前是 Quick 编译器，6.0 以后是 Optimizing 编译器）和解释器它俩完成添加 safePoint 的操作

