Android组件：Handler机制浅析

## 一、前言

Android Handler机制是每个Android开发者成长道路上一道绕不过去的坎，了解Handler机制对于解决开发中的遇到的卡顿检测、ANR监控，以及APP是如何运行的等问题有着非常大的帮助

市面上已经有许多讲解Handler机制的文章，各种角度的都有，其中不乏有不少深度好文；本文不讲解源码(主要是讲不过其他文章)，从Android Handler机制的设计思想开始讲起，由浅入深，带你一步步走进Handler内部的实现原理

以下，enjoy：

**我相信每个Android开发者都或多或少的了解过Handler机制，为了不浪费大家时间，在文章开始之前，我觉得有必要说明一下本文的目标受众群体**

适合人群：

> 1、对Handler机制还不是很熟悉，想要了解Handler内部是如何运行的
>
> 2、其他客户端开发，比如iOS、Windows开发等，想要了解在Android系统中是如何在非UI线程更新UI的

不适合人群：

> 1、高级工程师，对Handler机制了解的很深入了，这篇文章纯属浪费时间
>
> 2、觉得源码过于枯燥，想要找一篇文章对照着看，本文可能不是很合适，因为文章中并不会涉及太多的源码

### 1、Handler机制介绍

在开始介绍Handler机制之前，我们先来聊一聊Handler的设计背景，它为了解决什么问题

聊一聊UI单线程

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

本文将Handler机制拆成了两个部分，

第一部分是介绍Android是如何使用生产者消费者模型实现Handler机制的

第二部分介绍的是在Handler迭代的过程中，Android为Handler量身定做了哪些功能，或为了便于使用，或为了运行效率



希望看完本篇文章后的你能对Handler有个初步的认识

全文完

异步消息与同步屏障

Handler Callback

Android Handler机制是每个Android开发者成长路上，如何体现Handler机制在Android OS中的重要性呢？

Android系统2003年10月立项，2005年7月被Google收购

我们总结一下Handler从Android 1.6 (API 3) 一直到 Android 12 (API 31)的演变过程：

Handler机制在Android 1.6到Android 10进化一览表

- **[Android 1.6(API 4)](https://android.googlesource.com/platform/frameworks/base/+/refs/tags/android-1.6_r1/core/java/android/os)**

  > 这也许是你能找到最老版本的Handler源码，此时Handler的完成度已经很高了，特点是：
  >
  > **1、队列空闲时等待方案使用的是Java的Object.wait()/notfity()**
  >
  > > Android 1.6发布时间是09年9月，Java5的代码是09年11月份公开的，要是Android晚两个月发布，空闲时等待方案可能就能用上juc中的锁了
  >
  > **2、支持IdleHandler**
  >
  > **3、不支持异步消息和同步屏障消息**
  >
  > > 所有消息一视同仁，这也是早期Android设备体验不好的原因之一
  >
  > **4、退出loop循环的方案是提交一个target为空的msg**
  >
  > > 这里插一嘴，老的版本退出循环是调用Handler.quit()方法向MessageQueue插入一条target为null的消息，Looper.loop()方法中，若检测到msg.target=null则退出循环
  
- **[Android 2.3(API 9)](https://android.googlesource.com/platform/frameworks/base/+/refs/tags/android-2.3_r1/core/java/android/os)**

  > **1、MessageQueue.next()方法中，空闲时等待方案从Object.wait()改为nativePollOnce()实现**
  >
  > > 空闲时等待指的是，当消息队列和mIdleHandlers都为空时，当前APP就没啥事干了，Looper线程将进入等待唤醒状态
  >
  > **2、MessageQueue.enqueueMessage()方法中，唤醒方案从Object.notify()改为nativeWake()实现**
  >
  > > 还有个小细节，2.3之前只要调用enqueueMessage()方法就会调用this.notify()唤醒线程，哪怕加入的这个消息是个延迟消息要求一万年后才执行，在2.3的enqueueMessage()方法中修复了这个问题

- **[Android 4.0(API 14)](https://android.googlesource.com/platform/frameworks/base/+/refs/tags/android-cts-4.0_r1)**

  > **1、Message增加flags属性，用于标识该消息是否已经消费过了，防止同一消息无限次提交**
  >
  > > 调用isInUse()方法可以查询当前消息是否使用过，这个flags后续也还会加入更多的含义

- **[Android 4.1.1(API 16)](https://android.googlesource.com/platform/frameworks/base/+/refs/tags/android-4.1.1_r1/core/java/android/os)**

  > 这个版本变化略微大一些，主要是增加了对异步消息和同步屏障消息的支持
  >
  > **1、Message支持设置为异步消息**
  >
  > > 调用setAsynchronous(true)方法可以将Message设置为异步消息，判断是否为异步消息的标识保存在Message的成员变量flags中
  >
  > **2、MessageQueue支持处理异步消息**
  >
  > > 主要是在enqueueMessage()方法和next()方法中增加异步消息的处理逻辑
  >
  > **3、MessageQueue支持添加/删除同步屏障消息**
  >
  > > 对应方法为：enqueueSyncBarrier()和removeSyncBarrier()
  > >
  > > 在MessageQueue的next()方法中也增加了对同步屏障消息的处理逻辑
  >
  > **4、MessageQueue支持quit()方法**
  >
  > > 这个版本的退出逻辑是将MessageQueue的成员变量mQuiting设置为true，在调用MessageQueue.next()方法时检查mQuiting变量值，为true则返回null给Lopper，Looper.loop()判断时null值直接结束当前循环
  > >
  > > 注意这里并不会清空MessageQueue中的消息，也就是说若消息持有外部的强引用，那么会造成**内存泄漏**
  >
  > **5、Lopper删除成员变量mRun**
  >
  > > 这货本来就没啥用~，早期打印msg执行日志的时候会带上它

- **[Android 4.2 (API 17)](https://android.googlesource.com/platform/frameworks/base/+/refs/tags/android-4.2_r1/core/java/android/os)**

  > **1、Handler支持设置为异步Handler，@hide修饰**
  >
  > > 如下，新增Handler(boolean async)构造函数，使用该Handler发送的消息均为异步消息
  > >
  > > ```java
  > > public Handler(boolean async) {
  > > 	mAsynchronous = async;
  > > }
  > > ```
  >
  > **2、Handler支持子线程提交同步任务，新增runWithScissors()方法，@hide修饰**
  >
  > > runWithScissors()方法接受一个Runnable和超时时间，调用此方法提交一个任务后：
  > >
  > > 1、若消息发送线程和Handler创建线程是同一线程，那么执行Runnable的run方法
  > >
  > > 2、若消息发送线程和Handler创建线程不在同一线程，可以理解为子线程向主线程提交了一个任务，任务提交后，子线程会进入休眠状态等待唤醒，一直等到任务执行结束
  > >
  > > 
  > >
  > > 注意，该方法不但被@hide修饰，在代码注释也向开发者告知这是个危险方法，不建议使用，因为runWithScissors()方法有两个严重缺陷：
  > >
  > > 1、无法取消已提交的任务，即使消息的发送线程已经死亡，主线程仍然会取出消息队列的任务执行，但这时候运行的程序是不符合我们的预期的
  > >
  > > 2、可能会造成死锁：子线程向主线程(创建Handler的线程)提交了延迟任务后，子线程是处于等待被唤醒的状态，此时若主线程退出了loop循环并清空了消息队列，那子线程提交的任务就永远不会被唤醒执行，该任务持有的锁永远不会被释放，造成**死锁**

- **[Android 4.3 (API 18)](https://android.googlesource.com/platform/frameworks/base/+/refs/tags/android-4.3_r1/core/java/android/os)**

  > **1、MessageQueue支持安全退出，quit(safe)方法**
  >
  > > 新增以下两个方法，safe参数为true时调用removeAllFutureMessagesLocked()
  > >
  > > - removeAllMessagesLocked() ：清空所有消息
  > > - removeAllFutureMessagesLocked()：清空延迟消息，到期消息交给Handler分发

- **[Android 6.0 (API 23)](https://android.googlesource.com/platform/frameworks/base/+/refs/tags/android-6.0.0_r1/core/java/android/os)**

  > **1、MessageQueue支持监听文件描述符，对应方法：addOnFileDescriptorEventListener()**
  >
  > > 这部分代码我没懂，目前的知识储备不足以让我看懂~ /哭唧唧.jpg
  >
  > **2、MessageQueue发送同步屏障消息方法改名，从enqueueSyncBarrier()改为postSyncBarrier()**
  >
  > > 我很认真的确认过，主要逻辑没动过，真的只是改个名

- **[Android 8.0 (API 26)](https://android.googlesource.com/platform/frameworks/base/+/refs/tags/android-8.0.0_r1/core/java/android/os)**

  > **1、Handler增加getMain()方法，用于获取运行在UI线程的Handler实例，@hide修饰**
  >
  > > getMain()检查成员变量MAIN_THREAD_HANDLER是否已经保存了Handler实例，若MAIN_THREAD_HANDLER为空，则使用Looper.getManLooper()创建一个新的Hnadler实例，赋值给MAIN_THREAD_HANDLER变量，最后返回结果
  > >
  > > 注意，该方法只是返回一个运行在UI线程的Handler，并不是ActivityThread中的成员变量mH！！！

- **[Android 9.0 (API 28)](https://android.googlesource.com/platform/frameworks/base/+/refs/tags/android-9.0.0_r1/core/java/android/os)**

  > **1、Handler增加executeOrSendMessage()方法，@hide修饰**
  >
  > > 这个方法比较简单，提供的功能和字面意思相同
  > >
  > > 判断消息发送线程和消息消费线程是同一线程，是的话调用Handler.dispatchMessage()方法分发消息，否则塞进消息队列等待被分发
  >
  > **2、Handler允许APP创建异步Handler**
  >
  > > 增加了静态方法createAsync()，调用该方法会返回一个Handler实例，这个Handler实例就是异步Handler

- **[Android 10.0 (API 29)](https://android.googlesource.com/platform/frameworks/base/+/refs/tags/android-10.0.0_r1/core/java/android/os)**

  > **1、Looper增加setObserver(observer)方法，监听消息分发过程，@hide修饰**
  >
  > > 一共有3个方法
  > >
  > > 1. Object messageDispatchStarting()：发送消息前调用
  > > 2. messageDispatched(token, msg)：当消息被 Handler 处理时调用
  > > 3. dispatchingThrewException(token, msg, exception)：在处理消息时抛出异常时调用



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













