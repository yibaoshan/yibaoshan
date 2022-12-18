
- 原子更新基本类型
  - AtomicBoolean
  - AtomicInteger
  - AtomicLong
- 原子更新数组
  - AtomicIntegerArray
  - AtomicLongArray
  - AtomicReferenceArray
- 原子更新引用
  - AtomicReference
  - AtomicStampedReference：原子更新带有版本号的引用类型
  - AtomicMarkableReference：原子更新带有标记的引用类型
- 原子更新属性
  - AtomicIntegerFieldUpdater

## AtomicReference 用法

```
AtomicReference<Book> atomicReference = new AtomicReference<>();
Book book1 = new Book("三国演义", 42);
atomicReference.set(book1);
Book book2 = new Book("水浒传", 40);
atomicReference.compareAndSet(book1, book2);
System.out.println("Book name is " + atomicReference.get().name + ",价格是" + atomicReference.get().price);           
```        

## CAS 原理

CAS 就是比较和交换的原子操作，是 CPU 提供的指令，jvm 只不过是对这个指令的一种封装

例如 Unsafe.java中 提供的 compareAndSwapInt() 方法，它在 hotspot 中的实现是这样的

```
Unsafe_CompareAndSwapInt(JNIEnv *env, jobject unsafe, jobject obj, jlong offset, jint e, jint x)
  UnsafeWrapper("Unsafe_CompareAndSwapInt");
  oop p = JNIHandles::resolve(obj);
  jint* addr = (jint *) index_oop_from_field_offset_long(p, offset);
  return (jint)(Atomic::cmpxchg(x, addr, e)) == e;
```

这段代码中所使用的 Atomic::cmpxchg() 方法，在不同的硬件平台和操作系统上都有不同的实现，以 x86，linux 为例，它的实现是这样的：

```
Atomic::cmpxchg(jlong exchange_value, volatile jlong* dest, jlong compare_value) {
bool mp = os::is_MP();
__asm__ __volatile__ (LOCK_IF_MP(%4) "cmpxchgq %1,(%3)"
: "=a" (exchange_value)
: "r" (exchange_value), "a" (compare_value), "r" (dest), "r" (mp)
: "cc", "memory");
return exchange_value;
}
```

看，这是一段 C 的内嵌汇编。

也就是说，对 cmpxchg 的调用最终会翻译成 "**cmpxchgq**" 指令。

内嵌汇编的第一部分是指令，这个不用解释

第二部分是输出参数，"=a"(exchange_value)，就是把 RAX 寄存器中的值赋给 exchange_value 变量；

第三部分是输入参数，表示 compare_value 放到 RAX 寄存器

其他三个值让编译器给一个寄存器就好，不强制指定（"r"就是这个意思），并且%0代表"=a"(exchange_value)，%1代表"r"(exchange_value)，依次类推；第四部分是指示编译器，这一段代码可能影响到哪些内容，这个不具体展开，能看懂这一段就行了。

关于 cas 的介绍废话比较多

从内存角度出发

CAS 的函数公式：compareAndSwap(V,E,N)； 其中：

- V：表示要更新的变量
- E：表示预期值
- N：表示期望更新的值

调用 compareAndSwap 函数来更新变量 V

如果 V 的值等于期望值 E，那么将其更新为 N

如果 V 的值不等于期望值E，则说明有其它线程跟新了这个变量，此时不会执行更新操作

而是重新读取该变量的值再次尝试调用 compareAndSwap 来更新。

