
今天换个姿势来聊 binder

- 既然 binder 是驱动层提供的功能，首先我们先来看，如果我们写内核代码，应该怎么使用 binder
- 然后，如果我们使用 c/C++ 开发，应该怎么使用 binder
- 最后，才是 Java 开发，有哪些类，分别提供了哪些功能？
- binder 和 epoll 是同级别的，都是内核提供的功能
- 大量介绍 handler 和 binder 的文章
- binder 复杂的原因之一是跨层太多了，
- 我们要知道，Google 引进 binder 到 Android ，是为了在保证性能的同时，最大程度的方便开发者。使用匿名内存
- 第一步，我们先来看一看 binder.c 的 include 列表，就能大致猜到 binder 的实现原理


### 驱动设计

有缘千里来相会

### framework 设计


### java 设计


### service_manager 启动流程

### app 启动流程，建立通信
