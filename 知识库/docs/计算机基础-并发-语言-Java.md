
基础线程支持

```java
// 创建线程
Thread thread = new Thread(() -> {
    // 线程代码
});
thread.start();  // 启动线程

// 线程控制
thread.join();     // 等待线程结束
thread.interrupt(); // 中断线程
Thread.sleep();    // 线程休眠
Thread.yield();    // 线程让步
```

# 同步机制

synchronized 关键字

```java
// 对象锁
synchronized(object) {
    // 临界区代码
}

// 方法锁
public synchronized void method() {
    // 临界区代码
}
```

volatile 关键字

```java
Lock lock = new ReentrantLock();
try {
    lock.lock();
    // 临界区代码
} finally {
    lock.unlock();
}
```

Lock 接口

```java
Lock lock = new ReentrantLock();
try {
    lock.lock();
    // 临界区代码
} finally {
    lock.unlock();
}
```

### JUC（java.util.concurrent）包

原子类

```java
AtomicInteger counter = new AtomicInteger(0);
counter.incrementAndGet();  // 原子自增
counter.compareAndSet(1, 2);  // CAS 操作
```

并发集合

```java
ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(10);
```

线程池

```java
ExecutorService executor = Executors.newFixedThreadPool(5);
Future<?> future = executor.submit(() -> {
    // 任务代码
});
```

### 同步工具类

```java
// CountDownLatch
CountDownLatch latch = new CountDownLatch(3);
latch.countDown();  // 计数减一
latch.await();      // 等待计数为零

// CyclicBarrier
CyclicBarrier barrier = new CyclicBarrier(3);
barrier.await();    // 等待其他线程

// Semaphore
Semaphore semaphore = new Semaphore(5);
semaphore.acquire();  // 获取许可
semaphore.release();  // 释放许可
```

### 并发工具类

CompletableFuture

```java
CompletableFuture<String> future = CompletableFuture
    .supplyAsync(() -> "Hello")
    .thenApply(s -> s + " World");
```

Fork/Join 框架

```java
class MyTask extends RecursiveTask<Integer> {
    protected Integer compute() {
        // 分治任务实现
        return null;
    }
}
```