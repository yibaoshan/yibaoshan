### Overview
1. List接口简介
2. 源码解析之ArrayList
3. 源码解析之LinkedList
4. 源码解析之Vector
5. 源码解析之Stack

### 一、list接口简介

### 二、ArrayList
#### 1、底层数据结构
ArrayList底层是Object数组：transient Object[] elementData

#### 2、默认容量
默认容量10，Java1.8之后创建时长度为0，初始化操作延迟到添加首个元素
1.8之前默认创建大小为10的数组

#### 3、扩容机制
每次调用add方法时会检查是否超出，放不下会调用公开方法ensureCapacity来扩容1.5倍。
数组进行扩容时，会将老数组中的元素重新拷贝一份到新的数组中

#### 4、其他方法
1. trimToSize()：容量调整为当前实际元素的大小

#### 5、Fail-Fast机制
ArrayList不允许遍历时动态更改元素数量

### 三、LinkedList
#### 1、底层实现
LinkedList内部使用**双向链表**实现，同时它还实现了List接口和Deque接口，所以可以当做List集合、queue队列和deque双端队列使用

### 四、Vector
#### 1、底层实现
数组实现，可以参考ArrayList，增删改查都加锁而已

### 五、Stack
#### 1、底层实现
继承自Vector，所以操作元素的方法都加锁，想要更快的可以自己实现