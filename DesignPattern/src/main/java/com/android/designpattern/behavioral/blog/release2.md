## 漫谈设计模式（三）：行为型模式

## 一、前言

在漫谈设计模式(一)、(二)中分别介绍了创建型模式和结构型模式，每种类型的设计模式侧重点不同
创建型模式关注的是如何创建对象，为工程师提供服务
结构型模式关注的是如何根据业务解耦各个模块/类，最后通过组合/关联的方式在一起工作，服务于业务

今天要介绍的是设计模式专题中的最后一种类型：行为型模式(Behavioral Pattern)
行为型模式关注的是类或对象的职责分配，同样也是为业务提供服务；和结构型模式相比较，行为型模式关注的粒度更小，更像是对结构型模式的补充

行为型模式有11种，数量看起来会比前两种类型要多，不过随着这些年语言特性和开发模式的进化，能够留下来在项目中真正落地的就不多了。
经过筛选，本文将会介绍行为型中的4种模式：

1. **观察者模式(Observer)**
2. **责任链模式(Chain of Responsibility)**
3. **中介者模式(Mediator)
5. **策略模式(Strategy)**

以下7种模式不包含在本文中：

1. **命令模式(Command)**：简单的指令封装，参考Java封装概念
2. **解释器模式(Interpreter)**：为了解决业务需求必须存在的方案，个人认为称不上是设计模式，参考Android LayoutInflater
3. **迭代器模式(Iterator)**：源于容器访问，使用场景单一，参考Java iterator接口
4. **模板方法(Template Method)**：规范方法调用顺序，到处都是，参考Android生命周期及各种base层
5. **状态模式(State)**：不同的状态下执行不同的策略，个人认为等同策略模式
6. **访问者模式(Visitor)**：看不懂/头大.jpg (´･_･`)
6. **备忘录模式(Memento)**：纯属业务需求，参考Android Canvas的save和restore方法

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

小结完

## 三、行为型模式：责任链模式

### 1、模式介绍

责任链模式(Chain Of Responsibility Design Pattern)：将请求的发送和接收解耦，让多个接收对象都有机会处理这个请求。将这些接收对象串成一条链，并沿着这条链传递这个请求，直到链上的某个接收对象能够处理它为止。

根据以上GoF对责任链模式的定义，一旦某个处理器能处理这个请求，就不会继续将请求传递给后续的处理器了，事实上，在实际的开发中会有不允许请求中断的情况，每个处理器处理完成后需要继续向下传递，这个请求最终被所有的处理器都处理一遍，所以责任链模式的实现大体上可以分成两种：

1. **允许中断请求：Android事件分发机制、Android有序广播**
2. **不中断请求：Android OKhttp**

我们接着来看责任链模式的角色分配，在一个完整的责任链模式中，至少要包含两个角色：

1. **Handler：抽象处理者角色，声明一个请求处理的方法，并在其中保持一个对下一个处理节点Handler对象的引用**
2. **ConcreteHandler：具体处理者角色，对请求进行处理，如果不能处理则将该请求转发给下一个节点上的处理对象**

责任链模式UML类图

我是类图

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

责任链模式多使用于框架的设计中，我们可以利用责任链模来提供框架的扩展点，这样就能够让使用者在不修改框架源码的情况下，复用和扩展框架的功能，以**Android OkHttp**举例：

我们知道，Okhttp使用方法是通过OkHttpClient的newCall方法创建了一个Call对象，并调用execute方法发起同步请求或者调用enqueue方法发起异步请求

这其中，每一个Call对象就代表一个网络请求，它的实现类只有一个RealCall：

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

**getResponseWithInterceptorChain中的代码不是很复杂，就是将用户传入的拦截器收集起来再加上默认的一些缓存拦截器、连接拦截器等，然后组装成一个RealInterceptorChain类，再调用其proceed方法，得到响应报文response**

**在proceed方法中，会不断的查找下一个拦截器，然后再调用拦截器的intercept()方法**

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

**在intercept方法中，我们就可以对请求报文和响应报文做处理**

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

责任链模式通常由链式结构组成，对于链式结构来说，每个节点都可以被拆开再连接(也叫可插拔)，因此，责任链模式生来就具有很好的灵活性

我们可以把链上的每一个节点看作是一个对象，不同的对象拥有不同的处理逻辑；将一个请求从链式的首端发出，沿着链的路径依次传递，每一个注册到链上的处理者可以选择要不要消费当前的请求，当前节点处理完成后，可以再根据业务场景选择要不要向下传递，直到最后一个处理者处理完成或者某个节点终止传递

小节完

## 四、行为型模式：中介者模式

### 1、模式介绍

中介模式（Mediator）定义了一个单独的中介对象，来封装一组对象之间的交互；将这组对象之间的交互委派给与中介对象交互，来避免对象之间的直接交互。

当对象之间的交互操作很多且每个对象的行为操作都依赖彼此时，为防止在修改一个对象的行为时，同时涉及修改很多其他对象的行为，可采用中介者模式，来解决紧耦合问题。

通过引入中介这个中间层，将一组对象之间的交互关系（或者说依赖关系）从多对多（网状关系）转换为一对多（星状关系）。原来一个对象要跟 n 个对象交互，现在只需要跟一个中介对象交互，从而最小化对象之间的交互关系，降低了代码的复杂度，提高了代码的可读性和可维护性。

举个例子来解释一下：电商首页要求banner轮播切换的同时要修改背景图，还要注意色值来动态改变状态栏颜色深浅，下拉展示二楼和左右滑动切换tab时要渐变透明度，浏览商品时状态栏要复原同时banner停止滚动，对于客户端来说，当一个页面的控件改变需要同步给其他若干个控件时，其它控件状态改变也需要互相之间同步，这代码逻辑写起来简直要爆炸，这个时候，我们就需要引入一个中间人，把每个控件状态改变都交给同一人处理，每个控件控制权也交由中间人，看图：

我是图

中介者模式是一种行为模式，在我看来并没有标准的实现方式，所以这里也就不展示UML类图和示例代码

### 2、小结

中介者模式从某种角度来说像是算法里面的贪心策略，回顾4.1中的例子，当一个页面的控件改变需要同步给其他若干个控件时，单单维护控件通信一项就已经很麻烦了，这时候会自然而然的选择最优解，即引入中间层然后将每个控件状态改变都交给它来处理，正因为如此，在客户端开发中，中介者模式常常与观察者模式同时出现

小节完

## 五、行为型模式：策略模式

### 1、模式定义

策略模式，英文全称是 Strategy Design Pattern，在 GoF 的《设计模式》一书中，它是这样定义的：

> Define a family of algorithms, encapsulate each one, and make them interchangeable. Strategy lets the algorithm vary independently from clients that use it.
>
> 定义一系列算法，将每个算法分别封装起来，并使它们可互换；策略模式可以使算法的变化独立于使用它们的客户端

GoF中还提到，在策略模式中，应当由客户端自己决定在什么情况下使用什么具体策略角色，策略模式仅仅封装算法

在翻阅了大量书籍/文章后，笔者认为上面的话也可以这么理解：在一个系统中，不同对象的同一行为会有不同的结果，便可使用策略模式

听起来好像很熟悉，和Java语言中的多态解决的场景是重复的，我们可以把策略模式等同于多态的话理解起来就容易多了

小节完

## 六、番外篇：生产者-消费者模式
### 1、模式介绍

生产者-消费者模式虽然不在23种设计模式之列，但我认为它在软件工程的重要性绝不亚于23种设计模式中任何一种
无论是Android客户端的Handler机制，还是后端的各种MQ消息中间件，包括java.util.concurrent包的线程池，他们的设计思想都是基于生产者-消费者模式
简单一句话概括什么是生产者消费者模式：多个进程/线程共享一个阻塞队列，生产者负责push任务进队列，消费者负责取出任务去执行

我是类图

### 2、源码示例

笔者是Android工程师，所以本章节我们来探讨一下Handler机制的设计

我们干脆利实现一个handler好了，在实现之前，我们要先来理清原有的Handler机制是如何设计的，方便理清思路

我们知道，一个线程代码执行完成后就死了，所以looper的任务就是保证当前线程一直是活的，消息队列可以为空，但线程不能死，死了就没得玩了

looper内部持有消息队列，通过java的ThreadLocal来保证当前线程只有一份队列

你别动，我去拿消息，我去拿消息，我去拿消息，消息是hA，这是你的，hB这是你的

```java
final static class Message {
    int what;
    long when;
    Object obj;
    Handler target;
    Runnable callback;
}
```

```java
final static class MessageQueue {
    PriorityQueue<Message> messages = new PriorityQueue<>();
    
    synchronized void queueMessage(Message msg) {
        messages.offer(msg);
    }

    Message next() {
        for (; ; ) {
            Message msg = messages.peek();
            if (msg != null) {
                synchronized (this) {
                    if (System.currentTimeMillis() >= msg.when) {
                        return messages.poll();
                    } else continue;
                }
            }
        }
    }

}
```

```java
final static class Looper {

    final MessageQueue mQueue;

    private static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal<>();

    private Looper() {
        mQueue = new MessageQueue();
    }

    static Looper getLooper() {
        return sThreadLocal.get();
    }

    static void prepare() {
        if (sThreadLocal.get() != null) throw new RuntimeException("looper only init once");
        sThreadLocal.set(new Looper());
    }

    static void loop() {
        Looper looper = getLooper();
        if (looper == null) throw new RuntimeException("please init looper");
        for (; ; ) {
            Message msg = looper.mQueue.next();
            if (msg == null) return;
            msg.target.dispatchMessage(msg);
        }
    }

}
```

```java
final static class Handler {

    private final Looper mLooper;
    private final MessageQueue mQueue;
    private Callback mCallback;

    public Handler() {
        mLooper = Looper.getLooper();
        if (mLooper == null) throw new RuntimeException("mLooper is null");
        mQueue = mLooper.mQueue;
    }

    void dispatchMessage(Message msg) {
        if (msg.callback != null) {
            msg.callback.run();
        } else {
            if (mCallback != null) {
                if (mCallback.handleMessage(msg)) return;
            }
            handleMessage(msg);
        }
    }

    void sendMessage(Message msg) {
        sendMessageDelayed(msg, 0);
    }

    void sendMessageDelayed(Message msg, int delay) {
        msg.when = System.currentTimeMillis() + delay;
        enqueueMessage(msg);
    }

    void post(Runnable runnable) {
        postDelayed(runnable, 0);
    }

    void postDelayed(Runnable runnable, long delay) {
        Message msg = Message.obtain();
        msg.callback = runnable;
        if (delay < 0) delay = 0;
        msg.when = System.currentTimeMillis() + delay;
        enqueueMessage(msg);
    }

    void enqueueMessage(Message msg) {
        msg.target = this;
        mQueue.queueMessage(msg);
    }

    public interface Callback {
        boolean handleMessage(Message msg);
    }

}
```

### 3、小结

实际上，不只是Android，其他操作系统，即使在多线程设备中，应用程序用户界面也始终是单线程的，即UI线程(User Interface Thread)，比如iOS和Windows
对显示内容的任何更改都需要通过单个"接入点"进行协调，这可以防止多个线程尝试同时更新同一像素

由于消息队列的加入，将生产者类和消费者类进行解耦

## 七、总结

至此，行为型模式已经全部介绍完了，我们再来简单回顾一下：

属于业务需求必须这么设计的：解释器、

方便工程师内部逻辑处理，自然而然选择最优解的：中介者

还有我认为雷同的，状态模式和策略模式，在实际开发中，从实现和解决问题的场景来看我不认为它们有什么区别

策略模式比较有意义，可惜被面向对象的多态概念覆盖了，我个人认为它们两者之间可以画等号

观察者解决的是事件监听的问题

责任链用在框架中，可以给外部提供扩展的接口

生产消费解决的是消息的生产和消费速度不一致的问题，或者可以

在设计业务的时候，结构型和行为型相比而言，前者讲思想，后者讲行为

这篇文章耗时超出笔者预期了，大部分时间都花在理解每种设计模式诞生的背景、能够解决哪些问题、以及能否在项目中落地
举个例子，策略模式是什么，经过查阅好几本书籍和主流博客后笔者认为，选择不同的策略的这个行为本身，叫做策略模式，但凡有一点意义，也不至于一点意义都没有

高频的观察者、责任链、策略、生产者消费者

其中策略模式和责任链模式在实际开发中比较常用，通常用于在不改变源码的情况下服用和扩展框架的功能，比如使用策略模式更换图片加载库，或者使用责任链来拦截网络做请求验签

行为型模式的数量看起来比较多，毕竟20多年过去了，以前的模式或不适用当前环境，或直接被融入语言，开发模式中，无需刻意按模板去实现

服务端工程师必备的Spring，用的就是抽象工厂和工厂模式，回调函数可以简单理解成观察者模式，servlet中的filter使用的是责任链模式，对于第三方的库一律使用代理模式，对于系统中的各个功能模块使用了装饰者模式来进行执行的权限控制和监控。

命令模式和解释器模式，稍有开发经验的工程师，为了代码的可读性，但随着语言特性和开发模式的进化，能够留下来在项目中真正落地的就不多了

## 八、参考资料

[Wilson712：理解【观察者模式】和【发布订阅】的区别

[jimuzz：从设计模式角度看OkHttp源码](https://www.cnblogs.com/jimuzz/p/14536105.html)