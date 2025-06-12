# Jetpack 速通之 Lifecycle

Lifecycle 的作用是让某一个类变成 Activity 或 Fragment 的生命周期观察者类，让其拥有感知 Act、Frag 生命周期的能力，核心设计分为三个角色

- LifecycleOwner，一般是 act、frag，被观察者
- LifecycleObserver，观察者，等待生命周期回调
- LifecycleRegistry，被观察者的具体实现，管理观察者并分发事件，当组件生命周期发生改变时，通知观察者

使用方式上

- 写个类继承 LifecycleObserver 然后 addObserver() 注册到想要观察的组件中
- 注解方式已被标记为废弃，接口方式更清晰且避免反射开销

其他

- ProcessLifecycleOwner，可以作为监听前后台切换的手段之一，start 前台 stop 后台
- 新增的观察者会立刻收到当前状态的通知，历史的生命周期不会走，如果有初始化操作，需要自己处理好
- ON_DESTROY 会在 onDestroy() 才调用，所以这里释放资源，不能使用做和 UI 相关的操作

# Activity 实现

androidx.core.app.ComponentActivity 实现 LifecycleOwner 接口，管理和分发交给 lifecycle runtime 包下面有个 ReportFragment 类

> 注意，not androidx.activity.ComponentActivity

API 低于 29 用的是 Fragment 的生命周期，我猜是为了共用 LifecycleRegistry 实现类

dispatch() -> handleLifecycleEvent() -> moveToState() -> sync()

29 以上直接回调，create/start/resume

# Fragment 实现

androidx.fragment.app 实现了 LifecycleOwner 接口，功能同样交给成员变量 LifecycleRegistry 持有观察者并分发事件

handleLifecycleEvent() -> moveToState() -> sync()

fragment 的生命周期依赖 act，不是 ams 直接调度，可以简单看做有生命周期的 View，配合 LiveData 时 注意区分 mViewLifecycleOwner 和 mLifecycleRegistry，即 getLifecycle() 和 getViewLifecycleOwner().getLifecycle()

# ActivityLifecycleCallbacks

create、start、resume、pause、stop、saveInstanceState、destroy 七大核心回调函数

每个回调函数对应三个阶段，pre、ed、post，以 create 举例，它的调用顺序是：

1. onActivityPreCreated()，最先调用，在 Activity 的 onCreate(Bundle) 方法被调用之前触发
2. onCreate(Bundle)，Activity 自己的生命周期方法，来自 AMS
3. onActivityCreated()，在 super.onCreate() 执行后立即触发
4. onActivityPostCreated()，onCreate() 完全执行完以后，包括你在 onCreate() 中写的一系列初始化的工作全部做完