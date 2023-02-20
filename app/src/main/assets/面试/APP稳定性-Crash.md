
- crash 预防与治理
- crash 监控告警
- crash 排查定位与修复

APP 的稳定性治理包括 卡顿、ANR 、Crash（Java / Native）、内存溢出（OOM）等几个方面

本篇探讨的是 Java / Native 的 Crash 问题，Crash 率是衡量 APP 好坏的一个重要指标

就 Crash 率而言，目前行业内崩溃率的底线是千分位，比如 0.5% 是个坎

对于普通的异常预防，除了一定的开发规范，和定期 review 外

还需要有监控方案，发现问题及时上报，根据 bug 的重要程度来选择解决方案。有降级方案就用降级，需要强制升级的就发版

另外，还需要搭建一套，在测试阶段就能发现异常的预防机制，比如后台下发了大图片、 intent 跳转携带大参数等等

## 异常的捕获与治理

我负责的项目体量都不大，稳定性监控方案都是使用腾讯免费的 bugly，项目中一些自定义的异常日志也都会上报到 bugly 统一处理，比如 Gson 解析异常、控件滑动显示异常等等

我们的热修复使用的也是腾讯的 tinker （虽然接入比较麻烦），主要因为：

- 当时阿里的 Hotfix 不支持 kotlin 的协程，但是我们的项目大量使用了 协程 执行异步任务和网络请求
- tinker 的后台和 bugly 是同一个，减少了使用的成本

另外，tinker 还支持监控告警，自定义告警规则，绑定微信后就可以接受消息了

### 三方库引发的崩溃

如果因为三方库的代码错误引发的奔溃，而我们又没有源码的情况下

那就只有选择 hook 一条路了，hook 分为两种，字节码 hook 和运行时 hook

对于三方库，通常情况下，我们会选择字节码 hook 的方案，框架的话可以选择：

- 饿了么的 lancet
- 滴滴的 booster

### 三方库合并

对于同一个依赖库，gradle 打包合并时默认选择最高版本，或者强制指定的版本，其他版本会被废弃

这就会导致一个问题，module A 引用了 1.0 版本，B 引用了 2.0 版本，最终打包的是 2.0

如果 module A 调用了 1.0 的某个类/方法，在 2.0 被剔除时，打包阶段可是检测不出来的

那么在运行时会发生 NoClassDefFoundError、NoSuchFieldError、NoSuchMethodError 等异常

对于这种场景，我们可以

1. 接入了依赖检查插件 Defensor，编译时检查每个文件里引用的类、字段、方法等是否存在
2. 对于小项目，统一管理三方库，不允许模块单独引用库

目前我的项目都是通过抽出 OpenLib 模块，来管理所有的三方库，其他 module 引用 OpenLib

### 资源重复检查

对于一些资源文件，如layout、drawable等如果同名则下层会被上层覆盖，这时layout里view的id发生了变化就可能导致空指针的问题

我们参考了美团的方案

自定义 Gradle 插件通过 hook MergeResource这个Task，拿到所有library和主库的资源文件

如果检查到重复则会中断编译过程，输出重复的资源名及对应的library name

同时避免有些资源因为样式等原因确实需要覆盖，因此我们设置了白名单。

同时在这个过程中我们也拿到了所有的的图片资源，可以顺手做图片大小的本地监控

## 常见异常

### NullPointerException

空指针异常可能是最常见的异常，不过我们项目使用 kotlin 开发，加之在各个组件的生命周期都会有解除注册

比如，我们在 base 取消了协程的网络请求（其实不取消也不会导致空指针）

我们遇到的问题反而是，因为没有空指针异常导致的业务不可用

比如，后端没有按照规定下发数据，我们在解析的时候检测到异常默认为空。那么有可能新页面都不可用，因为请求的接口没数据

此时，反而不好排查页面空白的原因。目前的方案是把 Gson 解析遇到的异常，当做错误上报到 bugly

### IndexOutOfBoundsException

数组越界也是比较常见的异常，比如操作 EditText 的光标的时候

## Activity

### ActivityNotFoundException

- 隐式 startActivity，可能因为目标 action 改动，导致找不到 Activity
- 用户 Root 后删除系统类，比如直接把相册删除了，也会导致找不到类
- js 接口调用的页面，可能因为业务变化导致页面不在了

> 解决方案：跳转页面由全局路由负责，路由中加入 try catch，对于三方库，必要时 hook startActivity()

### ClassCastException

- intent 获取参数时，对方未按要求传参，此时可能会发生类型转换异常

> 解决方案：基本类型尽可能使用 string 传递，然后转换时做检查。引用类型参数一定要 try catch

### 其他

- intent 携带大数据，1M - 8k ，oneway 方式需要再除以 2
  - 注意，此时 system_server 进程可能直接 kill 我们进程，这种情况下连错误日志都不会有

> 大数据检测只能在开发阶段预防，我们可以 hook IActivityTaskManager ，再通过动态代理拦截并检测参数的大小
>
> 1. 如果传递数据超过300k，我们可以在控制台打印error或者弹出toast提醒，又或者上报到服务端产生一个报警；
>
> 2. 如果在DEBUG状态，传递超过512k，则我们可以直接崩溃，注意这个崩溃我们是可以捕获到的，也就是崩溃后台可以看到日志；

- GC 超时：多个对象重写 finalize() 方法却没有在 10s 内完成，FinalizerDaemon 线程抛出 finalizerTimedOut 异常


## 组件

### EditText

- Android 10 开启自动填充账号和密码，导致的崩溃
- 设置光标位置下标越界，比如打开修改用户信息页面，需要把用户名的光标挪到最后，不下心越界
- 设置最大购买数量，比如99件，替换/删除多余字符时发生异常

> 解决方案，关闭自动填充，国内应用用不上。修改光标代码 try catch ，宁愿不生效也不能崩溃

## 线程治理（资源超限 / 死锁）

在 Android 中

- 如果主线程发生死锁，那么通常会以 ANR 结束 app 的生命周期
- 如果是两个子线程，那么就会白白浪费 cpu。就像一颗“肿瘤”，永远藏在 app 中

### 死锁条件

在业务开发中，有两把锁并且有两个及以上的线程嵌套使用，这就形成了循环依赖

运行过程中有可能会导致死锁发生，如下：

```
public fun main() {
    val o1,o2;
    thread {
        synchronized(o1) sleep(10);
        synchronized(o2) print("t1 take o2")
    }
    thread {
        synchronized(o2) sleep(10);
        synchronized(o1) print("t2 take o1")
    }
}
```

### 检测死锁

我们知道，在使用 synchronized 做并发同步时

如果某个线程多次获取不到锁，线程就会进入悲观锁状态，然后就会尝试进入阻塞状态，避免进一步的 cpu 资源消耗

那我们可以通过遍历所有线程，查看是否处于 block 状态，来进行死锁判断的第一步

如果有两个线程长期处于 block 阻塞的状态，可以猜测这两条线程就有可能产生死锁（只是有可能）

```
val threads = Thread.currentThread().threadGroup.toArray;
threads.forEach {
    if(it?.isAlive == true && it.state == Thread.State.BLOCKED){
       进入死锁判断
    }
}
```

如果判断可能发生了死锁，那么下一步，就是要检测当前线程所持有的锁

如果确定两个线程同时持有对方所需要的锁，那一定要有一方先释放资源才能解除死锁

### 解决死锁

Java 层没有相关 api 可以让我们知道：当前线程想要请求的锁，所以只能在 native 层想想办法

我们可以通过 Thread#GetContendedMonitor() 函数查看当前线程持有的“锁”，然后检测 Java 传入的线程中，有没有发生死锁

具体操作可以查看参考资料列表中《死锁监控与其背后的小知识》这篇文章

### 线程资源超限

线程数超限是一种典型的 OOM 异常

```
java.lang.OutOfMemoryError: pthread_create (1040KB stack) failed: Try again
```

即 proc/pid/status 中记录的线程数，超过了内核限制

每个设备可使用的最大线程数量都不一样，我们可以使用 /proc/sys/kernel/threads-max 查看当前系统规定的最大线程数

我手里的 Pixel 3 最大线程数为 26155，华为某些机型控制在 500 左右

线程数超限的解决方案比较简单：使用线程池来规范线程的使用

对于三方库，像 Glide 之类的都支持配置线程池，不支持自定义配置的，我们可以 hook 字节码来收敛线程的创建

## 参考资料

- [货拉拉Android稳定性治理](https://juejin.cn/post/7100743641953468452)
- [美团外卖Android Crash治理之路](https://juejin.cn/post/6844903620920492046)
- [得物App Android Crash治理演进](https://blog.csdn.net/SmartCodeTech/article/details/119958923)
- [Android 无所不能的 hook，让应用不再崩溃](https://juejin.cn/post/7034178205728636941)
- [ASM 字节码插桩 ：线程治理](https://juejin.cn/post/7165499628304449567)
- [不可思议的OOM](https://www.jianshu.com/p/e574f0ffdb42)
- [Android性能优化 - 死锁监控与其背后的小知识](https://juejin.cn/post/7159784805293359141)
- [黑科技！让Native Crash 与ANR无处发泄！- Pika](https://juejin.cn/post/7159784805293359141)
- [Android 平台 Native Crash 问题分析与定位](https://juejin.cn/post/7124689738811834382)