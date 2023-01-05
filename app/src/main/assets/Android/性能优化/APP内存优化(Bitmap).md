
```
ActivityManager.getMemoryClass()：     虚拟机java堆大小的上限，分配对象时突破这个大小就会OOM
ActivityManager.getLargeMemoryClass()：manifest中设置largeheap=true时虚拟机java堆的上限
Runtime.getRuntime().maxMemory() ：    当前虚拟机实例的内存使用上限，为上述两者之一
Runtime.getRuntime().totalMemory() ：  当前已经申请的内存，包括已经使用的和还没有使用的
Runtime.getRuntime().freeMemory() ：   上一条中已经申请但是尚未使用的那部分。那么已经申请并且正在使用的部分used=totalMemory() - freeMemory()
ActivityManager.MemoryInfo.totalMem:   设备总内存
ActivityManager.MemoryInfo.availMem:   设备当前可用内存
/proc/meminfo                                           记录设备的内存信息
```

- bitmap内存如何计算
- 如何在不改变图片质量的情况下优化？
- Bitmap内存复用（Options.inBitmap）
- 超大图加载（BitmapRegionDecoder）
- 跨进程传递大图（Bundle#putBinder）
- xhdpi的图片分别显示到hdpi和xxhdpi的手机上，显示的大小和内存是怎样的？
- 资源文件加载规则。比如说图片存放在drawable-hdpi和drawable-xxhdpi下，xhdpi的手机会加载哪张？如果删除掉drawable-xxhdpi下的图片呢？