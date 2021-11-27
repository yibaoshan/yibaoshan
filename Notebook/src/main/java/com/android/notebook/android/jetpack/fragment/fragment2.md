
原理篇，带着问题看源码，没有目的看源码只会像无头苍蝇一样乱撞
笔者个人阅读习惯是想到某个问题，不知道某个功能如何实现，或者想要看某个流程等才会去阅读源码。
带着目的去看源码，而不是跟着代码调用一层层跟进，而是适可而止，得到想要的答案即可
这样形成一个个锚点，以点成线，渐渐的知识体系就丰满起来了

> 这篇文章主要向大家介绍Android fragment 标签加载过程分析,主要内容包括基础应用、实用技巧、原理机制等方面，希望对大家有所帮助。
> 笔者环境：macOS Mojave10.14.6;android-11.0.0_r4;Redmi Note 7
>
>
1. Fragment的本质是什么
2. Fragment的生命周期是由谁来管理的，如何保存状态
3. XML中<fragment>是如何被加载的
4. FragmentManager和FragmentTransaction分别扮演什么角色
5. Fragment回退栈是如何实现的
6. Fragment设计者使用了哪些设计模式
7. 一个Activity加入了Fragment之后，事件分发的流程会怎样流转
8. View是如何被添加到window上的，或者说FM是如何帮助Fragment显示到window上的

> Fragment核心的类有：
Fragment：Fragment的基类，任何创建的Fragment都需要继承该类。
FragmentManager：管理和维护Fragment。他是抽象类，具体的实现类是FragmentManagerImpl。
FragmentTransaction：对Fragment的添加、删除等操作都需要通过事务方式进行。他是抽象类，具体的实现类是BackStackRecord。
Nested Fragment（Fragment内部嵌套Fragment的能力）是Android 4.2提出的，support-fragment库可以兼容到1.6。  
> 通过getChildFragmentManager()能够获得管理子Fragment的FragmentManager，在子Fragment中可以通过getParentFragment()获得父Fragment。