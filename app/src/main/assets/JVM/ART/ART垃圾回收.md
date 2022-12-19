
## GC 触发时机

- 应用需要分配内存时，如果堆空间不能提供一个足够大的空间时

## GC 源码结构

- accounting （一些用于辅助记录对象修改情况的数据结构）
  - bitmap.h/.cc
  - card_table.h/.cc
  - heap_bitmap.h/heap_bitmap.cc
  - mod_union_table.h/.cc
  - remembered_set.h/.cc
  - space_bitmap.h/.cc
- allocator (负责具体内存分配的实现，目前只包含了 dlmalloc和 rosalloc两种实现)
  - dlmalloc.h/.cc
  - rosalloc.h/.cc
- collector (垃圾回收器的实现)
  - gc_type.h
  - imumune_region.h/.cc
  - imumune_space.h/.cc
  - semi_space.h/.cc
  - garbage_collector.h/.cc
  - concurrent_coping.h/.cc
  - mark_sweep.h/.cc
  - partial_mark_sweep.h/.cc
  - sticky_mark_sweep.h/.cc
- space （heap进行内存分配时实际的内存资源来源）
  - space.h/.cc
  - region_space.h/.cc
  - malloc_space.h/.cc
  - rosalloc_space.h/.cc
  - dlmalloc_space.h/.cc
  - zygote_space.h/.cc
  - image.space.h/.cc
  - bump_pointer_space.h/.cc
  - large_object_space.h/.cc
- heap.h/.cc
- allocation_record.h/.cc
- reference_queue.h/.cc
- reference_processor.h/.cc
