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

## 二、生产者/消费者模式介绍

## 三、基于生产者/消费者模式手写Handler

## 四、Handler还提供了什么功能？

本章节聊一聊Handler机制除了实现生产者/消费者模式之外，还给我们提供了什么功能，其实也就是老生常谈的几样：

- 异步消息与同步屏障
- Handler Callback

## 五、总结

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

目前可追溯最早的Android版本是2009年9月15日发布的[Android 1.6(API 4)](https://android.googlesource.com/platform/frameworks/base/+/refs/tags/android-1.6_r1/core/java/android/os)，前3个版本虽然没找到源码，但我猜测从API1起Handler机制就已经存在了

Handler MessageQueue阻塞/唤醒自[Android 2.3(API 9)](https://android.googlesource.com/platform/frameworks/base/+/refs/tags/android-2.3_r1/core/java/android/os)开始，从object.wait()/notify()改为了epoll()

Android Handler发展历史：

- [Android 1.6(API 4)](https://android.googlesource.com/platform/frameworks/base/+/refs/tags/android-1.6_r1/core/java/android/os)

  > Android 1.6是目前网上能找到完整代码的最早版本，在这个版本中Handler，个人猜测Handler机制在Android1.0开始就诞生了
  >
  > 有趣的是，早在Handler诞生之初，IdleHandle也随之孕育而生

- [Android 2.3(API 9)](https://android.googlesource.com/platform/frameworks/base/+/refs/tags/android-2.3_r1/core/java/android/os)

  > 1、MessageQueue空闲时挂起的实现方案从Object.wait()/notify()，更改为Linux的epoll

- [Android 4.0](https://android.googlesource.com/platform/frameworks/base/+/refs/tags/android-cts-4.0_r1)

> 1、Message增加flags属性和isInUse()方法，用于标识该消息是否已经消费过了，防止同一消息无限次提交

- [Android 4.1.1](https://android.googlesource.com/platform/frameworks/base/+/refs/tags/android-4.1.1_r1/core/java/android/os)

  > 1、Message支持异步消息，通过setAsynchronous()方法设置，是否为异步消息的标识保存在flags中
  >
  > 2、MessageQueue支持处理异步消息，enqueueMessage()方法和next()方法增加异步消息处理逻辑

- [Android 4.2 (API )](https://android.googlesource.com/platform/frameworks/base/+/refs/tags/android-4.2_r1/core/java/android/os)

  > 1、支持Handler创建异步消息，也就是新增Handler(boolean async)构造函数，使用该Handler发送的消息均为异步消息
  >
  > ```java
  >  public Handler(boolean async) {
  >      this(null, async);
  >  }
  > ```

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