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

在实际的项目开发中如果不觉得使用步骤较为繁琐，代码量可以接受的话，DCL也是比较推荐的一种使用方式

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

枚举类是没有构造方法的，枚举类的创建完全由JVM内部实现，不对外开放。这样的特性也使得我们不能使用new的方式来创建枚举对象，对应的缺点就是枚举类的单例模式不是懒加载的

枚举类单例除了能保证线程安全外，还加入了新功能：防止单例模式被破坏，原因有两点：

1. 枚举类无法被反射创建

   强行使用反射创建会收到错误：java.lang.IllegalArgumentException: Cannot reflectively create enum objects

2. 枚举类不能被反序列化

   序列化的时候，仅仅是将枚举对象的name属性输出到结果中，反序列化的时候则是通过java.lang.Enum的valueOf方法来根据名字查找枚举对象。

   同时，编译器是不允许任何对这种序列化机制的定制的，因此禁用了writeObject、readObject、readObjectNoData、writeReplace和readResolve等方法。

最后，关于枚举类还有一点需要注意：枚举类不能继承任何类，枚举类在编译后会继承自java.lang.Enum，由于Java单继承的特性导致枚举类不能再继承任何其他类，但是枚举类可以实现接口



### 2.3 破坏单例模式及防止破坏单例

正如2.2.4小节中提到的，除了枚举类单例外，其他的三种单例模式都可以通过使用反射来破坏在进程中的唯一性

单例类由于自身的特殊性导致无法被反射和反序列化创建，所以对象的唯一性暂时无法被破坏，但是可以修改枚举类的值

笔者这里写了一个简单的示例代码来破坏单例，感兴趣的朋友也可以自己在单元测试中试一试

```java

    @Test
    public void main() {
        testSingleton1();
        testSingleton2();
        testSingleton3();
    }

    /*1.恶汉模式*/
    private static void testSingleton1() {
        Object fromInstance = Singleton1.getInstance();
        Object fromReflect = createClassWithReflect(Singleton1.class);
        System.out.println(fromInstance.equals(fromReflect));
    }

    /*2.懒汉模式-静态内部类*/
    private static void testSingleton2() {
        Object fromInstance = Singleton2.getInstance();
        Object fromReflect = createClassWithReflect(Singleton2.class);
        System.out.println(fromInstance.equals(fromReflect));
    }

    /*3.懒汉模式-DoubleCheckLock*/
    private static void testSingleton3() {
        Object fromInstance = Singleton3.getInstance();
        Object fromReflect = createClassWithReflect(Singleton3.class);
        System.out.println(fromInstance.equals(fromReflect));
    }

    private static <T> T createClassWithReflect(Class<T> clz) {
        Constructor<?> constructor = clz.getDeclaredConstructor();
        constructor.setAccessible(true);
        return (T) constructor.newInstance();
    }

```

打印结果

```java
false
false
false
```

最后提一句，网上其他文章提到了使用clone方法来破坏单例，笔者认为这点不成立，因为不重写clone()方法并且不将访问修饰符改为public，使用者是无法调用clone方法来创建新的实例对象的

### 2.4 小结

至此，几种常见的单例实现方式都介绍完了，简单总结下：上述几种实现方式都可以在保证多线程环境下安全性；除了枚举单例之外，其他几种方式的单例模式都可以被破坏

此小节涉及到的代码在[这里](https://github.com/yibaoshan/Blackboard/tree/master/DesignPattern/src/main/java/com/android/designpattern/%E5%88%9B%E5%BB%BA%E5%9E%8B/%E5%8D%95%E4%BE%8B%E6%A8%A1%E5%BC%8F)

## 三、创建型模式：工厂方法

### 3.1 模式定义

工厂方法模式又称为工厂模式，它属于类创建型模式。在工厂方法模式中，工厂父类负责定义创建产品对象的公共接口，而工厂子类则负责生成具体的产品对象，这样做的目的是将产品类的实例化操作延迟到工厂子类中完成，即通过工厂子类来确定究竟应该实例化哪一个具体产品类。
工厂方法模式包含四个角色：

1. 抽象产品是定义产品的接口，是工厂方法模式所创建对象的超类型，即产品对象的共同父类或接口；

2. 具体产品实现了抽象产品接口，某种类型的具体产品由专门的具体工厂创建，它们之间往往一一对应；

3. 抽象工厂中声明了工厂方法，用于返回一个产品，它是工厂方法模式的核心，任何在模式中创建对象的工厂类都必须实现该接口；

4. 具体工厂是抽象工厂类的子类，实现了抽象工厂中定义的工厂方法，并可由客户调用，返回一个具体产品类的实例。

### 3.2 代码示例

角色1：抽象产品类

```java
public abstract class AbstractProduct {

    public abstract String getName();

}
```

角色2：产品实现类

```java
public class ProductA extends AbstractProduct {

    @Override
    public String getName() {
        return "A";
    }

}

public class ProductB extends AbstractProduct {

    @Override
    public String getName() {
        return "B";
    }

}
```

角色3：抽象工厂类

```java
public abstract class AbstractFactory {

    public abstract AbstractProduct createProduct();

}
```

角色4：工厂实现类

```java
public class ProductAFactory extends AbstractFactory {

    @Override
    public AbstractProduct createProduct() {
        return new ProductA();
    }
  
}

public class ProductBFactory extends AbstractFactory {

    @Override
    public AbstractProduct createProduct() {
        return new ProductB();
    }
  
}
```

工厂方法使用示例

```java
@Test
public void main() {
    AbstractFactory factoryA = new ProductAFactory();
    AbstractFactory factoryB = new ProductBFactory();

    AbstractProduct productA = factoryA.createProduct();
    AbstractProduct productB = factoryB.createProduct();

    System.out.println("工厂A生产的产品名称：" + productA.getName());
    System.out.println("工厂B生产的产品名称：" + productB.getName());

}
```

打印结果

```jav
工厂A生产的产品名称：A
工厂B生产的产品名称：B
```

### 3.3 源码锚点

从本小节开始，笔者将会对每种设计模式都以Java/Android源码举例说明，用锚点记忆法来辅助记忆；比如提起建造者模式就像到Android AlertDialog，提到观察者模式就想到OnClickListener一样

### 3.4 小结

工厂模式又称为工厂方法

此小节涉及到的代码在[这里](https://github.com/yibaoshan/Blackboard/tree/master/DesignPattern/src/main/java/com/android/designpattern/%E5%88%9B%E5%BB%BA%E5%9E%8B)

## 四、创建型模式：抽象工厂

### 4.1 模式定义

### 4.2 代码示例

### 4.3 源码锚点

### 4.4 小结

此小节涉及到的代码在[这里](https://github.com/yibaoshan/Blackboard/tree/master/DesignPattern/src/main/java/com/android/designpattern/%E5%88%9B%E5%BB%BA%E5%9E%8B)

## 五、创建型模式：建造者模式

### 5.1 模式定义

### 5.2 代码示例

### 5.3 源码锚点

### 5.4 小结

此小节涉及到的代码在[这里](https://github.com/yibaoshan/Blackboard/tree/master/DesignPattern/src/main/java/com/android/designpattern/%E5%88%9B%E5%BB%BA%E5%9E%8B)

## 六、总结

本文介绍了，由于笔者水平有限，文中难免会出现遗漏甚至错误的地方

## 七、参考资料

- [图说设计模式](https://design-patterns.readthedocs.io/zh_CN/latest/index.html)
- [《Android 源码设计模式解析与实战》-何红辉 / 关爱民](https://item.jd.com/11793928.html)
- [知乎：简单工厂模式、工厂方法模式和抽象工厂模式有何区别](https://www.zhihu.com/question/27125796)
