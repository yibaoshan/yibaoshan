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

由于单例使用简单，同时理解起来也比较容易，所以这里笔者就直接开门见山，介绍一下常见的几种使用方式

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

饿汉式是几种常见使用方法中最简单的一种，由于JVM特性得以保证类加载过程中是线程安全的，所以多线程环境下使用也是没问题的

唯一的问题可能就是并非懒加载，只要单例类被虚拟机加载，就必然会创建instance实例。此时，若是被提前初始化的对象工程中用不到，那的确是白白消耗了初始化的时间和内存空间

不过考虑到单例模式的使用场景，笔者认为大多数情况下使用饿汉式并不会给项目带来多余的负担。毕竟，谁没事会在单例里面放的静态属性/方法呢？

### 2.2.2 懒汉式：静态内部类

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

静态内部类的单例是笔者个人在项目中使用较多的一种方式，原因无他，简单方便。

和2.2.1一样，由JVM类加载机制保证了多线程环境下的安全性；同时

如果非要找出静态内部类的缺点的话，多创建了内部类导致内存增加了4个字节，考虑到字节对齐32位系统下
