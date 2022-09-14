Android组件系列：再谈Handler机制（Native层）

android - what is message queue native poll once in android?

https://stackoverflow.com/questions/38818642/android-what-is-message-queue-native-poll-once-in-android

之前已经写过一篇关于 Handler 机制的文章，从应用开发工程师的角度出发，详细介绍了 Handler 机制的设计背景、如何自己如何实现一套 Handler 机制、Handler 使用注意事项，以及

在输出几篇图形系列的文章后，我意识到 Native 层的活动同样重要，诸如 nativePollOnce() 阻塞调用、nativeWake() 唤醒队列全都发生在 Native 层

所以，只有加上 Native 层的逻辑才算是完整的 Handler 机制

## Java层

### Handler 创建流程

### 消息循环与阻塞

### 消息发送与唤醒

我们也不去纠结另外几个重载方法各个形参的含义，只需要了解当调用 sendMessage() 方法后，最终是由 en

### 消息分发处理

四句话总结消息分发逻辑


## 潜入 Native



主要介绍了阻塞实现的原理