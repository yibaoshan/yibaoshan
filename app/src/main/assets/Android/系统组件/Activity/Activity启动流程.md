
## 客户端

### 启动 Activity

启动自己的

```
context.startActivity(new Intent(context, SomeActivity.class));
```

启动其他应用的

```
Intent intent = new Intent();
intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.DeviceAdminSettings"));
context.startActivity(intent);
```

Activity 也是 Context 的子类，也实现了该方法，所以我们来看 Activity 中的方法调用链：

```
startActivity(intent)
startActivity(intent, null)
startActivityForResult(intent, -1)
startActivityForResult(intent, -1, null)
```

关键代码:

```
public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
	// ...
	Instrumentation.ActivityResult ar = mInstrumentation.execStartActivity(this, mMainThread.getApplicationThread(), mToken, this, intent, requestCode, options);
	if (ar != null) {
		mMainThread.sendActivityResult(mToken, mEmbeddedID, requestCode, ar.getResultCode(), ar.getResultData());
	}
}
```

最终调用了 Instrumentation 的 execStartActivity() 方法

### Instrumentation

execStartActivity 主要有两个作用:

1. 更新所有激活的 ActivityMonitor 对象，调用它们的 onStartActivity 回调以及更新对应的计数信息；
2. 将当前调用分发到 system activity manager 中；

```
public ActivityResult execStartActivity( who, contextThread, token, target, intent, requestCode, options) {
  // 1. 更新 ActivityMonitor
  for (int i = 0; i < mActivityMonitors.size(); i++) {
    ActivityMonitor am = mActivityMonitors.get(i);
    am.onStartActivity(intent);
  }
  // 2. 调用 System Activity Manager
  int result = ActivityTaskManager.getService().startActivity();
  checkStartActivityResult(result, intent);
}
```

我们可以覆写此方法来观察 APP 启动新 Activity 的事件

并且可以修改启动 Activity 的行为和返回。

许多插件化框架就是通过修改此方法来实现 Activity 的拦截。

### ActivityTaskManager

ActivityTaskManager.getService() 获取到的类型为 IActivityTaskManager 的对象，这其实是一个 Service

```
public interface IActivityTaskManager extends android.os.IInterface
{
  public static final java.lang.String DESCRIPTOR = "android.app.IActivityTaskManager";
  
  public int startActivity(...);
}
```

调用 startActivity() 方法后，APP 进程也就走到头了，接下来需要去服务端那边去看看

## 服务端

根据 AIDL 的原理，RPC 的另一端需要实现 IActivityTaskManager 接口

一般来说是要继承 IActivityTaskManager.Stub，搜索可以发现这个类为 ActivityTaskManagerService。

### ActivityTaskManagerService

在 ActivityTaskManagerService 类中， 由 startActivityAsUser() 方法最终响应启动操作

通过一系列 builder 获取 starter 并设置对应参数，最终执行 execute

```
private int startActivityAsUser(...) {
    // TODO: Switch to user app stacks here.
    return getActivityStartController().obtainStarter(intent, "startActivityAsUser")
            .setCaller(caller)
            .setCallingPackage(callingPackage)
            .setCallingFeatureId(callingFeatureId)
            .setResolvedType(resolvedType)
            .setResultTo(resultTo)
            .setResultWho(resultWho)
            .setRequestCode(requestCode)
            .setStartFlags(startFlags)
            .setProfilerInfo(profilerInfo)
            .setActivityOptions(bOptions)
            .setUserId(userId)
            .execute();
}
```

### ActivityStarter

从 Android 10 还是哪个版本中，启动代码单独抽离到 ActivityStarter 去处理

路径在 frameworks/base/services/core/java/com/android/server/wm/ActivityStarter.java

关键调用路径:

- execute: 处理 Activity 启动请求的接口；
- executeRequest: 执行一系列权限检查，对于合法的请求才继续；
- startActivityUnchecked: 调用该方法时表示大部分初步的权限检查已经完成，执行 Trace，以及异常处理；
- startActivityInner: 启动 Activity，并更新全局的 task 栈帧信息；

在 Android 中系统维护了所有应用的状态信息，因此用户才可以在不同应用中无缝切换和返回。

同时在处理启动应用请求的时候还需要进行额外的判断，比如当前栈顶是否是同样的 Activity，如果是则根据设置决定是否重复启动等等。

忽略 ActivityStack、WindowContainer 等任务窗口管理的代码，其中真正启动应用相关的:

- mTargetRootTask.startActivityLocked()
- mRootWindowContainer.resumeFocusedTasksTopActivities()

mTargetRootTask 是 Task 类型用于管理和表示属于同一栈帧的所有 activity，其中每个 activity 使用 ActivityRecord 表示。

mRootWindowContainer 是 RootWindowContainer 类型，也是 WindowContainer 的子类，特别代表根窗口。

startActivityLocked() 方法的主要作用是判断当前 activity 是否可见以及是否需要为其新建 Task，根据不同情况将 ActivityRecord 加入到对应的 Task 栈顶中。

resumeFocusedTasksTopActivities() 方法正如其名字一样，将所有聚焦的 Task 的所有 Activity 恢复运行，因为有些刚加入的 Activity 是处于暂停状态的。

### Task / ActivityStack

Task 是一系列 Activity 界面的集合，根据先进后出的栈结构进行组织。

大多数 task 可以认为是从桌面点击某个应用开始启动，随着不断点击深入打开其他界面，使对应的 Activity 入栈

在点击返回时将当前 Activity 出栈并销毁

同时，整个 Task 本身也可以被移动当后台

比如当用户点击 HOME 键时，此时 Task 中的所有 Activity 都会停止。

Task 中的 Activity 可以同属于一个 APP，也可能属于不同的 APP 和进程。

在 Android R (11) 以及 Android S(12) beta 的代码中(甚至更早的代码之前)，Task 类实际上是 ActivityStack

可以认为 Task 就是 ActivityStack，ActivityStack 就是 Task

resumeFocusedTasksTopActivities() 中主要是判断传入的 targetRootTask 是否等于当前栈顶的 Task

不管是否相等，后续都是调用栈顶 Task 的 resumeTopActivityUncheckedLocked 方法。

该方法中主要是寻找合适的 ActivityRecord、设置 resume 条件、准备启动目标 Activity。最后，来到了我们的关键逻辑:

```
private boolean resumeTopActivityInnerLocked(ActivityRecord prev, ActivityOptions options) {
  	ActivityRecord next = topRunningActivity(true /* focusableOnly */);
    // ...
  	if (next.attachedToProcess()) {
        // Activity 已经附加到进程，恢复页面并更新栈
    } else {
        // Whoops, need to restart this activity!
					mTaskSupervisor.startSpecificActivity(next, true, true);
    }
}
```

startSpecificActivity() 主要是判断目标 Activity 所在的应用是否正在运行。

如果已经在运行就直接启动

否则就启动新进程

## 进程已存在

如果待启动的 Activity 所属的 Application 已经在运行中，那么只需要在其对应进程启动 Activity

此时所走的分支是 realStartActivityLocked。

该函数的核心是创建事务并分发给生命周期管理器进行处理

### Transaction

transaction.schedule() 开始调度事务，主要按照以下顺序执行:

- 客户端调用 preExecute，触发所有需要在真正调度事务前执行完毕的工作；
- 发送事务的 message 信息到客户端；
- 客户端调用 TransactionExecutor.execute，执行所有回调以及必要的生命周期事务；

事务的封装、发送、执行的过程暂时不用管，只需要了解到最终是 ActivityThread 类中的 TransactionExecutor 来执行启动即可

```
class ActivityThread {
  // ...
  private final TransactionExecutor mTransactionExecutor = new TransactionExecutor(this);
}
```

### ActivityThread

ActivityThread 中的 performLaunchActivity 是启动 Activity 的核心实现。其主要伪代码为:

```
private Activity performLaunchActivity(ActivityClientRecord r, Intent customIntent) {
    ContextImpl appContext = createBaseContextForActivity(r);
  	java.lang.ClassLoader cl = appContext.getClassLoader();
  	activity = mInstrumentation.newActivity(cl, component.getClassName(), r.intent);
  	Application app = r.packageInfo.makeApplication(false, mInstrumentation);
  	// loadLabel
  	// initialize Activity resources
  	// activity.attach(appContext, ...)
  	mInstrumentation.callActivityOnCreate(activity, ...);
}
```

长话短说，主要流程为:

- 创建应用上下文(Context)，获取 ClassLoader；
- 创建 Activity 对象，实质上是 classLoader.loadClass(name).newInstance()，这里会对 Activity 类进行初始化，调用对象的 <cinit> 方法，从而执行目标类里 static block 中的代码；
- 根据应用的 AndroidManifest.xml 创建 Application对象，并调用其 onCreate 回调；
- 调用对应 Activity 的 onCreate 回调；

值得一提的是上面的代码都在 APP 的主线程中执行

Application.onCreate 仅在应用初次启动时调用一次

并且早于任意 Activity/Service/Receiver 执行，不过 ContentProvider 是个例外。

## 启动新进程

如果进程不存在，此时需要通过 mService.startProcessAsync 去启动进程

### AMS

### ZygoteServer

### 子进程初始化

对于子进程，调用链路为:

- handleChildProc
- ZygoteInit.zygoteInit
- RuntimeInit.applicationInit
- RuntimeInit.findStaticMain

```
protected static Runnable applicationInit( targetSdkVersion, disabledCompatChanges, argv, classLoader) {
    final Arguments args = new Arguments(argv);
    return findStaticMain(args.startClass, args.startArgs, classLoader);
}
```

最终调用的实际上是 startClass 的 static main(argv[]) 方法。

即 android.app.ActivityThread。因此，子进程的入口是 ActivityThread 的 main 方法


### ActivityThread#main()

### 启动 Application

```
private void handleBindApplication(AppBindData data) {
  // 将当前 UI 线程注册为 JavaVM 的重要线程
	VMRuntime.registerSensitiveThread();
  // 设置调试的跟踪栈深度
  String property = SystemProperties.get("debug.allocTracker.stackDepth");
  VMDebug.setAllocTrackerStackDepth(Integer.parseInt(property));
  // 设置应用的真正名称
  Process.setArgV0(data.processName);
  android.ddm.DdmHandleAppName.setAppName(data.processName, ...);
  VMRuntime.setProcessPackageName(data.appInfo.packageName);
  // 为 ART 设置应用数据的路径
  VMRuntime.setProcessDataDirectory(data.appInfo.dataDir);
  // 如果设置了调试模式，会等待调试器连接，同时显示弹窗信息
  if (data.debugMode != ApplicationThreadConstants.DEBUG_OFF) {
  	Debug.changeDebugPort(8100);
    mgr = ActivityManager.getService();
    mgr.showWaitingForDebugger(mAppThread, true);
    Debug.waitForDebugger();
    mgr.showWaitingForDebugger(mAppThread, false);
  }
  // 创建目标 APP 的上下文
  ContextImpl appContext = ContextImpl.createAppContext(this, data.info);
  // 设置 HTTP 代理
  final ConnectivityManager cm = appContext.getSystemService(ConnectivityManager.class);
  Proxy.setHttpProxyConfiguration(cm.getDefaultProxy());
  // 加载 Network Security Config: APP 自定义证书、SSL-Pinning ...
  NetworkSecurityConfigProvider.install(appContext);
  // Applicaiton 初始化以及 ContentProvider 注册
  app = data.info.makeApplication(data.restrictedBackupMode, null);
  ActivityThread.updateHttpProxy(app)；
  installContentProviders(app, data.providers);
  // 启动应用
  mInstrumentation.onCreate(data.instrumentationArgs);
  mInstrumentation.callApplicationOnCreate(app);
  // 预加载字体资源 ...
}
```

可以看到，如果应用启用了调试，那么调试器在 Application 启动之前初始化

而且在应用启动之前还设置了系统的的 HTTP 代理

这也是为什么在 Android 中 native 进程不使用系统代理，因为对于代理是在 ActivityThread 即 Java 应用的 UI 线程中进行初始化的。

从上面的代码我们还能看到，ContentProvider 的注册还在 callApplicationOnCreate 之前

这其实属于历史遗留问题，因为有时候 Application 的初始化过程会访问 ContentProvider

所以如果我们想要自己的代码尽早执行，可以将其放到 ContentProvider.onCreate 方法中

比如实现一些野路子的加固方案，不过 Android 并没有对这个时序做应用层的承诺就是了。

### 启动 Activity

此时，APP 进程已经启动

前文中我们执行到了 ActivityThread 的 main 函数，其中最后会调用 Looper.loop() 进入循环等待

也是在这里收到 AMS 的 BIND_APPLICATION 请求从而进行子进程初始化。

除了这个请求，还有一个前面我们见过的请求: EXECUTE_TRANSACTION

所以在子进程初始化后，AMS 会进而完成 Activity 的启动处理。

### 小结

在 ActivityThread 中，APP 应用的启动顺序为:

- 创建 Application 实例，调用构造函数;
- 调用 app.attach();
- 调用 app.attachBaseContext();
- 安装 ContentProvider；
- 调用 app.onCreate();

因此如果我们想要自己的程序在应用启动初期就执行，可以将代码写在 Application.onCreate() 回调中；

如果想要更早可以在 Application.attachBaseContext 中，但此时 ContentProvider 没有安装完毕，因此只能执行不依赖于所注册 ContentProvider 的代码。










