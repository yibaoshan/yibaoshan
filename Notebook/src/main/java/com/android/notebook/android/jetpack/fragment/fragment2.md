
原理篇，带着问题看源码，没有目的看源码只会像无头苍蝇一样乱撞

1. Fragment的本质是什么
2. Fragment的生命周期是由谁来管理的，如何保存状态
3. FragmentManager和FragmentTransaction分别扮演什么角色
4. Fragment回退栈是如何实现的
5. Fragment设计者使用了哪些设计模式

> Fragment核心的类有：
Fragment：Fragment的基类，任何创建的Fragment都需要继承该类。
FragmentManager：管理和维护Fragment。他是抽象类，具体的实现类是FragmentManagerImpl。
FragmentTransaction：对Fragment的添加、删除等操作都需要通过事务方式进行。他是抽象类，具体的实现类是BackStackRecord。
Nested Fragment（Fragment内部嵌套Fragment的能力）是Android 4.2提出的，support-fragment库可以兼容到1.6。  
> 通过getChildFragmentManager()能够获得管理子Fragment的FragmentManager，在子Fragment中可以通过getParentFragment()获得父Fragment。