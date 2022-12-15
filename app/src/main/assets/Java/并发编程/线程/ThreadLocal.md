
线程局部存储（thread-local storage, TLS）是绝大多数编程语言都会提供的一个功能，用于保存和当前线程相关的数据

保证线程的唯一来源

在 Java 中，线程局部存储是 ThreadLocal

## 使用方法

声明 ThreadLocal 对象以后，在任意线程调用，保存的值只会在调用线程生效，举个例子：

在 thread A 调用 set() 存入了 "111" 以后，不管其他线程对这个 ThreadLocal 对象做了什么

当 thread A 调用 get() 方法获取存入的值时，依旧是 "111"

```
void main(){
    val threadLocal = ThreadLocal<String>();
    thread A { 
        threadLocal.set("111"); 
        print(threadLocal.get()); 
    }
    thread B { 
        threadLocal.set("222"); 
        print(threadLocal.get()); 
    }
}
```

打印结果

```
thread A 111
thread B 222
```

在不同的线程能够存储不同的数据，让数据有了线程的隔离性

## ThreadLocal 原理

大致的实现原理是，Thread 类中有 ThreadLocalMap 变量，用于保存自己线程的数据

```
class Thread {
    ThreadLocal.ThreadLocalMap threadLocals = null;
}
```

ThreadLocalMap 内部是用 Entry 类型的环形数组结构，来保存数据

Entry 类是以 WeakReference 的 ThreadLocal 作为 key， Object 类型作为 value

```
class ThreadLocalMap {

    private Entry[] table;
    
    class Entry extends WeakReference<ThreadLocal<?>> {
        WeakReference<ThreadLocal> key;
        Object value;
    }
}
```

ThreadLocalMap 的查找、扩容、擦除等算法的实现过程暂时忽略，了解大致流程和原理即可

小结：

- Thread 持有 ThreadLocalMap 变量
- ThreadLocalMap 是以 ThreadLocal 作为 key，Object 作为 value 的 map（或数组）
- 使用时，ThreadLocal 通过 Thread#currentThread() 拿到当前线程，继而拿到 Thread 持有 ThreadLocalMap 对象
- ThreadLocal 拿到 ThreadLocalMap 对象后，把自己作为 key ，调用 set() / get() 方法保存、获取 Object

## 注意事项

- 如果我们没有 set() 直接调用 get()，默认值为 initialValue() 方法返回的 null
- ThreadLocal 可以理解为是线程安全的，因为每个线程其实都是在操作自己内部的 map 变量，该变量对其他线程不可见
- 使用完 ThreadLocal 后，记得调用 remove() 方法清除数据，因为 value 被 map 强引用，只要当前线程不结束，value 就不会被释放
