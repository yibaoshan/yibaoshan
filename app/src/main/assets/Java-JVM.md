对象创建

对象头

Java和kotlin编译流程

反射原理

方法签名

内存分配

#### 内存区域划分

1. 线程共享：OutOfMemeoryError区域
   1. 堆内存Heap
   2. 方法区Method Area
2. 线程私有：StackOutflowError
   1. 虚拟机栈VM Stack
   2. 本地方法栈Native Method Stack
   3. 程序计数器：CPU时间片轮转机制，记录方法执行位置

tips：

1. 在Android中，虚拟机栈和本地方法栈都是同一块内存
2. 一个method方法对应一个栈帧

### 类加载机制

##### 双亲委托机制

在Android中，pathClassLoader用于系统默认加载，自定义使用dexClassLoader

##### 破坏双亲委托

自定义classLoader复写loadClass

####Java虚拟机指令

1. invokevirtual——对实例方法的标准分派
2. invokestatic——用于分派静态方法
3. invokeinterface——用于通过接口进行方法调用的分派
4. invokespecial——当需要进行非虚（也就是“精确”）分派时会用到
5. invokedynamic——jdk1.7加入

#### 类的生命周期

1. 加载
2. 连接：验证->准备->解析
3. 初始化
4. 使用
5. 卸载

#### 垃圾回收算法

1. 标记-清除

   原理：共两个阶段，标记，清除

   优缺点：清理完成产生大量的不连续空间，俗称内存碎片

2. 复制

   原理：保留一半空间，回收时将标记的存活对象复制到空白区域

   优缺点：可用内存减少一半

回收器

#### 判断对象可回收

1. 引用计数：无法解决互相引用的问题
2. 可达性分析

####GC Roots

1. System或Boot ClassLoader加载的class对象
2. 方法区常量引用的对象

#### 问题分析

1. 传入native的对象是如何判断对象状态的？

   答：传入JNI函数的对象都属于局部引用，一旦从C返回至Java方法中，局部引用将会失效

   想要局部引用在返回后不被回收，可以通过JNI函数NewGlobalRef转为全局引用，当然也可以通过DeleteGlobalRef消除全局引用