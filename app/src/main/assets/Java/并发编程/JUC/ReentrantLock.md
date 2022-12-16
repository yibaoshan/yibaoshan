
java 1.5 之前，java 主要靠 synchronized 关键字来实现锁功能的

在 java 1.5 版本，加入了由 Doug Lea 大师主导开发的 juc 包，意在给 java 提供更好、更便捷的并发编程方式

juc 提供了 lock 接口，虽然用起来比 synchronized 繁琐许多，但有了更多的可能性，开发者可以自行选择获取和释放锁

## 从 ReentrantLock 看 JUC

```
public void main(){
    Lock lock = new ReentrantLock(false); // true-公平锁，false-为非公平锁，默认为非公平锁
    // 拿锁，如果拿不到会一直等待
    lock.lock();
    try {
        // 再次尝试拿锁(可重入)，拿锁最多等待100毫秒
        if (lock.tryLock(100, TimeUnit.MILLISECONDS))
            i++;
    } catch (InterruptedException e) {
        e.printStackTrace();
    } finally {
        // 释放锁
        lock.unlock(); 
    }
}
```

ReentrantLock 实现了 Lock 接口

```
public interface Lock {
    // 获取锁
    void lock();
    // 获取可中断锁，即在拿锁过程中可中断，synchronized是不可中断锁。
    void lockInterruptibly() throws InterruptedException;
    // 尝试获取锁，成功返回true，失败返回false
    boolean tryLock();
    // 在给定时间内尝试获取锁，成功返回true，失败返回false
    boolean tryLock(long time, TimeUnit unit) throws InterruptedException;
    // 释放锁
    void unlock();
    // 等待与唤醒机制
    Condition newCondition();
}
```

Lock 接口的各个方法由 Sync 类实现

```
public class ReentrantLock implements Lock {
    private final Sync sync;
    
    public void lock() {
        sync.acquire(1);
    }
    
    public void unlock() {
        sync.release(1);
    }
}
```

Sync 则继承自 AbstractQueuedSynchronizer ，内部的方法由 cas 实现

```
abstract static class Sync extends AbstractQueuedSynchronizer {
    // 尝试获取非公平锁
    final boolean nonfairTryAcquire(int acquires) {
        final Thread current = Thread.currentThread();
        int c = getState();
        // 未上锁状态
        if (c == 0) {
            // 通过CAS尝试拿锁
            if (compareAndSetState(0, acquires)) {                    
                // 设置持有排他锁的线程
                setExclusiveOwnerThread(current);
                return true;
            }
        }
        ...
    }
}
```