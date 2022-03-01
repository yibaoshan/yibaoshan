### Overview
1. Map接口简介
2. HashMap
3. LinkedHashMap
4. Hashtable
4. TreeMap

### 一、Map接口简介

### 二、HashMap
JDK1.8 之前 HashMap 由数组+链表组成的，数组是 HashMap 的主体，链表则是主要为了解决哈希冲突而存在的（“拉链法”解决冲突）。
JDK1.8 以后在解决哈希冲突时有了较大的变化，当链表长度大于阈值（默认为 8）（将链表转换成红黑树前会判断，如果当前数组的长度小于 64，那么会选择先进行数组扩容，而不是转换为红黑树）时，将链表转化为红黑树，以减少搜索时间

### 三、LinkedHashMap
LinkedHashMap 继承自 HashMap，所以它的底层仍然是基于拉链式散列结构即由数组和链表或红黑树组成。
另外，LinkedHashMap 在上面结构的基础上，增加了一条双向链表，使得上面的结构可以保持键值对的插入顺序

### 四、Hashtable
数组+链表组成的，数组是 Hashtable 的主体，链表则是主要为了解决哈希冲突而存在的

### 五、TreeMap
红黑树（自平衡的排序二叉树）