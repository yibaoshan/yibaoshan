从设计模式角度谈Android Handler机制【非源码解析】

## 一、前言

Android Handler机制是每个Android开发者成长道路上绕不过去的一道坎

市面上已经有许多讲解Handler机制的文章，各种角度的都有，不乏有深度文章，本文意在从Android Handler的设计开始讲起，由浅入深，不适合边看文章边看源码，对照着理解的这部分同学。

适合人群：

> 1、对Handler机制还不是很熟悉，相信看完本文后，您将会对Handler机制是如何运行的有个初步了解
>
> 2、其他客户端开发，比如iOS、Windows开发等，想要了解Android是如何保证UI线程是单一线程的机制是什么的同学

不适合人群：

> 1、高级工程师，对Handler机制了解的很深入了，这篇文章纯属浪费时间
>
> 2、觉得源码过于枯燥，想要找一篇文章对照着看，很遗憾，本篇文章并不会涉及太多的源码

### 1、Handler介绍

在开始介绍Handler机制之前，我们先来聊一聊Handler的设计背景，它为了解决什么问题

我们知道，

### 2、Handler的发展历程

二、生产者/消费者模式介绍

想要理解Android Handler机制，就必须先理解什么是生产者/消费者模型

三、基于生产者/消费者模式手写Handler

同学们注意，我要开始变形了

四、Handler还提供了什么功能？

本章节聊一聊Handler机制除了实现生产者/消费者模式之外，还给我们提供了什么功能，其实也就是老生常谈的几样：

- 异步消息与同步屏障
- Handler Callback

五、总结

ThreadLocal是属于Java并发中的内容，Handler只是借用了ThreadLocal来保证MessageQueue在当前线程线程的唯一性，不用ThreadLocal也是可以的哈。

比如我在程序的入口ActivityThreadl声明一个静态final的MessageQueue，所有线程提交/取出消息都操作该消息队列就行了

我们学习某个知识点，没必要把这个知识点涉及到所有的内容都了解一遍。拿Handler举例来说，我们只需要了解它是如何设计的，某个功能是自己实现的还是借用其他模块功能完成的，epoll机制是Linux，跟踪源码下去每个问题都想在当下解决，往往会浪费比较多的时间，最后落得只见树叶不见森林的结构才是真正得不偿失

本文将Handler机制拆成了两个部分，一是实现生产者消费者模式的部分，二是除此之外的功能

希望看完本篇文章后的你能对Handler有个初步的认识

全文完

异步消息与同步屏障

Handler Callback

Android Handler机制是每个Android开发者成长路上，如何体现Handler机制在Android OS中的重要性呢？

Android系统2003年10月立项，2005年7月被Google收购

我们总结一下Handler从Android 1.6 (API 3) 一直到 Android 12 (API 31)的演变过程：

Android Handler机制从version 1.6 (API 3) 到version 12 (API 31)进化过程一览表

- **[Android 1.6(API 4)](https://android.googlesource.com/platform/frameworks/base/+/refs/tags/android-1.6_r1/core/java/android/os)**

  > 这也许是你能找到最老版本的Handler源码，此时Handler的完成度已经很高了，特点是：
  >
  > **1、队列空闲时等待方案使用的是Java的Object.wait()/notfity()**
  >
  > > 1.6代码是09年9月发布的，Java5代码是09年11月份公开的，要是Android晚两个月发布，空闲时等待方案就可能用上juc中的锁了
  >
  > **2、支持IdleHandler**
  >
  > > IdleHandler早在Handler诞生之初就设计好了
  >
  > **3、不支持异步消息和同步屏障消息**
  >
  > > 所有消息一视同仁，这也是早期Android设备体验不好的原因之一
  >
  > **4、退出loop循环的方案是提交一个target为空的msg**
  >
  > > *这里插一嘴，老的版本退出循环是调用Handler.quit()方法向MessageQueue插入一条target为null的消息，Looper.loop()方法中，若检测到msg.target=null则退出循环*

- **[Android 2.3(API 9)](https://android.googlesource.com/platform/frameworks/base/+/refs/tags/android-2.3_r1/core/java/android/os)**

  > **1、MessageQueue.next()方法中，空闲时等待方案从Object.wait()改为nativePollOnce()实现**
  >
  > > *空闲时等待指的是，当消息队列和mIdleHandlers都为空时，当前APP就没啥事干了，Looper线程将进入等待唤醒状态*
  >
  > **2、MessageQueue.enqueueMessage()方法中，唤醒方案从Object.notify()改为nativeWake()实现**
  >
  > > *这里还有个小细节，2.3之前只要调用enqueueMessage()方法就会调用this.notify()唤醒线程，哪怕加入的这个消息是个延迟消息要求一万年后才执行，在2.3的enqueueMessage()方法中修复了这个问题*

- **[Android 4.0(API 14)](https://android.googlesource.com/platform/frameworks/base/+/refs/tags/android-cts-4.0_r1)**

  > **1、Message增加flags属性，用于标识该消息是否已经消费过了，防止同一消息无限次提交**
  >
  > > *调用isInUse()方法可以查询当前消息是否使用过，这个flags后续也还会加入更多的含义*

- **[Android 4.1.1(API 16)](https://android.googlesource.com/platform/frameworks/base/+/refs/tags/android-4.1.1_r1/core/java/android/os)**

  > 这个版本变化略微大一些，主要是增加了对异步消息和同步屏障消息的支持
  >
  > **1、Message支持设置为异步消息**
  >
  > > *调用setAsynchronous(true)方法将Message设置为异步消息，是否为异步消息的标识保存在flags中*
  >
  > **2、MessageQueue支持处理异步消息**
  >
  > > *enqueueMessage()方法和next()方法增加异步消息处理逻辑，next()方法还增加了对同步屏障消息的处理逻辑*
  >
  > **3、MessageQueue支持添加/删除同步屏障消息，对应方法为：enqueueSyncBarrier()/removeSyncBarrier()**
  >
  > **4、MessageQueue支持quit()方法**
  >
  > **5、Lopper删除成员变量mRun**

- **[Android 4.2 (API 17)](https://android.googlesource.com/platform/frameworks/base/+/refs/tags/android-4.2_r1/core/java/android/os)**

  > **1、Handler支持设置为异步Handler，新增Handler(boolean async)构造函数，使用该Handler发送的消息均为异步消息**
  >
  > ```java
  > public Handler(boolean async) {
  >   this(null, async);
  > }
  > ```
  >
  > **2、Handler新增runWithScissors()方法，支持子线程向主线程提交同步任务**

- **[Android 4.3 (API 18)](https://android.googlesource.com/platform/frameworks/base/+/refs/tags/android-4.2_r1/core/java/android/os)**

  > **1、MessageQueue支持quit(safe)方法**

- **[Android 6.0 (API 23)](https://android.googlesource.com/platform/frameworks/base/+/refs/tags/android-6.0.0_r1/core/java/android/os)**

  > **1、MessageQueue支持监听文件描述符，对应方法：addOnFileDescriptorEventListener()**
  >
  > **2、MessageQueue发送同步屏障消息方法改名，从enqueueSyncBarrier()改为postSyncBarrier()**

- **[Android 8.0 (API 26)](https://android.googlesource.com/platform/frameworks/base/+/refs/tags/android-8.0.0_r1/core/java/android/os)**

  > **1、Handler增加getMain()方法，用于获取运行在UI线程的Handler实例**
  >
  > > *getMain()检查成员变量MAIN_THREAD_HANDLER是否已经保存了Handler实例，若MAIN_THREAD_HANDLER为空，则使用Looper.getManLooper()创建一个新的Hnadler实例，赋值给MAIN_THREAD_HANDLER变量，最后返回结果*
  > >
  > > *注意，该方法只是返回一个运行在UI线程的Handler，并不是ActivityThread中的成员变量mH*

- **[Android 9.0 (API 28)](https://android.googlesource.com/platform/frameworks/base/+/refs/tags/android-9.0.0_r1/core/java/android/os)**

  > **1、Handler增加executeOrSendMessage()方法**
  >
  > > *这个方法比较简单，提供的功能和字面意思相同*
  > >
  > > *判断消息发送线程和消息消费线程是同一线程，是的话调用Handler.dispatchMessage()方法分发消息，否则塞进消息队列等待被分发*

- **[Android 10.0 (API 29)](https://android.googlesource.com/platform/frameworks/base/+/refs/tags/android-10.0.0_r1/core/java/android/os)**

  > **1、Looper增加setObserver(observer)方法，监听消息分发过程**

以上是每次Android版本更新，接下来我们站在各个类的角度，为每个类总结一下，它们从诞生之初到现在的演变过程

- **Handler**

1. **增加getMain()方法，返回运行在UI线程的Handler，@hide修饰**

   > getMain()检查成员变量MAIN_THREAD_HANDLER是否已经保存了Handler实例，若MAIN_THREAD_HANDLER为空，则使用Looper.getManLooper()创建一个新的Hnadler实例，赋值给MAIN_THREAD_HANDLER变量，最后返回结果
   >
   > 注意，该方法只是返回一个运行在UI线程的Handler，并不是ActivityThread中的成员变量mH

2. **支持创建异步Handler，通过该Handler发送的消息均为异步消息**

   > 两种方式创建异步Handler
   >
   > 1、调用构造函数创建Handler时指定为异步Handler，该构造函数被@hide修饰，APP不可用
   >
   > 2、Android 9 (API 28)之后，增加createAsync()静态方法，调用该方法会返回一个Handler实例，这个Handler实例就是异步Handler
   >
   > 以上两种方式都是将成员变量mAsynchronous设置为true

3. **增加runWithScissors()方法，用于子线程向主线程提交一个同步任务，子线程会等待该任务执行结束后在运行，@hide修饰**

   > 该方法接受一个Runnable和超时时间，调用此方法提交一个任务后：
   >
   > 1、若消息发送线程和Handler创建线程是同一线程，那么执行Runnable的run方法
   >
   > 2、若消息发送线程和Handler创建线程不在同一线程，可以理解为子线程向主线程提交了一个任务，任务提交后，子线程会进入休眠状态等待唤醒，一直等到任务执行结束
   >
   > 
   >
   > 注意，该方法不但被@hide修饰，在代码注释也向开发者告知这是个危险方法，不建议使用，因为runWithScissors()方法有两个严重缺陷：
   >
   > 1、无法取消已提交的任务，即使消息的发送线程已经死亡，主线程仍然会取出消息队列的任务执行，但这时候运行的程序是不符合我们的预期的
   >
   > 2、可能会造成死锁：子线程向主线程(创建Handler的线程)提交了延迟任务后，子线程是处于等待被唤醒的状态，此时若主线程退出了loop循环并清空了消息队列，那子线程提交的任务就永远不会被唤醒执行，该任务持有的锁永远不会被释放，造成死锁

4. **增加executeOrSendMessage()方法，提供的功能和方法名一样，@hide修饰**

   > 这个方法比较简单，判断消息发送线程和消息消费线程是同一线程
   >
   > 是的话调用Handler.dispatchMessage()方法分发消息，否则塞进消息队列等待被分发

- **Looper**

1. **删除成员变量mRun**

   > 本来就没啥用~，早期打印msg执行日志的时候会带上它

2. **增加setObserver(observer)方法，@hide修饰，监听消息派发过程，共有3个方法：**

   > 1、Object messageDispatchStarting()：发送消息前调用
   >
   > 2、messageDispatched(token, msg)：当消息被 Handler 处理时调用
   >
   > 3、dispatchingThrewException(token, msg, exception)：在处理消息时抛出异常时调用

- **MessageQueue**

1. **增加postSyncBarrier(when)、removeSyncBarrier(token)方法以支持发送同步屏障消息，@hide修饰**

2. **增加isIdle()方法，如果当前Looper没有待处理的待处理消息，则返回 true**

3. **增加addOnFileDescriptorEventListener(fd)、removeOnFileDescriptorEventListener(fd)**

   > 从方法名可以看出是添加/删除文件描述符事件监听器，这一块代码我没看懂是什么意思，有了解的同学可以在评论区留言

4. **增加quit(safe)方法**

5. **next()方法中，休眠方案从Object.wait()改为nativePollOnce()实现**

   > 消息队列和mIdleHandlers都为空时，Looper线程将进入等待唤醒状态

6. **enqueueMessage(msg)方法中，唤醒方案从Object.notify()改为nativeWake()实现**

- **Message**

1. **增加flags变量，int类型，用于标识该条消息是否已经使用过、是否是异步消息等**
2. **增加setAsynchronous()方法，将消息设置为异步消息**



代码太长了我知道你们也懒得看

- Message成员属性

| 类型    | 名称    | 说明                                                         |
| ------- | ------- | ------------------------------------------------------------ |
| int     | what    | 标识消息类型，搭配sendEmptyMessage()方法使用                 |
| int     | arg1    | 如果只是想传递一些int类型的值使用                            |
| int     | arg2    | 参考arg1                                                     |
| Object  | obj     | 通常用于传递参数                                             |
| long    | when    | 消息的到期执行时间，大于等于当前时间(开机时间)判定为可以执行 |
| Handler | handler | 消息属于                                                     |
| Object  | obj     | 通常用于它传递值                                             |
| Bundle  | data    | 传递的数据                                                   |

- Message成员方法

| 方法&参数            | 说明     |
| -------------------- | -------- |
| Message obtain()系列 | 返回一个 |
| Handler              | target   |
| Runnable             | callback |
| Message              | next     |

- Handler成员方法

| 方法&参数                    | 说明                                                         |
| ---------------------------- | ------------------------------------------------------------ |
| dispatchMessage(Message msg) | 分发消息，优先执行msg.callback(也就是runnable)，其次mCallback.handleMessage()，最后handleMessage() |
| Handler                      | target                                                       |
| Runnable                     | callback                                                     |
| Message                      | next                                                         |













