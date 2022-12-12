
# 我所理解的缓存一致性和内存屏障

## 理解并发

当有多个线程在操作时，如果系统只有一个 CPU，则根本不可能真正同时进行一个以上的线程

CPU 只能分成若干个时间段，再将时间段分配给各个线程执行

在一个时间段的线程代码运行时，其它线程处于挂起状态。

这种方式我们称之为 "**并发(Concurrent)**"

## 理解并行

当系统有一个以上 CPU 时，则线程的操作有可能非并发。

当一个 CPU 执行一个线程时，另一个 CPU 可以执行另一个线程，两个线程互不抢占 CPU 资源，可以同时进行

这种方式我们称之为 "**并行(Parallel)**"

ps：多核 CPU 和多 CPU 架构，两者的主要区别在于

"**多核 CPU**" 只有一套 "**内存管理单元** 和 "**高速缓存**"

而多 CPU ，每块 CPU 都配有单独专属的 "**MMU**" 和 "**Cache**"

## 多核 CPU 引发的 "缓存一致性"

Intel 的 "**MESI**" 缓存写回无效协议，为高速缓存中的每个存储单元行 "**cache line**" 赋予了一个状态属性

状态类型共有 4 种：

- **Modified**：缓存数据有效，读入缓存后曾经被自己修改过却没有写回主存，而且其它核的高速缓存中并没有缓存这一内存数据。因此，数据是有效的
- **Exclusive**：缓存数据有效，自己读取后也没有修改过；并且，其他核也没有这个数据
- **Shared**：缓存数据有效，自己读取后没修改过，与内存中的对应数据保持一致。其他核也读了一份，但是没有修改，因此数据是有效的
- **Invalid**：缓存数据无效

我们通过几个例子来看状态的流转：**主存 x = 0，CPU A 、B 对应缓存，cache a、cache b**

*注意1：CPU 每个 core 持有自己私有的 L1/L2 缓存，L3 所有 core 共享（骁龙 Kryo 为首的 ARM，和酷睿为首的 X86 都是如此）。L3 缓存和主存的读写操作由 bus 总线负责，所谓的主存是 L3 或者内存*

*注意2：在 X86 中，各个 CPU 通过 环形总线（Ring Bus）来传输数据/通信，利用 ring bus ，每个 core 都有一个 cbox(bus agent)负责监听其它核对 cache 的操作（remote 远程读写指令）行为, 从而根据协议采取对应的行动*

### 读数据

```
 CPU A 通过 bus 读取 x 到 cache a，此时：
 - cache a 模式为 Exclusive（独享）
 
 CPU B 通过 bus总线 读取变量 x 到 cache b，CPU A 监听到请求，直接拷贝一份到 CPU B，此时：
 - cache a 模式为 Shared（共享）
 - cache b 模式也是 Shared（共享）
```

### 写数据

```
 CPU A 修改 x 后同步到主存，将 cache a 模式改为 Modified
 CPU B 监听到其他核发生了修改，将本地的 cache b 设置无效，此时：
 - cache a 模式为 Modified
 - cache b 模式为 Invalid (无效)
```

### 同步数据

```
 CPU B 读取本地 cache b，但数据行状态为 "失效"
 总线先将 CPU A 修改后的数据同步到主存，此时：
 - cache a 模式为 Exclusive（独享）
 
 然后，将数据拷贝给 CPU B，此时：
 - cache a 模式为 Shared（共享）
 - cache b 模式也是 Shared（共享）
```

总结，"**MESI**" 协议是由 CPU 硬件实现，作用是保证 "**CPU 高速缓存**" 和 "**主存**" 之间缓存数据的一致性

## MESI 性能优化

读写 "**cache line**" 是同步操作，这也就意味着，多个核操作同一个 "**cache line**" 时，有且只有一个核能成功，其他核只能等待，这就造成了资源浪费

为了提升了整体的性能，增加了 "**store buffer**" 和 "**失效队列**" 来进行优化

未完待续。。

## 内存屏障

"**store buffer**" 和 "**失效队列**"  导原来的数据强一致性变成了弱一致性

为此，CPU 提供了 "内存屏障" 指令，解决 MESI优化 带来的某些场景数据不一致的场景

未完待续。。

## 参考资料

- [并发研究之CPU缓存一致性协议(MESI) - Alance ](https://www.cnblogs.com/yanlong300/p/8986041.html)
- [浅论Lock 与X86 Cache 一致性- wiles super ](https://zhuanlan.zhihu.com/p/24146167)
- [高速缓存一致性协议MESI与内存屏障 - 小熊餐馆 ](https://www.cnblogs.com/xiaoxiongcanguan/p/13184801.html)