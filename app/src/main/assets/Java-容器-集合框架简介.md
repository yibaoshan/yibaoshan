### Overview
1. 集合框架简介
2. 集合工具类Arrays&Collections

### 一、Java集合框架
Java集合框架大致可以分为4类：List、Set、Queue、Map
其中，前三个实现的是Collection接口，最后一个实现的是Map接口，它们的顶级上司是Iterator迭代器，详细的结构图在**附录**

### 二、集合工具类Arrays&Collections
#### 1、Arrays
1. int binarySearch()：使用二分查找在排好序的数组中查找指定Key，找不到-1
2. object[] copyOf()/copyOfRange()：复制数组
3. boolean equals()：比较数组内容
4. void fill()：填充
5. int mismatch()：返回第一个元素不匹配下标，没有-1
6. String toString()：打印数组用
7. object[] sort()：升序排序

#### 2、Collections
##### 2.1、排序
1. void reverse(List<?> list)：翻转元素
2. void shuffle(List<?> list)：随机排序(洗牌)
3. void sort(List list)：升序排序
4. void sort(List list, Comparator c)：自定义比较器进行排序
5. void swap(List list, int i, int j)：将指定List集合中i处元素和j出元素进行交换
6. void rotate(List list, int distance)：将所有元素向右移位指定长度，如果distance等于size那么结果不变

##### 2.2、查找和替换
1. binarySearch(List list, Object key)：使用二分搜索法，以获得指定对象在List中的索引，前提是集合已经排序
2. max(Collection coll)：返回最大元素
3. max(Collection coll, Comparator comp)：根据自定义比较器，返回最大元素
4. min(Collection coll)：返回最小元素
5. min(Collection coll, Comparator comp)：根据自定义比较器，返回最小元素
6. fill(List list, Object obj)：使用指定对象填充
7. frequency(Collection Object o)：返回指定集合中指定对象出现的次数
8. replaceAll(List list, Object old, Object new)：替换

##### 2.3、同步控制：对线程不安全的集合所有操作方法加锁
1. Collections.synchronizedCollection()
2. Collections.synchronizedList()
3. Collections.synchronizedSet()
4. Collections.synchronizedMap()

##### 2.4、设置不可变集合
1. emptyXxx()：返回一个空的不可变的集合对象
2. singletonXxx()：返回一个只包含指定对象的，不可变的集合对象。
3. unmodifiableXxx()：返回指定集合对象的不可变视图

### 附录

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
