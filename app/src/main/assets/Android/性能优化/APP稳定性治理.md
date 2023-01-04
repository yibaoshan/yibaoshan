
目前行业内崩溃率要低于 0.5%，负责的项目都是基于 bugly 平台来监控

## Java Crash

## OOM

OutOfMemoryError 内存溢出是一类难以解决的 Crash

上报的堆栈信息，用户日志等都没有太多的参考价值

因为内存已经在其他位置增加到内存溢出的临界点，而上报的位置很可能不存在内存问题

而引起内存溢出主要的原因有：内存泄漏，引用大对象，内存抖动，线程使用不合理等。

## 内存泄漏

## 三方SDK崩溃

如果因为三方库的代码错误引发的奔溃，而我们又没有源码的情况下

那就只有选择 hook 一条路了，hook 分为两种，字节码 hook 和运行时 hook

通常情况下，我们会选择字节码 hook 的方案，框架的话可以选择：

- 饿了么的 lancet
- 滴滴的 booster

## 参考资料

- [货拉拉Android稳定性治理](https://juejin.cn/post/7100743641953468452)
- [美团外卖Android Crash治理之路](https://juejin.cn/post/6844903620920492046)
- [得物App Android Crash治理演进](https://blog.csdn.net/SmartCodeTech/article/details/119958923)
- [Android 无所不能的 hook，让应用不再崩溃](https://juejin.cn/post/7034178205728636941)