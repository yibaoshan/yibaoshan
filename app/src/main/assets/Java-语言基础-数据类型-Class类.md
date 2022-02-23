## Overview
1. 常用方法
2. 拆箱和装箱

## 一、常用方法
### 1、比较相关
1. equals
2. native hashCode()
返回**对象实例**的哈希码，主要用在哈希表中，比如JDK中的HashMap
通过对象的内部地址转换成一个整数来实现哈希码，通常不同的对象产生的哈希码是不同的
注意，equals相同hashCode必须相同，反之不需要

### 2、并发相关
1. notify()
2. notifyAll()
3. wait() throws InterruptedException
4. wait(long timeout) throws InterruptedException
5. wait(long timeout, int nanos) throws InterruptedException

### 3、其他
1. final native getClass()
不允许重写，**运行时**获取对象实例类型
2. clone() throws CloneNotSupportedException
3. toString()
4. finalize() throws Throwable