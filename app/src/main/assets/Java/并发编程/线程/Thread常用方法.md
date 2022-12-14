
## 创建与运行

Java 线程使用方式

1. 实现 Runnable 接口的 run 方法
2. 继承 Thread 类并重写 run 方法

FutureTask 没用过，忽略

## Object 中的并发方法

### **Object#wait()**

一个线程调用一个共享变量的 **wait()** 方法后，此时调用线程从 **runnable** 转入等待状态

- 在 jvm 中是 **waiting** 或者 **timed_waiting** 状态
- 在 Linux 中也是 **waiting** 状态，不参与 Linux 实际调度执行

jvm 中，直到发生以下动作才会返回：

1. 有其他线程调用了该 "**共享对象**" 的 **notify()** 或者 **notifyAll()** 方法
2. 其他线程调用了该线程的 **interrupt()** 方法，线程抛出 **InterruptedException** 异常返回

### **Object#/notify()**

一个线程调用一个共享变量的 **notify()** / **notifyAll()** 方法后，由 jvm 随机唤醒一个线程或、唤醒所有线程，怎么实现的先不管

## 常见的静态方法

### **Thread#sleep()**

静态方法，从 **runnable** 转入 **timed_waiting** 状态，Linux 为 **waiting** ，不参与实际调度

- 在 jvm 设计中，**sleep()** 方法依旧持有锁（如果有的话）
- 必须传入睡眠时间
- 睡醒后状态从 **timed_waiting** 转到 **runnable**，参与 Linux 调度
- 如果睡眠期间 **interrupt()** 被调用，在调用 **sleep()** 方法处抛出 **InterruptedException** 异常

### **Thread#yield()**

静态方法，当前线程想要放弃执行，让出 CPU 使用权，没用过，不管

## 常见的成员方法

### **Thread#join()**

等待线程执行终止的方法，调用线程将会进入阻塞状态，直到目标线程执行结束

```
void main(){
    val thread = Thread{ sleep(100); print("1")}.start();
    thread.join();
    print("2")
}
```

打印结果

```
1
2
```

## 中断线程的方式

### **Thread#stop()**

Thread#stop() 方法在 java 8 已被弃用，调用会抛出 UnsupportedOperationException 异常

### **Thread.interrupt()**

Thread 里面有三个中断线程的方法，命名比较相似，很容易让人混乱懵逼。。

```
public void interrupt() // 无返回值
public boolean isInterrupted() // 有返回值
public static boolean interrupted() // 静态，有返回值
```

一个个来介绍

- **interrupt()**
  - 设置当前线程中断状态为 true
  - 处于 **wait/join/sleep** 时，线程抛出 InterruptedException 异常，并清除中断状态（置为false）
- **isInterrupted()**
  - 返回中断状态，并清除中断状态（置为false），也就是说，连续调用两次，第二次结果必为 false
- **static interrupted()**
  - 返回中断状态

## 参考资料

- [如何停止一个正在运行的java线程](http://ibruce.info/2013/12/19/how-to-stop-a-java-thread/)