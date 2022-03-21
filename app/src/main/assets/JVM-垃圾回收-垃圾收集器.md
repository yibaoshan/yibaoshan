### Overview
1. Dalvik的垃圾回收器
2. Art的垃圾回收器
3. 总结

### 一、Dalvik的垃圾回收

Dalvik采用标记-清除算法，在标记的过程中，会有3次停顿：
1. 在标记开始前遍历堆地址空间的停顿
2. 一次是在标记的第一个阶段标记所有根集对象时的停顿
3. 还有一次是在标记的第二个子阶段完成后处理card table时的停顿

参考：https://cruise1008.github.io/2016/03/30/Android-GC-%E4%BB%8Edalvik%E5%88%B0ART%E7%9A%84%E6%94%B9%E8%BF%9B%E5%88%86%E6%9E%90/

### 二、Art的垃圾回收器

根据轻重程度不同，分为三类，sticky，partial，full，也称为CMS(Concurrent Mark Sweep)
而根据GC时是否暂停所有的线程分类并行和非并行两类

综上，在ART中一共定义了6个mark-sweep收集器，其中最复杂的就是Concurrent Mark Sweep 收集器
如果理解了最复杂的Concurrent Mark Sweep算法，其他5种GC收集器的工作原理就也理解了

1. mark标记阶段

最重要的提升就是这个阶段只暂停线程一次。将dalvik的三次缩短到一次，得到了较大的优化。和dalvik一样，标记阶段完成的工作也是完成从根集对象出发，进行递归遍历标记被引用的对象的整个过程。用到的主要的数据结构也是同样的live bitmap和mark bitmap， 以及card table和在递归遍历标记时用到的mark stack。
一个被引用的对象在标记的过程中先被标记，然后存入mark stack中，等待该对象的父对象全部被标记完成，再依次弹出栈中每一个对象然后，标记这个对象的引用，再把引用存入mark stack，重复这个过程直至整个栈为空。这个过程对mark stack的操作使用以及递归的方法和dalvik的递归过程是一样的。但是在dalvik小节里提到了，在标记时mark阶段划分成了两个阶段，第一小阶段是禁止其他线程执行的，在mark两个阶段完成后处理card table时也是禁止其他线程执行的。但是在ART里做出了改变，即先Concurrent标记两遍，也就是说两个子阶段都可以允许其他线程运行了。然后再Non-Concurrent标记一遍。这样就大大缩短了dalvik里的第二次停顿的带来的卡顿时间。这个改进非常重要。
在对mark stack使用时，在初始阶段，为后面的mark准备好markstack。
但是值得一提的是，在标记开始阶段，有别于dalvik的要暂停所有线程进行堆地址空间的遍历，ART去掉了这个过程，替代的是增加了一个叫作allocation stack结构，所有新分配的对象会记录到allocation stack，然后在Mark的时候，再在Live Bitmap中打上live的标记。Allocation stack和live stack其实是一个工作栈和备份栈。当在GC的时候，需要处理allocation stack，那么会把两个stack互换。新分配的对象会压到备份栈中，这个时候备份栈就当作新的工作栈。这样一来，dalvik在GC时产生的第一次停顿就完全消除了，从而产生了巨大的性能提升。

### 总结

首先通过对dalvik的GC的过程的分析，我们可以发现dalvik的在GC时出现的几个主要问题，首先即在GC时会有三次暂停其他进程运行，三次停顿导致的总的时间太长会导致丢帧卡顿现象严重。其次，就是在堆空间中给较大的对象分配空间后会导致碎片化比较严重，并且可能会导致GC调用次数变多增加开销。
我们可以发现，针对dalvik的以上两个问题，ART都有做了对应的优化来解决这些问题。针对第一个问题，ART在标记阶段做了非常大的优化，消除了第一次遍历堆地址空间的停顿，和第二次标记根集对象的停顿，并缩短了第三次处理card table的停顿，因此大大的缩短了应用程序在执行时的卡顿时间。针对第二个问题，提出了LOS的管理方法。
除此以外，还提供了丰富的GC收集器，例如继承自mark sweep的sticky mark sweep和partial mark sweep，二者的回收力度都要比full mark sweep小，因此性能上也得到了一些提升。一般情况下的收集器的主力就是sticky mark sweep, 这是对应用程序的性能影响最小的一种方式，因此大多数情况的GC表现，都要比dalvik的GC表现更好。