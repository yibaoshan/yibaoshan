## Overview
1. 常用方法
2. 拆箱和装箱

## 一、常用方法
### 1、比较相关
1. equals
比较两个对象是否相等。Object类的默认实现，即比较2个对象的内存地址是否相等：
2. native hashCode()
返回**对象实例**的哈希码，主要用在哈希表中，比如JDK中的HashMap
通过对象的内部地址转换成一个整数来实现哈希码，通常不同的对象产生的哈希码是不同的
注意，equals相同hashCode必须相同，反之不需要

### 2、并发相关
1. notify()
随机唤醒一个已经调用wait进入等待的线程，只能在拥有这个实例的同步代码里面调用，否则会抛异常：IllegalMonitorStateException
2. notifyAll()
唤醒所有进入等待的线程，限制条件同上
3. wait() throws InterruptedException
在同步代码块内调用，释放锁资源，一直等到其他线程调用notify
4. wait(long timeout) throws InterruptedException
同上释放锁资源，到了设定时间(耗时统计不准确)自动去抢锁执行，若此时有个线程永久持有所资源，那么该同步代码块后续代码永远不会执行
5. wait(long timeout, int nanos) throws InterruptedException
同上,加入纳秒精度更高

### 3、其他
1. final native getClass()
不允许重写，**运行时**获取对象实例类型
2. clone() throws CloneNotSupportedException
创建并返回当前对象的一份拷贝，浅拷贝
3. toString()
输出类的名字@实例的哈希码的16进制
4. finalize() throws Throwable
用于GC，对象不可达时判断是否复写finalize()，否的话直接回收，是的话加入F-Queue队列后续由低优先级线程执行

### 引用
1. https://fangjian0423.github.io/2016/03/12/java-Object-method/