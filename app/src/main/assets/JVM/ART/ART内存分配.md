
## 内存分配器 allocator

负责具体内存分配的实现，目前只包含了 dlmalloc和 rosalloc两种实现

- dlmalloc.h/.cc ：传统 Linux 多用
- rosalloc.h/.cc：Google 为 ART 虚拟机开发了一个新的内存分配器

在 ART 虚拟机中，这两种内存分配器都有使用。

## Space 体系结构

ART虚拟机启动中便会创建 Heap 对象。其实在 Heap 的构造函数，还会创建下面两类对象：

- 若干个Space对象：Space用来响应应用程序对于内存分配的请求
- 若干个 GarbageCollector对 象：GarbageCollector用来进行垃圾收集，不同的GarbageCollector执行的策略不一样

Space有下面几种类型：

```
enum SpaceType {
  kSpaceTypeImageSpace,
  kSpaceTypeMallocSpace,
  kSpaceTypeZygoteSpace,
  kSpaceTypeBumpPointerSpace,
  kSpaceTypeLargeObjectSpace,
  kSpaceTypeRegionSpace,
};
```

在一个运行的ART的虚拟机中，上面这些Space未必都会创建。

有哪些Space会创建由ART虚拟机的启动参数决定。

Heap对象中会记录所有创建的Space，如下所示：

```
// heap.h
std::vector<space::ContinuousSpace*> continuous_spaces_ GUARDED_BY(Locks::mutator_lock_);
std::vector<space::DiscontinuousSpace*> discontinuous_spaces_ GUARDED_BY(Locks::mutator_lock_);
std::vector<space::AllocSpace*> alloc_spaces_;
...
```

Heap类的AllocObject是为对象分配内存的入口

在这个方法的实现中，会首先通过 Heap::TryToAllocate尝试进行内存的分配

在Heap::TryToAllocate方法，会根据AllocatorType，选择不同的Space进行内存的分配，AllocatorType的类型有如下一些：

```
enum AllocatorType {
  ...
  kAllocatorTypeLOS,  // Large object space, also doesn't have entrypoints.
  kAllocatorTypeRegion,
  kAllocatorTypeRegionTLAB,
};
```

如果Heap::TryToAllocate失败（返回nullptr），会尝试进行垃圾回收，然后再进行内存的分配

### 源码结构

- space.h/.cc
- region_space.h/.cc
- malloc_space.h/.cc
- rosalloc_space.h/.cc
- dlmalloc_space.h/.cc
- zygote_space.h/.cc
- image.space.h/.cc
- bump_pointer_space.h/.cc
- large_object_space.h/.cc

### Image Space

包括 boot.oat 和 boot.art ，这些image以向量的形式存储在 boot_image_spaces_ 中。

Android Framework中通用的类都都是存储在这里的。

### Zygote Space

即 zygote_space_ 对象，此空间的内存是不会进行回收的。

包含Zygote进程启动过程中创建的所有对象。这些对象是所有进程共享的。

### Large Object Space

即 large_object_space_ 对象，用于加载大对象。简称 LOS，GC的日志中的 LOS 即是大对象。
