## Overview
1. **LocalBroadcastManager介绍**
2. **LocalBroadcastManager源码解析**
3. **LocalBroadcastManager总结**

## 一、LocalBroadcastManager介绍

### 1、LocalBroadcastManager是什么

在Android中，`Broadcast`是一种广泛运用的在应用之间传输信息的方式

`Broadcast`本身是**进程间通信**，发送的消息其他APP可以监听，同时其他应用也可以通过不断的发送广播来攻击你的APP，应用的安全性无法保证

为了解决这个问题，`LocalBroadcastManager`就应运而生了

`LocalBroadcastManager`是**Android X库**中提供的工具包，和`Broadcast`相比较，它的优点在于：

> 1、无需进行进程间通信，效率更高
>
> 2、只在应用内传播，无需考虑其他应用在收发我的广播时带来的任何安全问题

综上，如果只是想在应用内通信，那么可以选择**本地广播**作为应用的事件总线

### 2、LocalBroadcastManager使用方式

- LocalBroadcastManager对象的创建

  > LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance( context ) ;

- 注册广播接收器

  > LocalBroadcastManager.registerReceiver( broadcastReceiver , intentFilter );

- 发送广播

  > LocalBroadcastManager.sendBroadcast( intent ) ;

- 发送同步广播

  > LocalBroadcastManager.sendBroadcastSync ( intent ) ;

- 取消注册广播接收器

  > LocalBroadcastManager.unregisterReceiver( broadcastReceiver );

## 二、LocalBroadcastManager源码解析

`LocalBroadcastManager`是基于**发布/订阅模式**来设计的，`LocalBroadcastManager`本身是**发布订阅中心**，提供订阅、取消订阅、发布消息的功能，后续的文章中提到的**订阅者**即表示的是**广播接收器**，**消息事件**指的是**过滤器**的`Action`

**在开始阅读`LocalBroadcastManager`源码之前，让我们先来认识一下`LocalBroadcastManager`的成员属性，理解各个成员属性的含义，对接下来源码的阅读会有非常大的帮助**

### 1、LocalBroadcastManager成员属性

#### 1. LocalBroadcastManager的成员变量

| 类型                                                  | 名称                        | 说明                      |
| ----------------------------------------------------- | --------------------------- | ------------------------- |
| Context                                               | mAppContext                 | Application的Context      |
| HashMapBroadcastReceiver, ArrayList<ReceiverRecord>> | mReceivers                  | 广播接收者/订阅者集合     |
| HashMap&lt;String, ArrayList<ReceiverRecord&gt;&gt;   | mActions                    | 订阅事件集合              |
| ArrayList<BroadcastRecord>                           | mPendingBroadcasts          | 待执行分发事件的集合      |
| Handler                                               | mHandler                    | 使用MainLoop创建的Handler |
| int                                                   | MSG_EXEC_PENDING_BROADCASTS | Message的标识             |

在以上的**成员变量**中，我们需要重点关注的是`mReceivers`和`mActions`

##### 1.1 **mReceivers**

上一节我们介绍了`LocalBroadcastManager`是基于**发布/订阅模式**设计的，**事件**发生时需要通知所有的**订阅者**，那么这里必然有一个保存所有**订阅者**的集合，在`LocalBroadcastManager`中这个集合就是`mReceivers`

到这里事情本该结束了，但当我们尝试发送一个**事件**时就会发现：一个**订阅者**可以订阅多个**事件**(`Action`)，不同的**订阅者**也可以订阅同一个**事件**(`Action`)。

当一个**事件**发生后，每次都要先去遍历**订阅者集合**(`mReceivers`)，再从每个**订阅者**订阅的**事件集合**(`actions`)中匹配是否订阅了正在发生的**事件**，伪代码：

```java
    public void sendBroadcast(Intent intent){
       //遍历订阅者集合
        for(Receiver receiver :mReceivers){
           //遍历订阅者订阅的事件集合
            for(Action action : receiver.actions){
               if(intent.action == action){
                   //do something
                }
            }
        }
    }
```

显然，这样做的时间效率并不高，为了解决这个问题，`LocalBroadcastManager`新增了一个**事件集合**：`mActions`

##### 1.2 **mActions**

在`LocalBroadcastManager`中，`mActions`表示的就是**事件集合**；其中，**事件**`Action`作为集合的`key`，对应的`value`是订阅这个**事件**的**订阅者**们，这样，每次发送**事件**时，只需要去**事件集合**查找对应的**订阅者**们，通知它们即可，**订阅者**和**事件**(`Action`)的关系如下图：


![image_android_component_local_broadcast_manager_receiver_and_action_relation.jpg](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/c2f4ea2c28064b83b2cc15802a3cea9a~tplv-k3u1fbpfcp-watermark.image?)

#### 2. 内部类：ReceiverRecord

| 类型              | 名称         | 说明                                           |
| ----------------- | ------------ | ---------------------------------------------- |
| IntentFilter      | filter       | 广播接收的过滤器                               |
| BroadcastReceiver | receiver     | 广播接收者                                     |
| boolean           | broadcasting | 无意义标识                                     |
| boolean           | dead         | 描述订阅者状态，这个广播接收器是不是解除注册了 |

`ReceiverRecord`是`LocalBroadcastManager`的内部类，存在的意义是包装**广播接收器**，给**广播接收器**增加一些**属性**

#### 3. 内部类：BroadcastRecord

| 类型                       | 名称      | 说明                       |
| -------------------------- | --------- | -------------------------- |
| Intent                     | intent    | 发送的广播                 |
| ArrayList<ReceiverRecord> | receivers | 已经匹配上的广播接收者集合 |

`BroadcastRecord`中记录的是待分发的**订阅者**元素

### 2、LocalBroadcastManager成员方法

在`2.1`小节中我们把`LocalBroadcastManager`的**成员属性**都介绍完了，这里再唠叨一下，重点关注`mReceivers`和`mActions`这两个集合，广播的**注册**和**解除注册**都是操作这俩集合

在`2.2`小节中，我们将会介绍`LocalBroadcastManager`的各个**成员方法**的**功能**以及**如何实现**的，`LocalBroadcastManager`方法数量不多，算上**构造函数**一共也才7个，接下来我们就先从**构造函数**开始讲起：

#### 1. 构造方法

```java
private LocalBroadcastManager(Context context) {
    mAppContext = context;
    mHandler = new Handler(context.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_EXEC_PENDING_BROADCASTS:
                    executePendingBroadcasts();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };
}
```

`LocalBroadcastManager`被设计成**全局单例**，所以它的**构造函数**是`private`修饰的，在`LocalBroadcastManager`的**构造函数**中一共做了两件事：

1. **保存Context，注意这里保存的是Application的上下文，接下来的getInstance方法介绍里也会提到这一点**
2. **创建Handler**

> 注意看创建`Handler`的时候使用的是`MainLooper`，也就是说将来通过这个`Handler`提交的消息都会添加到主消息队列，然后再由`MainLooper`分发给当前`Handler`，那么在`executePendingBroadcasts`中派发消息的时候，里面的代码都是运行在**Main线程**了

`Handler`里面的逻辑也比较简单，接收到`Message`后调用`executePendingBroadcasts()`方法来**分发消息**，由于使用了`MainLooper`，所以在广播接收器`BroadcastReceiver`的`onReceive`函数中，是可以进行**UI操作**的，比如这样：

```java
new Thread(() -> {
    LocalBroadcastManager.getInstance(null).registerReceiver(new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //执行UI操作
        }
    },new IntentFilter());
}).start();
```

示例代码中在子线程中注册了一个**广播接收器**，在里面做执行**UI**的操作是完全没问题的，因为`onReceive()`方法最终运行在Main线程

#### 2. getInstance()：获取实例

```java
public static LocalBroadcastManager getInstance(@NonNull Context context) {
    synchronized (mLock) {
        if (mInstance == null) {
           //使用Application的上下文创建实例
            mInstance = new LocalBroadcastManager(context.getApplicationContext());
        }
        return mInstance;
    }
}
```

`getInstance()`方法只做一件事：**检查`mInstance`实例是否为空，为空的话创建一个新的对象赋值给`mInstance`**

> 这里调用`context`的g`etApplicationContext`()方法，获取的是`Application`的上下文，这样就不用担心传入生命周期短的组件造成内存泄漏的问题了

**我们细看这段代码会发现，LocalBroadcastManager只要初始化过一次之后，再次传进来的`context`其实是没有用到的**

这时候你可能会想：**既然用不到，那我是不是可以在应用创建之初调用LocalBroadcastManager.getInstance()方法初始化实例，之后在其他的地方使用时不传context呢？**

答案是**当然可以**，**一旦创建`LocalBroadcastManager`实例后，我们可以在任意线程调用`getInstance()`且不传入`context`来获取实例发送广播啥的**

> 如果你使用Kotlin语言开发，因为`context`被@NonNull注解修饰，直接传null的话编译器会不通过~
>
> 使用Java语言开发的同学可以试一试

#### 3. registerReceiver()：注册广播

```java
public void registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
    synchronized (mReceivers) {
        //point 1
        ReceiverRecord entry = new ReceiverRecord(filter, receiver);
        //1. 获取订阅者的过滤器集合，目的是给这个订阅者新增一个过滤器
        ArrayList<ReceiverRecord> filters = mReceivers.get(receiver);
        if (filters == null) {
            filters = new ArrayList<>(1);
            mReceivers.put(receiver, filters);
        }
        //point 2
        filters.add(entry);//point 2 这里需要注意，因为entry是重新创建的，所以多次调用时，哪怕传入的接收器和过滤器是相同的，在发送广播时也会回调接收器多次
        //解析过滤器要监听的事件(Action)
        for (int i = 0; i < filter.countActions(); i++) {
            String action = filter.getAction(i);
            //2. 获取事件(Action)绑定的订阅者集合，目的是给这个事件增加一个订阅者
            ArrayList<ReceiverRecord> entries = mActions.get(action);
            if (entries == null) {
                entries = new ArrayList<>(1);
                mActions.put(action, entries);
            }
            entries.add(entry);
        }
    }
}
```

注册广播的关键步骤注释都已经标好了，总结一下在`registerReceiver()`方法中一共完成了两件事：

1. **向订阅者集合添加一条数据，增加一个订阅者**
2. **遍历订阅者要订阅的事件集合，把当前的订阅者绑定到订阅的事件上去**

> 这里的订阅者指的是广播接收器`(BroadcastReceiver`)，事件集合指的是广播接收器的过滤器(`IntentFilter`)包含的`actions`

在日常开发中调用`registerReceiver()`方法注册广播时，有一点需要注意，我们回头看**注释**标注`point 1`、`point 2`的地方会发现，不管传入的**广播接收器**和**过滤器**是否已经存在**订阅者集合**中，在`point 1`的位置总会重新创建`ReceiverRecord`来描述**广播接收器**和**过滤器**

什么意思呢

**当你在注册广播时小手一抖，不小心按了`Ctrl+D`复制了一行**

```java
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //do something * 手抖次数
        }
    };
    
    private IntentFilter filter = new IntentFilter();
    
    public void test(){
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,filter);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,filter);//不小心手抖出来的
    }
```

**那么当事件发生时，广播接收器会收到多个回调，手抖多少次就会收到多少次~**

#### 4. unregisterReceiver()：解除注册

```java
public void unregisterReceiver(@NonNull BroadcastReceiver receiver) {
    synchronized (mReceivers) {
        //1. 获取订阅者绑定的过滤器集合，并将当前订阅者从订阅者集合中删除
        final ArrayList<ReceiverRecord> filters = mReceivers.remove(receiver);
        if (filters == null) {
            return;
        }
        for (int i = filters.size() - 1; i >= 0; i--) {
            final ReceiverRecord filter = filters.get(i);
            filter.dead = true;//宣告当前广播接收器死亡
            //2. 遍历当前订阅者订阅的事件集合，再将它从每个事件绑定的订阅者集合中删除
            for (int j = 0; j < filter.filter.countActions(); j++) {
                final String action = filter.filter.getAction(j);
                //找到这个事件绑定的所有订阅者
                final ArrayList<ReceiverRecord> receivers = mActions.get(action);
                if (receivers != null) {
                    //这里的倒序遍历应该是Google的小优化，通常最后注册的广播都会被优先删除，因为当前Activity被kill解除注册后就返回上一级页面了
                    for (int k = receivers.size() - 1; k >= 0; k--) {
                        final ReceiverRecord rec = receivers.get(k);
                        if (rec.receiver == receiver) {
                            rec.dead = true;//我觉得这一步多余了，因为这个订阅者在方法入口时就已经宣告死亡了，这里直接将订阅者删除就行了
                            receivers.remove(k);
                        }
                    }
                    //安全检查，删除掉当前订阅者后，这个事件没有订阅者愿意监听了，那么从事件集合中删除
                    if (receivers.size() <= 0) {
                        mActions.remove(action);
                    }
                }
            }
        }
    }
}
```

在`unregisterReceiver()`解除注册广播的方法中，同样也是做了两件事：

1. **把订阅者从订阅者集合中删除**
2. **遍历全局的事件集合(mActions)，把订阅者从绑定的事件集合中删除**

这里和广播的注册流程是一样的，无非一个是增，一个是删，没什么需要特别注意的地方

#### 5. sendBroadcast()：发送广播

```java
public boolean sendBroadcast(@NonNull Intent intent) {
    synchronized (mReceivers) {
        //匹配规则详情
        final String action = intent.getAction();
        final String type = intent.resolveTypeIfNeeded(
                mAppContext.getContentResolver());
        final Uri data = intent.getData();
        final String scheme = intent.getScheme();
        final Set<String> categories = intent.getCategories();

        //获取当前事件的所有订阅者们
        ArrayList<ReceiverRecord> entries = mActions.get(intent.getAction());
        if (entries != null) {
            //receivers集合里面保存的是，一圈遍历匹配下来，符合规则的订阅者们
            ArrayList<ReceiverRecord> receivers = null;
            for (int i = 0; i < entries.size(); i++) {
                ReceiverRecord receiver = entries.get(i);
                //point 1
                if (receiver.broadcasting) {
                    continue;
                }
                //规则匹配过程
                int match = receiver.filter.match(action, type, scheme, data,
                        categories, "LocalBroadcastManager");
                if (match >= 0) {
                    if (receivers == null) {
                        receivers = new ArrayList<>();
                    }
                    receivers.add(receiver);
                    //point 2
                    receiver.broadcasting = true;
                }
            }

            if (receivers != null) {
                //point 3
                for (int i = 0; i < receivers.size(); i++) {
                    receivers.get(i).broadcasting = false;
                }
                //将订阅者们添加到待执行的任务集合中
                mPendingBroadcasts.add(new BroadcastRecord(intent, receivers));

                //point 4 防止重复发消息，代价是要遍历消息队列里所有消息 ummmm..
                if (!mHandler.hasMessages(MSG_EXEC_PENDING_BROADCASTS)) {
                    mHandler.sendEmptyMessage(MSG_EXEC_PENDING_BROADCASTS);
                }
                return true;
            }
        }
    }
    return false;
}
```

发送广播的方法中就只做了一件事：**从订阅事件集合(`mActions`)中找到符合`intent`条件的订阅者们，并将它们放入待执行集合(`mPendingBroadcasts`)**

> 注意哦，虽然方法名称叫做发送广播，但是其实里面并没有发送的动作，只是找出符合发送规则的订阅者们丢进待执行集合，等待`Handler`来执行

这里有两个槽点要说一下：

**一是`point 1/2/3`标注的`broadcasting`变量，压根就没用到，不知道存在的意义是什么**

**二是`point 4`标注的防止重复发消息的设计，咱创建一个bool类型的变量来标识不行嘛，为啥要想不开去遍历消息队列一个个检查**

#### 6. sendBroadcastSync()：发送同步广播

```java
//从方法名称就可以看出这是一个同步方法，调用者会阻塞到消息分发给所有订阅者才会返回
public void sendBroadcastSync(@NonNull Intent intent) {
    if (sendBroadcast(intent)) {
        executePendingBroadcasts();
    }
}
```

`sendBroadcastSync()`方法代码量不多，一共做了两件事：

1. **调用`sendBroadcast()`方法，将符合条件的订阅者放入待执行集合(`mPendingBroadcasts`)，等待执行**
2. **调用`executePendingBroadcasts()`方法，立刻执行消息分发**

**这里需要重点关注的点时：由于`sendBroadcastSync()`方法内部调用了`executePendingBroadcasts()`立刻执行了消息分发，所以，各个广播接收器的onReceive()函数运行的线程，取决于广播发送者所在的线程**

什么意思呢

**我们知道，当调用`sendBroadcast()`方法发送广播时，`executePendingBroadcasts()`方法最终是由`Handler`来调用的，也就是说不管广播接收器是在哪个线程注册的，都会切换到Main线程来执行`onReceive()`方法中的代码**

**而当广播的发送者可以直接调用`executePendingBroadcasts()`分发消息时，性质就不一样了！！！**

```java
public void test(){
    //子线程发送同步广播
    new Thread(() -> {
        LocalBroadcastManager.getInstance(null).sendBroadcastSync(new Intent("action"));
    }).start();
}
```

**如上，当发送者处于子线程调用发送同步广播方法时，广播接收器的`onReceive()`方法也运行在这个子线程**

**这时候，如果在`onReceive()`方法中执行操作UI的代码，那你将会收到异常：`Only the original thread that created a view hierarchy can touch its views.`**

#### 7. executePendingBroadcasts()：派发广播

```java
void executePendingBroadcasts() {
    while (true) {
        //保存待分发的订阅者集合
        final BroadcastRecord[] brs;
        synchronized (mReceivers) {
            final int N = mPendingBroadcasts.size();
            if (N <= 0) {
                return;
            }
            brs = new BroadcastRecord[N];
            mPendingBroadcasts.toArray(brs);
            mPendingBroadcasts.clear();
        }
        for (int i = 0; i < brs.length; i++) {
            final BroadcastRecord br = brs[i];
            final int nbr = br.receivers.size();
            for (int j = 0; j < nbr; j++) {
                final ReceiverRecord rec = br.receivers.get(j);
                if (!rec.dead) {
                    //分发事件
                    rec.receiver.onReceive(mAppContext, br.intent);
                }
            }
        }
    }
}
```

`executePendingBroadcasts()`的职责就是分发广播，在源码中也只有两个地方调用

一是**构造函数**的`Handler`，二就是`sendBroadcastSync()`方法，两者调用区别在`2.2.6`小节已经讲过了，这里就不再赘述

### 3、小结

介绍完`LocalBroadcastManager`所有的**成员属性**和**成员方法**后，我们可以总结**本地广播**使用的几个特点：

1. **本地广播基于发布/订阅模式实现，通信范围只能在当前进程，若组件在`manifest`文件中指定运行在其他进程，就无法使用本地广播通信了**
2. **`Handler`的加入让本地广播有切换到主线程执行代码的能力，不管注册广播和创建广播接收器的动作执行在哪个线程，最终的回调函数都会切换到Main线程**
3. **`LocalBroadcastManager`在初始化后，`getInstance(context)`的`context`参数可以不传**
4. **`sendBroadcastSync()`发送同步广播最终的广播接收器的回调函数是运行在广播发送者线程的，要小心使用**



## 三、总结

在本篇文章中经常把**广播接收器**称为**订阅者**，之所以这样称呼是因为`LocalBroadcastManager`的设计与**发布/订阅模式**太相似了，呐，你看**Android开发者官网**也是这样介绍的


![image_android_component_local_broadcast_manager_android_developer.jpg](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/a962816164794b70bf2a4a8380435eca~tplv-k3u1fbpfcp-watermark.image?)

`LocalBroadcastManager`目前在开发者官网已经Google声明为**弃用状态**，猜测可能因为**任何组件**都可以使用**本地广播**来发送、注册与解除注册，甚至不需要在组件内使用，**任意线程**都可以获取**本地广播**的**实例**来操作

若APP应用内完全依靠**本地广播**来通信，对整个工程来说，如何管理这些**广播接收器**是个问题，若再因为疏忽大意忘记手动**解除绑定**，那么还会造成**内存泄漏**的问题

不过不管是否弃用，`LocalBroadcastManager`内部的设计思想依旧值得学习

全文完