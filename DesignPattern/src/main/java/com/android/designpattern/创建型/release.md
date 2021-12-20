## 漫谈设计模式（一）：创建型模式

## 一、前言

`创建型模式`对类的实例化过程进行了抽象，能够将软件模块中对象的创建和对象的使用分离。
为了使软件的结构更加清晰，外界对于这些对象只需要知道它们共同的接口，而不清楚其具体的实现细节，使整个系统的设计更加符合单一职责原则。

简单来讲，相较于另外两种类型(`结构型`和`行为型`)，`创建型模式`的侧重点在于如何创建对象

本文会简单介绍几种常见的`创建型模式`：

1. 单例模式
2. 工厂模式
3. 建造者模式

注意，[原型模式](https://www.runoob.com/design-pattern/prototype-pattern.html)不包含在本文中，想要了解更多的可以自行冲浪搜索



## 二、创建型模式：单例模式

### 2.1 模式定义

`单例模式`的目的是保证一个类仅有一个实例，并提供一个访问它的全局访问点。
单例类拥有一个私有构造函数，确保用户无法通过`new`关键字直接实例化它。
除此之外，该模式中包含一个静态私有成员变量与静态公有的工厂方法。
该工厂方法负责检验实例的存在性并`实例化`自己，然后存储在静态成员变量中，以确保只有一个`实例`被创建。

![image-20211216173814128](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/DesignPattern/src/main/java/com/android/designpattern/创建型/单例模式/image_20211216173814128.png)

*图片来源：自己画的*

### 2.2 使用方法

单例使用方法简单，理解起来也比较容易，笔者这里就直接开门见山，介绍四种常见的使用方式

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

`饿汉式`是常见使用方法中最简单的一种，由于`类加载机制`的特性得以保证类加载过程中是线程安全的，所以多线程环境下使用也是没问题的

`饿汉式`单例模式唯一的问题可能就是：并非懒加载，只要单例类被虚拟机加载，就必然会创建`instance`实例。此时，若是被提前初始化的示例工程中用不到，那的确是白白消耗了初始化的时间和内存空间

不过考虑到`单例模式`的使用场景，笔者认为大多数情况下使用`饿汉式`并不会给项目带来多余的负担。毕竟，谁没事会在单例里面放的`静态属性`呢？

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

和`2.2.1`的代码示例相同，静态内部类同样由`JVM类加载机制`保证了多线程环境下的安全性；同时，由于没有在局部变量中声明，所以就算因为非主观因素导致单例类被虚拟机加载，也无需担心创建了暂时用不到的对象导致占用内存。

`静态内部类`的单例模式既能保证多线程环境下的安全，又实现了懒加载，代码量也不多，这样看起来好像非常适合在实际项目开发中使用

的确，`静态内部类`的单例是笔者在项目中使用较多的一种方式，原因无他，就是简单、方便。如果非要给这种方式找个缺点的话，可能是由于多创建了内部类，在`32位`系统下增加了`4个字节`占用内存，考虑到字节对齐，最坏情况下多占用`8个字节`内存

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

`DCL(DoubleCheckLock)双重校验锁`可能是所有`单例模式`中传播最广的方式了，这也是笔者在工作早期接触最多的单例模式；`DCL双重检验锁`的方式把线程安全和懒加载都考虑到了，并且涉及到Java`并发编程`中两个比较重要的关键字：`volatile`和`synchronized`，相较于上面的两种示例，`DCL`可以拎出来的点比较多，笔者猜测可能这也是为什么`DCL`传播这么广的原因

在实际的项目开发中如果不觉得使用步骤较为繁琐，代码量可以接受的话，`DCL`也是比较推荐的一种使用方式

注：关于为什么要加`volatile`关键字和`synchronized`锁对象为什么是class笔者这里未展开，感兴趣的朋友请点击[这里](https://www.zhihu.com/question/46903811)

#### 2.2.4 枚举类

```java
public enum Singleton4 {
    
    getInstance();

    public void doSomething() {
    		//doSomething
    }

}
```

Java1.5以后，`单例模式`的实现终于迎来的新面孔：枚举类单例，这也是《Effective Java》作者Joshua 力荐的一种使用方式

`枚举类`是没有构造方法的，`枚举类`的创建完全由`JVM`内部实现，不对外开放。这样的特性也使得我们不能使用`new`的方式来创建枚举对象，对应的缺点就是`枚举类`的`单例模式`不是懒加载的

`枚举类`单例除了能保证线程安全外，还加入了新功能：防止单例模式被破坏，原因有两点：

1. `枚举类`无法被反射创建

   强行使用反射创建会收到错误：java.lang.IllegalArgumentException: Cannot reflectively create enum objects

2. `枚举类`不能被反序列化

   序列化的时候，仅仅是将枚举对象的name属性输出到结果中，反序列化的时候则是通过java.lang.Enum的valueOf方法来根据名字查找枚举对象。

   同时，编译器是不允许任何对这种序列化机制的定制的，因此禁用了writeObject、readObject、readObjectNoData、writeReplace和readResolve等方法。

最后，关于`枚举类`还有一点需要注意：`枚举类`不能继承任何类，`枚举类`在编译后会继承自java.lang.Enum，由于Java`单继承`的特性导致枚举类不能再继承任何其他类，但是`枚举类`可以实现`接口`



### 2.3 破坏单例模式及防止破坏单例

正如`2.2.4`小节中提到的，除了`枚举类单例`外，其他的三种`单例模式`都可以通过使用反射来破坏在进程中的唯一性

`单例类`由于自身的特殊性导致无法被`反射`和`反序列化`创建，所以对象的唯一性暂时无法被破坏，但是可以修改枚举类的值

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

从打印结果可以看出，除`枚举类`外的三种实现方式全部沦陷，都可以通过`反射`来创建不同的实例对象来破坏`单例模式`的唯一性

`反射`创建`枚举类`会受到报错信息：java.lang.IllegalArgumentException: Cannot reflectively create enum objects，笔者这里就不展示了，感兴趣的同学可以自己动手试一试

最后提一句，网上其他文章提到了使用`clone`方法来破坏单例，笔者认为这点不成立，因为不重写`clone()`方法并且不将访问修饰符改为`public`，使用者是无法调用`clone`方法来创建新的`实例对象`的

### 2.4 小结

至此，几种常见的`单例模式`实现方式都介绍完了，简单总结下：上述几种实现方式都可以在保证多线程环境下安全性；除了`枚举单例`之外，其他几种方式的`单例模式`都可以被破坏

此小节涉及到的代码在[这里](https://github.com/yibaoshan/Blackboard/tree/master/DesignPattern/src/main/java/com/android/designpattern/%E5%88%9B%E5%BB%BA%E5%9E%8B/%E5%8D%95%E4%BE%8B%E6%A8%A1%E5%BC%8F)



## 三、创建型模式：工厂方法

### 3.1 模式定义

`工厂方法`模式又称为工厂模式，在`工厂方法`模式中，工厂父类负责定义创建产品对象的公共接口，而工厂子类则负责生成具体的产品对象，这样做的目的是将产品类的`实例化`操作延迟到工厂子类中完成，即通过工厂子类来确定究竟应该实例化哪一个具体产品类。

工厂方法模式包含四个角色：

1. `抽象产品`是定义产品的接口，是工厂方法模式所创建对象的超类型，即产品对象的共同父类或接口；
2. `具体产品`实现了抽象产品接口，某种类型的具体产品由专门的具体工厂创建，它们之间往往一一对应；
3. `抽象工厂`中声明了工厂方法，用于返回一个产品，它是工厂方法模式的核心，任何在模式中创建对象的工厂类都必须实现该接口；
4. `具体工厂`是抽象工厂类的子类，实现了抽象工厂中定义的工厂方法，并可由客户调用，返回一个具体产品类的实例。

![image-20211220161859390](/Users/bob/Library/Application Support/typora-user-images/image-20211220161859390.png)

*图片来源：自己画的*

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

从本小节开始，笔者将会对每种设计模式都以Java/Android源码举例说明，用`锚点记忆法`来辅助记忆；比如提起`建造者模式`就像到`Android AlertDialog`，提到`观察者模式`就想到`OnClickListener`一样

`工厂模式`在源码中的提现，我们可以记住`Java容器类`的迭代器：Iterator

为了方便理解，我们这里简单剖析迭代器的角色分工：

1. 抽象工厂
   1. Iterable
2. 工厂实现
   1. ArrayList
   2. HashMap
3. 抽象产品
   1. Iterator
4. 产品实现(jdk1.8)
   1. ArrayList中的Itr类
   2. HashMap中的EntryIterator/KeyIterator/ValueIterator类

从上面的结构来看，`ArrayList`和`HashMap`中的`iterator`方法其实就相当于一个`工厂方法`，专为`new`对象而生，这里`iterator`方法是构造并返回一个具体的迭代器

### 3.4 小结

`工厂方法`模式的主要优点是增加新的产品类时无须修改现有系统，并封装了产品对象的创建细节，系统具有良好的灵活性和可扩展性；其缺点在于增加新产品的同时需要增加新的工厂，导致系统类的个数成对增加，在一定程度上增加了系统的复杂性

笔者这里没有提到`简单工厂`，实际上，当把`工厂方法`的抽象工厂类删除掉，就是`简单工厂`模式了，所以可以把`简单工厂`理解为`工厂方法`的简化版本；笔者这里没有给出示例，想要了解更多的可以去看`阿里巴巴淘系技术`在知乎上的回答：[简单工厂模式、工厂方法模式和抽象工厂模式有何区别](https://www.zhihu.com/question/27125796)

此小节涉及到的代码在[这里](https://github.com/yibaoshan/Blackboard/tree/master/DesignPattern/src/main/java/com/android/designpattern/%E5%88%9B%E5%BB%BA%E5%9E%8B)



## 四、创建型模式：抽象工厂

### 4.1 模式定义

`抽象工厂`模式是所有形式的工厂模式中最为抽象和最具一般性的一种形态。`抽象工厂`模式与`工厂方法`模式最大的区别在于，`工厂方法`模式针对的是一个产品等级结构，而`抽象工厂`模式则需要面对多个产品等级结构

`抽象工厂`模式同样包含四个角色：

1. `抽象工厂`用于声明生成抽象产品的方法；
2. `具体工厂`实现了抽象工厂声明的生成抽象产品的方法，生成一组具体产品，这些产品构成了一个产品族，每一个产品都位于某个产品等级结构中；
3. `抽象产品`为每种产品声明接口，在抽象产品中定义了产品的抽象业务方法；
4. `具体产品`定义具体工厂生产的具体产品对象，实现抽象产品接口中定义的业务方法。

虽然`抽象工厂`的角色数量和`工厂方法`相同，都是4个，但由于每个角色下还有其他角色，理解起来就会稍微有点费劲，这一点也可以从`抽象工厂`和`工厂方法`的`UML类图`看出来区别，`抽象工厂`明显更复杂一些

由于`抽象工厂`稍微有点复杂，不能再用上面的ProductA、FactoryB等无实际意义的类名来举例，为了方便理解，笔者这里使用`手机组装厂`来做类比，他们的角色分工如下：

1. 抽象产品
   1. 电池组装厂
   2. 屏幕组装厂
2. 具体产品实现
   1. 电池
      1. 比亚迪电池供应商
      2. 德赛电池供应商
   2. 屏幕
      1. 京东方屏幕供应商
      2. LG屏幕供应商
3. 抽象工厂
   1. 手机组装厂
4. 具体工厂实现
   1. 华为P50的组装厂
   2. 小米10的组装厂

有了上面的角色分工结构，下面代码示例我们就直接对号入座，笔者觉得这样的方式会理解的更透彻

![image-20211220161933092](/Users/bob/Library/Application Support/typora-user-images/image-20211220161933092.png)

*图片来源：自己画的*

### 4.2 代码示例

角色1-1：抽象产品类-电池

```java
public abstract class AbstractProductBattery {

    public AbstractProductBattery() {
        createBattery();
    }

    public abstract String getBatteryName();

    protected abstract void createBattery();

}
```

角色1-2：抽象产品类-屏幕

```java
public abstract class AbstractProductScreen {

    public AbstractProductScreen() {
        createScreen();
    }

    public abstract String getScreenName();

    protected abstract void createScreen();

}
```

角色2-1-1：产品实现类-电池-比亚迪

```java
public class BYDBattery extends AbstractProductBattery {

    @Override
    public String getBatteryName() {
        return "比亚迪(BYD)";
    }

    @Override
    protected void createBattery() {
        System.out.println("加班加点生产比亚迪电池中...");
    }
}
```

角色2-1-2：产品实现类-电池-德赛

```java
public class DesayBattery extends AbstractProductBattery {

    @Override
    public String getBatteryName() {
        return "德赛(Desay)";
    }

    @Override
    protected void createBattery() {
        System.out.println("加班加点生产德赛电池中...");
    }
}
```

角色2-2-1：产品实现类-屏幕-京东方

```java
public class BOEScreen extends AbstractProductScreen {

    @Override
    public String getScreenName() {
        return "京东方(BOE)";
    }

    @Override
    protected void createScreen() {
        System.out.println("加班加点生产京东方屏幕中...");
    }

}
```

角色2-2-2：产品实现类-屏幕-LG

```java
public class LGScreen extends AbstractProductScreen {

    @Override
    public String getScreenName() {
        return "LG";
    }

    @Override
    protected void createScreen() {
        System.out.println("加班加点生产LG屏幕中...");
    }

}
```

角色3：抽象工厂类

```java
public abstract class AbstractPhoneFactory {

    protected String brand;//工厂生产的手机品牌
    protected String model;//工厂生产的手机型号
    protected AbstractProductScreen phoneScreen;//手机使用的屏幕
    protected AbstractProductBattery phoneBattery;//使用的电池

    public AbstractPhoneFactory(String brand, String model) {
        this.brand = brand;
        this.model = model;
        this.phoneScreen = createPhoneScreen();
        this.phoneBattery = createPhoneBattery();
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public AbstractProductScreen getPhoneScreen() {
        return phoneScreen;
    }

    public AbstractProductBattery getPhoneBattery() {
        return phoneBattery;
    }

    protected abstract AbstractProductScreen createPhoneScreen();

    protected abstract AbstractProductBattery createPhoneBattery();
}
```

角色4-1：工厂实现类-华为

```java
public class HuaWeiPhoneFactory extends AbstractPhoneFactory {

    public HuaWeiPhoneFactory() {
        super("华为(HuaWei)", "P50");
    }

    @Override
    protected AbstractProductScreen createPhoneScreen() {
        return new LGScreen();//Lg屏幕
    }

    @Override
    protected AbstractProductBattery createPhoneBattery() {
        return new DesayBattery();//德赛的电池
    }
}
```

角色4-2：工厂实现类-小米

```java
public class XiaoMiPhoneFactory extends AbstractPhoneFactory {

    public XiaoMiPhoneFactory() {
        super("小米(XiaoMi)", "10");
    }

    @Override
    protected AbstractProductScreen createPhoneScreen() {
        return new BOEScreen();//京东方的屏幕
    }

    @Override
    protected AbstractProductBattery createPhoneBattery() {
        return new DesayBattery();//德赛电池
    }
}
```

抽象工厂使用示例

```java
    public void main() {
        AbstractPhoneFactory phoneFactory1 = new HuaWeiPhoneFactory();
        AbstractPhoneFactory phoneFactory2 = new XiaoMiPhoneFactory();
        print(phoneFactory1);
        print(phoneFactory2);
    }

    private void print(AbstractPhoneFactory phoneFactory) {
        System.out.println("产线品牌：" + phoneFactory.getBrand()
                + ",生产型号：" + phoneFactory.getModel()
                + ",电池厂商：" + phoneFactory.getPhoneBattery().getBatteryName()
                + ",屏幕厂商：" + phoneFactory.getPhoneScreen().getScreenName());
    }
```

打印结果

```java
产线品牌：华为(HuaWei),生产型号：P50,电池厂商：德赛(Desay),屏幕厂商：LG
产线品牌：小米(XiaoMi),生产型号：10,电池厂商：德赛(Desay),屏幕厂商：京东方(BOE)
```



### 4.3 源码锚点

`抽象工厂`方法模式在Android源码中的实现相对来说是比较少的，在`《Android 源码设计模式解析与实战》`一书中提到是Android底层对`MediaPlayer`的创建是可以看作为`抽象工厂`，这一块代码笔者不熟悉，为了防止误人子弟，`抽象工厂`模式的源码这里笔者就不再举例，对`抽象工厂`源码体现感兴趣的可以自行查找其他资料

### 4.4 小结

`抽象工厂`模式的主要优点是隔离了具体类的生成，使得客户并不需要知道什么被创建，而且每次可以通过具体工厂类创建一个产品族中的多个对象，增加或者替换产品族比较方便，增加新的具体工厂和产品族很方便；主要缺点在于增加新的产品等级结构很复杂，需要修改抽象工厂和所有的具体工厂类，对“开闭原则”的支持呈现倾斜性。

为了方便记忆，笔者个人总结的`抽象工厂`和`工厂方法`不同点：`抽象工厂`模式拥有多个`抽象产品`类，也就是本示例的电池抽象类和屏幕抽象类

`抽象工厂`模式适用情况包括：一个系统不应当依赖于产品类实例如何被创建、组合和表达的细节；系统中有多于一个的产品族，而每次只使用其中某一产品族；属于同一个产品族的产品将在一起使用；系统提供一个产品类的库，所有的产品以同样的接口出现，从而使客户端不依赖于具体实现。

此小节涉及到的代码在[这里](https://github.com/yibaoshan/Blackboard/tree/master/DesignPattern/src/main/java/com/android/designpattern/%E5%88%9B%E5%BB%BA%E5%9E%8B)



## 五、创建型模式：建造者模式

### 5.1 模式定义

`建造者模式`将一个复杂对象的构建与它的表示分离，使得同样的构建过程可以创建不同的表示。`建造者模式`是一步一步创建一个复杂的对象，它允许用户只通过指定复杂对象的类型和内容就可以构建它们，用户不需要知道内部的具体构建细节

下面我们以Android中创建一个弹窗类来举例，该类有以下几个特性：

- 弹窗类有左右两个按钮，标题和内容共四个属性，弹窗类提供`Builder类`来组装自己
- 一旦弹窗被创建，左右两个按钮文字不希望被更改，也就说`Dialog`本身不提供修改左右按钮的方法
- `Builder`提供不同的创建方法，比如：创建左右两个按钮的弹窗、只有确认按钮的弹窗，创建过程由Builder来管理

接下来我们来康康`5.2`中的代码示例

### 5.2 代码示例

```java
public class CommonDialog {

    private Params mParams;

    private CommonDialog(Params params) {
        this.mParams = params;
    }

    public void setTitleText(String title) {
        mParams.titleText = title;
    }

    public void setMessageText(String message) {
        mParams.messageText = message;
    }

    public void show() {
        //set view...
    }

    public static class Builder {

        protected Params mParams = new Params();

        public Builder setTitleText(String title) {
            mParams.titleText = title;
            return this;
        }

        public Builder setMessageText(String message) {
            mParams.messageText = message;
            return this;
        }

        public Builder setConfirmText(String confirm) {
            mParams.confirmText = confirm;
            return this;
        }

        public Builder setCancelText(String cancel) {
            mParams.cancelText = cancel;
            return this;
        }

        public CommonDialog create() {
          	//create normal dialog logic
            return new CommonDialog(mParams);
        }

        public CommonDialog createOnlyConfirm() {
          	//create only have confirm btn dialog logic
            mParams.cancelText = null;
            return new CommonDialog(mParams);
        }

    }

    private static class Params {

      	/*public to anyone*/
        private String titleText;
        private String messageText;

      	/*private field , runtime not change*/
        private String confirmText;
        private String cancelText;

    }

}
```

建造者模式使用示例

```java
public void main() {
    CommonDialog.Builder builder = new CommonDialog.Builder();
    builder.setMessageText("will you marry me");
    builder.setConfirmText("yes");
    builder.setCancelText("no");
    CommonDialog normalDialog = builder.create();//创建普通对话框
    normalDialog.show();

    builder.setMessageText("Am I handsome?");
    builder.setConfirmText("yes");
    CommonDialog onlyConfirmDialog = builder.createOnlyConfirm();//创建只有确认按钮的对话框
    onlyConfirmDialog.show();
    onlyConfirmDialog.setMessageText("so~ Let's go to the movies?");
    onlyConfirmDialog.show();
}
```

### 5.3 源码锚点

建造者模式在Android源码中的实现：`AlertDialog`

### 5.4 小结

`建造者模式`的主要优点在于客户端不必知道产品内部组成的细节，将产品本身与产品的创建过程解耦，使得相同的创建过程可以创建不同的产品对象，每一个具体建造者都相对独立，而与其他的具体建造者无关，因此可以很方便地替换具体建造者或增加新的具体建造者，符合“`开闭原则`”，还可以更加精细地控制产品的创建过程

当然，`建造者模式`也是有缺点的，其主要缺点在于由于`建造者模式`所创建的产品一般具有较多的共同点，其组成部分相似，因此其使用范围受到一定的限制，相对应的代码量也会增加不少；如果产品的内部变化复杂，可能会导致需要定义很多具体`建造者`类来实现这种变化，导致系统变得很庞大。

`建造者模式`适用情况包括：

1. 需要生成的产品对象有复杂的内部结构，这些产品对象通常包含多个成员属性；
2. 需要生成的产品对象的属性相互依赖，需要指定其生成顺序；
3. 对象的创建过程独立于创建该对象的类；
4. 隔离复杂对象的创建和使用，并使得相同的创建过程可以创建不同类型的产品。

此小节涉及到的代码在[这里](https://github.com/yibaoshan/Blackboard/tree/master/DesignPattern/src/main/java/com/android/designpattern/%E5%88%9B%E5%BB%BA%E5%9E%8B)



## 六、总结

本文介绍了常见的几种`创建型设计模式`，简单总结一下：

1. `单例模式`：保证一个类仅有一个实例，常见实现方式有
   1. 饿汉式：若被提前加载会占用内存
   2. 懒汉式-静态内部类
   3. 懒汉式-双重校验锁DCL
   4. 枚举类单例：由虚拟机初始化，防止破坏唯一性
2. `工厂方法模式`，通过工厂子类来确定究竟应该实例化哪一个具体产品类
   1. 简单工厂：工厂方法的简化模式
3. `抽象工厂模式`：提供一个创建一系列相关或相互依赖对象的接口，而无须指定它们具体的类
4. `建造者模式`：将复杂对象的构造与它的表示分开，这样可以在相同的构造过程中创建不同的表示形式。

`由于笔者水平有限，文中难免会出现遗漏甚至错误的地方，若您发现任何问题或者有任何建议请在`[这里](https://github.com/yibaoshan/Blackboard/issues)`提交Issue，感谢`

全文完



## 七、参考资料

- [图说设计模式](https://design-patterns.readthedocs.io/zh_CN/latest/index.html)
- [《Android 源码设计模式解析与实战》-何红辉 / 关爱民](https://item.jd.com/11793928.html)
- [知乎：简单工厂模式、工厂方法模式和抽象工厂模式有何区别](https://www.zhihu.com/question/27125796)
