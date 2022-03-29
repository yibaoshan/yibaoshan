### 

Android Handler机制是每个Android开发者成长路上，如何体现Handler机制在Android OS中的重要性呢？

Android系统2003年10月立项，2005年7月被Google收购

目前可追溯最早的Android版本是2009年9月15日发布的[Android 1.6(API 4)](https://android.googlesource.com/platform/frameworks/base/+/refs/tags/android-1.6_r1/core/java/android/os)，前3个版本虽然没找到源码，但我猜测从API1起Handler机制就已经存在了

Handler MessageQueue阻塞/唤醒自[Android 2.3(API 9)](https://android.googlesource.com/platform/frameworks/base/+/refs/tags/android-2.3_r1/core/java/android/os)开始，从object.wait()/notify()改为了epoll()

Android Handler发展历史：

- Android 1.6 (API 4) 

  > 能追溯到的最早Android源码版本，前3个版本网上没找到源码，个人猜测Handler机制在Android1.0开始就诞生了
  >
  > 有趣的是，早在Handler诞生之初，IdleHandle也随之孕育而生

- Android 2.3 (API 9)

  > 1、将MessageQueue空闲时挂起的实现方案从Object.wait()/notify()，更改为Linux的epoll

- [Android 4.0](https://android.googlesource.com/platform/frameworks/base/+/refs/tags/android-cts-4.0_r1)

> 1、Message增加flags属性和isInUse()方法，用于标识该消息是否已经消费过了，防止同一消息无限次提交

- [Android 4.1.1](https://android.googlesource.com/platform/frameworks/base/+/refs/tags/android-4.1.1_r1/core/java/android/os)

  > 1、Message支持异步消息，通过setAsynchronous()方法设置，是否为异步消息的标识保存在flags中
  >
  > 2、MessageQueue支持处理异步消息，enqueueMessage()方法和next()方法增加异步消息处理逻辑

- [Android 4.2 (API )](https://android.googlesource.com/platform/frameworks/base/+/refs/tags/android-4.2_r1/core/java/android/os)

  > 1、支持Handler创建异步消息，也就是新增Handler(boolean async)构造函数
  >
  > ```java
  >     public Handler(boolean async) {
  >         this(null, async);
  >     }
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