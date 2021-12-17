## 漫谈设计模式（一）：创建型模式

## 一、前言

创建型模式对类的实例化过程进行了抽象，能够将软件模块中对象的创建和对象的使用分离。
为了使软件的结构更加清晰，外界对于这些对象只需要知道它们共同的接口，而不清楚其具体的实现细节，使整个系统的设计更加符合单一职责原则。

简单来讲，相较于另外两种类型(结构型和行为型)，创建型模式的侧重点在于如何创建对象

本文会简单介绍几种常见的创建型模式：

1. 单例模式
2. 工厂模式
3. 建造者模式

注意，[原型模式](https://www.runoob.com/design-pattern/prototype-pattern.html)不包含在本文中，想要了解更多的可以自行冲浪搜索

## 二、创建型模式：单例模式

### 2.1 模式定义

单例模式的目的是保证一个类仅有一个实例，并提供一个访问它的全局访问点。
单例类拥有一个私有构造函数，确保用户无法通过new关键字直接实例化它。
除此之外，该模式中包含一个静态私有成员变量与静态公有的工厂方法。
该工厂方法负责检验实例的存在性并实例化自己，然后存储在静态成员变量中，以确保只有一个实例被创建。

![image-20211216173814128](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/DesignPattern/src/main/java/com/android/designpattern/创建型/单例模式/image_20211216173814128.png)



### 2.2 使用方法

由于单例使用方法简单，理解起来也比较容易，所以这里笔者就直接开门见山，介绍四种常见的使用方式

#### 2.2.1 饿汉式

```java
public class Singleton1 {

    private static final Singleton1 instance = new Singleton1();

    public Singleton1() {
    }

    public static Singleton1 getInstance() {
        return instance;
    }

    public void doSomething() {
    		//doSomething
    }

}
```

饿汉式是常见使用方法中最简单的一种，由于类加载机制的特性得以保证类加载过程中是线程安全的，所以多线程环境下使用也是没问题的

饿汉式单例模式唯一的问题可能就是：并非懒加载，只要单例类被虚拟机加载，就必然会创建instance实例。此时，若是被提前初始化的对象工程中用不到，那的确是白白消耗了初始化的时间和内存空间

不过考虑到单例模式的使用场景，笔者认为大多数情况下使用饿汉式并不会给项目带来多余的负担。毕竟，谁没事会在单例里面放的静态属性呢？

#### 2.2.2 懒汉式：静态内部类

```java
public class Singleton2 {

    private Singleton2() {

    }

    public static Singleton2 getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final Singleton2 instance = new Singleton2();
    }

    public void doSomething() {
    		//doSomething
    }

}
```

和2.2.1的代码示例相同，静态内部类同样由JVM类加载机制保证了多线程环境下的安全性；同时，由于没有在局部变量中声明，所以就算因为非主观因素导致单例类被虚拟机加载，也无需担心创建了暂时用不到的对象导致占用内存。

静态内部类的单例模式既能保证多线程环境下的安全，又实现了懒加载，代码量也不多，这样看起来好像非常适合在实际项目开发中使用

的确，静态内部类的单例是笔者在项目中使用较多的一种方式，原因无他，就是简单、方便。如果非要给这种方式找个缺点的话，可能是由于多创建了内部类，在32位系统下增加了4个字节占用内存，考虑到字节对齐，最坏情况下多占用8个字节内存

#### 2.2.3 懒汉式：双重校验锁DCL

```java
public class Singleton3 {

    private volatile static Singleton3 instance;

    private Singleton3() {
    }

    public static Singleton3 getInstance() {
        if (instance == null) {
            synchronized (Singleton3.class) {
                if (instance == null) {
                    instance = new Singleton3();
                }
            }
        }
        return instance;
    }

    public void doSomething() {
    		//doSomething
    }

}
```

DCL(DoubleCheckLock)双重校验锁可能是所有单例模式中传播最广的方式了，这也是笔者在工作早期接触最多的单例模式；DCL双重检验锁的方式把线程安全和懒加载都考虑到了，并且涉及到Java并发编程中两个比较重要的关键字：volatile和synchronized，相较于上面的两种示例，DCL可以拎出来的点比较多，笔者猜测可能这也是为什么DCL传播这么广的原因

在实际的项目开发中如果不觉得使用步骤繁琐，代码量可以接受的话，DCL也是比较推荐的一种使用方式

注：关于为什么要加volatile关键字和synchronized锁对象为什么是class笔者这里未展开，感兴趣的朋友请点击[这里](https://www.zhihu.com/question/46903811)

#### 2.2.4 枚举类

```java
public enum Singleton4 {
    
    getInstance();

    public void doSomething() {
    		//doSomething
    }

}
```

Java1.5以后，单例模式的实现终于迎来的新面孔：枚举类单例，这也是《Effective Java》作者Joshua 力荐的一种使用方式

枚举类单例除了能保证多线程安全外，还加入了最重要的新功能，防止反射破坏



### 2.3 破坏单例模式及防止破坏单例

正如2.2.4小节中提到的，除了枚举类单例外，其他的三种单例模式都可以通过使用反射来破坏在进程中的唯一性

