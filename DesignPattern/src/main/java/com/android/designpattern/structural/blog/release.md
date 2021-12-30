## 漫谈设计模式（二）：结构型模式

## 一、前言

结构型模式(Structural Pattern)描述如何将类或者对象结合在一起形成更大的结构，就像搭积木，可以通过简单的组合形成复杂的、功能更为强大的结构。
结构型模式的实现可以分为两种，类结构型模式和对象结构型模式，其中：

- 类结构型模式关心类的组合，由多个类可以组合成一个更大的系统，在类结构型模式中一般只存在继承关系和实现关系。
- 对象结构型模式关心类与对象的组合，通过关联关系使得在一个类中定义另一个类的实例对象，然后通过该对象调用其方法。根据“合成复用原则”，在系统中尽量使用关联关系来替代继承关系，因此大部分结构型模式都是对象结构型模式。

本文将会介绍几种常见的结构型模式，他们分别是：
1. 享元模式(Flyweight)
2. 代理模式(Proxy)
3. 装饰模式(Decorator)
4. 适配器模式(Adapter)
5. 桥接模式(Bridge)

同时，为了更加方便的理解结构型模式的设计思想，本文将加入一个项目阶段的概念：
1. 开发中，也包含开发前期，指的是代码未发布可以随意更改的阶段
2. 开发完成，已发布，只能补救的阶段
3. 封装完成，无法更改内部代码

不同的开发阶段所能适用的设计模式也不同，在理解设计者的意图时，这一点尤其重要

另外，本篇文章是笔者个人对结构型模式的理解，由于结构型模式的概念有些抽象(相较于创建型模式)，每个人的理解也不尽相同
因此，若您发现笔者的描述有不准确甚至完全错误的地方，请到这里进行反馈，感谢

注意，外观模式和组合模式不包含在本文中，想要了解更多的可以点击这里

## 二、结构型模式：享元模式

### 1、模式定义

享元模式是对象池的一种实现，它的英文名称叫做Flyweight，代表轻量级的意思。
享元模式用来尽可能减少内存使用量，它适合用于可能存在大量重复对象的场景，来缓存可共享的对象，达到对象共享、避免创建过多对象的效果，这样一来就可以提升性能、避免内存移除等。
以上，是《图说设计模式》一书中对享元模式的解释

一直以来，笔者都不太能分清享元模式和复用池技术之间的区别，在大多数的技术文章中，似乎都将这两者直接画等号
而在文章的评论区，往往又有质疑的声音：是不是写错了？享元模式关注的是对象的内部状态和外部状态，不单单是复用池。每每看到类似评论，笔者都一脸问号：什么叫做对象的内部状态和外部状态？

为此，在享元模式下笔之前，笔者查阅了《设计模式：可复用面向对象软件的基础》、《Head First设计模式》、《Android源码设计模式解析与实战》、《设计模式之美-王争》、《深入浅出设计模式-LeetCode》等书籍资料
在2.2的代码示例中，笔者会通过一个小栗子，来谈一谈笔者个人理解的享元模式

> A **flyweight** is a shared object that can be used in multiple contexts simultaneously. The flyweight acts as an independent object in each context—it's indistinguishable from an instance of the object that's not shared. Flyweights cannot make assumptions about the context in which they operate. The key concept here is the distinction between **intrinsic** and **extrinsic** state.  **Intrinsic ** state is stored in the flyweight; it consists of information that's independent of the flyweight's context, thereby making it sharable.  **Extrinsic ** state depends on and varies with the flyweight's context and therefore can't be shared. Client objects are responsible for passing  **extrinsic ** state to the flyweight when it needs it.
>
> —《Design Patterns: Elements of Reusable Object-Oriented Software》
>
> **flyweight**是一个共享对象，它可以同时在多个场景(context)中使用，并且在每个场景中flyweight都可以作为一个独立的对象—这一点与非共享对象的实例没有区别。flyweight不能对它所运行的场景做出任何假设，这里的关键概念是**内部状态**和**外部状态**之间的区别。**内部状态**存储于flyweight中，它包含了独立于flyweight场景的信息，这些信息使得flyweight可以被共享。而**外部状态**取决于flyweight场景，并根据场景而变化，因此不可共享。用户对象负责在必要的时候将**外部状态**传递给flyweight。
>
> —《设计模式：可复用面向对象软件的基础》机械工业出版社

### 2、代码示例

```java
/*性别枚举类*/
public enum Gender {
  
    MAN("男"), WOMAN("女"), UNKNOWN("不想透露");

    public String name;

    Gender(String name) {
        this.name = name;
    }
  
}

/*国家类*/
public class Country {

    private String countryName;//国家名称
    private String countryArea;//国土面积
  	//more...

    private final static Map<String, Country> mpCache = new HashMap<>();

    private Country() {

    }

    private Country(String countryName, String countryArea) {
        this.countryName = countryName;
        this.countryArea = countryArea;
    }

  	/*查询国家对象*/
    public static Country query(String countryName) {
        if (!mpCache.containsKey(countryName)) {
            queryLatest();
        }
        return mpCache.get(countryName);
    }

  	/*去服务器查询最新数据*/
    private static void queryLatest() {
        mpCache.put("China", new Country("中国(China)", "959.7万平方公里(2021)"));
        mpCache.put("The USA", new Country("美国(The USA)", "983.4万平方公里(2021)"));
    }

    public String getCountryName() {
        return countryName;
    }

    public String getCountryArea() {
        return countryArea;
    }
  
}

/*人类*/
public class Person {

    public int age;
    public String name;
    public Gender gender;
    public Country country;

    public Person(int age, String name, Gender gender, Country country) {
        this.age = age;
        this.name = name;
        this.gender = gender;
        this.country = country;
    }
}

/*测试类*/
public class PersonTest {

    @Test
    public void main() {
      	print(new Person(16, "小明", Gender.MAN, Country.query("China")));
        print(new Person(17, "小红", Gender.WOMAN, Country.query("China")));
        print(xgnew Person(18, "小刚", Gender.UNKNOWN, Country.query("The USA")));
    }

    private void print(Person person) {
        System.out.println("name=" + person.name
                + " , country=" + person.country
                + " , gender=" + person.gender.name
                + " , age=" + person.age
        );
    }

}
```

打印结果

```java
name=小明,country=Country{countryName='中国(China)',countryArea='959.7万平方公里(2021)', address='4cdf35a9'},gender=男,age=16
name=小红,country=Country{countryName='中国(China)',countryArea='959.7万平方公里(2021)', address='4cdf35a9'},gender=女,age=17
name=小刚,country=Country{countryName='美国(The United States of America)', countryArea='983.4万平方公里(2021)', address='4c98385c'} ,gender=不想透露,age=18
```

从示例代码可以看到，小明和小红是来自同一个国家，那么国家这个对象肯定是可以使用相同的
这就是所谓的对象的内部状态，而xx则是对象的外部状态

内部状态的改变并不影响外部状态，也就是说，小明傍上了富婆移民美国，小明对象的国家属性从中国改为美国了，那也只是小明对象内部属性的更新，外部国家类不影响，返回给小明一个美国的共享对象即可

可以理解为这个被共享的对象有个属性是

典型的比如string常量池，integer，基础类型的包装类型，string str1="hahaha"

对象本身不可变

内部状态不可变，比如国家对象

外部状态可变，比如Android Message

所以才有复用的价值

Android Message所有属性都可变

### 3、源码锚点

看过了2.2小节的示例之后，我们再来看看Android中Message的实现
代码块
从代码中可以看到，Message用链表实现了个复用池

### 4、小结

享元模式是典型的空间换时间的设计模式

在GoF设计模式中，作者提到因为对象是可以被共享的，用户不能直接对其实例化，因此FlyweightFactory可以帮助用户查找某个特定的Flyweight对象。

总的来说，享元模式设计思想是通过复用对象，以达到节省内存的目的，这一点笔者认为

最后我们来总结一下享元模式的使用场景：

1. 系统中存在大量的相似对象，例如2.2中的国家对象，适用阶段：开发中
2. 需要缓冲池的场景，例如Android Message对象

有些书籍/资料将享元模式翻译为蝇量模式，这一点需要注意，享元模式和蝇量模式是同一个

以上是笔者个人对享元模式的理解，若有不同的看法请到这里进行反馈，感谢

## 三、结构型模式：代理模式

### 1、模式定义

### 2、代码示例

### 3、源码锚点

### 4、小结

## 四、结构型模式：装饰者模式

### 1、模式定义

### 2、代码示例

### 3、源码锚点

### 4、小结

## 五、结构型模式：适配器模式

### 1、模式定义

适配器模式使接口不兼容的那些类可以一起工作，其别名为包装器。在Android开发中，不管是Android5.0之前的ListView还是现在的RecyclerView，都需要使用到Adapter适配器，所以对Android开发者来说适配器模式并不会很陌生

适配器模式有两种实现方式：类适配器和对象适配器
其中，类适配器使用继承关系来实现，对象适配器使用组合关系来实现，我们常用的LV/RV就属于对象适配器

5.2小节中，笔者将展示

### 2、代码示例

### 3、源码锚点

在Android源码中，ListView和RecyclerView的适配器无疑是最合适的，但需要注意的是
这里的Adapter并不是经典的适配器模式，但是却是对象适配器模式的优秀示例，也很好的体现了面向对象的一些基本原则

### 4、小结

关于剖析LV/RV适配器的更多更详细的解析文章可以点击[这里](https://www.kancloud.cn/alex_wsc/android_framework/502061)

## 六、结构型模式：外观模式

### 1、模式定义

### 2、代码示例

### 3、源码锚点

### 4、小结

## 七、结构型模式：桥接模式

### 1、模式定义

### 2、代码示例

### 3、源码锚点

### 4、小结

## 八、结构型模式：组合模式

### 1、模式定义

### 2、代码示例

### 3、源码锚点

### 4、小结

## 九、总结

外观模式是给一个复杂的系统提供一个统一的外观(接口)，方便调用者使用，可以简单理解为再封装，没有改变任何接口
适用阶段：笔者个人认为任何阶段都可以使用外观模式，因为复杂有复杂的价值，简单有简单的局限
adapter模式使用在两个部分有不同的接口的情况,目的是改变接口,使两个部分协同工作，代码已经有了而且还改不了
桥梁模式是为了分离抽象和实现，属于代码设计之处需要考虑的事情

再次声明，以上是笔者个人对结构型模式的理解，由于结构型模式的概念有些抽象(相较于创建型模式)，每个人的理解会不尽相同
若您觉得笔者的描述有任何不对的地方，请到这里进行反馈，再次感谢

## 十、参考资料

- [图说设计模式](https://design-patterns.readthedocs.io/zh_CN/latest/index.html)
- [refactoringguru.cn](https://refactoringguru.cn/design-patterns)
- [极客时间：设计模式之美-王争](https://time.geekbang.org/column/intro/250)
- [《设计模式：可复用面向对象软件的基础》](https://item.jd.com/12623588.html)
- [《Android 源码设计模式解析与实战》-何红辉 / 关爱民](https://item.jd.com/11793928.html)
- [《深入浅出设计模式-LeetCode》](https://leetcode-cn.com/leetbook/detail/design-patterns/)
- [《Head First 设计模式》](https://item.jd.com/10100236.html)