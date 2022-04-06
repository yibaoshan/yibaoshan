Android组件：Handler机制(一)

## Overview

每个Android开发者都或多或少的了解过Handler机制，为了不浪费大家时间，在文章开始之前，我觉得有必要说明一下本文的目标受众

适合人群：

> 1、Android新手开发，对Handler机制还不是很熟悉，想要了解Handler内部是如何运行的
>
> 2、其他客户端开发，比如iOS、Windows开发等，想要了解在Android系统中是如何在非UI线程更新界面的

不适合人群：

> 1、中高级工程师，对Handler机制了解的很深入了，这篇文章纯属浪费时间
>
> 2、觉得源码过于枯燥，想要找一篇文章对照着看，本文可能不是很合适，因为文章中并不会涉及太多的源码

## 一、前言

Android Handler机制是每个Android开发者成长道路上一道绕不过去的坎，了解Handler机制对于解决开发中的遇到的卡顿检测、ANR监控，以及了解APP组件是如何运行的等问题有着非常大的帮助

市面上已经有许多讲解Handler机制的文章，各种角度的都有，其中不乏有不少深度好文；本文不讲解源码(主要是讲不过其他文章)，从Android Handler机制的设计思想开始讲起，由浅入深，带你一步步走进Handler内部的实现原理

以下，enjoy：

## 二、Handler介绍

在Android开发者官网对View的介绍有这样一句话：

![image_android_component_handler_android_developer_view_ui_thread](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/AOSP/src/main/java/com/android/aosp/frameworks/base/core/android/os/handler/blog/imgs/image_android_component_handler_android_developer_view_ui_thread.jpg)

*图片来源：[Android Developer](https://developer.android.com/reference/android/view/View)*

**整个视图树是单线程的， 在任何视图上调用任何方法时，必须始终在 UI 线程上。如果在其他线程上工作并希望从该线程更新视图的状态，则应该使用 Handler**

不只是Android，在iOS开发者官网对UIKit介绍中同样有类似提示：

![image_android_component_handler_ios_developer_uikit_ui_thread](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/AOSP/src/main/java/com/android/aosp/frameworks/base/core/android/os/handler/blog/imgs/image_android_component_handler_ios_developer_uikit_ui_thread.jpg)

*图片来源：[Apple iOS Developer](https://developer.apple.com/documentation/uikit)*

**除非另有说明，否则只能从应用程序的主线程或主调度队列中使用 UIKit 类。 此限制特别适用于从 UIResponder 派生的类或涉及以任何方式操作应用程序用户界面的类。**

奇了怪了，Android和iOS作为一个有图形用户界面(Graphical User InterfaceI)的操作系统，为什么都要求在主线程(UI线程)操作页面？

### 1、GUI为什么要设计成单线程？

前Sun的副总裁Graham Hamilton在设计Java Swing时，曾经写过一篇文章专门谈这个问题：[Multithreaded toolkits: A failed dream?](https://community.oracle.com/hub/blogs/kgh/2004/10/19/multithreaded-toolkits-failed-dream)

> 最近有人提出一个问题，“我们是否应该让 Swing 库真正实现多线程？” 我个人觉得不应该，下面阐述理由。
>
> **无法实现的梦想（Failed Dream）**
>
> 借用 Vernor Vinge 的术语，在计算机科学中，某些想法是“无法实现的梦想”。这种想法初步看来很好，人们隔一段时间就会重新冒出这种想法，并为此花费很多时间。通常在研究阶段，事情进展顺利，有一些让人感兴趣的成果，差不多可以应用到生产规模上了。只是总有些问题解决不了，解决了这边的问题，那边又有问题冒出来。
>
> 在我看来，多线程的 GUI 工具包，就是这种无法实现的梦想。在多线程环境中，任意一个线程都可以去更新按钮(Button)、文本字段(Text Field)等 GUI 状态，这似乎是理所当然、直截了当的做法。任意线程去更新 GUI 状态，无非是加一些锁，又有什么难的呢？实现过程中可能会有一些错误，但我们可以修复这些错误，对吧？可惜事实证明没有这样简单。
>
> 多线程的 GUI 有种不可思议的趋势，会不断发生死锁或者竞争条件。我第一次知道这趋势，是在 80 年代初期，从 Xerox PARC 的 Cedar GUI 库中工作的那些人中听来的。这批人都十分聪明，也真正了解多线程编程。他们的 GUI 代码时不时就有死锁问题，这件事本身就很有趣。单独这事不能说明什么，或者只是特殊情况。
>
> 只是这些年来不断重复这一模式。人们最开始采用多线程，慢慢地，他们转换到了事件队列模型。“最好让事件线程做 GUI 的工作。"
>
> ...略
>
> **为什么这么难**
>
> 我们使用抽象来编写代码，很自然地会在每个抽象层中单独上锁。于是很不幸地，我们就遇到经典的锁定顺序噩梦：有两种不同类型的活动，按照相反的顺序，试图获取锁。因而死锁不可避免。
>
> ...略
>
> 来自：[Multithreaded toolkits: A failed dream?](https://community.oracle.com/hub/blogs/kgh/2004/10/19/multithreaded-toolkits-failed-dream)，原网页已经404，查看原文可以点击下面两个链接
>
> - [CSDN原文备份](https://blog.csdn.net/coderAndy/article/details/51761691)
> - [知乎翻译：多线程 GUI 工具包：无法实现的梦想？](https://zhuanlan.zhihu.com/p/44639688)

**简单来说，多线程操作一个UI，很容易导致，或者极其容易导致反向加锁和死锁问题**

我们通过用户级的代码去改变界面，如TextView.setText走的是个自顶向下的流程：

![image_android_component_handler_cnbolgs_from_top](D:\work\androidstudio\Blackboard\AOSP\src\main\java\com\android\aosp\frameworks\base\core\android\os\handler\blog\imgs\image_android_component_handler_cnbolgs_from_top.jpg)

*图片来源：[GUI为什么不设计为多线程](https://blog.csdn.net/liuqiaoyu080512/article/details/12895005)*

而系统底层发起的如键盘事件、点击事件走的是个自底向上的流程：

![image_android_component_handler_cnbolgs_from_bottom](D:\work\androidstudio\Blackboard\AOSP\src\main\java\com\android\aosp\frameworks\base\core\android\os\handler\blog\imgs\image_android_component_handler_cnbolgs_from_bottom.jpg)

*图片来源：[GUI为什么不设计为多线程](https://blog.csdn.net/liuqiaoyu080512/article/details/12895005)*

这样就麻烦了，**因为为了避免死锁，每个流程都要走一样的加锁顺序，而GUI中的这两个流程却是完全相反的，如果每一层都有一个锁的话加锁就是个难以完成的任务了，而如果每一层都共用一个锁的话，那就跟单线程没区别了。**

综上，目前主流的带有用户界面(GUI)的操作系统，除了DOS外，几乎都是使用UI单线程模型的方案，即**消息队列机制**

### 2、什么是消息队列机制？

一提到消息队列，自然而然就会想到生产者-消费者模型；在[《从Android源码角度谈设计模式（三）：行为型模式》](https://juejin.cn/post/7072263857015619621#heading-19)一文中我们已经介绍过了生产者-消费者模式，还没有看过的同学可以先看完再回来，这里来简单回顾一下：

在一个生产者-消费者模式中，通常会有三个角色

- **消息队列(single)**

  > 负责保存消息，提供存取消息的功能

- **生产者(multiple)**

  > 负责生产消息，塞到共享的消息队列中

- **消费者(multiple)**

  > 负责从共享消息队列中取出消息，执行消息对应的任务

![image_uml_design_pattern_behavioral_producer_consumer_queue](D:\work\androidstudio\Blackboard\AOSP\src\main\java\com\android\aosp\frameworks\base\core\android\os\handler\blog\imgs\image_uml_design_pattern_behavioral_producer_consumer_queue.jpg)

这三个角色中，因为生产者和消费者要从同一个消息队列取放消息，所以消息队列要求是唯一的，生产者和消费者的数量可以任意

我们回到Android系统，GUI框架多数使用单线程设计，那么就要求所有的UI操作都只能发生在一个线程当中，即：

**消费者线程只有一个，且消费者线程就是UI线程**

到了这里，关于消息队列机制应该有个清晰的概念了，我们来总结一下：

**Android、Swing、iOS等的GUI库都使用消息队列机制来处理绘制界面、事件响应等消息**

**在这种设计中，每个待处理的任务都被封装成一个消息添加到消息队列里**

**消息队列是线程安全的（消息队列自己通过加锁等机制保证消息不会在多线程竞争中丢失），任何线程都可以添加消息到这个队列中**

**但是，只有主线程（UI线程）从中取出消息，并执行消息的响应函数，这就保证了只有主线程才去执行这些操作**

小节完

### 3、Android中的消息队列：Handler

在前两小节中，我们分别介绍了GUI，主要是想尝试解释Handler的设计背景

在Android系统中，Handler就是这么一套提供在任意线程都可以发消息给UI线程的线程间通信工具

当然，除了发消息外，Android还为Handler增加了一些其他功能

我们这里先浅谈一下Android Handler的设计：

首先，Handler使用了

Handler使用了Java并发中的ThreadLocal来保证消息队列在线程中的唯一性，

2.1小节中我们知道了大多数GUI框架都是单线程设计，Android也不例外

GUI库都使用单线程消息队列机制来处理绘制界面、事件响应等消息，在这种设计中，每个待处理的任务都被封装成一个消息添加到消息队列中。消息队列是线程安全的（消息队列自己通过加锁等机制保证消息不会在多线程竞争中丢失），任何线程都可以添加消息到这个队列中，但是只有主线程（UI线程）从中取出消息，并执行消息的响应函数，这就保证了只有主线程才去执行这些操作。

既然是GUI都是单线程设计，那么想要在子线程更新UI就必须要通知主线程

## 三、如何自己实现一套Handler机制？

### 1、基于Object.wait()/notifiy()

### 2、基于DelayQueue

### 3、基于Linux epoll

ummm..

## 五、Handler详解

本章节聊一聊Handler机制除了实现生产者/消费者模式之外，还给我们提供了什么功能，其实也就是老生常谈的几样：

- 异步消息与同步屏障
- Handler Callback

## 六、总结

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
  > **1、队列空闲时等待以及唤醒方案用的是Java的Object.wait()/notfity()**
  >
  > > 调用MessageQueue的next方法获取消息，这时候检查队列有没有消息
  > >
  > > - 没有消息调用this.wait()无限期等待
  > > - 有消息但消息未到期调用this.wait()传入到期时间
  > >
  > > 调用MessageQueue.enqueueMessage()添加消息，消息加入队列后会调用this.notifity()唤醒next()方法，这里有个bug是没有判断添加的消息要什么时候执行，延迟消息也会唤醒
  >
  > **2、MessageQueue支持IdleHandler**
  >
  > > IdleHandler是MessageQueue的内部类，调用addIdleHandler(handler)方法将一个IdleHandler添加到mIdleHandlers集合中，消息队列空闲时才会执行
  >
  > **3、不支持异步消息和同步屏障消息**
  >
  > > 所有消息一视同仁，这也是早期Android设备体验不好的原因之一
  >
  > **4、Handler支持Callback回调**
  >
  > > 提到这个方法是因为它比较重要，先来看一下消息分发的先后顺序：
  > >
  > > *优先执行msg.callback(也就是runnable)，其次mCallback.handleMessage()，最后handleMessage()*
  > >
  > > *其中，调用mCallback.handleMessage()方法时会要求返回bool类型的值，这个值为true就不会向下分发给Handler的handleMessage()方法，为false会继续向下分发*
  > >
  > > 知道了分发逻辑之后我们就可以hook掉ActivityThread中的H类，传入callback拦截我们想要的信息，进而做组件化或监控方面的工作
  
- **[Android 2.3(API 9)](https://android.googlesource.com/platform/frameworks/base/+/refs/tags/android-2.3_r1/core/java/android/os)**

  > 2.3比较大的变化是增加了native层的Handler机制
  >
  > **1、MessageQueue支持处理native层消息**
  >
  > > MessageQueue通过mPtr变量保存NativeMessageQueue对象，从而使得MessageQueue成为Java层和Native层的枢纽，既能处理上层消息，也能处理native层消息
  > >
  > > 关于native层的Handler想要了解更多的同学看这里：
  > >
  > > - 袁辉辉老师的文章：[Android消息机制2-Handler(Native层)](http://gityuan.com/2015/12/27/handler-message-native/)
  > > - native的MessageQueue源码：[android-2.3_r1/core/jni/android_os_MessageQueue](https://android.googlesource.com/platform/frameworks/base/+/refs/tags/android-2.3_r1/core/jni/android_os_MessageQueue.cpp)
  >
  > **2、MessageQueue.next()方法中，空闲时等待方案从Object.wait()改为nativePollOnce()实现**
  >
  > > 由于加入native层Handler，队列空闲时不能只判断Java层的MessageQueue，MessageQueue将原先的this.wait()方法改成了调用native的nativePollOnce()方法，若大家都空闲，方法会阻塞到native的epoll_wait()方法中，等待唤醒
  >
  > **3、MessageQueue.enqueueMessage()方法中，唤醒方案从Object.notify()改为nativeWake()实现**
  >
  > > 参考上一小节，这里还有个小细节，2.3之前只要调用enqueueMessage()方法就会调用this.notify()唤醒线程，哪怕加入的这个消息是个延迟消息要求一万年后才执行，在2.3的enqueueMessage()方法中修复了这个问题

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
  > > **注意，该方法不但被@hide修饰，在代码注释也向开发者告知这是个危险方法，不建议使用，因为runWithScissors()方法有两个严重缺陷：**
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

至此，历代Android更新的，每个方法都进行了标注，标题也加入了超链接，读者可以点击，一起卷起来

难免会有疏漏，补充

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

写好一篇文章比理解一个知识点难许多，Handler本身并不难，难的是如何文章是，写给不懂的人看的

## 注意事项

### 1、Handler

### 2、Looper

### 3、MessageQueue

- 享元模式带来的问题

  > 在设计模式二提到了，Java是值传递，所以当你把享元对象传递给异步线程后，当异步线程开始执行时，这个消息可能在回收池里，也可能在回收池取出来给其他人用了，总之，在异步线程中的消息不是原来的消息了

### 4、Message







