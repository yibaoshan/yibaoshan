Android组件：Handler机制(一)

## Overview

每个Android开发者都或多或少的了解过Android Handler机制，为了不浪费大家时间，在文章开始之前，我认为有必要说明一下本文的目标受众

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

## 二、Handler设计背景

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

关于GUI为什么要设计成单线程这个问题，前Sun的副总裁Graham Hamilton在设计Java Swing时，曾经写过一篇文章专门聊过：[Multithreaded toolkits: A failed dream?](https://community.oracle.com/hub/blogs/kgh/2004/10/19/multithreaded-toolkits-failed-dream)

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

![image_android_component_handler_cnbolgs_from_top](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/AOSP/src/main/java/com/android/aosp/frameworks/base/core/android/os/handler/blog/imgs/image_android_component_handler_cnbolgs_from_top.jpg)

*图片来源：[GUI为什么不设计为多线程](https://blog.csdn.net/liuqiaoyu080512/article/details/12895005)*

而系统底层发起的如键盘事件、点击事件走的是个自底向上的流程：

![image_android_component_handler_cnbolgs_from_bottom](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/AOSP/src/main/java/com/android/aosp/frameworks/base/core/android/os/handler/blog/imgs/image_android_component_handler_cnbolgs_from_bottom.jpg)

*图片来源：[GUI为什么不设计为多线程](https://blog.csdn.net/liuqiaoyu080512/article/details/12895005)*

这样就麻烦了，**因为为了避免死锁，每个流程都要走一样的加锁顺序，而GUI中的这两个流程却是完全相反的，如果每一层都有一个锁的话加锁就是个难以完成的任务了，而如果每一层都共用一个锁的话，那就跟单线程没区别了。**

综上，目前主流的带有用户界面(GUI)的操作系统，除了DOS外，几乎都是使用UI单线程模型的方案，即**消息队列机制**

### 2、什么是消息队列机制？

提到消息队列，自然而然就会想到生产者-消费者模型；在[《从Android源码角度谈设计模式（三）：行为型模式》](https://juejin.cn/post/7072263857015619621#heading-16)一文中我们已经介绍过了生产者-消费者模式，还没有看过的同学可以先看完再回来，这里来简单回顾一下：

在一个生产者-消费者模式中，通常会有三个角色

- **消息队列(single)**

  > 负责保存消息，提供存取消息的功能

- **生产者(multiple)**

  > 负责生产消息，塞到共享的消息队列中

- **消费者(multiple)**

  > 负责从共享消息队列中取出消息，执行消息对应的任务

![image_uml_design_pattern_behavioral_producer_consumer_queue](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/AOSP/src/main/java/com/android/aosp/frameworks/base/core/android/os/handler/blog/imgs/image_uml_design_pattern_behavioral_producer_consumer_queue.jpg)

这三个角色中，因为生产者和消费者要从同一个消息队列取放消息，所以消息队列的数量要求是唯一的，生产者和消费者的数量可以任意

我们回到Android系统，GUI框架多数使用单线程设计，那么就要求所有的UI操作都只能发生在一个线程当中，即：

**消费者线程只有一个，且消费者线程就是UI线程**

到了这里，关于消息队列机制应该有个清晰的概念了，我们来总结一下：

**Android、Swing、iOS等的GUI库都使用消息队列机制来处理绘制界面、事件响应等消息**

**在这种设计中，每个待处理的任务都被封装成一个消息添加到消息队列里**

**消息队列是线程安全的（消息队列自己通过加锁等机制保证消息不会在多线程竞争中丢失），任何线程都可以添加消息到这个队列中**

**但是，只有主线程（UI线程）从中取出消息，并执行消息的响应函数，这就保证了只有主线程才去执行这些操作**

小节完

### 3、Android中的消息队列：Handler

前两小节我们介绍了Android系统中为什么只能在UI线程更新界面的原因，最终发现不只是Android系统，其他大部分有GUI框架的操作系统都是采用单线程消息队列机制的设计。既然都是单线程设计，那么想要在子线程更新UI就必须要通知主线程

**在Android系统中，Handler就是这么一套提供在任意线程都可以发消息给UI线程的线程间通信工具**

![image_uml_design_pattern_behavioral_producer_consumer_handler](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/AOSP/src/main/java/com/android/aosp/frameworks/base/core/android/os/handler/blog/imgs/image_uml_design_pattern_behavioral_producer_consumer_handler.jpg)

从图中可以看到，Handler相当于消息的生产者，每个Handler都持有共享MessageQueue的引用

**当调用Handler.sendMessage()方法发送消息时，Handler就会把消息保存到共享消息队列中**

和普通消息队列机制不同的时，Android中的MessageQueue大小没有阈值上限，理论上可以一直发消息到队列，把内存撑爆~

**在Handler机制中，消息的消费者是Looper，严谨点是调用Looper.loop()所在的线程，因为Looper本质上只是封装对消息队列的操作，Looper.loop()方法负责取出共享消息队列里面的消息，然后交由Handler去执行**

这里又和普通消息队列机制不同的点时，通常消息的消费者会去执行消息的响应函数；但在Android Handler机制中，消费者本身并不执行消息任务，而是将消息取出后，再重新分发给消息的发送者执行，也就是Handler，所以我们平时才说

**在整个Handler的机制中，Handler首先是消息的生产者，其次才是消息的执行者**

除了发消息外，Android还为Handler增加了一些其他功能，比如子线程提交同步任务、异步消息和同步屏障等等

本小节的目的是了解Handler的设计背景，关于Handler其他功能这里就不展开介绍，在Handler系列的下一篇文章中会详细剖析Handler内部源码

小结完

## 三、如何自己实现一套Handler机制？

在上一章节中，我们介绍了Handler的设计背景，本章节将会尝试自己实现一套简单的Handler机制

我们假设读者已经有一定的开发经验，并且使用过Handler，那么接下来就是枯燥的代码时间

### 1、Handler成员介绍

在开撸之前，按照惯例，我们先来介绍一下组成Handler机制的几位成员，以及它们常用的方法

#### **1.1 Handler类**

Handler类是应用程序开发的入口，在消息队列机制中，扮演着生产者的角色，同时还肩负着消息执行者的重担，常用的方法有：

| 方法名称                     | 说明                                                         |
| ---------------------------- | ------------------------------------------------------------ |
| sendMessage()系列            | 发送普通消息、延迟消息，最终调用queue.enqueueMessage()方法将消息存入消息队列 |
| post()系列                   | 提交普通/延迟Runnable，随后封装成Message，调用sendMessage()存入消息队列 |
| dispatchMessage(Message msg) | 分发消息，优先执行msg.callback(也就是runnable)，其次mCallback.handleMessage()，最后handleMessage() |

#### **1.2 Looper类**

Looper在消息队列机制中扮演消费者的角色，内部持有共享的消息队列，其本质是封装对消息队列的操作，常用的方法只有两个：

| 方法名称  | 说明                                                       |
| --------- | ---------------------------------------------------------- |
| prepare() | 创建消息队列                                               |
| loop()    | 遍历消息队列，不停地从消息队列中取消息，消息队列为空则等待 |

#### **1.3 MessageQueue类**

实际的共享消息队列，提供保存和取出消息的功能，底层由链表实现，常用方法就一个：

| 方法名称 | 说明                                                         |
| -------- | ------------------------------------------------------------ |
| next()   | 获取消息，三种情况 1. 有消息，且消息到期可以执行，返回消息 2. 有消息，消息未到期，进入限时等待状态 3. 没有消息，进入无限期等待状态，直到被唤醒 |

#### **1.4 Message类**

消息的承载类，使用享元模式设计，根据API不同缓冲池大小也不同，API 4时缓冲池大小为10，常用方法：

| 方法名称     | 说明             |
| ------------ | ---------------- |
| obtain()系列 | 获取一个消息实例 |
| recycle()    | 回收消息实例     |

#### **1.5 小结**

至此，Handler的几个主要成员类都介绍完了，有同学可能已经发现了，成员介绍中没有包含ThreadLocal类？

我个人认为ThreadLocal是属于Java并发中的内容，Handler只是借用了ThreadLocal来保证MessageQueue在当前线程线程的唯一性，就算不适用ThreadLocal对整个Handler机制也没啥影响~

本章节的目的是实现一套简单的Handler机制，所以Handler其他功能诸如异步消息、IdleHandler等不再介绍了，其实当对整个Handler机制了然于胸后，再回来看这些知识点就会觉得很简单了

### 2、基于Object.wait()/notifiy()实现Handler机制

3.1小节介绍了Handler的几个成员类，以及成员类中常用的方法，本章节将会用Java同步机制来实现Handler

话不多说，直接开撸

####  **2.1 手写消息队列机制**

前面的章节我们已经了解过消息队列机制的概念，这里直接来看代码

**在生产者线程中，我们写了个死循环一直发送消息，来模拟用户操作；每次发完消息后，唤醒可能在等待状态的消费者线程，然后将自己个儿置入限时等待状态**

![image_android_component_handler_code_object_v1_producer](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/AOSP/src/main/java/com/android/aosp/frameworks/base/core/android/os/handler/blog/imgs/image_android_component_handler_code_object_v1_producer.png)

**在消费者线程中，同样是死循环，不停的轮询消息队列，有消息就处理，没消息就将自己置入无限期等待的状态，直到被唤醒**

![image_android_component_handler_code_object_v1_consumer](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/AOSP/src/main/java/com/android/aosp/frameworks/base/core/android/os/handler/blog/imgs/image_android_component_handler_code_object_v1_consumer.png)

**消息队列使用的是Java集合工具包，来看最终的测试代码：**

![image_android_component_handler_code_object_v1_test](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/AOSP/src/main/java/com/android/aosp/frameworks/base/core/android/os/handler/blog/imgs/image_android_component_handler_code_object_v1_test.png)

**打印结果：**

![image_android_component_handler_code_object_v1_result](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/AOSP/src/main/java/com/android/aosp/frameworks/base/core/android/os/handler/blog/imgs/image_android_component_handler_code_object_v1_result.png)

两个boss张三和李四时不时发指令给下属，两个下属小明和小红在不停的轮询等待上司的指令，一个简单的消息队列机制就完成了；在不考虑延迟消息的情况下，加上测试代码，整个消息队列机制不到100行代码就搞定了，还是比较简单的



#### **2.2 手写Handler机制**

用Object.wait()/notifiy()来实现Handler机制很简单，只需要在上面的消息队列的基础上，稍微改动一下就能完成：

**同学们注意，我要开始变形了！！！**

**首先，我们把消费者线程中的逻辑挪到Looper当中，把轮询的任务放到loop()方法中；若取到了消息，模拟Android Handler机制，将消息分发给消息所属的生产者者(Handler)去执行**

![image_android_component_handler_code_object_v2_looper](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/AOSP/src/main/java/com/android/aosp/frameworks/base/core/android/os/handler/blog/imgs/image_android_component_handler_code_object_v2_looper.png)

**接着，将消费者线程中获取消息的逻辑抽离出来，放到MessageQueue的next()方法中**

**和上面的消费者线程比较，这里加了一条逻辑：当发送的消息时延迟消息时，判断消息是否到期，到期返回给Looper去分发(这里偷懒使用了Java集合包中的优先级队列PriorityQueue，来保证时间最小的消息排在最前面)**

![image_android_component_handler_code_object_v2_message_queue](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/AOSP/src/main/java/com/android/aosp/frameworks/base/core/android/os/handler/blog/imgs/image_android_component_handler_code_object_v2_message_queue.png)

**然后，把生产者线程中直接将消息存入消息队列的操作的逻辑也抽出来，放到Handler的sendMessage()方法中**

![image_android_component_handler_code_object_v2_handler](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/AOSP/src/main/java/com/android/aosp/frameworks/base/core/android/os/handler/blog/imgs/image_android_component_handler_code_object_v2_handler.png)

**最后，看一眼Message类的设计，加了个Handle类型的成员变量target**

![image_android_component_handler_code_object_v2_message](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/AOSP/src/main/java/com/android/aosp/frameworks/base/core/android/os/handler/blog/imgs/image_android_component_handler_code_object_v2_message.png)

**好！大功告成，一起看看测试代码：**

**在main()方法中创建了handler1和handler2，模拟用户在主线程申请Handler的场景；随后开启俩子线程，让子线程代码使用刚刚创建的Handler发送消息，这样，子线程使用该Handler发送的消息就会添加到主线程的消息队列，等待主线程的Looper去处理**

![image_android_component_handler_code_object_v2_test](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/AOSP/src/main/java/com/android/aosp/frameworks/base/core/android/os/handler/blog/imgs/image_android_component_handler_code_object_v2_test.png)

**打印结果：**

![image_android_component_handler_code_object_v2_result](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/AOSP/src/main/java/com/android/aosp/frameworks/base/core/android/os/handler/blog/imgs/image_android_component_handler_code_object_v2_result.png)

看，如果是普通任务，loop()里面就直接分发掉了，延迟任务因为使用的是PriorityQueue的缘故，会排到最后才放出来

到这里一个简单的Handler机制就完成了，没看懂的同学请在评论区扣1

关于此小节设计到的代码在这里，把代码转成图片是因为源码太长了我知道你们也懒得看~

小节完

## 四、Handler机制详解

此小节设计之初的想法是要详细的剖析Handler机的内部源码，在写完了二、三章节后回头看才发现，除了同步屏障与异步消息、IdleHandler、Callback等没有讲以外，整个Handler机制好像已经讲的差不多了

于是便将本小节换个目标，聊一聊Android Handler除了实现消息队列机制外，还给我们提供了什么功能，它们是如何实现的，以及在使用Handler过程中我们有哪些需要特别注意的地方

### 1、Handler除了收发消息之外的功能

#### **1.1 IdleHandler**

IdleHandler是在Handler机制诞生之初就存在的机制，其存在的意义在于，提交一个不重要的任务单独存放在MessageQueu中的mIdleHandlers变量中，当消息队列空闲时会执行此任务

```java
    /**
     * Callback interface for discovering when a thread is going to block
     * waiting for more messages.
     */
    public static interface IdleHandler {
        /**
         * Called when the message queue has run out of messages and will now
         * wait for more.  Return true to keep your idle handler active, false
         * to have it removed.  This may be called if there are still messages
         * pending in the queue, but they are all scheduled to be dispatched
         * after the current time.
         */
        boolean queueIdle();
    }
```

**IdleHandler要求返回bool类型的值，返回false表示执行完该任务后会把它从集合中删除，返回true表示该任务可以重复执行**

我们来看一下Android 1.6版本中对IdleHandler的处理逻辑：

#### **1.2 异步消息与同步屏障**

#### **1.3 Handler Callback机制**



### 2、使用Handler的注意事项

#### **2.1 内存泄漏**

#### **2.2 享元模式的坑**

在设计模式二提到了，Java是值传递，所以当你把享元对象传递给异步线程后，当异步线程开始执行时，这个消息可能在回收池里，也可能在回收池取出来给其他人用了，总之，在异步线程中的消息不是原来的消息了

### 3、Handler有哪些妙用

#### **3.1 永不崩溃的APP**

#### **3.2 ANR监控**

#### **3.3 组件化**

内存泄漏

享元模式的坑

### 

## 五、总结

在文章的最后，我想先来来总结一下Handler从Android 1.6 (API 3) 一直到 Android 12 (API 31)的演变过程：

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
  > **1、Message支持设置为异步消息，@hide修饰**
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
  > > **注意！！！该方法不但被@hide修饰，在代码注释也向开发者告知这是个危险方法，不建议使用，因为runWithScissors()方法有两个严重缺陷：**
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



至此，Handler历代更新的内容都已梳理完成，每个方法都进行了标注，标题中也加入了超链接方便读者点击查看；ps：个人整理难免会有疏漏，欢迎在留言区补充

总结，本文将Handler机制拆成了三个部分

**第一部分是介绍Handler诞生的背景，Android为什么要设计出Handler**

**第二部分主要讲如何手写一套Handler机制，使用的是Java同步方法Object.wait()/notifiy()**

**第三部分介绍的是Handler除了实现消息队列外，还提供了哪些功能？以及开发中使用Handler有哪些需要注意的地方**

希望每位同学看完本篇文章都能够有所收获

全文完



## 六、参考资料

- CSDN-liuqiaoyu080512：[GUI为什么不设计为多线程](https://blog.csdn.net/liuqiaoyu080512/article/details/12895005)







