####Java集合框架

1. java.util.Collection [I]
   - java.util.List [I]：有序集合
     - java.util.ArrayList [C]：数组结构，默认容量10，每次1.5倍扩容
     - java.util.LinkedList [C]：双向链表结构
     - java.util.Vector [C]：同ArrayList，每次2倍扩容，可设置单次扩容大小，增删改加锁
       - java.util.Stack [C]
   - java.util.Set [I]：无序集合
     - java.util.HashSet [C]：内部维护一个HashMap实现，每次add将value存入map的key，value用present替代
       - java.util.LinkedHashSet[C]：内部维护LinkedHashMap
     - java.util.SortedSet [I]
       - java.util.TreeSet [C]：TreeMap实现
   - java.util.Queue[I]：有序队列
     - java.util.LinkedList [C]
2. java.util.Map [I]：KV键值对
   - java.util.SortedMap [I]
     - java.util.TreeMap [C]：红黑树实现，按key排序
   - java.util.Hashtable [C]：线程安全，不允许null
   - java.util.HashMap [C]
     - java.util.LinkedHashMap [C]：
   - java.util.WeakHashMap [C]

tips：

1. queue常用方法区别
   1. add()和offer()和put（）添加元素，队列如果有大小限制，add()会抛异常，offer()返回false，put则阻塞
   2. poll()和remove()和take()删除元素并返回头部元素，队列为空时remove()抛异常，poll返回null，take阻塞
   3. element()和peek()，队列为空element抛异常，peek返回null

####HashMap详解

1. hashmap数组部分被称为哈希桶
2. 数组长度大于64并且链表长度大于8则会将链表转为红黑树，链表长度小于6会转为链表

####工具类

- Arrays
- Collections

####问题分析

- HashMap和HashTable的区别？
  1. 父类不同，hashmap的父类是AbstractMap，hashtable父类是dictionary
  2. hashtable方法加锁
  3. hashtable的put方法判断value为空时抛空异常，如果key也为空，调用key的hashcode时jvm也会报空异常。hashmap调用putVal时会调用hash方法，如果key为空的话返回0
  4. table初始容量11，map为16，负载因子都是0.75，而且hashtable在构造方法就进行初始化数组，map是在resize方法中，也就是put的时候