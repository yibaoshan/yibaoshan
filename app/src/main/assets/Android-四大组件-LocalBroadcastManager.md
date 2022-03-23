## Overview
1. LocalBroadcastManager介绍
1. LocalBroadcastManager源码解析
1. 总结

## 一、LocalBroadcastManager介绍

### 1、诞生背景

### 2、使用方式

LocalBroadcastManager是典型的发布/订阅模式，LocalBroadcastManager本身是发布订阅中心，提供订阅、取消订阅、发布消息的功能；在后续的介绍过程中提到的订阅者即为广播接收器

## 二、LocalBroadcastManager源码解析

在开始阅读LocalBroadcastManager源码之前，让我们先来认识一下LocalBroadcastManager的成员属性，理解各个成员属性的含义，对接下来源码的阅读会有非常大的帮助

### 1、LocalBroadcastManager成员属性

#### 1. LocalBroadcastManager变量

| 类型                                                    | 名称                        | 说明                      |
| ------------------------------------------------------- | --------------------------- | ------------------------- |
| Context                                                 | mAppContext                 | Application的Context      |
| HashMap<BroadcastReceiver, ArrayList\<ReceiverRecord\>> | mReceivers                  | 广播接收者/订阅者集合     |
| HashMap<String, ArrayList\<ReceiverRecord>>             | mActions                    | 订阅事件集合              |
| ArrayList\<BroadcastRecord>                             | mPendingBroadcasts          | 待执行分发事件的集合      |
| Handler                                                 | mHandler                    | 使用MainLoop创建的Handler |
| int                                                     | MSG_EXEC_PENDING_BROADCASTS | Message的标识             |

在以上所有的成员变量中，我们需要重点关注其中的mReceivers和mActions

##### 1.1 mReceivers

上一届我们介绍了LocalBroadcastManager是基于发布/订阅模式设计的，事件发生时需要通知所有的订阅者，那么这里必然有一个保存所有订阅者的集合，在LocalBroadcastManager中这个集合就是mReceivers

事情到这里本该结束了，但当我们发送一个事件时就会发现：一个订阅者可以订阅多个事件(Action)，不同的订阅者也可以订阅同一个事件(Action)。当事件发生后，每次都要先去遍历订阅者集合(mReceivers)，再从每个订阅者订阅的事件集合(actions)中匹配是否订阅了正在发生的事件，伪代码如下：

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

显然，这样做的时间效率并不高，为了解决这个问题，LocalBroadcastManager新增了一个事件集合：mActions

##### 1.2 mActions

在LocalBroadcastManager中，mActions表示的就是订阅事件集合；其中，事件Action作为集合的key，对应的value是订阅这个事件的订阅者们(广播接收器)，这样，每次发送广播时，只需要去事件集合查找对应的订阅者们，通知它们即可，订阅者和事件(Action)的关系如下图：



![image-20220323170247078](/Users/bob/Library/Application Support/typora-user-images/image-20220323170247078.png)

#### 2. 内部类：ReceiverRecord

| 类型              | 名称         | 说明                                           |
| ----------------- | ------------ | ---------------------------------------------- |
| IntentFilter      | filter       | 广播接收的过滤器                               |
| BroadcastReceiver | receiver     | 广播接收者                                     |
| boolean           | broadcasting | 无意义标识                                     |
| boolean           | dead         | 描述订阅者状态，这个广播接收器是不是解除注册了 |

ReceiverRecord是LocalBroadcastManager的内部类，存在的意义是包装广播接收器，给广播接收器增加一些属性

各个成员变量的含义看表

#### 3. 内部类：BroadcastRecord

| 类型                       | 名称      | 说明                       |
| -------------------------- | --------- | -------------------------- |
| Intent                     | intent    | 发送的广播                 |
| ArrayList\<ReceiverRecord> | receivers | 已经匹配上的广播接收者集合 |

各个成员变量的含义看表

### 2、LocalBroadcastManager成员方法

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

LocalBroadcastManager设计成全局单例，所以它的构造函数是private修饰的，在构造函数一共做了两件事：

1. 保存Context，注意这里保存的是ApplicationContext，接下来的getInstance会提到
2. 创建Handler

> 注意这里创建Handler的时候使用的是MainLooper，也就是说将来通过这个Handler提交的消息都会添加到主消息队列，然后再由MainLooper分发给当前Handler，那么在executePendingBroadcasts中派发消息的时候，里面的代码都是运行在Main线程了

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

getInstance()方法只做一件事：检查mInstance实例是否为空，为空的话创建一个新的对象赋值给mInstance

> 注意这里调用context的getApplicationContext()方法，获取的是Application的上下文，这样就不用担心传入生命周期短的组件造成内存泄漏的问题了

我们仔细看这段代码会发现，只要初始化过一次之后，再次传进来的context其实是没有用到的

这时候你可能会想：**既然用不到，那我是不是可以在Application.onCreate()调用LocalBroadcastManager.getInstance()方法初始化实例，然后在其他的地方使用时不传context呢？**

是的，我们甚至可以在子线程调用**LocalBroadcastManager.getInstance(null)**获取实例来发送广播啥的

> 但如果你使用Kotlin语言开发，因为@NonNull注解的关系，直接传null的话编译器会不通过~
>
> 使用Java语言开发的同学可以试一试

#### 3. registerReceiver()：注册广播

```java
public void registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
    synchronized (mReceivers) {
        ReceiverRecord entry = new ReceiverRecord(filter, receiver);
        //获取订阅者的过滤器集合，目的是给这个订阅者新增一个过滤器
        ArrayList<ReceiverRecord> filters = mReceivers.get(receiver);
        if (filters == null) {
            filters = new ArrayList<>(1);
            mReceivers.put(receiver, filters);
        }
        filters.add(entry);//point 1 这里需要注意，因为entry是重新创建的，所以多次调用时，哪怕传入的接收器和过滤器是相同的，在发送广播时也会回调接收器多次

        //解析过滤器要监听的事件(Action)
        for (int i = 0; i < filter.countActions(); i++) {
            String action = filter.getAction(i);
            //获取事件(Action)绑定的订阅者集合，目的是给这个事件增加一个订阅者
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



#### 4. unregisterReceiver()：解除注册

#### 5. sendBroadcast()：发送广播

#### 6. sendBroadcastSync()：发送同步广播

#### 7. executePendingBroadcasts()：派发广播

使用过本地广播的朋友都知道，不管是发送广播还是注册广播，都要先获取广播管理器：LocalBroadManager

而获取LocalBroadManage的方式只有一个：LocalBroadManager.getInstance（context）

```java
public static LocalBroadcastManager getInstance(Context context) {
    synchronized (mLock) {
        if (mInstance == null) {
            mInstance = new LocalBroadcastManager(context.getApplicationContext());
        }
        return mInstance;
    }
}
```

Google是将LocalBroadManager设计成全局单例的模式，想想也很合理，毕竟都是在同一个APP内使用

不过有一点要注意，如果APP的组件或Application指定在其他进程启动，那么本地广播就无法跨进程使用了，为什么？我们点开源码看一看，LocalBroadManage的构造函数里面都做了些什么

```java

```

2. 

## 三、总结

