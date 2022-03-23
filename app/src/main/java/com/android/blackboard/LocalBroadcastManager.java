package com.android.blackboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public final class LocalBroadcastManager {

    private static final class ReceiverRecord {
        final IntentFilter filter;
        final BroadcastReceiver receiver;
        boolean broadcasting;
        boolean dead;

        ReceiverRecord(IntentFilter _filter, BroadcastReceiver _receiver) {
            filter = _filter;
            receiver = _receiver;
        }

    }

    private static final class BroadcastRecord {
        final Intent intent;
        final ArrayList<ReceiverRecord> receivers;

        BroadcastRecord(Intent _intent, ArrayList<ReceiverRecord> _receivers) {
            intent = _intent;
            receivers = _receivers;
        }
    }

    private final Context mAppContext;

    //持有所有订阅者的集合
    private final HashMap<BroadcastReceiver, ArrayList<ReceiverRecord>> mReceivers = new HashMap<>();
    //持有所有订阅事件的集合
    private final HashMap<String, ArrayList<ReceiverRecord>> mActions = new HashMap<>();

    private final ArrayList<BroadcastRecord> mPendingBroadcasts = new ArrayList<>();

    static final int MSG_EXEC_PENDING_BROADCASTS = 1;

    private final Handler mHandler;

    private static final Object mLock = new Object();
    private static LocalBroadcastManager mInstance;

    public static LocalBroadcastManager getInstance(@NonNull Context context) {
        synchronized (mLock) {
            if (mInstance == null) {
                mInstance = new LocalBroadcastManager(context.getApplicationContext());
            }
            return mInstance;
        }
    }

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

    /**
     * 注册广播监听，发布/订阅模型中的订阅动作
     *
     * @param receiver 广播接收器-订阅者
     * @param filter   过滤器，监听什么事件的广播
     *                 <p>
     *                 这个方法一共完成了两件事：
     *                 1. 向订阅者集合添加一条新纪录，增加一个订阅者
     *                 2. 由于需要事件(Action)作为发送广播的匹配规则，所以要解析过滤器中包含的事件集合，那么我们就需要一个事件集合，集合元素为订阅这个事件的订阅者们
     *                 第二件事就是解析添加进来的订阅者要订阅哪些事件，把这个订阅者绑定到订阅的事件
     */
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

    /**
     * 两件事：
     * 1. 把订阅者从订阅者集合中删除
     * 2. 把订阅者从事件集合中删除
     */
    public void unregisterReceiver(@NonNull BroadcastReceiver receiver) {
        synchronized (mReceivers) {
            //获取订阅者绑定的过滤器集合，并将当前订阅者从订阅者集合中删除
            final ArrayList<ReceiverRecord> filters = mReceivers.remove(receiver);
            if (filters == null) {
                return;
            }
            for (int i = filters.size() - 1; i >= 0; i--) {
                final ReceiverRecord filter = filters.get(i);
                filter.dead = true;//宣告死亡，这一步本来可以免掉，但为了防止
                //遍历当前订阅者订阅的事件集合，再将它从每个事件绑定的订阅者集合中删除
                for (int j = 0; j < filter.filter.countActions(); j++) {
                    final String action = filter.filter.getAction(j);
                    //找到这个事件绑定的所有订阅者
                    final ArrayList<ReceiverRecord> receivers = mActions.get(action);
                    if (receivers != null) {
                        //这里的倒序遍历应该是Google的小优化，通常最后注册的广播都会被优先删除，因为当前Activity被kill解除注册返回上一级页面了
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

    /**
     * 发送广播，该方法做了1件事：
     * 1. 从订阅事件集合(mActions)中找到符合intent条件的订阅者们，并将它们放入待执行集合(mPendingBroadcasts)
     *
     * 调用此方法发送的广播，不管发布者处于UI线程或其他子线程，消息的执行都将切换到UI线程
     * */
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
                //receivers里面保存的是，一圈匹配下来，符合规则的订阅者们
                ArrayList<ReceiverRecord> receivers = null;
                for (int i = 0; i < entries.size(); i++) {
                    ReceiverRecord receiver = entries.get(i);
                    if (receiver.broadcasting) {//point 1，
                        continue;
                    }
                    //规则匹配
                    int match = receiver.filter.match(action, type, scheme, data,
                            categories, "LocalBroadcastManager");
                    if (match >= 0) {
                        if (receivers == null) {
                            receivers = new ArrayList<>();
                        }
                        receivers.add(receiver);
                        receiver.broadcasting = true;
                    }
                }

                if (receivers != null) {
                    //假动作
                    for (int i = 0; i < receivers.size(); i++) {
                        receivers.get(i).broadcasting = false;
                    }
                    //将订阅者们添加到待执行的任务集合中
                    mPendingBroadcasts.add(new BroadcastRecord(intent, receivers));

                    //防止重复发消息，代价是要遍历消息队列里所有消息 ummmm..
                    if (!mHandler.hasMessages(MSG_EXEC_PENDING_BROADCASTS)) {
                        mHandler.sendEmptyMessage(MSG_EXEC_PENDING_BROADCASTS);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 发送同步广播，该方法做了两件事：
     * 1. 从订阅事件集合(mActions)中找到符合intent条件的订阅者们，并将它们放入待执行集合(mPendingBroadcasts)
     * 2. 调用executePendingBroadcasts立刻执行消息分发
     *
     * 注意，因为消息分发方法执行在消息发布者线程，也就是调用发送广播的线程
     * 所以，如果是非UI线程调用该方法，那么各个订阅者但凡在onReceive回调函数中操作UI，就over了..
     *
     * */
    public void sendBroadcastSync(@NonNull Intent intent) {
        if (sendBroadcast(intent)) {
            executePendingBroadcasts();
        }
    }

    /**
     * 执行分发消息任务
     * */
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
}
