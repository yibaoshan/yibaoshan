
## 使用方法

- 修饰成员方法：锁对象为 this
- 修饰静态方法：锁对象为 class
- 修饰代码块：开发者自行指定锁对象

## Java 对象头

我们编写一个 Java 类，在编译后会生成 .class 文件

类加载器将 class 文件加载到 jvm 时，会生成一个 Klass(C++) 类型的对象，称为类描述元数据，存储在方法区中，即 jdk1.8之后的元数据区

当使用new创建对象时，就是根据类描述元数据 Klass 创建的对象oop，存储在堆中。

每个 java 对象都有相同的组成部分，称为 "**对象头**"

### 查看 Java 对象头

使用 jol-core ，我们在代码中计算 java 对象的大小，以及查看 java 对象内存布局

使用jol计算对象的大小（单位为字节）：

```
ClassLayout.parseInstance(obj).instanceSize() 
```

使用 jol 查看对象的内存布局：

```
ClassLayout.parseInstance(obj).toPrintable()
```

### 对象头的大小

现在几乎都是 64 位设备，所以我们也只讨论 64 位处理器的情况

在 jvm 中，可以使用

```
-XX:-UseCompressedOops
```

来选择是否开启 "指针压缩"，Android 的 art 不知道有没有这个选项

开启指针压缩以后，object header 为 **12** 个字节，也就是 12 * 8 = **96 bit**

java 8 以后默认是开启状态，关闭指针压缩后的大小占 **16** 个字节，16 * 8 = **128 bit**

### 对象头 MarkWord

在 Java 程序运行的过程中，每创建一个新的对象，JVM 就会相应地创建一个对应类型的 oop 对象，存储在堆中

对象头的前 64 位是 MarkWord，后 32 / 64位是类的元数据指针（取决于是否开启指针压缩）

```
enum {  
    locked_value             = 0, // 0 00 轻量级锁 java 1.6
    unlocked_value           = 1,// 0 01 无锁
    monitor_value            = 2,// 0 10 重量级锁
    marked_value             = 3,// 0 11 gc标志
    biased_lock_pattern      = 5 // 1 01 偏向锁 java 1.6
  };
```

使用 jol-core 工具，我们可以查看对象头的 MarkWord 信息，举两个例子

```
void main(){
    val obj;
    synchronized(obj){
        ...
    }
}
```

我们打印 obj 的内存布局结果如果是：

```
... 00010000（共计64位）
```

直接看倒数 3 位标识

倒数第三位为 "0"，说明不是偏向锁状态

倒数两位为"00"，因此，是轻量级锁状态，那么前面 62 位就是指向栈中锁记录的指针。

结果如果是：

```
... 00010010（共计64位）
```

倒数第三位为"0"，说明不是偏向锁状态；

倒数两位为"10"，因此，是重量级锁状态，那么前面 62 位就是指向互斥量的指针

## 管程模型（Monitor）

了解操作系统中的并发相关概念

1. 临界资源：虽然多个进程可以共享系统中的各种资源，但其中许多资源一次只能为一个进程所使用，我们把一次仅允许一个进程使用的资源称为临界资源。许多物理设备都属于临界资源，如打印机等。此外，还有许多变量、数据等都可以被若干进程共享，也属于临界资源。
2. 临界区：对临界资源的访问，必须互斥地进行，在每个进程中，访问临界资源的那段代码称为临界区。
3. 互斥：只有一个线程能访问临界区。

。。。太多了，管程暂时不理解，放弃

总之 synchronized 是 Java 管程实现，在编译期会自动生成相关加锁和解锁的代码

对于 synchronized 实现的管程而言，本质上就是由两个队列，除掉等待队列外，条件队列就只有一个

所以 notifyAll() 是唤醒条件队列中的进程

而对应的入口等待队列，则是在当前的进程退出管程的时候会去执行，是隐式地唤醒

## 代码示例

### 同步代码块

```
public void add() {
    synchronized (this) {
        i++;
    }
}
```

对应的字节码指令

```
public void add();
    Code:
       3: monitorenter    // synchronized关键字的入口
      13: monitorexit  // synchronized关键字的出口
      19: monitorexit // synchronized关键字的出口
    Exception table:
       from    to  target type
           4    14    17   any
          17    20    17   any
```

注意，字节码中有两个退出指令（13行和19行），这是因为在加锁的代码块中，编译器会隐式加入 try finally，代码中的 Exception table 部分

防止线程发生异常崩溃，却不释放锁的情况发生（在 finally 中会调用 monitorexit 命令释放锁）

当该对象的 monitor 的计数器count为0时，那线程可以成功取得 monitor，并将计数器值设置为 1，取锁成功。

如果当前线程已经拥有该对象 monitor的持有权，那它可以重入这个 monitor ，计数器的值也会加 1。

而当执行 monitorexit 指令时，锁的计数器会减 1

直到计数器的值减为0，执行线程才会释放 monitor(锁)，其他线程才有机会持有 monitor 。

倘若其他线程已经拥有 monitor 的所有权，那么当前线程获取锁失败将被阻塞并进入到_EntryList中，直到等待的锁被释放为止。

### 同步方法

```
public synchronized void add(){
       i++;
}
```

对应的字节码指令

```
 public synchronized void add();
    descriptor: ()V
    flags: (0x0021) ACC_PUBLIC, ACC_SYNCHRONIZED
    Code: // ...
```

同步方法的字节码没有 monitorenter 和 moniterexit 两条指令，而是在方法的 flag 上加入了 ACC_SYNCHRONIZED 的标记位

jvm 执行时发现 ACC_SYNCHRONIZED 会尝试获取 monitor 对象

## 锁升级

java 1.6 引入偏向锁和轻量级锁，原理也非常简单

### 偏向锁

偏向锁的核心思想是，如果一个线程获得了锁，那么锁就进入偏向模式

当这个线程再次请求锁时，无需再做任何同步操作，即可获取锁的过程，这样就省去了大量有关锁申请/释放的操作

### 轻量级锁

当有第二个线程加入竞争时，JVM 会利用 CAS 尝试把对象原本的 Mark Word 更新为 Lock Record 的指针，成功就说明加锁成功，改变锁标志位为00，然后执行相关同步操作

### 自旋优化（非锁）

cas 如果失败了，JVM 会不断的重试（次数由 jvm 决定），尝试抢锁，这个过程成为自旋，java 7 开始默认启用

超过自旋次数，进入重量级锁 Monitor

### 锁降级

在 safepoint 中可以，也就是方法的出口，jdk8 中本身也有个 MonitorInUseLists 的开关

## 注意事项

- monitor 能够保证变量的可见性和有序性
- monitorenter 指令还有Load屏障的作用，也就是说 synchronized内部的共享变量，每次读取数据的时候被强制从主内存读取最新的数据
- 同理，monitorexit 指令也具有 Store 屏障的作用，也就是 synchronized 代码块内的共享变量，如果数据有变更的，强制刷新会主内存
- synchronized 是非公平锁，也就是说，当线程需要去竞争锁的时候，不会去管前面是不是已经有人在排队了。而是直接尝试获取锁

## monitor 源码实现

```
InterpreterRuntime:: monitorenter()
IRT_ENTRY_NO_ASYNC(void, InterpreterRuntime::monitorenter(JavaThread* thread, BasicObjectLock* elem))
    ...
    //如果使用偏向锁，就进入 Fast_enter，避免不必要的锁膨胀，
  if (UseBiasedLocking) {
    // Retry fast entry if bias is revoked to avoid unnecessary inflation
    ObjectSynchronizer::fast_enter(h_obj, elem->lock(), true, CHECK);
  } else { // 如果不是偏向锁，就进入slow_enter,也就是锁升级
    ObjectSynchronizer::slow_enter(h_obj, elem->lock(), CHECK);
  }
```

### 进入偏向锁

fast_enter 偏向锁判断过程

```
void ObjectSynchronizer::fast_enter(Handle obj, BasicLock* lock, bool attempt_rebias, TRAPS) {
 if (UseBiasedLocking) {
     //如果使用偏向锁，那么尝试偏向
    if (!SafepointSynchronize::is_at_safepoint()) {
        //如果线程不在安全点，那么就尝试BiasedLocking::revoke_and_rebias实现撤销并且重偏向
      BiasedLocking::Condition cond = BiasedLocking::revoke_and_rebias(obj, attempt_rebias, THREAD);
        //偏向成功，直接返回
      if (cond == BiasedLocking::BIAS_REVOKED_AND_REBIASED) {
        return;
      }
    } else {
      assert(!attempt_rebias, "can not rebias toward VM thread");
        //进入这里说明线程在安全点并且撤销偏向
      BiasedLocking::revoke_at_safepoint(obj);
    }
    assert(!obj->mark()->has_bias_pattern(), "biases should be revoked by now");
 }
//使用兜底方案，slow_enter
 slow_enter (obj, lock, THREAD) ;
}
```

如果是偏向锁的话，第一个访问代码块的线程，把它的线程ID写到对象的Markword里面。

因为偏向锁是针对原始的不常发生线程争用时的方案，如果发送线程争用，那么会撤销偏向锁，然后调用 slow_enter

### 进入轻量级锁

```
void ObjectSynchronizer::slow_enter(Handle obj, BasicLock* lock, TRAPS) {
    //获取上锁对象头部标记信息
  markOop mark = obj->mark();
    //如果对象处于无锁状态
  if (mark->is_neutral()) {
    //将对象头部保存在lock对象中
    lock->set_displaced_header(mark);
    //通过cmpxchg进入自旋替换对象头为lock对象地址，如果替换成功则直接返回，表明获得了轻量级锁，不然继续自旋
    if (mark == (markOop) Atomic::cmpxchg_ptr(lock, obj()->mark_addr(), mark)) {
      TEVENT (slow_enter: release stacklock) ;
      return ;
    }
  } else // 否则判断当前对象是否上锁，并且当前线程是否是锁的占有者，如果是markword的指针指向栈帧中的LR，则重入
  if (mark->has_locker() && THREAD->is_lock_owned((address)mark->locker())) {
    assert(lock != mark->locker(), "must not re-lock the same lock");
    assert(lock != (BasicLock*)obj->mark(), "don't relock with same BasicLock");
    lock->set_displaced_header(NULL);
    return;
  }
  // 代码执行到这里，说明有多个线程竞争轻量级锁，轻量级锁通过`inflate`进行膨胀升级为重量级锁
  lock->set_displaced_header(markOopDesc::unused_mark());
  ObjectSynchronizer::inflate(THREAD, obj())->enter(THREAD);
}
```

这里轻量级锁是通过 BasicLock 对象来实现的

在线程JVM栈中产生一个LR（lock Record）的栈桢，然后他们两个CAS竞争锁

成功的，就会在 Markword 中记录一个指针（62位），这个指针指向竞争成功的线程的LR

另外一个线程 CAS 自旋继续竞争，等到前面线程用完了才进入。这就是 "**自旋锁**" 的由来。

### 进入重量级锁（monitor）

```
ObjectMonitor * ATTR ObjectSynchronizer::inflate (Thread * Self, oop object) {
​   //通过无意义的循环实现自旋操作
  for (;;) { 
      const markOop mark = object->mark() ;
      assert (!mark->has_bias_pattern(), "invariant") ;
​
      if (mark->has_monitor()) {//has_monitor是markOop.hpp中的方法，如果为true表示当前锁已经是重量级锁了
          ObjectMonitor * inf = mark->monitor() ;//获得重量级锁的对象监视器直接返回
          return inf ;
      }
​
      if (mark == markOopDesc::INFLATING()) {//膨胀等待，表示存在线程正在膨胀，通过continue进行下一轮的膨胀
         TEVENT (Inflate: spin while INFLATING) ;
         ReadStableMark(object) ;
         continue ;
      }
​
      if (mark->has_locker()) {//表示当前锁为轻量级锁，以下是轻量级锁的膨胀逻辑
          ObjectMonitor * m = omAlloc (Self) ;//获取一个可用的ObjectMonitor
          /**将object->mark_addr()和mark比较，如果这两个值相等，则将object->mark_addr()
          改成markOopDesc::INFLATING()，相等返回是mark，不相等返回的是object->mark_addr()**/
                     markOop cmp = (markOop) Atomic::cmpxchg_ptr (markOopDesc::INFLATING(), object->mark_addr(), mark) ;
          if (cmp != mark) {//CAS失败
             omRelease (Self, m, true) ;//释放监视器
             continue ;       // 重试
          }
​
          markOop dmw = mark->displaced_mark_helper() ;
​
          //CAS成功以后，设置ObjectMonitor相关属性
          m->set_header(dmw) ;
          return m ; //返回ObjectMonitor
      }
      //如果是无锁状态
      ObjectMonitor * m = omAlloc (Self) ; ////获取一个可用的ObjectMonitor
      /**将object->mark_addr()和mark比较，如果这两个值相等，则将object->mark_addr()
          改成markOopDesc::encode(m)，相等返回是mark，不相等返回的是object->mark_addr()**/
      if (Atomic::cmpxchg_ptr (markOopDesc::encode(m), object->mark_addr(), mark) != mark) {
          //CAS失败，说明出现了锁竞争，则释放监视器重行竞争锁
          m = NULL ;
          continue ;
      }
      return m ; //返回ObjectMonitor对象
  }
}
```

该方法有四种情况

1. mark->has_monitor() 说明已经膨胀为重量级锁，判断lock标志位是否为10，如果是则获得对象监视器
2. 锁处于inflating，说明其他线程正在执行锁膨胀，那么当前线程通过自旋等待其他线程完成锁膨胀
3. 如果当前线程已经是轻量级锁需要锁膨胀，那么通过omAlloc方法获得一个可用的ObjectMonitor，并设置初始数据；然后通过CAS将对象头设置为`markOopDesc:INFLATING，表示当前锁正在膨胀，如果CAS失败，继续自旋
4. 如果是无锁状态，类似情况3

### 总结

字节码层面，synchronized 的原理可以理解为在字节码中加了monitorenter和monitorexit来实现的；

而深入HotSpot源码的monitorenter源码部分，可以看出synchronized底层原理其实涉及到偏向锁->轻量级锁->重量级锁的锁膨胀过程。

多数时间，被 synchronized 修饰的代码块只有一个线程在执行，根本没有多线程竞争，这种情况没必要设置竞争机制，所以产生了偏向锁

在monitorenter中是通过fast_enter来实现的，实际上就是第一个访问代码块的线程，把它的线程ID写到MarkWord中去。

一旦有线程来竞争了，fast_enter就撤销偏向锁，然后调用slow_enter

竞争的线程通过CAS自旋竞争，成功的线程就会在Markword中记录一个指针，指向竞争成功的线程的LR

而另一个线程CAS自旋继续竞争，等前面线程用完了才进入。

由于自旋是消耗CPU资源的，并且是无意义的，如果锁的时间过长或者自旋线程多，CPU资源会被大量消耗

而重量级锁有等待队列，拿不到锁的线程会进入等待队列，不需要消耗CPU资源（采用CFS完全公平策略，根据时间片比例分配）

所以如果线程自旋超过10次，或者自旋线程数超过CPU核数的一半，此时会升级为重量级锁

也就是调用inflate用于获取监视器monitor，enter用于抢占锁

## 参考资料

- [64位JVM的Java对象头详解，从hotspot源码中寻找答案 - Java艺术](https://juejin.cn/post/6844904069845221384)
- [我所理解的管程以及Java中的管程样例 - 平笙](https://zhuanlan.zhihu.com/p/478819094)
- [从 Monitorenter 源码看 Synchronized 锁优化的过程 - 小水滴滴滴](https://juejin.cn/post/7104638789456232478)

