## 漫谈设计模式（三）：行为型模式

## 一、前言

在漫谈设计模式(一)、(二)中分别介绍了创建型模式和结构型模式，每种类型的设计模式侧重点不同
创建型模式关注的是如何创建对象，为工程师提供服务
结构型模式关注的是如何根据业务解耦各个模块/类，最后通过组合/关联的方式在一起工作，服务于业务

今天要介绍的是设计模式专题中的最后一种类型：行为型模式(Behavioral Pattern)
行为型模式关注的是类或对象的职责分配，同样也是为业务提供服务；和结构型模式相比较，行为型模式关注的粒度更小，更像是对结构型模式的补充

行为型模式有11种，数量看起来会比前两种类型要多，不过随着这些年语言特性和开发模式的进化，能够留下来在项目中真正落地的就不多了。
经过筛选，本文将会介绍行为型中的5种模式：

1. **观察者模式(Observer)**
2. **责任链模式(Chain of Responsibility)**
3. **中介者模式(Mediator)**
4. **备忘录模式(Memento)**
5. **策略模式(Strategy)**

以下6种模式不包含在本文中：

1. **命令模式(Command)**：简单的指令封装，参考Java封装概念
2. **解释器模式(Interpreter)**：为了解决业务需求必须存在的方案，个人认为称不上是设计模式，参考Android LayoutInflater
3. **迭代器模式(Iterator)**：源于容器访问，适用场景单一，参考Java iterator接口
4. **模板方法(Template Method)**：规范方法调用顺序，到处都是，参考Android生命周期及各种base层
5. **状态模式(State)**：不同的状态下执行不同的策略，个人认为等同策略模式
6. **访问者模式(Visitor)**：缺乏落地的业务场景(主要是看不懂)

以上观点属于个人理解，若您发现描述有不准确甚至完全错误的地方，请到[这里](https://github.com/yibaoshan/Blackboard/issues)进行反馈，感谢

最后，生产者消费者虽然不在23种设计模式中，但考虑到它的使用范围非常广泛，本文将在最后一章番外篇中介绍

以下，enjoy：

## 二、行为型模式：观察者模式
### 1、模式介绍
观察者模式(Observer Pattern)通常有由至少一个可被观察的对象和多个观察这个对象的观察者组成，当被观察者的状态发生变化时，会通知这些观察者
还有一种做法是增加一个中介角色，也叫发布订阅中心，把被观察者中的订阅和通知的逻辑抽离处理放在发布订阅中心，类似于Android EventBus，这种做法被叫做发布/订阅模式(Publish-Subscribe Design Pattern)
为了方便记忆，本章会把两者区分开来，两个角色的叫观察者模式，三个角色的叫做发布/订阅模式

接下来我们通过类图来看一看两者之间的区别

观察者模式：

我是类图

如图所示，观察者模式一般由至少一个可被观察的对象(示例中的LiveData) ，和多个观察这个对象的观察者(示例中的LiveDataObserver)组成去观察。二者的关系是通过被观察者来建立的，所以在被观察者中，至少要有三个方法：添加观察者、删除观察者、通知消息。
当被观察者将某个观察者添加到自己的观察者列表(observers)后，观察者与被观察者的关联就建立起来了。此后只要被观察者在某种时机触发通知观察者方法时，观察者即可接收到来自被观察者的消息。

发布/订阅模式：

我是类图

如图所示，发布订阅模式是将原先在被观察者中的添加、删除、通知逻辑抽离出来，放在发布订阅中心；观察者和被观察者之间不直接进行通讯，而是发布者将要发布的消息交由中心管理，订阅者也是根据自己的情况，按需订阅中心中的消息。

讲完了两者的区别后，我们通过两个简单的代码示例来分别实现一下观察者模式和发布/订阅模式：

### 2、代码示例

观察者模式：

```java
//定义通知方法及值类型
public interface Observer<T> {
    void onChanged(T t);
}
//被观察者
public class LiveData<T> {
    private final List<Observer<T>> observers = new LinkedList<>();
    
    //添加观察者
    public void addObserver(Observer<T> observer) {
        observers.add(observer);
    }
    
    //删除观察者
    public void remove(Observer<T> observer) {
        observers.remove(observer);
    }
    
    //通知消息
    public void setValue(T t) {
        for (Observer<T> observer : observers) observer.onChanged(t);
    }
}
//观察者
public class LiveDataObserver implements Observer<String>{

    @Override
    public void onChanged(String s) {
        System.out.println("received message:"+s);
    }
}
//测试代码
public class Test {

    @org.junit.Test
    public void main() {
        LiveData<String> liveData = new LiveData<>();
        liveData.addObserver(new LiveDataObserver());
        liveData.setValue("404");
    }

}
```

打印结果：

```java
received message:404
```

以Android LiveData为例，在LiveData中提供添加/删除观察者等一系列方法，当调用setValue改变状态时就会去通知保存在观察者列表(observers)各个观察者

发布/订阅模式：

```java
//定义通知方法及值类型
public interface BroadcastReceiver {
    void onReceive(Object obj);
}
//消息订阅者
public class LoginBroadcastReceiver implements BroadcastReceiver {
    private String pageName;
    public LoginBroadcastReceiver(String pageName) {
        this.pageName = pageName;
    }
    @Override
    public void onReceive(Object obj) {
        System.out.println(pageName+"  :  "+obj);
    }
}
//发布订阅中心
public class LocalBroadcastManager {
    private static final List<BroadcastReceiver> broadcasts = new LinkedList<>();
    
    public static void sendBroadcast(Object obj) {
        for (BroadcastReceiver receiver : broadcasts) receiver.onReceive(obj);
    }

    public static void register(BroadcastReceiver broadcastReceiver) {
        broadcasts.add(broadcastReceiver);
    }

    public static void unregister(BroadcastReceiver broadcastReceiver) {
        broadcasts.remove(broadcastReceiver);
    }
}
//消息发布者
public class LoginPage {
    private HashMap<String, String> dp;
    public LoginPage() {
        dp = new HashMap<>();
        dp.put("admin", "admin");
    }
    
    public void login(String name, String pwd) {
        if (dp.containsKey(name)) {
            if (dp.get(name).equals(pwd)) LocalBroadcastManager.sendBroadcast("successful login");
            else LocalBroadcastManager.sendBroadcast("login failed, access denied");
        } else {
            LocalBroadcastManager.sendBroadcast("login failed, user does not exist");
        }
    }
}
//测试代码
public class Test {

    @org.junit.Test
    public void main() {
        register();
        LoginPage loginPage  = new LoginPage();
        loginPage.login("admin","admin");
    }

    private void register(){
        LoginBroadcastReceiver mainReceiver = new LoginBroadcastReceiver("主页");
        LoginBroadcastReceiver basketReceiver = new LoginBroadcastReceiver("购物车");
        LoginBroadcastReceiver UCReceiver = new LoginBroadcastReceiver("用户中心");

        LocalBroadcastManager.register(mainReceiver);
        LocalBroadcastManager.register(basketReceiver);
        LocalBroadcastManager.register(UCReceiver);
    }
}
```

打印结果：

```java
主页 : successful login
购物车 : successful login
用户中心 : successful login
```

同样的，在发布订阅中心LocalBroadcastManager中也提供添加/删除等一系列方法，和观察者相比较有一点不同的是：任意一个消息发布者都可以通过发布订阅中心来发布消息

### 3、源码锚点

在Android源码中，大部分的事件监听都是使用观察者模式，所以我们随便挑一个记住就行了，比如OnClickListener

发布/订阅模式有经典的Android EventBus，如果在代码中发现有watch、watcher、observe、observer、listen、listener、dispatch、on、event、register这类单词出现的地方，很有可能是在使用`观察者模式`或`发布订阅`的思想

### 4、小结

2.2的代码示例实现了观察者和发布订阅的简化版，在实际应用中，对于以上二者的实现可能会更加的复杂，所以只需理解两种模式的设计思想即可，我们来简单回顾一下：

观察者模式由观察者和被观察者两个角色组成，一旦发生被观察者的状态/数据发生变化，被观察者就会通知在观察者列表中的各个观察者

发布订阅模式是在观察者模式的基础上增加一个发布订阅角色，加入这个角色的最重要作用就是解耦，将被观察者和观察者分离，使得它们之间的依赖性更小，发布者的发布动作和订阅者的订阅动作相互独立，无需关注对方，消息派发由发布订阅中心负责

## 三、行为型模式：责任链模式

### 1、模式介绍

责任链顾名思义是一种基于链式结构实现的设计模式，对于链式结构来说，每个节点都可以被拆开再连接，因此，链式结构具有很好的灵活性。在责任链模式中，我们可以把链上的每一个节点看作是一个对象，不同的对象拥有不同的处理逻辑，将一个请求从链式的首端发出，沿着链的路径依次传递，每一个注册到链上的处理者可以选择要不要消费当前的请求

在Android中的

### 3、源码锚点

责任链模式多使用于框架的设计中，我们可以利用责任链模来提供框架的扩展点，这样就能够让使用者在不修改框架源码的情况下，复用和扩展框架的功能，还是以square公司的**OkHttp**框架来举例：

我们知道，Okhttp使用方法是通过OkHttpClient的newCall方法创建了一个Call对象，并调用execute方法发起同步请求或者调用enqueue方法发起异步请求；

这其中，每一个Call对象就代表一个网络请求，它的实现类只有一个RealCall，我们这里重点关注RealCall是如何使用责任链模式的

```java
package okhttp3;

final class RealCall implements Call {
    
    @Override
    public Response execute() throws IOException {
        //...
        try {
            client.dispatcher().executed(this);
            return getResponseWithInterceptorChain();
        } finally {
            client.dispatcher().finished(this);
        }
    }

    Response getResponseWithInterceptorChain() throws IOException {
        // Build a full stack of interceptors.
        List<Interceptor> interceptors = new ArrayList<>();
        interceptors.addAll(client.interceptors());
        interceptors.add(new RetryAndFollowUpInterceptor(client));
        interceptors.add(new BridgeInterceptor(client.cookieJar()));
        interceptors.add(new CacheInterceptor(client.internalCache()));
        //...
        Interceptor.Chain chain = new RealInterceptorChain(interceptors, transmitter, null, 0,
                originalRequest, this, client.connectTimeoutMillis(),
                client.readTimeoutMillis(), client.writeTimeoutMillis());
        try {
            Response response = chain.proceed(originalRequest);
            return response;
        } catch (IOException e) {
            //...
        }
    }
}
```

代码不是很复杂，就是 加加加 拦截器，然后组装成一个chain类，调用proceed方法，得到响应报文response。

```java
package okhttp3.internal.http;
public final class RealInterceptorChain implements Interceptor.Chain {
    public Response proceed(Request request, Transmitter transmitter, Exchange exchange) throws IOException {
    // Call the next interceptor in the chain.
    RealInterceptorChain next = new RealInterceptorChain(interceptors, transmitter, exchange,
        index + 1, request, call, connectTimeout, readTimeout, writeTimeout);
    Interceptor interceptor = interceptors.get(index);
    Response response = interceptor.intercept(next);
    return response;
  }
}
```

```java
public final class Interceptor implements Interceptor {
    Request request = chain.request(); //对request请求做些处理，请求报文加密之类就是在这一步
    Response response = chain.proceedrequest);//处理返回结果，比如统一报错拦截
    return response;
}
```

提醒一下，OkHttp从4.0转为Kotlin实现，对Kotlin代码比较吃力的同学可以将分支切换到okhttp_3.14.x，源码点[这里](https://github.com/square/okhttp)

### 4、小结

责任链模式和观察者模式有一点相似，同样都是链式结构，区别在于观察者允许接收者动态地订阅或取消接收请求，而责任链通常不会这么做；另一个不相同的是：责任链上的每个处理者都会持有下一个处理者，而在观察者中，一般是由观察者列表来管理每个处理者

需要根据场景来选择不同的设计模式

## 四、行为型模式：中介者模式

## 五、行为型模式：备忘录模式

## 六、行为型模式：策略模式

### 1、模式定义

策略模式(Strategy Pattern)：在一个系统中，不同对象对同一行为会有不同的结果，便可使用策略模式

策略模式是一个很容易理解的设计模式，在我看来，策略模式甚至可以直接等同于Java语言中的多态；

策略模式通常有两种实现方法：

1. 子类继承父类(extends）
2. 类实现相同接口(implements)

## 七、番外篇：生产者-消费者模式
### 1、模式介绍

生产者-消费者模式虽然不在23种设计模式之列，但我认为它在业务中的重要性不亚于23种设计模式中任何一种
无论是Android客户端的Handler机制，还是后端的各种MQ消息中间件，包括java.util.concurrent包的线程池，他们的设计思想都是基于生产者-消费者模式
简单一句话概括什么是生产者消费者模式：多个进程/线程共享一个阻塞队列，生产者负责push任务进队列，消费者负责取出任务去执行

我是类图

### 2、源码示例

笔者是Android工程师，所以本章节我们简单来探讨一下Handler机制的设计
Android Handler机制

### 3、小结

实际上，不只是Android，其他操作系统，即使在多线程设备中，应用程序用户界面也始终是单线程的，即UI线程(User Interface Thread)，比如iOS和Windows
对显示内容的任何更改都需要通过单个"接入点"进行协调，这可以防止多个线程尝试同时更新同一像素

## 八、总结

至此，设计模式三大类型都已经介绍完了，我们再来简单回顾一下：

在设计业务的时候，结构型和行为型相比而言，前者讲思想，后者讲行为

这篇文章耗时超出笔者预期了，大部分时间都花在理解每种设计模式诞生的背景、能够解决哪些问题、以及能否在项目中落地
举个例子，策略模式是什么，经过查阅好几本书籍和主流博客后笔者认为，选择不同的策略的这个行为本身，叫做策略模式，但凡有一点意义，也不至于一点意义都没有

其中策略模式和责任链模式在实际开发中比较常用，通常用于在不改变源码的情况下服用和扩展框架的功能，比如使用策略模式更换图片加载库，或者使用责任链来拦截网络做请求验签


行为型模式的数量看起来比较多，命令模式和解释器模式，稍有开发经验的工程师，为了代码的可读性，但随着语言特性和开发模式的进化，能够留下来在项目中真正落地的就不多了

## 九、参考资料

[Wilson712：理解【观察者模式】和【发布订阅】的区别](https://juejin.cn/post/6978728619782701087)

[flyingcr：经典并发同步模式：生产者-消费者设计模式](https://zhuanlan.zhihu.com/p/73442055)