
> 前排提示，本文档系个人整理，难免会有疏漏，如有错误，欢迎在 [issues](https://github.com/yibaoshan/yibaoshan/issues) 留言联系我改正。

# 计算机基础

## 1、数据结构和算法

LeetCode Hot 100，每天 1~3 题。

https://leetcode.cn/studyplan/top-100-liked/

## 2、操作系统

基础版 OS 可以阅读 [MIT6.S081 XV6 操作系统专栏](https://www.zhihu.com/column/c_1883164478474000252)

## 3、编译

两个维度

- 一是了解 [C、Java 编译过程](docs/计算机基础-编译-C&Java.md) ，每个阶段都有哪些工具，是否有扩展的可能性。
    - 比如 C 语言的预处理、编译、汇编、链接，预处理阶段可以通过宏定义进行修改，编译阶段可以用自定义的编译器插件，链接阶段可以替换库文件。
    - 再比如 Java 的字节码增强工具 ASM 修改代码，在 CI/CD 中可能用于性能监控或代码注入。
- 二是了解 [Android APK 的构建流程](docs/计算机基础-编译-Android.md)，资源处理、代码编译、打包签名等步骤
    - 资源处理阶段使用aapt/aapt2，可以修改资源或生成额外的R.java；
    - 代码编译阶段，Java 转 dex 时，可以用Transform API（AGP）插入自定义任务，修改字节码

## 4、并发编程

并发安全问题的本质，是 **共享资源的竞争** 和 **操作的原子性**

1. [在硬件层面上](docs/计算机基础-并发-硬件支持.md)
   - 共享资源就是主存，多个执行单元（CPU 核心）对共享资源（主存）的并发访问，这点由 MESI 缓存一致性协议保证数据一致性。
   - 操作原子性方面，CPU 提供了原子操作指令和内存屏障指令。
2. [操作系统层面的支持](docs/计算机基础-并发-操作系统.md)
3. 语言层面，[C 的 POSIX 线程库](docs/计算机基础-并发-语言-C.md) 和 [Java 对并发的支持](docs/计算机基础-并发-语言-Java.md)

## 5、Java & JVM

- [Java 基础语法和特性](docs/计算机基础-编程语言-Java-基础语法和特性.md)
  - 面向对象基础，类和对象，继承、封装、多态，抽象类和接口，内部类和匿名类
  - Java 基本类型和包装类，基本数据类型，自动装箱和拆箱，String 和字符串池，常量池机制
  - 异常处理，异常体系，try-catch-finally，自定义异常
- [Java 集合框架](docs/计算机基础-编程语言-Java-集合框架.md)
  - Collection 体系，List（ArrayList、LinkedList），Set（HashSet、TreeSet），Queue（ArrayDeque、PriorityQueue）
  - Map 体系，HashMap 原理和实现，TreeMap 和红黑树，ConcurrentHashMap 实现
- [Java 反射和注解](docs/计算机基础-编程语言-Java-反射和注解.md)
  - 反射机制，Class 类，反射 API，动态代理
  - 注解，内置注解，自定义注解，注解处理器
- [Java IO/NIO](docs/计算机基础-编程语言-Java-IO&NIO.md)
  - 传统 I/O，字节流和字符流，缓冲流，序列化
  - NIO，Buffer 和 Channel，Selector，文件系统
- [JVM/Dalvik/ART](docs/计算机基础-编程语言-Java-JVM.md)
  - 内存模型，运行时数据区，对象创建过程，垃圾回收机制
  - 类加载机制，类加载过程，类加载器，双亲委派模型

# 架构设计

架构设计不是很好分类，我个人是按照 设计思想/原则（设计模式、SOLID）、基础开发模式（MVX、Clean），和高级框架技术（组件化、动态化、IoC）这三类来划分的

- [架构设计思想](docs/架构设计-架构设计思想.md)
  1. SOLID 原则
     - 单一职责原则、开闭原则、里氏替换原则、接口隔离原则、依赖倒置原则
     - 尤其是依赖倒置（DIP）与接口隔离（ISP）在模块化中的应用
  2. 设计模式，常用的几个
     - 创建型：工厂模式（组件化中模块初始化）、单例模式（需注意线程安全）。
     - 行为型：观察者模式（LiveData/RxJava）、策略模式（动态替换算法，如图片加载策略）。
     - 结构型：代理模式（AOP切面）、装饰器模式（扩展功能，如OkHttp Interceptor）。
- [基础开发模式](docs/架构设计-基础开发模式.md)
  1. MVC → MVP → MVVM → MVI
     - 各架构模式的优缺点及适用场景，如 MVP 的 View 接口解耦、MVVM 的 DataBinding/ViewModel 双向绑定
     - 对比 Jetpack 的 ViewModel 与纯 MVVM 实现，注意 LiveData 与 RxJava 在响应式中的差异。
  2. Clean Architecture
     - 分层设计（Entities → Use Cases → Interface Adapters → Frameworks），依赖规则（内层不依赖外层）
     - 中心思想，分层、依赖倒置原则、边界划分、数据流转
     - 结合 Dagger/Hilt 实现依赖注入，业务逻辑与框架解耦
- [高级架构实践](docs/架构设计-高级架构实践.md)
  1. 组件化
     - 解决模块间通信（ARouter原理）、资源冲突（资源命名规范）、独立调试（Gradle动态切换Application/Library）。
     - 架构图：分层设计（App壳工程 → 业务组件 → 功能层 → 基础库）。
  2. 动态化方案
     - 热修复（Tinker/Sophix）、插件化（RePlugin/Shadow），重点理解类加载机制与资源 Hook。
     - 动态换肤方案。
  3. 依赖注入（IoC）
     - Dagger2/Hilt的核心：@Component、@Module、@Inject，解决依赖图的构建与生命周期管理。
  4. AOP 切面编程
     - 使用 AspectJ 或 ASM 实现无侵入埋点、性能监控（如方法耗时统计）。
  5. RxJava/Coroutine Flow
     - 对比观察者模式与协程的挂起机制，掌握背压处理（Flow的buffer()/conflate()）。
     - 应用场景：多数据源合并、UI事件防抖（如搜索框输入）

# Android 系统与主流框架技术

# ⼯程化和团队协作

# 稳定性治理和性能优化

