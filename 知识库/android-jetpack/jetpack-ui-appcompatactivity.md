# Jetpack 速通之 AppcompatActivity

现在的项目多多少少都会使用到 jetpack 组件，比如 lifecycle、fragment 啥的，那 Activity 一般只能继承自 AppCompatActivity

在 AppCompatActivity 以上还有几个父类，他们的继承关系如下：

```
Activity
  └── androidx.core.app.ComponentActivity
    └── androidx.activity.ComponentActivity
          └── FragmentActivity
                └── AppCompatActivity
```

1. Activity，android.jar 中 framework 提供，版本取决于你项目的 compileSdk
2. androidx.core.app.ComponentActivity，隐藏类，不推荐直接使用
   - 在 AndroidX Fragment 1.1.0 和 Activity 1.0.0 之前作为临时解决方案存在
   - 实现了 LifecycleOwner 接口，主要功能是通过 ReportFragment 实现生命周期分发
3. androidx.activity.ComponentActivity，属于 androidx.activity 库，是 Jetpack 架构的正式组件，增强版 Activity，实现了好多个接口，比如：
   - ContextAware，context 感知功能，提供一个回调，在 Context 可用时通知你
   - LifecycleOwner，对外提供生命周期感知功能，被观察者的实现
   - ViewModelStoreOwner、HasDefaultViewModelProviderFactory，创建、保存、恢复 ViewModel 用
   - OnBackPressedDispatcherOwner，提供 OnBackPressedDispatcher，支持多级组件（比如包含 Fragment）注册返回处理器，用来替代 onBackPressed()
   - ActivityResultRegistryOwner、ActivityResultCaller，结合 ActivityResultCaller 使用，实现组件化解耦，startActivityForResult 的替代方案
4. FragmentActivity，类如其名，主要是增加对 Fragment 支持
   - 内部持有 FragmentManager，用来添加/删除 Fragment、事务管理以及负责生命周期派发
   - act 的 onSaveInstanceState/onRestoreInstanceState 时负责保存与恢复 Fragment 状态
5. AppCompatActivity，主要增加主题支持，兼容矢量图、夜间模式等一致的 UI 行为支持
   - 持有 AppCompatDelegate 和 Resources（动态替换资源/换肤关键类）
   - AppCompatDelegateImpl 处理 setContentView，将 TextView 替换为 AppCompatTextView

androidx.activity.ComponentActivity 增加的能力一览表

| 接口名                                  | 作用                            | 用途示例                             |
| ------------------------------------ | ----------------------------- | -------------------------------- |
| `ContextAware`                       | Context 可用时回调                 | 初始化依赖 Context 的组件                |
| `LifecycleOwner`                     | 生命周期感知                        | LiveData、LifecycleObserver       |
| `ViewModelStoreOwner`                | 提供 ViewModel 容器               | 使用 ViewModelProvider             |
| `HasDefaultViewModelProviderFactory` | 提供默认 VM 工厂                    | 支持参数 ViewModel 或状态恢复             |
| `SavedStateRegistryOwner`            | 保存/恢复状态                       | 搭配 ViewModel 使用 SavedStateHandle |
| `OnBackPressedDispatcherOwner`       | 分发返回键事件                       | Fragment 回退处理                    |
| `ActivityResultRegistryOwner`        | 管理 startActivityForResult 的注册 | Jetpack Activity Result API 支持   |
| `ActivityResultCaller`               | 注册 launcher 的能力               | `registerForActivityResult` 的基础  |

FragmentActivity 增加的能力一览表

| 能力                                             | 说明                             |
| ---------------------------------------------- | ------------------------------ |
| ✅ Fragment 管理                                  | 内置支持 Fragment 的生命周期、事务、嵌套管理等   |
| ✅ Fragment 生命周期派发                              | 将宿主 Activity 的生命周期同步给 Fragment |
| ✅ 支持嵌套 Fragment、FragmentResult、FragmentManager | Jetpack Fragment 功能集成的核心       |

# 生命周期

- standard，每次启动都新建一个 Activity 实例对象，经历完整的生命周期，create -> start -> resume
- singleTop，如果启动时栈顶已是该 Activity，则重用走 onNewIntent(intent)，否则，重建新的 act 实例，create -> start -> resume
- singleTask，如果栈中启动过，那就重用实例同样走 onNewIntent(intent)，并且，清除其上所有的 Activity，上层页面会走 pause -> stop -> destroy
- singleInstance，和 singleTask 类似，但在 单独的任务栈中，基本不用于常规 Activity，用于通知跳转、广告等特殊场景

页面跳转生命周期变化

```
Activity 1 -> startActivity() 请求起飞 A2

AMS: 塔台收到，请求航班号正确，允许 A2 起飞🛫
   -> schedulePauseActivity 1
   -> scheduleLaunchActivity 2

Activity 1 -> pause
Activity 2 -> create -> start -> resume -> 报告塔台，我方起飞成功 😄

AMS：塔台收到
   -> 发送闲时广播，请求 Activity 1 降落，回到待定位置，等待复飞
   -> 创建 10s 倒计时，如果闲时广播未能发送成功，塔台将会派出战机强行 A1 降落（stop）

Activity 1 -> stop

-----------------------------------我是分割线----------------------------------------

用户返回

A2 -> pause -> stop -> destroy
A1 -> restart -> start -> resume
```

小细节，A2 启动成功走 resume 后，ActivityThread 会告知 AMS ，然后 AMS 要求上个页面进入 stop，对应源码是

- 方法是 ActivityManagerService.activityResumed(token)
- 发起方是 App 进程内的 ActivityThread
- 调用点在 performResumeActivity() → reportResumedToActivityManager()

之所以要这样设计，是因为：

1. Android 不希望多个 Activity 同时处于 resumed 状态，防止动画/输入焦点发生冲突
2. 所以，AMS 必须等到新的 Activity resumed 执行完毕后，才能去 stop 旧的 Activity

小细节 2，如果当前 act 是透明主题或对话框样式（能看到上一个页面），发生页面跳转时，当前 act 仅会执行 pause 不会 stop

另外还有两个 和 View/Window 相关回调（base Android 7.0）

- attach，ActivityThread 中 performLaunchActivity 函数，首次创建 act 对象，第一个调用的函数就是 attach，里面的工作是创建 PhoneWindow 以及创建和 wms 链接
- onCreate，attach() 后调用的函数，一般会调用 setContentView 初始化 View
- onContentChanged，成功将 View 添加到 DecorView
- makeVisible，成功将视图传递给 wms，等待下个 vsync 信号进入绘制显示

其他生命周期小 tips：

- onCreate 和 onStart 的区别是，onCreate 起始化 UI 和数据， onStart 在每次可见时都会执行
- onPause 和 onStop 的区别是，onPause 在有新 Activity 覆盖时调用， onStop 在完全被遮挡时
- 回到桌面或锁屏都是  onPause → onStop
- 旋转屏幕 onPause → onStop → onDestroy → onCreate → onStart → onResume
- 你的应用在后台，因为内存不足被系统强杀进程时，onDestroy 不会被调用，另外，ANR、用户强制停止应用也不会触发 onDestroy
- taskAffinity 

# 常见问题

- onStop() / onDestroy() 延迟 10s 问题，MessageQueue 消息没停过，ActivityThread 中 IdleHandler 未得到执行，setMessageLogging 或者 dump looper 查看是谁发的什么消息，导致 idle 消息无法得到执行
- 设置不同的 taskAffinity + launchMode 可以让两个 Activity 不在一个任务栈，并且可以用 Intent.FLAG_ACTIVITY_NEW_TASK 手动创建任务栈。
- View.post 和 ViewTreeObserver 常用来获取 view 宽高，一般 view#post 多点，相较于 handler#post，View.post 会等待 View attach 完成，而 Handler 不保证 View 状态
- Dialog 是一个独立的 Window，由 WindowManager 管理，但是，它也需要 act 的 context 才能显示，其他诸如 application、service 的 context 就不行
  - 这主要是 WindowManage 通过 token 来确认身份，决定它在屏幕上的显示顺序、所属层次以及处理输入事件，而 dialog 自己只有 view，在几个组件中，只有 activity 才有 token
  - 其他生命周期跟随、主题继承可以先不管
  - 当然，你可以使用非 act 的 context，需要申请系统弹窗权限 SYSTEM_ALERT_WINDOW 并且用户同意
- act 的泄漏，相较于全局静态变量、单例、application 来说，act 是个短命对象，如果被长生命周期引用，导致 act 对象无法被释放，就会导致内存泄漏
  - 检测是否发生泄漏也很简单，在 onDestroy 把当前 act 对象添加到弱引用，过个 5s 10s 的 get 一下，看看能不能获取到，正常被释放的话，你 get 的结果应该是空的
  - 如果 get 结果不为空，说明发生了泄漏，那就进入第二步：查找谁引用了这个 act 对象，这又分成两步
    - 第一步是 dump heap，生成一个 .hprof 文件，这里面包含了当前 art 虚拟机堆内存的一个快照，包含了所有对象的详细信息以及它们之间的引用关系，dump 的时候需要 Stop The World，这也是 leakcanary 发现泄漏我们感受到卡顿的原因
    - 第二步是分析，hprof 里面内容很多，所有已加载的类信息、字符串、数组 等都在这，最重要的是 GC Root Records，它们明确标记了哪些对象是 GC Root，leakcanary 在这一步是新起了个进程去遍历 gc roots 分析查找，刚才泄漏的 act 被谁引用

