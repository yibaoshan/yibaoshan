
## Recents 最近任务

按多任务键显示的最近任务窗口，是根据 TaskAffinity 的值来定的

同一个应用程序中默认所有 activity 的 taskAffinity 是一样的

如果一个 APP 的不同页面都想在最近任务展示，可以通过 "taskAffinity = '.xxx'" 设置

### 让自己的程序不显示在任务列表中

- 在 manifest 设置 android:excludeFromRecents = true
- 在启动 Activity 的时候加上 Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS 的 FLAG

## AMS 如何管理 Activity

整体关系如下

```
ActivityStackSupervisor.mActivityDisplays
    -> ActivityDisplay.mStack
        -> ActivityStack.mTaskHistory
            -> TaskRecord.mActivities
                -> ActivityRecord
```

### ActivityRecord

每一个在应用中启动的 Activity，在 AMS 中都有一个 ActivityRecord 实例来与之对应

这个 ActivityRecord 伴随着 Activity 的启动而创建，也伴随着 Activity 的终止而销毁

- app（ProcessRecord）：表示 APP 跑在哪个进程？
- task（TaskRecord）：表示跑在哪个 task
- info（ActivityInfo）：Activity 信息，包含 theme、launchMode、权限、系统配置信息等等
- state（ActivityState）：状态枚举类，有 INITIALIZING、RESUMED、STOPPING、STOPPED等等
- appInfo（ApplicationInfo）：跑在哪个app
- appToken（IApplicationToken.Stub）：Server 端的实体
- 其他成员变量：packageName、processName、userId、launchMode 等等

ActivityRecord 是在启动 activity 时候创建的

```
int startActivityLocked(...) {
          //为activity创建ActivityRecord
          ActivityRecord r = new ActivityRecord(...);
}
```

### TaskRecord(启动模式相关)

一般情况下，在启动 App 的第一个 activity 时，AMS 为其创建一个 TaskRecord 任务栈

当然启动后也可能创建新的 TaskRecord ，比如我们启动 singleInstance 的 Activity

- stack（ActivityStack）：当前所属的 stack
- mActivities（List\<ActivityRecord\>）：当前 task 的所有 Activity 列表
- affinity（String）

### ActivityStack

管理 TaskRecord，内部维护 Activity 所有状态、特殊状态的 Activity 和 Activity 相关的列表数据

- mTaskHistory（List\<TaskRecord\>）：保存所有的 Task 列表
- mStacks（List\<ActivityStack\>）：所有的 stack 列表
- mPauseingActivity（ActivityRecord）：正在暂停的 Activity
- mLastPausedActivity：上一个已暂停的 Activity
- mResumedActivity：已经 Resumed 的 Activity
- mLastStartedActivity：最近一次启动的 Activity

### ActivityStackSupervisor（屏幕相关）

管理所有的 ActivityStack

- mHomeStack（ActivityStack）：桌面的 stack
- mFocusedStack（ActivityStack）：当前聚焦的 stack
- mLastFocusedStack（ActivityStack）：正在切换到聚焦的 stack

### ActivityDisplay

表示一个屏幕，Android支持三种屏幕：主屏幕，外接屏幕（HDMI等），虚拟屏幕（投屏）

一般地，对于没有分屏功能以及虚拟屏的情况下，ActivityStackSupervisor 与 ActivityDisplay 都是系统唯一；

ActivityDisplay 主要有 Home Stack和 App Stack 这两个栈

##  启动模式launchMode

### standard

这个启动模式是最常见的，Activity 默认就是此启动模式。

每启动一次 Activity，就会创建一个新 Activity 实例并置于栈顶。

谁启动了这个 Activity，那么这个 Activity 就运行在启动它的那个 Activity 所在的栈中。

### singleTop(登录页)

如果一个 activity 的实例已经在 Task 栈的顶部，那么当再次打开这个 activity 时会复用这个实例而不再创建一个新的实例

同时通过 onNewIntent() 传递给这个实例 Intent

### singleTask(首页)

一个 activity 的实例在 Task 只能有一个实例

再次启动也会通过调用 onNewIntent() 方法处理，而非创建一个新的Activity实例对象

并且会把在它之上的其它 Activity 都出栈清理掉

但是如果实例不存在 Task 中，此时系统将会为比较调用方 activity 和被调用方 activity 的 taskAffnity

如果一致就将该 activity 创建一个实例，并加入到已存在的 Task 中

如果不一致，就为该 activity 创建一个新的 Task，此时 activity 是作为 Task 的 root activity 的。

事实上，Task 的 taskAffinity 正是由 Task 的 root activity决定的。

### singleInstance

此标记的 Activity 启动时，AMS 每次都会其创建新的 TaskRecord ，它自己单独待在这

Task 中只能有一个该 activity 的实例

##  Intent Flags

启动模式不仅可以在 AndroidManifest 中进行设定，同样的我们可以使用 Intent Flags 进行设定

一些常见的 Flags 可以更方便和灵活的处理 activity 和 Task 的关系：

- FLAG_ACTIVITY_NEW_TASK ，这个标记同singleTask的处理方式
- FLAG_ACTIVITY_SINGLE_TOP，这个标记同singleTop的处理方式
- FLAG_ACTIVITY_CLEAR_TOP，如果activity的实例已经在当前Task中，那么再启动时，将会将其之上的activity实例都销毁掉。比如Task A->B->C->D,如果在D中启动B添加了该标记，那么会将C和D都从Task销毁掉，如果此时B是默认的启动模式，那么会将B的实例也销毁，并重新创建一个实例添加到Task顶部，如果我们不想它重新创建，可以配合FLAG_ACTIVITY_SINGLE_TOP使用，这样复用顶部的B实例并触发onNewIntent。
- FLAG_ACTIVITY_REORDER_TO_FRONT，新启动的Activity将会被放到它所属task的顶部，例如，当Task A->B->C->D，如果D启动B使用了这个标记，B将会排在这个task的最上面，也即现在Task的顺序变成了A,C,D,B。
- FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET 这个标记将会在TASK重置时清理对应的Activity，比如Task当前为A->B，A启动B添加了该标记，那么当切换到Home再打开该应用后Task变为A，B被清理。这个其实是在Task中设置了一个还原点，当Task重置时将Task恢复到指定的还原点。

另外在 AndroidManifest 的中也可以设置和 Task 相关属性，下面我们看看几种：

- android:allowTaskReparenting
  - 这个属性定义的是activity具有reparent Task的能力，即标记一个Activity实例在当前应用退居后台后，是否能从启动它的那个Task移动到有共同TaskAffinity的Task，“true”表示可以移动，“false”表示它必须呆在当前应用的task中，默认值为false。下面通过一个例子说明这种情况：比如App A 有一个Activity:MainActivity，App B有两个Activity:BMainActivity和SecondActivity，其中SecondActivity定义了该属性为true，当我们在App A中的MainActivity中启动App B的SecondActivity后，此时A的Task为MainActivity->SecondActivity，注意此时MainActivity和SecondActivity的TaskAffinity并不是相同的，他们为各自的包名。如果此时按Home键回到Launcher，再启动App B后，会发现此时的页面是SecondActivity，也就是说SecondActivity从原来App A的Task移动到了和他TaskAffinity相同的Task中即App B的Task。
- android:clearTaskOnLaunch
  - 如果设置了这个属性为true，每次用户重新启动这个应用时，都只会看到Root Activity，task中的其他Activity都会被清除出栈。这个属性也只对Root Activity有效。
- android:alwaysRetainTaskState
  - 这个属性是让Task保持原来的状态，true表示保持，false不能够保持，此属性也只对Root Activity有效，一般情况下，如果Task切换到后台后太久系统会对Task进行清理，除了Root Activity其他Activity都会被清理。但如果设置了该属性为true，则可以保持上次操作的界面。
- android:noHistory
  - 这个属性设置为true后，则Activity将不会被记录到Task的history列表中，那么就是说，这个Activity不可见，那么它实际上就和销毁了一样，因为AMS没有它的相关信息。

## 参考资料

- [wanandroid 每日一问](https://wanandroid.com/wenda/show/21681)
- [深入理解ActivityRecord、TaskRecord、ActivityStack的作用及关系](https://juejin.cn/post/6856298463119409165)