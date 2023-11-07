
Java 集合框架大致可以分为4类：

**List**、**Set**、**Queue**、**Map**

其中，前三个实现的是 **Collection** 接口，用于存放单一元素

最后一个实现的是 **Map** 接口，用于存放 "**键值对**"

它们的顶级上司都是 **Iterator** 迭代器

## Iterator 迭代器

**fail-fast** 快速失败机制：

在遍历过程中改动元素数量就会抛异常

实现逻辑是在创建迭代器时记录下 **modCount**，每次取元素时校验两个值是否相同。

调用 Iterator.remove() 能够安全删除，是因为 Iterator 调用了原集合的删除方法后，将 **modCount** 值同步减去1

**fail-safe**：

拷贝一份原数据进行遍历，缺点是不保证数据一致性

## 集合框架结构

### **java.util.Collection [I]**

- **java.util.List [I]**：有序集合
  - java.util.ArrayList [C]：数组结构，默认容量10，每次1.5倍扩容
  - java.util.LinkedList [C]：双向链表结构
  - java.util.Vector [C]：同ArrayList，每次2倍扩容，可设置单次扩容大小，增删改加锁
    - java.util.Stack [C]
- **java.util.Set [I]**：无序集合
  - java.util.HashSet [C]：内部维护一个HashMap实现，每次add将value存入map的key，value用present替代
    - java.util.LinkedHashSet[C]：内部维护LinkedHashMap
  - java.util.SortedSet [I]
    - java.util.TreeSet [C]：TreeMap实现
- **java.util.Queue[I]**：有序队列
  - java.util.LinkedList [C]

### **java.util.Map [I]：KV键值对**

- java.util.SortedMap [I]
  - java.util.TreeMap [C]：红黑树实现，按key排序
- java.util.Hashtable [C]：线程安全，不允许null
- java.util.HashMap [C]
  - java.util.LinkedHashMap [C]：
- java.util.WeakHashMap [C]
