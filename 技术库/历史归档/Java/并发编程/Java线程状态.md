
# Java线程与操作系统线程的关系

在 Java1.2 之后. Linux 中的 JVM 是基于 pthread 实现的

即，现在的 Java 中线程的本质，其实就是操作系统中的线程

## Java 线程状态

```
//src/java.base/share/classes/java/lang/Thread.java
public enum State {
        NEW,
        RUNNABLE,
        BLOCKED,
        WAITING,
        TIMED_WAITING,
        TERMINATED;
}
```

- **new**: 线程还未 started 时的状态 即 new Thread().getState()
- **runnable**: 线程 start() 后的状态， 可在 run() 方法中 Thread.currentThread().getState() 获得
- **blocked**：等待锁的时的状态，在进入 synchronized 块
- **waiting**：表示正在等待另一个线程执行特定操作，当调用了 Object#wait()，Thread#join()， LockSupport#park() 进入
- **timed_waiting**: 具有指定等待时间的等待线程的线程状态,当调用 Thread#sleep(long)，Object#wait(long)，Thread.join(long)，LockSupport#parkNano(long) ,LockSupport#parkUntil(long)时进入
- **terminated**: 线程结束

## Linux 线程状态

Linux 操作系统中的线程，除去 **new** 和 **terminated** 状态，一个线程真实存在的状态，只有：

- **ready** ：表示线程已经被创建，正在等待系统调度分配 CPU 使用权。
- **running**：表示线程获得了 CPU 使用权，正在进行运算
- **waiting**：表示线程等待（或者说挂起），让出 CPU 资源给其他线程使用

**为什么没有 new 和 terminated 状态呢？**

是因为这两种状态实际上并不存在于线程运行中，所以也没什么讨论的价值。

## 两者对应关系

对于 Java 中的线程状态：

无论是 **timed_waiting** ，**waiting** 还是 **blocked**，对应的都是操作系统线程的 **waiting**（等待）状态。

而 **runnable** 状态，则对应了操作系统中的 **ready** 和 **running** 状态

## 参考资料

- [Java线程和操作系统线程的关系](https://zhuanlan.zhihu.com/p/133275094)
