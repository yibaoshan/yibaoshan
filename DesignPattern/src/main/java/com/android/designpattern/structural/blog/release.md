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

注，外观模式和组合模式不包含在本文中，想要了解更多的可以点击这里

## 二、结构型模式：享元模式

### 1、模式定义

享元模式(又称蝇量模式)是对象池的一种实现，它的英文名称叫做Flyweight，代表轻量级的意思。
享元模式用来尽可能减少内存使用量，它适合用于可能存在大量重复对象的场景，来缓存可共享的对象，达到对象共享、避免创建过多对象的效果，这样一来就可以提升性能，是典型的以空间换时间的设计模式。

关于享元模式唯一有[争议](https://blog.csdn.net/wangshihui512/article/details/51453839)的一点是：享元模式是否只是对象池的一种实现，如果是，那么原文中提到的需要关注对象的内部状态和外部状态是什么意思？

换句话说，享元模式和池化技术之间是否可以直接划等号？

要探讨这个问题，我们先来看一下《设计模式》中对享元模式的定义：

> A **flyweight** is a shared object that can be used in multiple contexts simultaneously. The flyweight acts as an independent object in each context—it's indistinguishable from an instance of the object that's not shared. Flyweights cannot make assumptions about the context in which they operate. The key concept here is the distinction between **intrinsic** and **extrinsic** state.  **Intrinsic ** state is stored in the flyweight; it consists of information that's independent of the flyweight's context, thereby making it sharable.  **Extrinsic ** state depends on and varies with the flyweight's context and therefore can't be shared. Client objects are responsible for passing  **extrinsic ** state to the flyweight when it needs it.
>
> —《Design Patterns: Elements of Reusable Object-Oriented Software》
>
> **flyweight**是一个共享对象，它可以同时在多个场景(context)中使用，并且在每个场景中flyweight都可以作为一个独立的对象—这一点与非共享对象的实例没有区别。flyweight不能对它所运行的场景做出任何假设，这里的关键概念是**内部状态**和**外部状态**之间的区别。**内部状态**存储于flyweight中，它包含了独立于flyweight场景的信息，这些信息使得flyweight可以被共享。而**外部状态**取决于flyweight场景，并根据场景而变化，因此不可共享。用户对象负责在必要的时候将**外部状态**传递给flyweight。
>
> —《设计模式：可复用面向对象软件的基础》机械工业出版社

从文中的描述可以看出，作者强调的是对象的内部状态可变，外部状态不可变且不可被共享。究竟什么是对象的内部状态和外部状态？我们一起来看2.2中的代码示例

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

享元对象指的是可以被共享的对象，在2.2的示例代码中，用户信息类就是享元对象。由于享元对象可以被共享，其内部属性(姓名、年龄、国家等)可以被重新赋值，这就叫做对象的内部状态。

在用户信息类的内部状态中，有一个国家属性，这个国家类内部维护一组数据，不公开set()方法。当某一个用户更改自身的国家属性时，只能通过国家名称重新查询国家对象，这就是所谓的享元对象内部属性更改时不影响其外部状态，国家类就叫做用户信息类的外部对象

### 3、源码锚点

看过了2.2小节的示例之后，我们再回头来看看Android中Message的实现

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

从代码中可以看到，Message用链表实现了个复用池，所以，回到2.1中的疑问，Android Message是享元模式吗？笔者认为，是的，只不过Message没有外部状态罢了

除了Android Message外，Java中还有String常量池，int，float等基础类型的包装类型也都使用了享元模式

### 4、小结

享元模式设计思想是通过复用对象，以达到节省内存的目的；关于享元模式的[角色介绍](https://design-patterns.readthedocs.io/zh_CN/latest/structural_patterns/flyweight.html)和[UML类图](https://design-patterns.readthedocs.io/zh_CN/latest/structural_patterns/flyweight.html)本文并没有包含，笔者认为理解设计者的意图，了解设计模式适用的场景以及能够解决哪些问题，才是我们开发者需要关注的重点

最后我们来总结一下享元模式的使用场景：

1. 系统中存在大量的相似对象，可以抽离为外部对象，例如2.2中的国家对象
2. 需要缓冲池的场景，例如Android Message对象，Java基本类型的包装类

说明：有些书籍/资料将享元模式翻译为，这一点需要注意，享元模式和蝇量模式是同一个

此小节涉及到的代码在[这里](https://github.com/yibaoshan/Blackboard/tree/master/DesignPattern/src/main/java/com/android/designpattern/structural/flyweight)

## 三、结构型模式：代理模式

### 1、模式定义

代理模式是指在不改版原始类的情况下，通过新增代理类的方式，来给原始类加入新的功能

代理模式实现的场景分为两种，外部类和内部类。外部类指的是原始类无法进行更改的情况，以经典的耗时统计举例来说，想要统计Stack每个方法的耗时，我们可以创建StackProxy类继承自Stack类，然后在StackProxy中调用Stack的方法同时加入耗时统计的代码。内部类指的是原始类代码可以更改的情况，通常会抽象出统一的接口，实现类和代理类实现该接口，在代理类中添加耗时统计的代码。代理模式的原理和代码实现都不难掌握，接下来笔者通过3.2中的代码示例来解释这段话

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

需求是统计Stack中插入和弹出的方法耗时，倘若上述角色只有Stack一个，那么统计耗时就需要耦合在业务代码之中，很明显违背了单一职责原则。我们可以使用代理模式来解决这个问题，将Stack类分成三个角色(统一接口、实现类、代理类)，这样就可以将统计逻辑和实际业务解耦，保持实现类的职责单一，这也是使用代理模式的优势

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

接着上面耗时统计的需求，在此示例中，Stack类是来自java.util包，内部代码不可以进行更改。这种情况便只有创建StackProxy继承java.util.Stack类，再对每个方法都加入耗时统计的代码来完成需求，这种方式被称为代理模式的外部类实现

### 3、动态代理

在2.1和2.2小节中我们发现，在每个方法中，耗时统计的逻辑代码是类似的，基于能自动生成绝不手动写的开发原则，我们可以利用动态代理来解决这个问题。

所谓动态代理（Dynamic Proxy），就是我们不事先为每个原始类编写代理类，而是在运行的时候，动态地创建原始类对应的代理类，然后在系统中用代理类替换掉原始类。如果您使用的是 Java 语言，实现动态代理就是件很简单的事情，因为 Java 语言本身就已经提供了动态代理的语法：java.lang.reflect.Proxy.Proxy.newProxyInstance()

我们来看一下，如何用 Java 的动态代理来实现刚刚的功能，具体的代码如下：

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

代理模式在Android源码中的应用，除了经典的retrofit外，Android Hook、插件化也都使用了代理模式

#### 1. Android的插件化

我们知道在Android开发中调用context.startActivity()方法启动Activity时，最终是由Instrumentation类负责创建Activity对象的。那么，我们可以创建代理类来替换掉系统的Instrumentation对象，在监控到启动的是AndroidManifest中占位Activity时，这样就可以替换成插件中的目标Activity，大致流程如下：

##### 1.1 创建Instrumentation的代理类InstrumentationProxy

```java
public class InstrumentationProxy extends Instrumentation {

    private final Instrumentation instrumentation;

    public InstrumentationProxy(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
    }
}
```

##### 1.2 Hook系统的Instrumentation

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

##### 1.3 代理类中重写execStartActivity方法

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

##### 1.4 代理类中重写newActivity方法，创建真正启动的Activity

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

1. Hook系统的Instrumentation对象，设置创建的代理类
2. 在代理类中修改启动Activity的Intent，将启动的目标Activity替换为占位Activity，从而避免注册清单的检查
3. 在代理类中重写newActivity（）将启动的活动换回真实目标，然后继续执行原有逻辑

最后，前段时间笔者用xposed解决了公司APP隐私合规中的权限调用自查的问题，Android提供的AIDL跨进程通信同样也用到了代理模式，Client端调用asInterface()保存的interface对象其实就是Server端的代理，笔者实现了一个简单的Demo，感兴趣的同学可以点击[这里](https://github.com/yibaoshan/Blackboard/tree/master/DesignPattern/src/main/java/com/android/designpattern/structural/proxy/binder)查看

### 5、小结

代理类在不改版原始类的情况下，通过新增代理类的方式，来给原始类加入新的功能

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
- [Android插件化—高手必备的Hook技术](https://juejin.cn/post/6844903941105270798#comment)