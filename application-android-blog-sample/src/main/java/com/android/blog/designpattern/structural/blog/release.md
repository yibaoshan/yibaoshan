## 漫谈设计模式（二）：结构型模式

## 一、前言

`结构型模式`(Structural Pattern)描述如何将类或者对象结合在一起形成更大的结构，就像搭积木，可以通过简单的`组合`形成复杂的、功能更为强大的结构。
`结构型模式`的实现可以分为两种，`类结构型模式`和`对象结构型模式`，其中：

- `类结构型模式`关心类的组合，由多个类可以`组合`成一个更大的系统，在`类结构型模式`中一般只存在继承关系和接口实现关系。
- `对象结构型模式`关心类与对象的组合，通过`关联`关系使得在一个类中定义另一个类的实例对象，然后通过该对象调用其方法。根据“**合成复用原则**”，在系统中尽量使用关联关系来替代继承关系，因此大部分`结构型模式`都是`对象结构型模式`。

本文将会介绍几种常见的`结构型模式`，他们分别是：
1. **享元模式(Flyweight)**
2. **代理模式(Proxy)**
3. **装饰模式(Decorator)**
4. **适配器模式(Adapter)**
5. **桥接模式(Bridge)**
5. **外观模式(Facade)**

同时，为了更加方便的理解`结构型模式`的设计思想，本文将加入一个`项目阶段`的概念：
1. 开发中，也包含开发前期，一般是代码未发布可以随意更改的阶段
2. 开发完成，已发布/封装完成，或是没有源码，只能补救的阶段

不同的开发阶段所能适用的设计模式也不同，在理解设计者的意图时，这一点尤其重要

另外，本篇文章是笔者个人对`结构型模式`的理解，由于`结构型模式`的概念有些抽象(相较于`创建型模式`)，每个人的理解也不尽相同，因此，若您发现笔者的描述有不准确甚至完全错误的地方，请到[这里](https://github.com/yibaoshan/Blackboard/issues)进行反馈，感谢



## 二、结构型模式：享元模式

### 1、模式定义

`享元模式`(又称蝇量模式)是对象池的一种实现，它的英文名称叫做Flyweight，代表轻量级的意思。
`享元模式`用来尽可能减少内存使用量，它适合用于可能存在大量重复对象的场景，来缓存可共享的对象，达到对象共享、避免创建过多对象的效果，这样一来就可以提升性能，是典型的以空间换时间的设计模式。

`享元模式`的设计思想比较简单，关于`享元模式`有[争议](https://blog.csdn.net/wangshihui512/article/details/51453839)的一点是：`享元模式`是否只是对象池的一种实现，如果是，那么原文中提到的需要关注对象的`内部状态`和`外部状态`是什么意思？

换句话说，`享元模式`和`池化`技术之间是否可以直接划等号？

要探讨这个问题，我们先来看一下《设计模式》一书中对`享元模式`的定义：

> A **flyweight** is a shared object that can be used in multiple contexts simultaneously. The flyweight acts as an independent object in each context—it's indistinguishable from an instance of the object that's not shared. Flyweights cannot make assumptions about the context in which they operate. The key concept here is the distinction between **intrinsic** and **extrinsic** state.  **Intrinsic** state is stored in the flyweight; it consists of information that's independent of the flyweight's context, thereby making it sharable.  **Extrinsic** state depends on and varies with the flyweight's context and therefore can't be shared. Client objects are responsible for passing  **extrinsic** state to the flyweight when it needs it.
>
> —《Design Patterns: Elements of Reusable Object-Oriented Software》
>
> **flyweight**是一个共享对象，它可以同时在多个场景(context)中使用，并且在每个场景中flyweight都可以作为一个独立的对象—这一点与非共享对象的实例没有区别。flyweight不能对它所运行的场景做出任何假设，这里的关键概念是**内部状态**和**外部状态**之间的区别。**内部状态**存储于flyweight中，它包含了独立于flyweight场景的信息，这些信息使得flyweight可以被共享。而**外部状态**取决于flyweight场景，并根据场景而变化，因此不可共享。用户对象负责在必要的时候将**外部状态**传递给flyweight。
>
> —《设计模式：可复用面向对象软件的基础》机械工业出版社

从这段话的描述可以看出，作者强调`享元模式`的特点之一是对象的`内部状态`可变、`外部状态`不可变且不可被共享。那么什么是对象的`内部状态`和`外部状态`？下面我们一起来通过`2.2`中的代码示例看一看能否找出答案

### 2、代码示例

```java
/*国家类*/
public class Country {

    private String countryName;//国家名称
    private String countryArea;//国土面积
  	//more..

  	public static Country query(String country){
      //通过key查询内存/数据库等保存的国家对象
    }
}

/*用户信息类*/
public class UserInfo {

    public Integer age;//用户年龄
    public String nickname;//昵称
    public Country country;//国家
  	//more..

  	public static UserInfo obtain(){
      //内部维护一个对象缓存池
    }

}

/*测试类*/
public class Test {

    @org.junit.Test
    public void main() {
        UserInfo sanZ = UserInfo.obtain(18, "张三", Country.query("中国"));
        UserInfo siL = UserInfo.obtain(18, "李四", Country.query("中国"));
        //移民到新加坡养老
        siL.country = Country.query("新加坡");
    }

}

```

代码示例中只有两个类，`用户信息`类和`国家`类，在`用户信息`类的内部状态中，有一个`country`属性，`country`属性对应的`国家`类中内部维护一组数据，不公开set()方法。当某一个用户更改自身的`country`属性时，只能通过国家名称重新查询国家对象，这就是所谓享元对象`内部属性`更改时不影响其`外部状态`的含义；

总结一下，`享元对象`指的是可以被`共享`的对象，在`2.2`的示例代码中，`用户信息`类就是`享元对象`。由于`享元对象`可以被共享，其内部属性(姓名、年龄、国家等)可以被重新赋值，这被叫做对象的`内部状态`；`国家`类是独立的一个组织，不管`用户信息`类中的`country`属性如何更改都不会影响到`国家`类中的内部数据，`国家`类就被称为`享元对象`类的`外部状态`

### 3、源码锚点

在看过了`2.2`小节的示例之后，我们再回过头来看看`Android Message`的实现

```java
/*伪代码*/
public class Message {

    private String val;
    private Message next;

    private static Message root;
    private static int size = 0;
    private static final int MAX_SIZE = 10;

    public static Message obtain() {
        if (root != null) {
            //获取链表表头的对象
            Message temp = root;
            root = temp.next;
            temp.next = null;
            size--;
            return temp;
        }
        return new Message();
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public void recycle() {
        //回收对象，将属性内容清空
        this.val = null;
        //若缓存池还没满，将该对象保存至链表表头位置
        if (size < MAX_SIZE) {
            next = root;
            root = this;
            size++;
        }
    }

}
```

从代码中可以看到，`Message`用链表实现了个复用池，回到`2.1`中的疑问，`Android Message`的链表复用池的设计思想是能被称为`享元模式`吗？笔者认为是可以的，只不过`Message`没有外部状态罢了

除了`Android Message`外，Java中还有String常量池，int，float等基础类型的包装类型也都使用了`享元模式`，感兴趣的朋友可以自行搜索了解

### 4、小结

总结一下，`享元模式`的设计思想是通过复用对象，以达到节省内存的目的，`享元模式`适合在以下的场景中使用：

1. 系统中存在大量的相似对象，可以抽离为外部对象，例如`2.2`中的`国家`对象
2. 需要缓冲池的场景，例如`Android Message`对象，Java基本类型的包装类

*有些书籍/资料将享元模式翻译为`蝇量模式`，这一点需要注意，`享元模式`和`蝇量模式`是同一个*

此小节涉及到的代码在[这里](https://github.com/yibaoshan/Blackboard/tree/master/DesignPattern/src/main/java/com/android/designpattern/structural/flyweight)



## 三、结构型模式：代理模式

### 1、模式定义

`代理模式`是指在不改版原始类的情况下，通过新增`代理类`的方式，来给原始类加入新的功能

代理模式实现的场景分为两种，`外部类`和`内部类`。

`外部类`一般是在原始类无法进行更改的情况使用，以`3.2`中的方法耗时统计举例来说，要统计`Stack`每个方法的耗时，我们可以创建`StackProxy`类继承自`Stack`类，然后在`StackProxy`中调用`Stack`的方法同时加入耗时统计的代码。

`内部类`指的是原始类代码可以更改的情况，通常会抽象出统一的接口，`实现类`和`代理类`都实现该接口，在`代理类`中添加逻辑代码；还是以`Stack`耗时统计为例，我们可以抽象出`IStack`类，创建`Stack`类实现`IStack`接口以完成功能，创建`StackProxy`类同样实现`IStack`接口以完成耗时统计逻辑；

`代理模式`的原理和代码实现都不难掌握，接下来一起来看`3.2`中的代码示例

### 2、代码示例

#### 2.1 内部类的实现

```java
/*统一接口类*/
public interface IStack {

    void push(int val);

    int pop();

}

/*实现类*/
public class Stack implements IStack {

    private Node root;
    private int size = 0;

    @Override
    public void push(int val) {
        root = new Node(val, root);
        size++;
    }

    @Override
    public int pop() {
        if (root != null) {
            Node temp = root;
            root = root.next;
            size--;
            return temp.val;
        }
        throw new EmptyStackException();
    }

    private static final class Node {
        int val;
        Node next;
        public Node(int val, Node next) {
            this.val = val;
            this.next = next;
        }
    }
}

/*代理类*/
public class StackProxy implements IStack {

    private final IStack stack;

    public StackProxy(IStack stack) {
        this.stack = stack;
    }

    @Override
    public void push(int val) {
        long start = System.currentTimeMillis();
        stack.push(val);
        System.err.println("push:" + (System.currentTimeMillis() - start));
    }

    @Override
    public int pop() {
        long start = System.currentTimeMillis();
        int res = stack.pop();
        System.err.println("pop:" + (System.currentTimeMillis() - start));
        return res;
    }

}

/*使用示例*/
public class Test {

    @org.junit.Test
    public void main() {
        IStack stack = new StackProxy(new Stack());
    }

}
```

需求是统计`Stack`中`插入`和`弹出`的方法耗时，倘若上述角色只有`Stack`一个，那么统计耗时就需要耦合在业务代码之中，违背了`单一职责原则`。这时候我们就可以引入`代理模式`来解决这个问题，将`Stack`类分成三个角色(统一接口、实现类、代理类)，将统计逻辑和实际业务解耦，保持实现类的`职责单一`，通过实现`抽象接口`来完成代理工作的被称为`代理模式`的`内部类实现`

#### 2.2 外部类的实现

```java
/*代理类*/
public class StackProxy<E> extends java.util.Stack<E> {

    @Override
    public E push(E item) {
        long start = System.currentTimeMillis();
        E push = super.push(item);
        System.err.println("push:" + (System.currentTimeMillis() - start));
        return push;
    }

    @Override
    public synchronized E pop() {
        long start = System.currentTimeMillis();
        E pop = super.pop();
        System.err.println("pop:" + (System.currentTimeMillis() - start));
        return pop;
    }
}

/*使用示例*/
public class Test {

    @org.junit.Test
    public void main() {
        Stack<Integer> stack = new StackProxy<>();
    }

}
```

接着上面耗时统计的需求，在此示例中，`Stack`类是来自`java.util`包，内部代码不可以进行更改。这种情况便只有创建`StackProxy`继承`java.util.Stack`类，再对每个方法都加入耗时统计的代码来完成需求，这种方式被称为`代理模式`的`外部类实现`

### 3、动态代理

在`2.1`和`2.2`小节中我们发现，在每个方法中，耗时统计的逻辑代码是类似的，根据能自动绝不手动的开发原则，我们可以使用`动态代理`来解决这个问题，这也是实际开发中经常使用的。所谓`动态代理`（Dynamic Proxy），就是我们不事先为每个原始类编写代理类，而是在运行的时候，动态地创建原始类对应的代理类，然后在系统中用代理类替换掉原始类。

如果您使用的是 Java 语言，实现`动态代理`就是件很简单的事情，因为 Java 语言本身就已经提供了`动态代理`的语法。接下来我们看一下如何用Java的`动态代理`来实现上一小节的功能：

```java
/*动态代理类*/
public class StackDynamicProxy {

    public Object createProxy(Object proxyObject) {
        Class[] interfaces = proxyObject.getClass().getInterfaces();
        DynamicProxyHandler handler = new DynamicProxyHandler(proxyObject);
        return Proxy.newProxyInstance(proxyObject.getClass().getClassLoader(), interfaces, handler);
    }

    private static class DynamicProxyHandler implements InvocationHandler {

        private final Object proxyObject;

        public DynamicProxyHandler(Object proxyObject) {
            this.proxyObject = proxyObject;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            long start = System.currentTimeMillis();
            Object result = method.invoke(proxyObject, args);
            System.err.println(method.getName() + ":" + (System.currentTimeMillis() - start));
            return result;
        }
    }
}

/*使用示例*/
public class Test {

    @org.junit.Test
    public void main() {
        StackDynamicProxy proxy = new StackDynamicProxy();
        IStack stack = (IStack) proxy.createProxy(new Stack());
    }

}
```

### 4、源码锚点

代理模式在Android源码中的应用，除了有经典的`retrofit`外，Android Hook、插件化也都使用了`代理模式`，笔者这里简单介绍一下插件化是如何使用`代理模式`做到替换Activity的：

在调用`context.startActivity()`方法启动`Activity`时，最终是由`Instrumentation`类负责创建`Activity`对象的。那么，我们可以创建代理类来替换掉系统的`Instrumentation`对象，在监控到启动的是`AndroidManifest`中占位`Activity`时，替换成插件中的目标`Activity`，大致流程如下：

1. 创建`Instrumentation`的代理类`InstrumentationProxy`

```java
public class InstrumentationProxy extends Instrumentation {

    private final Instrumentation instrumentation;

    public InstrumentationProxy(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
    }
}
```

2. Hook系统的`Instrumentation`

```java
//1. 获取进程中ActivityThread的对象
Class<?> classes = Class.forName("android.app.ActivityThread");
Method activityThread = classes.getDeclaredMethod("currentActivityThread");
activityThread.setAccessible(true);
Object currentThread = activityThread.invoke(null);
Field instrumentationField = classes.getDeclaredField("mInstrumentation");
instrumentationField.setAccessible(true);
Instrumentation instrumentationInfo = (Instrumentation) instrumentationField.get(currentThread);
//2. 创建代理对象，将mInstrumentation实例保存到代理对象中
InstrumentationProxy proxy = new InstrumentationProxy(instrumentationInfo);
//3. 将系统mInstrumentation示例替换为代理对象
instrumentationField.set(currentThread, proxy);
```

3. 代理类中重写`execStartActivity`方法

```java
public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, Activity target, Intent intent, int requestCode, Bundle options) {
  	//...
    ComponentName componentName = intent.getComponent();
    String packageName = componentName.getPackageName();
    String classname = componentName.getClassName();
    if (classname.equals("TargetActivity")) { //判断是否为目标Activity
        intent.setClassName(who, ProxyActivity.class.getCanonicalName()); // 替换为占位的Activity启动
    }
  	//...
}
```

4. 代理类中重写`newActivity`方法，创建真正启动的`Activity`

```java
public Activity newActivity(ClassLoader cl, String className, Intent intent) {
  	//...
    String classnameIntent = intent.getStringExtra(ACTIVITY_RAW);
    String packageName = intent.getComponent().getPackageName(); // 获取Intent中保存的真正Activity包名、类名
    if (className.equals(ProxyActivity.class.getCanonicalName())) {
        ComponentName componentName = new ComponentName(packageName, classnameIntent); // 替换真实Activity的包名和类名
        intent.setComponent(componentName);
        className = classnameIntent;
    }
  	//...
}
```

Hook Instrumentation实现Activity插件化启动小结：

1. Hook系统的`Instrumentation`对象，设置创建的`代理类`
2. 在代理类中修改启动`Activity`的`Intent`，将启动的目标`Activity`替换为`占位Activity`，从而避免注册清单的检查
3. 在代理类中重写`newActivity()`将启动的活动换回真实目标，然后继续执行原有逻辑

`Android`提供的`AIDL跨进程通信`同样也用到了`代理模式`，Client端调用asInterface()保存的interface对象其实就是Server端的代理，[这里](https://github.com/yibaoshan/Blackboard/tree/master/DesignPattern/src/main/java/com/android/designpattern/structural/proxy/binder)有笔者实现的一个小Demo，感兴趣的同学可以点击查看

### 5、小结

最后我们再来复习一下`代理模式`的定义：在不改版原始类的情况下，通过新增代理类的方式，来给原始类加入新的功能

实现方式分为`内部类`和`外部类`，`内部类`使用接口或抽象类，`外部类`使用继承实现

`代理模式`在平时的开发经常被用到，常用在业务系统中开发一些非功能性需求，比如：监控、统计、日志。我们将这些附加功能与业务功能解耦，放到`代理类`统一处理，让开发人员只需要关注业务方面的开发。除此之外，`代理模式`在跨进程通信、插件化等场景中也有使用

此小节涉及到的代码在[这里](https://github.com/yibaoshan/Blackboard/tree/master/DesignPattern/src/main/java/com/android/designpattern/structural/proxy)



## 四、结构型模式：装饰者模式

### 1、模式定义

从代码结构上来看，`装饰模式`和`代理模式`(内部类实现)两者间非常相似，从结构来看它俩不能说是毫无关系，只能说是一模一样；回顾上一小节对`代理模式`的定义：`代理模式`是指在不改变原始类的情况下，通过新增`代理类`的方式，来给原始类加入新的功能

这句话动动俩字就是`装饰模式`：`装饰模式`是指在不改变原始类的情况下，通过新增`装饰类`的方式，加强原始类的属性。

虽然`装饰模式`和`代理模式`结构非常相似，但适用场景中却太不一样；`装饰模式`在日常开发中主要解决继承过于复杂的问题，通过`组合`来替代`继承`，也就是说`装饰模式`是`继承`关系的一种替代方案之一

一般有两种方式可以实现给一个类或对象增加行为：

- **继承**：继承一个现有类可以使得子类在拥有自身方法的同时还拥有父类的方法，相当于给父类增加行为
- **关联**：将一个类的对象嵌入另一个对象中，由另一个对象来决定是否调用嵌入对象的行为以便扩展自己的行为

接下来我们通过`4.2`示例代码模拟的应用场景来解释为什么说`装饰模式`是继承关系的替代方案：

### 2、代码示例

```java
/*衣服-接口类*/
public interface IClothes {
    int getWarmValue();
}

/*衣服-大衣类*/
public class Coat implements IClothes {
    private final IClothes clothes;

    public Coat(IClothes clothes) {
        this.clothes = clothes;
    }

    @Override
    public int getWarmValue() {
        return clothes.getWarmValue() + 50;
    }
}

/*衣服-裤子类*/
public class Pants implements IClothes {
    private final IClothes clothes;

    public Pants(IClothes clothes) {
        this.clothes = clothes;
    }

    @Override
    public int getWarmValue() {
        return clothes.getWarmValue() + 50;
    }
}

/*衣服-衬衫类*/
public class Shirt implements IClothes {
    private final IClothes clothes;

    public Shirt(IClothes clothes) {
        this.clothes = clothes;
    }

    @Override
    public int getWarmValue() {
        return clothes.getWarmValue() + 30;
    }
}

/*使用示例*/
public class Test {
    @Test
    public void main() {
        IClothes bob = new Bob();
      	//在浴室洗澡
        System.out.println("什么都没穿时的保暖值：" + bob.getWarmValue());
        bob = new Shirt(bob);
      	//穿好衣服在家
        System.out.println("穿了件衬衫时的保暖值：" + bob.getWarmValue());
        bob = new Pants(bob);
        System.out.println("又穿了条裤子时的保暖值：" + bob.getWarmValue());
      	//出门约朋友
        bob = new Coat(bob);
        System.out.println("又穿了件外套时的保暖值：" + bob.getWarmValue());
    }

  private class Bob implements IClothes {
    	@Override
    	public int getWarmValue() {
        	return 0;
    	}
	}
}
```

打印结果

```java
什么都没穿时的保暖值：0
穿了件衬衫时的保暖值：30
又穿了条裤子时的保暖值：80
又穿了件外套时的保暖值：130
```

在该示例中，衬衫、裤子、外套等衣物分别实现了`IClothes`接口，不同的衣物保暖程度也不一样，也就是每件衣服增强的属性值都不一样；在不同场景的衣服搭配也不同，若本例不适用`装饰模式`而是使用`继承`的话，各种各样的排列组合可能会躲到爆炸；使用了`装饰模式`就只需要为每件衣服生成一个`装饰类`即可，所以就增加对象功能来说，`装饰模式`比生成子类实现更为灵活。

### 3、源码锚点

在[极客时间：设计模式之美](https://time.geekbang.org/column/intro/250)中王争老师提到了`Java I/O 类`的设计中使用了`装饰模式`，无奈笔者水平有限，无法将王争老师的思想以自己的语言重新表述；笔者平时读/写文件都是通过项目中封装的工具类来操作，`Java I/O`如何设计的笔者确实不是很清楚，关于`Java I/O`类的使用以及用到哪些设计模式笔者会单独写一篇文章来介绍，对此感兴趣的同学可以自行搜索其他资料阅读

除了`Java I/O`外，许多文章提到`Android Context`也是`装饰者模式`，关于这一点笔者倒是有不同的看法


![image_uml_design_pattern_structural_context](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/DesignPattern/src/main/java/com/android/designpattern/structural/blog/imgs/image_uml_design_pattern_structural_context.jpg)
*图片来源：自己画的*

从类图中可以看到，Application、Service、Activity都继承自`ContextWrapper`；虽然`ContextWrapper`和`ContextImpl`都是`Context`的实现类，但在`ContextWrapper`中，所有的方法其实都委托给`mBase`(也就是ContextImpl)来实现；所以，基于现在`Context`的结构，笔者认为`Context`说是`代理模式`可能更为贴切

但是，我们把场景稍微改动一下，假设现在新增`ContextImpl2`角色，增加的目的是为了使在`Application`和在`Activity`所能获取权限不同，`ContextImpl2`的权限更为强大，这样做就更符合`装饰模式`的设定


![image_uml_design_pattern_structural_context2](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/DesignPattern/src/main/java/com/android/designpattern/structural/blog/imgs/image_uml_design_pattern_structural_context2.jpg)
*图片来源：自己画的*

### 4、小结

`装饰模式`来实现扩展比继承更加灵活，`装饰模式`和`代理模式`内部类实现)极为相似，但它们所适用的场景不一样；当看到类似的代码结构时，需要联系上下文才能判断出设计者的意图；另外，`装饰模式`和`代理模式`在项目中所适用阶段也不相同，`代理模式`在任何阶段都可以使用，而`装饰模式`通常在项目前期设计阶段就应该考虑到。

此小节涉及到的代码在[这里](https://github.com/yibaoshan/Blackboard/tree/master/DesignPattern/src/main/java/com/android/designpattern/structural/decorator)



## 五、结构型模式：适配器模式

### 1、模式定义

`适配器模式`的定义是将不兼容的接口转换为可兼容的接口，让原本由于接口不兼容而不能一起工作的类可以一起工作

`适配器模式`有两种实现方式：`类适配器`和`对象适配器`；其中，`类适配器`使用`继承`关系来实现，`对象适配器`使用`组合`关系来实现，两者的区别可以在[这里](https://github.com/yibaoshan/Blackboard/tree/master/DesignPattern/src/main/java/com/android/designpattern/structural/adapter)查看

在实际开发中，`适配器模式`往往充当遗留问题的`实现转换器`，通常在项目后期代码更改代价很高，或者某个功能依赖第三方来实现的情况下使用，举个栗子：

假设我们在一个Android项目中集成了`阿里支付`SDK，在调用登录方法时需要传入`高德地图`提供的位置信息，但是早期项目中已经接入`腾讯地图`，`腾讯地图`能够提供`阿里支付`所需的位置信息，只是入参类型不同接口不兼容。这种场景我们就可以使用`适配器模式`，定义一个包装类，把`腾讯地图`提供的位置信息转换成`阿里支付`需要的类型，这个包装类指的就是`适配器(Adapter)`

### 2、代码示例

```java

/*阿里支付SDK*/
public class AliPay {

  	/*登录方法需要传入位置信息*/
    public boolean login(String id, String pwd, IAMap.AMapParams location) {
        return res;
    }

  	/*高德地图接口*/
	public interface IAMap {

    	AMapParams getAMapParams();

    	class AMapParams {
        	public float aMapLongitude;//经度
        	public float aMapLatitude;//纬度
            }
	}
}

/*腾讯地图SDK*/
public class TencentMap {

    private final TencentMapParams params;

    public TencentMap() {
        params = new TencentMapParams();
        params.tencentMapLongitude = new Random().nextFloat();
        params.tencentMapLatitude = new Random().nextFloat();
    }

  	/*提供腾讯地图的位置信息*/
    public TencentMapParams getTencentMapParams() {
        return params;
    }

    public static class TencentMapParams {
        public float tencentMapLongitude;//经度
        public float tencentMapLatitude;//纬度
    }
}

/*适配器，使用继承实现，也可以改为使用对象实现*/
public class Adapter extends TencentMap implements IAMap {

    @Override
    public AMapParams getAMapParams() {
      	//将腾讯地图提供的位置信息转为高德的位置信息
        AMapParams aMapParams = new AMapParams();
        TencentMapParams tencentMapParams = this.getTencentMapParams();
        aMapParams.aMapLongitude = tencentMapParams.tencentMapLongitude;
        aMapParams.aMapLatitude = tencentMapParams.tencentMapLatitude;
        return aMapParams;
    }
}

/*使用示例*/
public class Test {

    @Test
    public void main() {
        Adapter adapter = new Adapter();
        AliPay aliPay = new AliPay();
        aliPay.login("admin", "admin", adapter.getAMapParams());
    }

}
```

代码示例中有三个角色，`阿里支付`SDK、`腾讯地图`SDK、`适配器Adapter`，其中`适配器`的作用就是将`腾讯地图`提供的`位置`信息转换成`阿里支付`想要基于的`高德地图`接口的位置信息，这就是所谓的不兼容的接口转换为可兼容的接口

### 3、源码锚点

在`Android`领域的大多数博客中，总是以`RecyclerView.Adapter`为例来讲解`适配器模式`，这一点笔者认为可能不是很恰当，在查看了部分`RecyclerView`源码和讲解设计模式的书籍之后，笔者认为`RecyclerView`的`Adapter`虽然名字叫适配器，但它完成的工作说是`桥接模式`可能更适合一些

关于`适配器模式`的源码示例笔者暂时没有找到，若您发现`适配器模式`的源码应用，请到[这里](https://github.com/yibaoshan/Blackboard/issues)告诉我，感谢

### 4、小结

总结一下，`适配器模式`可以简单理解成`转换器`，大部分情况下是作为一种`补偿机制`，用来补救设计上的缺陷，或者像`5.2`代码示例中源码无法更改的情况；使用这种模式算是“无奈之举”，如果在设计初期，我们就能协调规避掉接口不兼容的问题，那这种模式就没有应用的机会了

此小节涉及到的代码在[这里](https://github.com/yibaoshan/Blackboard/tree/master/DesignPattern/src/main/java/com/android/designpattern/structural/adapter)



## 六、结构型模式：桥接模式

### 1、模式定义

`桥接模式`的定义比较简单：将抽象部分与它的实现部分分离，使它们都可以独立地变化。

由于桥接模式的定义比较简短，所以网上关于桥接模式的争议比较多，不管是在中文网站还是英文网站；存在争议的地方在于，`桥接模式`是GoF定义的的**将实现与抽象分离**，还是一个类存在两个(或多个)独立变化的维度，可以通过组合的方式，让这两个(或多个)维度可以独立进行扩展；若是后者，那么它和`策略模式`的区别是什么

目前关于`桥接模式`还没有定论，所以本篇文章根据GoF的原文定义来展开。笔者个人会将`桥接模式`等同于**依赖倒置原则**的实现，包括上文提到的`桥接模式`以及接下来的代码示例都基于此，若和您理解的有偏差，可以跳过此章节

### 2、代码示例

```java
public interface IImageLoader {

    void loadUrlIntoImageView(ImageView view, String url);

}

public class ImageLoader implements IImageLoader {

    @Override
    public void loadUrlIntoImageView(ImageView view, String url) {
      	//do something..
    }
}
```

上面的例子比较简单，但大多数`桥接模式`的实现也都是在此基础上进行扩展，增加更多的类和更多的方法

### 3、小结

`桥接模式`在实际开发中使用的比较多，多数`Android`项目都会使用`桥接模式`来隔离第三方库，以图片加载框架举例，其中，定义属性行为的抽象层放在Base模块，而实现方一般放在Business或APP层

此小节存在争议，笔者另提供其他的资料供您参考：[桥接模式](https://refactoringguru.cn/design-patterns/bridge)、[桥接模式2](https://design-patterns.readthedocs.io/zh_CN/latest/structural_patterns/bridge.html)、[Bridge Pattern](http://wiki.c2.com/?BridgePattern)、[Bridge Design Pattern in Java](https://www.journaldev.com/1491/bridge-design-pattern-java)



## 七、结构型模式：外观模式

### 1、模式定义

`外观模式`是指给一个复杂的系统提供一个统一的外观(接口)，方便调用者使用，不会改变任何接口
以商品下单为例，当服务端接收到客户端的下单请求时，需要检查用户身份，商品库存、价格、邮费、优惠券等信息，`外观模式`从很大程度上提高了客户端使用的便捷性，使得客户端无须关心子系统的工作细节，通过外观角色即可调用相关功能

从`外观模式`的职能来看，笔者认为可能叫做封装模式更合适，因为外观模式貌似也只解决了对外公开方法属性的问题，甚至都不能被称为设计模式



## 八、总结

本文介绍了7大结构型模式中的6种模式，其中，除了`享元模式`和`外观模式`相对独立之外，其他4种设计模式：`代理`、`装饰者`、`适配器`、`桥接`，它们之间的代码结构非常相似。笼统来说，它们都可以称为`Wrapper`模式，也就是通过`Wrapper类`二次封装原始类。

尽管代码结构相似，但这4种设计模式的用意完全不同，也就是说要解决的问题、应用场景、适用的项目阶段不同，这也是它们的主要区别

1. **代理模式**：代理模式在不改变原始类接口的条件下，为原始类定义一个代理类，主要目的是控制访问，而非加强功能，这是它跟装饰器模式最大的不同。
2. **装饰器模式**：装饰者模式同样在不改变原始类接口的情况下，对原始类功能进行增强，并且支持多个装饰器的嵌套使用。
3. **适配器模式**：适配器模式是一种事后的补救策略。适配器提供跟原始类不同的接口，而代理模式、装饰器模式提供的都是跟原始类相同的接口。
4. **桥接模式**：桥接模式的目的是将接口部分和实现部分分离，从而让它们可以较为容易、也相对独立地加以改变。

*区别结论来源：[极客时间：设计模式之美-王争](https://time.geekbang.org/column/intro/250)*

Stack Overflow有关于`代理、适配、适配器、桥接`这4种模式有什么不同的讨论话题，可以点击下面的链接进行查看

https://stackoverflow.com/questions/350404/how-do-the-proxy-decorator-adapter-and-bridge-patterns-differ/350471#350471

组合模式不包含在本文中，想要了解更多的可以点击[这里](https://refactoringguru.cn/design-patterns/composite)

全文完

*再次说明：以上是笔者个人对`结构型模式`的理解，由于`结构型模式`的概念有些抽象(相较于`创建型模式`)，每个人的理解也不尽相同
若您发现笔者的描述有不准确甚至完全错误的地方，请到[这里](https://github.com/yibaoshan/Blackboard/issues)进行反馈，感谢*



## 九、参考资料

- [图说设计模式](https://design-patterns.readthedocs.io/zh_CN/latest/index.html)
- [refactoringguru.cn](https://refactoringguru.cn/design-patterns)
- [极客时间：设计模式之美-王争](https://time.geekbang.org/column/intro/250)
- [《设计模式：可复用面向对象软件的基础》](https://item.jd.com/12623588.html)
- [《Android 源码设计模式解析与实战》-何红辉 / 关爱民](https://item.jd.com/11793928.html)
- [《深入浅出设计模式-LeetCode》](https://leetcode-cn.com/leetbook/detail/design-patterns/)
- [《Head First 设计模式》](https://item.jd.com/10100236.html)
- [Android插件化—高手必备的Hook技术](https://juejin.cn/post/6844903941105270798#comment)