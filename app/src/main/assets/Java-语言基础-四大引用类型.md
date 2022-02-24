## Overview
1. 四大引用
2. Reference类

## 一、四大引用类型(java 1.2)
### 1、强引用(Strong Reference)
Object obj = new Object();obj就是new Object()的强引用

### 2、软引用(Soft Reference)
内存充足不回收

### 3、弱引用(weak Reference)
只有弱引用的情况下，有gc就回收

### 4、虚引用(PhantomReference)
和软引用和弱引用相比较：
1. 构造函数必须传入ReferenceQueue
2. phantomReference.get()获取不到引用对象，永远返回null
3. 回收完成后ReferenceQueue.poll()可以获取到地址

### 5、FinalizerReference

## 二、Reference类
### 1、常用方法：
1. get()->native getReferent();
2. clear()->native clearReferent();

### 2、重要属性：
1. volatile T referent;//自身引用对象
2. final ReferenceQueue<? super T> queue;//传进来用来观察的队列，引用对象被回收以后会被添加到此队列
3. Reference queueNext;//
4. Reference<?> pendingNext;//

### 3、Reference的4种状态：
1. Active：创建的时候默认激活状态
2. Pending：此对象可被回收，referent置空，reference加入clear队列
3. Enqueued：referent置空，用户观察队列不为空时
4. Inactive：观察队列为空或从观察队列弹出

## 引用
1. https://dim.red/2018/11/18/android_reference_exploration/
2. https://www.cnblogs.com/flydean/p/java-reference-referencetype.html

