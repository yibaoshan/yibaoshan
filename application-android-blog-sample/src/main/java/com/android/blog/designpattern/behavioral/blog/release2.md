## 漫谈设计模式（三）：行为型模式

## 一、前言

在漫谈设计模式(一)、(二)中分别介绍了`创建型模式`和`结构型模式`，每种类型的设计模式侧重点不同
`创建型模式`关注的是如何创建对象，为工程师提供服务
`结构型模式`关注的是如何根据业务解耦各个模块/类，最后通过组合/关联的方式在一起工作，服务于业务

今天要介绍的是设计模式专题中的最后一种类型：`行为型模式(Behavioral Pattern)`
`行为型模式`关注的是类或对象的职责分配，同样也是为业务提供服务；和`结构型模式`相比较，`行为型模式`关注的粒度更小，更像是对`结构型模式`的补充

`行为型模式`有11种，数量看起来会比前两种类型要多，不过随着这些年语言特性和开发模式的进化，能够留下来在项目中真正落地的就不多了。
经过筛选，本文将会介绍行为型中的4种模式：

1. **观察者模式(Observer)**
2. **责任链模式(Chain of Responsibility)**
3. **中介者模式(Mediator)**
4. **策略模式(Strategy)**

以下7种模式不包含在本文中：

1. **命令模式(Command)**：简单的指令封装，参考Java封装概念
2. **解释器模式(Interpreter)**：为了解决业务需求必须存在的方案，个人认为称不上是设计模式，参考Android LayoutInflater
3. **迭代器模式(Iterator)**：源于容器访问，使用场景单一，参考Java iterator接口
4. **模板方法(Template Method)**：规范方法调用顺序，到处都是，参考Android生命周期及各种base层
5. **状态模式(State)**：不同的状态下执行不同的策略，个人认为等同策略模式
6. **访问者模式(Visitor)**：看不懂/头大.jpg (´･_･`)
7. **备忘录模式(Memento)**：纯属业务需求，参考Android Canvas的save和restore方法

以上观点属于个人理解，若您发现描述有不准确甚至完全错误的地方，请到[这里](https://github.com/yibaoshan/Blackboard/issues)进行反馈，感谢

最后，`生产者消费者`虽然不在23种设计模式中，但考虑到它的使用范围非常广泛，本文将在最后一章番外篇中介绍

以下，enjoy：

## 二、行为型模式：观察者模式
### 1、模式介绍
`观察者模式(Observer Pattern)`通常有由至少一个`可被观察`的对象和多个观察这个对象的`观察者`组成，当`被观察者`的状态发生变化时，会通知这些`观察者`
还有一种做法是增加一个`中介角色`，也叫`发布订阅中心`，把`被观察者`中的订阅和通知的逻辑抽离处理放在`发布订阅中心`，类似于Android EventBus，这种做法被叫做`发布/订阅模式(Publish-Subscribe Design Pattern)`
为了方便记忆，本章会把两者区分开来，两个角色的叫`观察者模式`，三个角色的叫做`发布/订阅模式`

接下来我们通过类图来看一看两者之间的区别

**观察者模式**：

![image_uml_design_pattern_behavioral_observer_livedata](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/DesignPattern/src/main/java/com/android/designpattern/behavioral/blog/imgs/image_uml_design_pattern_behavioral_observer_livedata.jpg)

如图所示，`观察者模式`一般由至少一个可`被观察`的对象(示例中的LiveData) ，和多个观察这个对象的`观察者`(示例中的LiveDataObserver)组成去观察。二者的关系是通过`被观察者`来建立的，所以在`被观察者`中，至少要有三个方法：**添加观察者**、**删除观察者**、**通知消息**。
当被`观察者`将某个`观察者`添加到自己的`观察者列表(observers)`后，`观察者`与`被观察者`的关联就建立起来了。此后只要`被观察者`在某种时机触发通知观察者方法时，`观察者`即可接收到来自`被观察者`的消息。

**发布/订阅模式**：

![image_uml_design_pattern_behavioral_observer_broadcast](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/DesignPattern/src/main/java/com/android/designpattern/behavioral/blog/imgs/image_uml_design_pattern_behavioral_observer_broadcast.jpg)

如图所示，`发布/订阅模式`是将原先在`被观察者`中的**添加**、**删除**、**通知**的逻辑抽离出来，放在`发布订阅中心`；`观察者`和被`观察者`之间不直接进行通讯，而是发布者将要发布的消息交由`发布订阅中心`管理，`订阅者`也是根据自己的情况，按需订阅`发布订阅中心`中的消息。

讲完了两者的区别后，我们通过两个简单的代码示例来分别实现一下`观察者模式`和`发布/订阅模式`：

### 2、代码示例

**观察者模式**：

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

**发布/订阅模式**：

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

同样的，在发布订阅中心LocalBroadcastManager中也提供添加/删除等一系列方法，和`观察者`相比较有一点不同的是：任意一个消息发布者都可以通过`发布订阅中心`来发布消息

### 3、源码锚点

在Android源码中，大部分的事件监听都是使用`观察者模式`，所以我们随便挑一个记住就行了，比如OnClickListener

`发布/订阅模式`有经典的Android EventBus，如果在代码中发现有watch、watcher、observe、observer、listen、listener、dispatch、on、event、register这类单词出现的地方，很有可能是在使用`观察者模式`或`发布订阅`的思想

### 4、小结

2.2的代码示例实现了`观察者`和`发布/订阅`的简化版，在实际应用中，对于以上二者的实现可能会更加的复杂，所以只需理解两种模式的设计思想即可，我们来简单回顾一下：

`观察者模式`由`观察者`和`被观察者`两个角色组成，一旦发生`被观察者`的状态/数据发生变化，`被观察者`就会通知在`观察者列表`中的各个`观察者`

`发布/订阅模式`是`在观察者模式`的基础上增加一个`发布订阅角色`，加入这个角色的最重要作用就是解耦，将`被观察者`和`观察者`分离，使得它们之间的依赖性更小，`发布者`的发布动作和`订阅者`的订阅动作相互独立，无需关注对方，消息派发由`发布订阅中心`负责

小结完

## 三、行为型模式：责任链模式

### 1、模式介绍

`责任链模式(Chain Of Responsibility Design Pattern)`：将`请求`的发送和接收解耦，让多个`接收对象`都有机会处理这个`请求`。将这些接收对象串成一条链，并沿着这条链传递这个`请求`，直到链上的某个`接收对象`能够处理它为止。

根据以上GoF对`责任链模式`的定义，一旦某个`处理器`能处理这个请求，就不会继续将请求传递给后续的`处理器`了，事实上，在实际的开发中会有不允许请求中断的情况，每个`处理器`处理完成后需要继续向下传递，这个请求最终被所有的`处理器`都处理一遍，所以`责任链模式`的实现大体上可以分成两种：

1. **允许中断请求：Android事件分发机制、Android有序广播**
2. **不中断请求：Android OKhttp**

我们接着来看`责任链模式`的角色分配，在一个完整的`责任链模式`中，至少要包含两个角色：

1. **Handler：抽象处理者角色，声明一个请求处理的方法，并在其中保持一个对下一个处理节点Handler对象的引用**
2. **ConcreteHandler：具体处理者角色，对请求进行处理，如果不能处理则将该请求转发给下一个节点上的处理对象**

责任链模式UML类图：

![image_uml_design_pattern_behavioral_corl](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/DesignPattern/src/main/java/com/android/designpattern/behavioral/blog/imgs/image_uml_design_pattern_behavioral_corl.jpg)

### 2、代码示例

```java
//抽象处理者
public abstract class Handler {

    protected Handler next;

    public abstract void handleRequest(String msg);

}
//具体的处理者1
public class ConcreteHandler1 extends Handler {
    
    @Override
    public void handleRequest(String msg) {
        if (msg.equals("ConcreteHandler1")) System.out.println("ConcreteHandler1 handled");
        else if (next != null) next.handleRequest(msg);
    }
}
//具体的处理者2
public class ConcreteHandler2 extends Handler {

    @Override
    public void handleRequest(String msg) {
        if (msg.equals("ConcreteHandler2")) System.out.println("ConcreteHandler2 handled");
        else if (next != null) next.handleRequest(msg);
    }
}
//测试类
public class Test {

    public void main() {
        ConcreteHandler1 handler1 = new ConcreteHandler1();
        ConcreteHandler2 handler2 = new ConcreteHandler2();
        handler1.next = handler2;
        handler2.next = handler1;
        handler1.handleRequest("ConcreteHandler2");
    }
}
```

打印结果

```java
ConcreteHandler2 handled
```

### 3、源码锚点

`责任链模式`多使用于框架的设计中，我们可以利用`责任链模式`来提供框架的`扩展点`，这样就能够让使用者在不修改框架源码的情况下，复用和扩展框架的功能，以`Android OkHttp`举例：

我们知道，`OkHttp`使用方法是通过`OkHttpClient`的`newCall`方法创建了一个`Call`对象，并调用`execute`方法发起`同步请求`或者调用`enqueue`方法发起`异步请求`

这其中，每一个`Call`对象就代表一个网络请求，它的实现类只有一个`RealCall`：

```java
package okhttp3;

//https://github.com/square/okhttp
//OkHttp从4.0转为Kotlin实现，对Kotlin代码比较吃力的同学可以将分支切换到okhttp_3.14.x
final class RealCall implements Call {
    
  	//1. 调用execute发起请求
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

  	//2. 获取响应报文，基于责任链模式
    Response getResponseWithInterceptorChain() throws IOException {
        // Build a full stack of interceptors.
        List<Interceptor> interceptors = new ArrayList<>();
        interceptors.addAll(client.interceptors());
        interceptors.add(new RetryAndFollowUpInterceptor(client));
        interceptors.add(new BridgeInterceptor(client.cookieJar()));
      	//...
      	//最终调用发起
        interceptors.add(new CallServerInterceptor(forWebSocket));
        
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

`getResponseWithInterceptorChain()`中的代码不是很复杂，就是将用户传入的`拦截器`收集起来再加上默认的一些`缓存拦截器`、`连接拦截器`等，然后组装成一个`RealInterceptorChain`类，再调用其`proceed`方法，得到响应报文`response`

在`proceed`方法中，会不断的查找下一个`拦截器`，然后再调用`拦截器`的`intercept()`方法

```java
package okhttp3.internal.http;
public final class RealInterceptorChain implements Interceptor.Chain {
    public Response proceed(Request request, Transmitter transmitter, Exchange exchange) throws IOException {
    // Call the next interceptor in the chain.
    RealInterceptorChain next = new RealInterceptorChain(interceptors, transmitter, exchange,
        index + 1, request, call, connectTimeout, readTimeout, writeTimeout);
    //找到下一个拦截器
    Interceptor interceptor = interceptors.get(index);
    Response response = interceptor.intercept(next);
    return response;
  }
}
```

最后在`intercept`方法中，我们就可以对`请求报文`和`响应报文`做处理

```java
public final class Interceptor implements Interceptor {
  @Override public Response intercept(Chain chain) throws IOException {
    //处理request请求，报文加密之类就是在这一步
    Request request = chain.request(); 
    //处理返回结果，比如统一报错拦截
    Response response = chain.proceed(request);
    return response;
  }
}
```

至此，整个调用链路就结束了，最后我们用一张图来总结拦截器责任链：

![image_uml_design_pattern_behavioral_corl_okhttp](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/DesignPattern/src/main/java/com/android/designpattern/behavioral/blog/imgs/image_uml_design_pattern_behavioral_corl_okhttp.jpg)

### 4、小结

`责任链模式`通常由链式结构组成，对于链式结构来说，每个节点都可以被拆开再连接(也叫可插拔)，因此，`责任链模式`生来就具有很好的灵活性

我们可以把链上的每一个节点看作是一个`对象`，不同的`对象`拥有不同的处理逻辑；将一个`请求`从链式的首端发出，沿着链的路径依次传递，每一个注册到链上的`处理者`可以选择要不要消费当前的`请求`，当前节点处理完成后，可以再根据业务场景选择要不要向下传递，直到最后一个`处理者`处理完成或者某个节点`终止`传递

小节完

## 四、行为型模式：中介者模式

### 1、模式介绍

`中介者模式(Mediator)`定义了一个单独的`中介对象`，来封装一组对象之间的交互；将这组对象之间的交互委派给与`中介对象`交互，来避免对象之间的直接交互。

当对象之间的交互操作很多且每个对象的行为操作都依赖彼此时，为防止在修改一个对象的行为时，同时涉及修改很多其他对象的行为，可采用`中介者模式`，来解决紧耦合问题。

举个例子来解释一下：在客户端电商首页开发总，要求`banner`轮播切换的同时要修改`背景图`，还要注意色值来动态改变`状态栏`颜色深浅，`下拉`展示`二楼`和左右滑动切换`tab`时要渐变透明度，浏览商品时`状态栏`要复原同时`banner`停止滚动，对于客户端开发来说，当一个页面的控件改变需要同步给其他若干个控件时，其它控件状态改变也需要互相之间同步时，这代码逻辑写起来简直要爆炸

![image_uml_design_pattern_behavioral_mediator_before](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/DesignPattern/src/main/java/com/android/designpattern/behavioral/blog/imgs/image_uml_design_pattern_behavioral_mediator_before.jpg)

这个时候，我们就需要引入一个`中间人`，把每个控件状态改变都交给同一人处理，每个控件控制权也交由`中间人`

加入了`中介者`之后的逻辑图：

![image_uml_design_pattern_behavioral_mediator_after](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/DesignPattern/src/main/java/com/android/designpattern/behavioral/blog/imgs/image_uml_design_pattern_behavioral_mediator_after.jpg)

有了`中介者`之后，砍去了各个类之间的直接联系，将杂乱无章的逻辑关系改为`星状`的逻辑关系，自己的状态控制和状态改变全部交由中介者来处理，大大减少各个类的逻辑处理

### 2、小结

`中介者模式`从某种角度来说像是算法里面的贪心策略，回顾4.1中的例子，当一个页面的控件改变需要同步给其他若干个控件时，单单维护控件通信一项就已经很麻烦了，这时候会自然而然的选择最优解，即引入`中间层`然后将每个控件状态改变都交给它来处理，正因为如此，在客户端开发中，`中介者模式`常常与`观察者模式`同时出现

小节完

## 五、行为型模式：策略模式

### 1、模式定义

`策略模式`，英文全称是` Strategy Design Pattern`，在 GoF 的《设计模式》一书中，它是这样定义的：

> Define a family of algorithms, encapsulate each one, and make them interchangeable. Strategy lets the algorithm vary independently from clients that use it.
>
> 定义一系列算法，将每个算法分别封装起来，并使它们可互换；策略模式可以使算法的变化独立于使用它们的客户端

GoF中还提到，在`策略模式`中，应当由客户端自己决定在什么情况下使用什么具体策略角色，策略模式仅仅封装算法

在翻阅了大量书籍/文章后，笔者认为上面的话也可以这么理解：在一个系统中，不同对象的同一行为会有不同的结果，便可使用策略模式

听起来好像很熟悉，和Java语言中的`多态`解决的场景是重复的，我们可以把`策略模式`等同于`多态`的话理解起来就容易多了

小节完

## 六、番外篇：生产者-消费者模式
### 1、模式介绍

`生产者/消费者模式`虽然不在23种设计模式之列，但我认为它在软件工程的重要性绝不亚于23种设计模式中任何一种
无论是Android客户端的`Handler机制`，还是后端的各种`MQ消息中间件`，他们的设计思想都是基于`生产者/消费者模式`
简单一句话概括什么是`生产者/消费者模式`：多个进程/线程共享一个`阻塞队列`，`生产者`负责push任务进队列，`消费者`负责取出任务去执行

![image_uml_design_pattern_behavioral_producer_consumer_queue](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/DesignPattern/src/main/java/com/android/designpattern/behavioral/blog/imgs/image_uml_design_pattern_behavioral_producer_consumer_queue.jpg)

### 2、代码示例

我们用代码实现UML类图的模式，拆分一下上面的图，有三个角色：`消息队列`、`生产者`、`消费者`

首先是`消息队列`，这个比较简单，使用集合框架中现成的队列就行了，如下：

```java
static final Queue<String> messageQueue = new ArrayDeque<>();
```

接着要有至少一个`生产者`，负责生产消息，示例这里的`生产者`是Java Scanner，负责不断获取控制台输入的消息，然后添加到消息队列当中

```java
final static class ProducerThread extends Thread {
    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("please input:");
      	//生产者线程获取控制台消息
        while (true) {
            String line = scanner.next();
            if (line.equals("exit")) break;
          	//获取到消息后添加到共享的消息队列，同时唤醒可能正在等待的消费者线程
            synchronized (messageQueue) {
                messageQueue.add(line);
                messageQueue.notify();
            }
        }
    }
}
```

接着要有一个`消费者`，负责消费消息队列中的消息

```java
final static class ConsumerThread extends Thread {

    public ConsumerThread(String name) {
        super(name);
    }

    @Override
    public void run() {
        synchronized (messageQueue) {
            //消费者一直不停的从共享消息队列取消息
            while (true) {
                if (messageQueue.isEmpty()) {
                   messageQueue.wait();// 没有消息则阻塞，等待唤醒
                }
                // 被唤醒后会执行该方法
                execute(messageQueue.poll());
            }
        }
    }

    private void execute(String msg) {
        //do something
        System.out.println(getName() + " take msg:" + msg);
    }

}
```

好了，三个角色都已经有了，下面我们来测试一下

```java
public static void main(String[] args) {
    ProducerThread producerThread = new ProducerThread();
    producerThread.start();//启动生产者线程
    ConsumerThread consumerThread1 = new ConsumerThread("消费者1号");
    ConsumerThread consumerThread2 = new ConsumerThread("消费者2号");
    //启动消费者线程
    consumerThread1.start();
    consumerThread2.start();
}
```

打印结果

```java
please input:
1
消费者1号 take msg:1
2
消费者2号 take msg:2
```

### 3、源码锚点

笔者是Android工程师，所以本章节我们来探讨一下`Android Handler`机制的设计

我们知道，`Andorid Handler`机制由`Handler`、`Looper`、`Message`和`MessageQueue`这4个类组成，他们的职责分工和`6.2`中的代码示例没什么区别：

- **Handler：消息生产者**

  只要是在MainLooper所在线程创建的Handler就会持有共享消息队列MessageQueue，在任意线程中调用该Handler的sendMessage发送的消息都会被添加到共享消息队列中，共享消息队列由Java的线程局部存储机制(ThreadLocal)保证唯一性

- **Looper：消息的消费者**

  loop()方法中会一直轮询消息队列，它的任务就是取消息、取消息和取消息，取到消息后就分发消息、分发消息和分发消息

- **MessageQueue：共享的消息队列**

  在next()方法中，若消息队列为空则会阻塞等待，参考6.2示例中消息队列为空时就挂起当前线程，知道被唤醒

- **Message：不配拥有姓名，只是个消息对象**

这里注意一点，在`Handler`机制中，`Looper`的角色虽然是消费者，但是它只负责从消息队列取消息，不负责处理。`loop()`方法中每次取到消息就交给消息所属的`Handler`类处理

![image_uml_design_pattern_behavioral_producer_consumer_handler](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/DesignPattern/src/main/java/com/android/designpattern/behavioral/blog/imgs/image_uml_design_pattern_behavioral_producer_consumer_handler.jpg)

此小节涉及到的代码在[这里](https://github.com/yibaoshan/Blackboard/blob/master/Notebook/src/main/java/com/android/notebook/android/framework/communication/Test.java)

### 4、小结

`生产者/消费者模式`解耦了`生产者`与`消费者`之间的直接联系，而且由于`共享消息队列`的存在，大多应用在各大多线程异步协作当中
在后端开发中，`共享消息队列`本身就是个缓冲区，所以我们可以调整缓冲区的大小来应对高并发时请求数量太大服务器应付不过来等类似场景
在Android或其他包含用户界面的操作系统中，`共享消息队列`可以保证所有更新界面的操作都只在UI线程进行

小结完

## 七、总结

至此，行为型模式已经全部介绍完了，我们来简单回顾一下：

1. **观察者模式(Observer)**：持有回调列表
2. **责任链模式(Chain of Responsibility)**：OkHttp
3. **中介者模式(Mediator)**：星状连接图
4. **策略模式(Strategy)**：Java多态
5. **命令模式(Command)**：Java封装
6. **解释器模式(Interpreter)**：Android LayoutInflater
7. **迭代器模式(Iterator)**：Java iterator接口
8. **模板方法(Template Method)**：Android生命周期及各种base层
9. **状态模式(State)**：迪迦奥特曼力量/速度形态，等同策略模式
10. **访问者模式(Visitor)**：嗯~看不懂/头大.jpg (´･_･`)
11. **备忘录模式(Memento)**：Android Canvas(save/restore)

`行为型模式`的数量看起来比较多，但毕竟20多年过去了，以前的模式或不适用当前环境，或直接被融入语言/开发模式中，以本章行为型模式来举例：
有为了业务需求必须这么设计的，例如：解释器模式、备忘录
有被融入语言特性的：命令模式(Java封装)、策略模式(Java多态)
有是人都会选择最优解的：中介者模式
还有我认为雷同的，状态模式和策略模式，在实际开发中，不管是从实现角度还是解决问题的场景来看我不认为它们两者有什么区别

有用的几种`行为型模式`中，如`观察者模式`、`责任链模式`主要是解决对象与对象之间职责分配的问题

## 八、参考资料

- [图说设计模式](https://design-patterns.readthedocs.io/zh_CN/latest/index.html)
- [refactoringguru.cn](https://refactoringguru.cn/design-patterns)
- [极客时间：设计模式之美-王争](https://time.geekbang.org/column/intro/250)
- [《设计模式：可复用面向对象软件的基础》](https://item.jd.com/12623588.html)
- [《Android 源码设计模式解析与实战》-何红辉 / 关爱民](https://item.jd.com/11793928.html)
- [《深入浅出设计模式-LeetCode》](https://leetcode-cn.com/leetbook/detail/design-patterns/)
- [《Head First 设计模式》](https://item.jd.com/10100236.html)
- [jimuzz：从设计模式角度看OkHttp源码](https://www.cnblogs.com/jimuzz/p/14536105.html)