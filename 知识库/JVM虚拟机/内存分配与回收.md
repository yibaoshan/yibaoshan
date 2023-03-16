
Dalvik 已过时，暂不讨论

## OAT 文件

- APK 安装过程中，会通过dex2oat工具生成一个 OAT 文件，保存到
- Android 私有的 ELF 文件格式
- 包含有从 DEX 文件翻译而来的 machine code（oatexec部分），还包含有原来的 DEX 文件内容（oatdata部分）

## ART 内存划分

源码地址：/art/runtime/gc/space/

ART 将 heap 划分为 spaces 进行管理（CardTable），系统版本不同 spaces 划分也不同，不过一般可以分为：

- RegionSpace：应用堆区对象几乎都在这，GC 活跃区；由一个个的 Region 组成，单个 region 256KB
- LargeObjectSpace：大对象分配区，比如 7.0 之前的 Java Bitmap，算应用占用内存
- Non-Moving Space：具体是哪个 space 暂未可知，jvm 规范的方法区，存放 class 信息，常量，静态变量等
- ImageSpace：暂时没搞清楚用途，无需 GC
- ZygoteSpace：写时复制的 art 虚拟机等资源，无需 GC

## ART 内存分配

源码地址：/art/runtime/gc/allocator/

经过几次迭代，Android 7.0 常用的三种分配器：

```
/art/runtime/gc/allocator_type.h（Android 7.1）
enum AllocatorType {
  kAllocatorTypeNonMoving,  // 分配不常移动，无需 GC 的分频器
  kAllocatorTypeLOS,  // Large object space,大对象分配，比如数组，字符串？
  kAllocatorTypeRegion, // 最常用的分配器，GC 活跃区
};
```

## ART 垃圾回收

源码地址：/art/runtime/gc/collector/

JVM 常见的垃圾回收算法有三种，它们各有优缺点：

- 标记-复制：实现简单，效率高，不产生内存碎片，缺点是内存空间利用率低
- 标记-清理：无需浪费控件，缺点是清理后内存碎片化严重
- 标记-整理：标记-清除+整理

art 支持的回收算法

```
enum CollectorType {
  kCollectorTypeMS,   mark-sweep 回收算法
  kCollectorTypeCMS, 并行 mark-sweep 算法  
  kCollectorTypeSS,  // semi-space和mark-sweep 混合算法
  kCollectorTypeGSS,  分代kCollectorTypeSS.
  kCollectorTypeMC,  mark-compact算法
  kCollectorTypeCC,  concurrent-copying 算法
};
```

## 触发 GC

源码地址：art/runtime/gc/gc_cause.h，常见的 GcCause 有三种：

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

