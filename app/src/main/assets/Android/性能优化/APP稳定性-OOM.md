
OutOfMemoryError 内存溢出是一类难以解决的 Crash

因为内存已经在其他位置增加到内存溢出的临界点，而上报的位置只是最后一次发起申请内存（最后一根稻草），很可能不存在内存问题

所以上报的堆栈信息，用户日志等都没有太多的参考价值

而引起内存溢出主要的原因有：

- 文件描述符(fd)数目超限，即proc/pid/fd下文件数目突破/proc/pid/limits中的限制。可能的发生场景有：
  - 短时间内大量请求导致socket的fd数激增，大量（重复）打开文件等
- 线程数超限，即proc/pid/status中记录的线程数（threads项）突破/proc/sys/kernel/threads-max中规定的最大线程数。可能的发生场景有：
- app内多线程使用不合理，如多个不共享线程池的 OKhttpclient 等等
- 传统的java堆内存超限，即申请堆内存大小超过了 Runtime.getRuntime().maxMemory()
  - 内存泄漏累积导致
  - 申请大对象
  - 无足够的连续内存空间
- 虚拟内存不足（低概率），常发生在 32 位系统，进程逻辑空间被占满导致 OOM

## 内存泄漏

主要是用 LeakCanary 进行检测，debug 下使用 StrictMode 来检查Activity的泄露、Closeable对象没有被关闭等问题。

通常 Activity 的泄漏会显得比较严重，因为它承载了整个页面的控件、数据等

常见引起Activity泄漏的原因

- 注册的监听器未及时反注册：EventBus，广播
- 网络请求，异步任务：由于早期网络部分封装的不好，网络请求没有与页面的生命周期绑定，在弱网或者用户关闭页面较快的情况下会出现内存泄漏。
- 匿名内部类实现Handler处理消息，可能导致隐式持有的Activity对象无法回收。
- Activity和Context对象被混淆和滥用，在许多只需要Application Context而不需要使用Activity对象的地方使用了Activity对象，比如注册各类Receiver、计算屏幕密度等等。
- View对象处理不当，使用Activity的LayoutInflater创建的View自身持有的Context对象其实就是Activity，这点经常被忽略，在自己实现View重用等场景下也会导致Activity泄漏。

## 查看内存配置

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

## Bitmap 优化

### NativeBitmap

无论是微信张绍文的《Android 开发高手课》，还是抖音、快手等大厂分享，在 OOM 方面关注的都是 Bitmap 的优化问题

我们自己的项目保存海报，邀请好友的场景比较多，因此也会经常遇到低版本手机因为 Bitmap 导致的 OOM 问题

常App中图片都是占用内存的大户，bitmap 的管理是内存治理中非常重要的环节。

对于这里的处理，我们首先是将图片加载统一使用 Glide，再将App中的原生加载图片替换成Glide。

另外，通过MAT工具，筛选、排序大对象，对头部大对象进行优化：

- 原本在同一个SharePreference的数据拆分到多个，使用完毕进行回收。
- 某个类中持有一个非常大的数据，而并不是经常用到，可以放入数据库或者文件中。

- bitmap内存如何计算
- 如何在不改变图片质量的情况下优化？
- Bitmap内存复用（Options.inBitmap）
- 超大图加载（BitmapRegionDecoder）
- 跨进程传递大图（Bundle#putBinder）
- xhdpi的图片分别显示到hdpi和xxhdpi的手机上，显示的大小和内存是怎样的？
- 资源文件加载规则。比如说图片存放在drawable-hdpi和drawable-xxhdpi下，xhdpi的手机会加载哪张？如果删除掉drawable-xxhdpi下的图片呢？

## 参考资料

- [抖音 Android 性能优化系列：Java OOM 优化之 NativeBitmap 方案](https://blog.csdn.net/ByteDanceTech/article/details/124487103)
- [关于虚拟机参数的调整 --- heapgrowthlimit/heapsize的配置](https://blog.csdn.net/yun_hen/article/details/120017180)
- [Android APP memory用量如何回收](https://blog.csdn.net/yun_hen/article/details/122555845)
