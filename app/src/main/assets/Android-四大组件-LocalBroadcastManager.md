## Overview
1. LocalBroadcastManager介绍
1. LocalBroadcastManager使用方法
1. LocalBroadcastManager源码解析

## 一、LocalBroadcastManager

### 1、LocalBroadcastManager的获取方式

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

### 2、LocalBroadcastManager的构造函数

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

构造函数一共做了两件事：

1. 保持入参context，注意这里的context是ApplicationContext
2. 使用MainLooper创建新的Handler，也就是说将来通过该handler提交的消息都会添加到主消息队列，再由主线程Looper派发给当前handler

## 总结

最后我们来总结一下本地广播的注册与发送流程，在开始之前先来看一下LocalBroadcastManager有那些结构

LocalBroadcastManager

| 类型                                                  | 名称               | 说明             |
| ----------------------------------------------------- | ------------------ | ---------------- |
| Context                                               | mAppContext        | 广播接收的过滤器 |
| HashMap<BroadcastReceiver, ArrayList<ReceiverRecord>> | mReceivers         | 广播接收器       |
| HashMap<String, ArrayList<ReceiverRecord>>            | mActions           |                  |
| ArrayList<BroadcastRecord>                            | mPendingBroadcasts |                  |
| Handler                                               | mHandler           |                  |

ReceiverRecord

| 类型              | 名称         | 说明                           |
| ----------------- | ------------ | ------------------------------ |
| IntentFilter      | filter       | 广播接收的过滤器               |
| BroadcastReceiver | receiver     | 广播接收器                     |
| boolean           | broadcasting | 是否正在发送广播               |
| boolean           | dead         | 这个广播接收器是不是解除注册了 |

BroadcastRecord

| 类型                      | 名称      | 说明             |
| ------------------------- | --------- | ---------------- |
| Intent                    | intent    | 广播接收的过滤器 |
| ArrayList<ReceiverRecord> | receivers | 广播接收器       |





