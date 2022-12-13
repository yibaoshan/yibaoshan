
前戏稍微有点长。

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

## 理解高速缓存

随着 CPU 的主频越来越高，其他外设的速度（*包括内存*）明显拖了计算机运行速度的后腿

为了解决 CPU 和 内存 的速度矛盾，硬件设计者在 CPU 和 内存 之间，加入了高速缓存机制

今年是 2022 年，以骁龙 Kryo（ARM） 系列为首的移动端处理器，和以酷睿（X86）为首的 PC 端处理器，基本上都设有三级缓存，其中：

CPU 每个 core 持有自己私有的 L1 / L2 缓存，而 L3 是所有 core 共享

## 缓存一致性和 MESI 协议

每个 CPU 都有自己的高速缓存（*cache*）以后，"**缓存一致性**" 的问题也相应而生：

**当多个核心同时操对同一个数据进行更新时，程序的执行结果可能不符合预期，这显然是无法容忍的**

Intel 的 "**MESI**" 缓存写回无效协议解决了这个问题

它为高速缓存中的每个存储单元行 "**cache line**" 赋予了一个状态属性

状态类型共有 4 种：

- **Modified**：缓存数据有效，读入缓存后曾经被自己修改过却没有写回主存，而且其它核的高速缓存中并没有缓存这一内存数据。因此，数据是有效的
- **Exclusive**：缓存数据有效，自己读取后也没有修改过；并且，其他核也没有这个数据
- **Shared**：缓存数据有效，自己读取后没修改过，与内存中的对应数据保持一致。其他核也读了一份，但是没有修改，因此数据是有效的
- **Invalid**：缓存数据无效

接下来我们通过一组读写 demo，来看这几个状态的流转顺序：**主存 x = 0，CPU A 、B 对应缓存，cache a、cache b**

*注意1：L3 缓存和主存的读写操作由 bus 总线负责，主存可能是 L3 也可能是内存*

*注意2：在 X86 中，各个 CPU 通过 环形总线（Ring Bus）来传输数据/通信，利用 ring bus ，每个 core 都有一个 cbox(bus agent)负责监听其它核对 cache 的操作（remote 远程读写指令）行为, 从而根据协议采取对应的行动*

### 读数据

```
 CPU A 通过 bus 读取 x 到 cache a，此时：
 - cache a 模式为 Exclusive（独享）
 
 CPU B 通过 bus总线 读取变量 x 到 cache b，CPU A 监听到请求，拷贝一份到 CPU B，此时：
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

总结，各个 CPU 通过 ring bus 监听其他核的 "远程读写操作"，根据其他核的动作来更改自己 cache 的状态，让 CPU 的设计符合 "**MESI**" 规则，保证各个 CPU 之间高速缓存数据的一致性

## MESI 性能优化

"**MESI**" 协议虽然解决了缓存数据的一致性，但同时也带来了性能问题，比如：

```
 CPU A 修改了 x，向总线发出同步更新指令
 
 由于此时 CPU B、C、D 都持有 x 的缓存行，并且监听到 CPU A 的远程更新指令
 
 于是，CPU A 等待 B、C、D 完成他们失效自己本地缓存行的操作以后，再向下执行
```

处于等待状态的 CPU A 是不必要的，我们只需要将增加 "**store buffer**" 队列，然后把 "远程同步" 交给队列去执行就行了

注意，StackOver Flow 有篇[帖子](https://stackoverflow.com/questions/11105827/what-is-a-store-buffer)对 "**store buffer**" 有不同的看法：

> ***store buffer 是将一堆小写入（ 8 字节写入）打包成一个较大的事务（64 字节缓存行），然后再将它们发送到主存，目的是节省总线带宽***

### 存储缓存(Store Buffer)和无效队列(Invalid Queue)

anyway， "**store buffer**" 加入后，数据更新不直接写回主存，而是先写到 "**store buffer**"，把同步操作改成了异步

相应的，其他核监听到某个 core 发生了修改，需要将自己的 cache 置为无效时，也不会立即中断操作，而是把失效记录在另一个叫做 "**invalid queue**" 的无效队列中

> 什么时候执行真正的写回，和执行真正的失效，目前查到的资料还无法下定论，就当是满了再执行吧。。

## 内存屏障指令

内存屏障指令(Memory Barrier) 是为了解决 "**store buffer**" 和 "**invalid queue**" 引发的数据强一致性变成了弱一致性的问题

什么意思呢？

```
 CPU A 修改了 x，向总线发出同步更新指令
 
 由于此时 CPU B、C、D 都持有 x 的缓存行，并且监听到 CPU A 的远程更新指令
 
 于是，CPU A 等待 B、C、D 完成他们失效自己本地缓存行的操作以后，再向下执行
```

假设 "**store buffer**"



"**store buffer**" 和 "**invalid queue**" 看的我头有点疼，wiki、



为此，CPU 又提供了 "内存屏障" 指令，解决 MESI优化 带来的某些场景数据不一致的场景

ARM 和 X86 指令集不同，所以硬件的汇编指令我们先不管。直接来看操作系统的实现

Linux 提供了 smp_rmb()，smp_wmb()，smp_mb() 三种内存屏障指令

- **smp_rmb()**	：**读屏障**，出现在 "**读屏障**" 之前的 **LOAD** 操作，比后面出现的要先执行完成
- **smp_wmb()**	：**写屏障**，出现在 "**写屏障**" 之前的 **STORE** 操作，比后面出现®的要先执行完成
- **smp_mb()**	：一般内存屏障保证所有出现在屏障之前的内存访问 (**LOAD** 和 **STORE**) 先于出现在屏障之后的内存访问执行。

## 参考资料

- [MESI protocol - Wikipedia](https://en.wikipedia.org/wiki/MESI_protocol)
- [并发研究之CPU缓存一致性协议(MESI) - Alance ](https://www.cnblogs.com/yanlong300/p/8986041.html)
- [浅论Lock 与X86 Cache 一致性- wiles super ](https://zhuanlan.zhihu.com/p/24146167)
- [高速缓存一致性协议MESI与内存屏障 - 小熊餐馆 ](https://www.cnblogs.com/xiaoxiongcanguan/p/13184801.html)