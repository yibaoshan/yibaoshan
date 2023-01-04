
项目中使用了 BlockCanary 的监控方案

## Looper Printer

无论是通过反射，替换 Looper 的 mLogging

还是通过 setMessageLogging() 方法设置 printer

都是基于 Handler 消息分发机制展开，这也是微信和 BlockCanary 三方库使用的方案

```
/frameworks/base/core/java/android/os/Looper.java
class Looper {
    public static void loop() {
         Message msg = queue.next(); // might block
         logging.println(">>>>> Dispatching to " + msg);
         msg.target.dispatchMessage(msg);
         logging.println("<<<<< Finished to " + msg);
    }
}
```

我们只需要替换主线程 Looper 的 printer 对象，通过计算执行 dispatchMessage() 方法之后和之前打印字符串的时间的差值

就可以拿到到 dispatchMessage() 方法执行的时间。

而大部分的主线程的操作最终都会执行到这个 dispatchMessage 方法中。

但是这样会有几种无法被监控到的情况，因为 next() 获取消息本质是调用 nativePollOnce() 方法

因此，首先 native 的耗时就无法被统计到，事件分发也是在 nativePollOnce() 中触发，也无法统计

另外，IdleHandler 的 queueIdle() 回调方法也不在统计之列

还有一类相对少见的问题是 SyncBarrier（同步屏障）的泄漏同样无法被监控到

### 1、监控事件分发

应用的 Touch / Key 事件也是在 nativePollOnce() 被处理的。

这就意味着，View 的 TouchEvent 中的卡顿这种方案是无法监控的

如果项目中有大量的自定义 View，而这些 View 中的 onTouch() 回调无法被监控，显然是不能接受的，因为卡在这里面的情况非常普遍

了解事件分发流程的同学应该知道，input 消息是使用一对 socket 来通信的

我们可以通过 PLT Hook（Native Hook的一种方式），去 Hook 这对 Socket 的 send 和 recv 方法，进而来监控 Touch 事件耗时

当调用到了 recvfrom 时，说明我们的应用接收到了Touch事件

当调用到了sendto时，说明这个Touch事件已经被成功消费掉了

当两者的时间相差过大时即说明产生了一次 Touch 事件的卡顿

这种方案微信上也在使用，比较可靠

### 2、监控 IdleHandler 卡顿

MessageQueue 中的 mIdleHandlers 是可以被反射的，这个成员变量保存了所有将要执行的 IdleHandler

我们只需要把 ArrayList 类型的 mIdleHandlers，通过反射，替换为 MyArrayList

在我们自定义的 MyArrayList 中重写 add 方法

再将我们自定义的 MyIdleHandler 添加到 MyArrayList 中，就完成了“偷天换日”。

从此之后 MessageQueue 每次执行 queueIdle() 回调方法，都会执行到我们的 MyIdleHandler 中的 queueIdle() 方法，就可以在这里监控 queueIdle() 的执行时间了

```
private static void detectIdleHandler() {
    MessageQueue mainQueue = Looper.getMainLooper().getQueue();
    Field field = MessageQueue.class.getDeclaredField("mIdleHandlers");
    field.setAccessible(true);
    MyArrayList<MessageQueue.IdleHandler> myIdleHandlerArrayList = new MyArrayList<>();
    field.set(mainQueue, myIdleHandlerArrayList);
}
```

### 3、监控SyncBarrier泄漏

微信的方案是不断轮询主线程Looper的MessageQueue的mMessage(也就是主线程当前正在处理的Message)。

而SyncBarrier本身也是一种特殊的Message，其特殊在它的target是null。

如果我们通过反射mMessage，发现当前的Message的target为null，并且通过这个Message的when发现其已经存在很久了，这个时候我们合理怀疑产生了SyncBarrier的泄漏

这种 case 比较极端，暂时不用考虑。。

## BlockCanary 原理

BlockCanary 是 Android 平台中，性能监控的开源三方库

在卡顿监控方面，BlockCanary 使用的是设置自定义的 printer 来实现的耗时统计

信息采集方面，一旦判定发生卡顿：

- 通过 main Looper 的 Thread#getStackTrace()，获取主线程堆栈信息
- 通过 /proc/stat 获取 CPU 使用率信息

## 参考资料

- [Android 线上卡顿监控 - 潇风寒月](https://juejin.cn/post/7177194449322606647)
- [微信Android客户端的卡顿监控方案](https://cloud.tencent.com/developer/article/1846821)