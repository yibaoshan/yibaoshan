
# 一、成因

我们的应用都是跑在操作系统之上的，在 Android 平台上，操作系统提供了诸如 Activity、Service、Receiver、ContentProvider 等组件给开发者使用

Android 系统对这些组件进行管理和调度，其中就包括 超时处理，也就是 看门狗 的设计：如果你的组件在规定时间内未响应，出于用户体验角度考虑，系统会弹窗提示用户，这个应用可能已经嘎了，要不要退出？

看门狗监控的目标有这么几个：

- 启动 Service 服务但在 onStartCommand 里面耗时超过 20s
- 接收到广播却在 onReceive 中耗时超过 10s
- ContentProvider create 超过 10s，query 方法超 5s
- 以及最重要的，input 事件 5s 内未被消费

其中，对于 ams 负责的几个系统组件，比如 启动服务，会在发要求启动过服务的消息给应用的 ActivityThread 类的 handler 处理时，会在另外发送延迟消息给 watchdog 线程，如果在规定时间内，应用进程有返回消息告知完成，那么，移出延迟消息，否则，延迟消息触发 anr

input 事件要稍微麻烦一点，native 的 InputDispatcher#loopOnce() 负责检查 waitQueue() 等待对列中，是否包含超时未处理完的消息，如果有，通知 IMS ，IMS 通知 AMS ，从而触发 anr

- 未处理完是指，input 事件通过 socket 跨进程通信，双方互相持有 InputChannel
- 当 View 的 onTouchEvent() 或 Activity 的 dispatchTouchEvent() 方法执行完毕并返回时，应用程序的 InputEventReceiver 持有的 InputChannel 会发消息通知 InputDispatcher
- 此时 InputDispatcher 才会修改此次 input 事件的状态为处理完成
- 触发 InputDispatcher#loopOnce() 检查的时机有，有新的 input 事件产生需要分发，或者，某个输入事件被处理完成

# 二、解决和监控

目前我们自建监控只能感知 ANR 次数，计算 ANR 率，具体的 trace 文件国内是由 bugly 查看，海外是 sentry 和 firebase 平台

Android 11 以下，我们有两套方案，都是利用 Handler 机制

1. 通过设置 Looper#setMessageLogging() 设置 Printer，监听每个 msg 执行的时间，我们目前的标准比较宽松，超过 3s 认为发生了卡顿，超过 5s 认为发生 ANR
2. 应用的看门狗程序，开一个子线程，每间隔 1s 发送一个延迟 5s 的消息给 handler，这个消息是为了改变 atomicBool 值状态，同时子线程 while 读取，判定是否超时，IdleHandler 执行的部分我们暂时忽略了，因为还有其他三方兜底（所以也没有使用监控 SIGQUIT 的方案）

Android 11 以上，我们使用了官方提供的 ApplicationExitInfo 获取上一次用户退出的原因，如果是 ANR，主动上报到面板。缺点是数据的时效性不行，得用户二次打开以后我们才能收到通知

分享几个 case 

- 滥用 SP 导致的 ANR

我之前碰到一个工程的 sp 文件只有一个，什么都往里面填，这就导致即使调用的 apply() 线上依旧有在 QueuedWork#waitToFinish() 卡死的情况，尤其是低端机

- 首先，只有一个 sp 文件肯定是不合理的，这部分代码做了改动并增加了迁移方案
- 然后是问题定位，为什么会 apply() 会 anr？apply 会提交任务到 QueueWork，ActivityThread 处理 pause/stop Activity 或者 stopService 时会调用 QueuedWorkwaitToFinish() 等待 sq 执行完成，如果遇到大数据场景就挂了
