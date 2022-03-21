### Overview
1. 生命周期
2. 启动方式和使用场景
3. 启动流程
4. 通信方式
5. 常见问题

### 一、生命周期

#### 1、正常流程：

```

onCreate():调用attach方法赋值PhoneWindow、ContextImpl等属性，Activity进入ON_CREATE状态
    ->onStart():准备进入前台时调用，可以理解为其他Activity都已经进入到后台，Activity进入ON_START状态
        ->onResume():准备交互时调用，首次启动时DecorView未获得token，再次调用时可以在此方法操作UI，Activity进入ON_RESUME状态
            ->onPause():失去焦点时调用，此时Activity可见但不可交互，Activity进入ON_PAUSE状态
                ->onStop():不可见时调用，新页面执行onResume()时调用，新页面不会等待onStop执行完毕之后再显示，所以在onStop方法里做耗时操作不会影响新页面，Activity进入ON_START状态
                    ->onDestroy():被系统kill或调用finish()方法时调用，Activity进入ON_DESTROY状态

```

#### 2、Activity页面发生切换

```

一：当从页面A跳转到页面B时的生命周期变化：

A:onPause()
    ->B:onCreate()->onStart()->onResume()
        ->A:onStop()

二：当从页面B跳转回页面A时的生命周期变化：

B:onPause()
    ->A:onRestart()->onStart()->onResume()
        ->B:onStop()->onDestroy()

三：当从页面B跳转回页面A时的生命周期变化(带返回值setResult())：

B:onPause()
    ->A:onActivityResult()->onRestart()->onStart()->onResume()
        ->B:onStop()->onDestroy()

```

#### 3、Activity配置发生变化(屏幕旋转)

```java

onPause -> onStop -> onSaveInstanceState -> onDestroy
onCreate -> onStart -> onRestoreInstanceState -> onResume

```

#### 4、重复启动(onNewIntent)

```

onPause() -> onNewIntent() -> onResume()

```

#### 5、其他

```java

onSaveInstanceState():每次进入后台时调用，调用时机随API不同而不同，在api 28(Android 9)中在onStop()之后调用，低版本在stop之前调用，且与onPause之间的顺序是不确定的，正常情况下，每个view都会重写这两个方法
onRestoreInstanceState(): 调用顺序onCreate -> onStart -> onRestoreInstanceState -> onResume

```



### 二、启动方式和应用场景

#### 1、Activity栈介绍

通常情况下，Activity栈指的是**TaskRecord**，其内部维护所有在这个栈(同一个Affinity描述)中的Activity

我们知道，一个Activity在系统中是由**ActivityRecord**类来表示，那么他们之间的关系可以表示为：

```java
在一个APP中，可以有多个Activity栈：TaskRecord，每个Activity(TaskRecord)栈都保管着一个ActivityRecord列表
在同一个Activity(TaskRecord)栈中，出栈和压栈都是在此基础上操作
```

##### 1.1 ActivityRecord

记录Activity的信息，并通过成员变量task指向TaskRecord。

| 类型            | 名称          | 说明                       |
| --------------- | ------------- | -------------------------- |
| ProcessRecord   | app           | 跑在哪个进程               |
| TaskRecord      | task          | 跑在哪个task               |
| ActivityInfo    | info          | Activity信息               |
| int             | mActivityType | Activity类型               |
| ActivityState   | state         | Activity状态               |
| ApplicationInfo | appInfo       | 跑在哪个app                |
| ComponentName   | realActivity  | 组件名                     |
| String          | packageName   | 包名                       |
| String          | processName   | 进程名                     |
| int             | launchMode    | 启动模式                   |
| int             | userId        | 该Activity运行在哪个用户Id |

##### 1.2 TaskRecord

描述Activity的Affinity所属的栈。

| 类型                        | 名称            | 说明                                              |
| --------------------------- | --------------- | ------------------------------------------------- |
| ActivityStack               | stack           | 当前所属的stack                                   |
| ArrayList\<ActivityRecord\> | mActivities     | 当前task的所有Activity列表                        |
| int                         | taskId          | TaskRecord的Id                                    |
| String                      | affinity        | root activity的affinity，即该Task中第一个Activity |
| int                         | mCallingUid     | 调用者的UserId                                    |
| String                      | mCallingPackage | 调用者的包名                                      |

##### 1.3 ActivityStack

管理着TaskRecord，内部维护Activity所有状态、特殊状态的Activity和Activity相关的列表数据。

| 类型                       | 名称                 | 说明                                                |
| -------------------------- | -------------------- | --------------------------------------------------- |
| ArrayList\<TaskRecord\>    | mTaskHistory         | 保存所有的Task列表                                  |
| ArrayList\<ActivityStack\> | mStacks              | 所有的stack列表                                     |
| int                        | mStackId             | ActivityStackvisor的mActivityContainers的key值Id    |
| int                        | mDisplayId           | ActivityStackSupervisor的mActivityDisplays的key值Id |
| ActivityRecord             | mPauseingActivity    | 正在暂停的Activity                                  |
| ActivityRecord             | mLastPausedActivity  | 上一个已暂停的Activity                              |
| ActivityRecord             | mResumedActivity     | 已经Resumed的Activity                               |
| ActivityRecord             | mLastStartedActivity | 最近一次启动的Activity                              |

#### 2、常见启动模式

了解完上述Activity栈之后，我们知道一个Activity的启动模式是由ActivityRecord中的launchMode决定的，那么也就是说，当发起启动一个Activity请求时，代码设置的启动模式永远高于XML指定的模式，因为XML指定的模式被更改了。

##### 1. Standard

标准启动模式，会新创建一个ActivityRecord放入TaskRecord中的mActivities中

##### 2. SingleTop：防止重复启动页面，例如登录页

和Standard几乎相同，唯一不同的是当要创建的Activity已经在栈顶时，取消创建，调用该Activity的onNewIntent()方法

##### 3. SingleTask：抛弃之前页面，例如电商主页

依旧在同一个TaskRecord中创建，与上面两个不同的是，若栈中已经有该Activity的实例，那么将会把该实例之前的Activity都清空，将该实例提升到栈顶，并调用其onNewIntent()方法

##### 4. SingleInstance

和上面三个都不同，该启动模式会在一个创建一个新的TaskRecord用于存放该Activity

#### 3、常见Flag

```
Intent：setFlags：
一、Intent.FLAG_ACTIVITY_NEW_TASK(比较关键)
使用除Application和Service时需要加
二、Intent.FLAG_ACTIVITY_CLEAR_TASK
只能与#FLAG_ACTIVITY_NEW_TASK配合使用，启动时清空当前栈，新启动的Activity变成这个空栈的根Activity并将这个栈移动到最前面。
Intent.FLAG_ACTIVITY_CLEAR_TASK的优先级最高，基本可以无视所有的配置，包括启动模式及Intent Flag，哪怕是singleInstance也会被finish并重建。
三、Intent.FLAG_ACTIVITY_CLEAR_TOP
如果栈中存在相同的Activity，则把这个Activity上面的Activity都干掉。
如果同时设置了FLAG_ACTIVITY_SINGLE_TOP ，则回调onNewIntent方法；否则先finish后create这个实例
四、Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
创建并启动Activity并提到栈顶，如果栈中存在此Activity，则不创建Activity实例并把栈中的实例移动到栈顶
五、Intent.FLAG_ACTIVITY_NO_ANIMATION
这个标志将阻止系统进入下一个Activity时应用Activity迁移动画
六、Intent.FLAG_ACTIVITY_NO_HISTORY
Activity正常启动，只是不被压入栈中
七、Intent.FLAG_ACTIVITY_TASK_ON_HOME
需要和FLAG_ACTIVITY_NEW_TASK配合使用。添加这个Flag后，启动的Activity会把系统桌面Activity放在LauncherAct上面(单独的Task)
然后启动目标Activity(新Task)在最上面，如果按返回按键，会返回到桌面Activity
八、Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENT
新的Activity不会在最近启动的Activity的列表中保存。
```

### 三、Activity通信方式

先个人理解一波

1. 发起startActivity之后，传入context和class类名，内部解析context和class组成ComponentName

```
Activity.startActivity()->Instrument.execStartActivity(rpc)
->ActivityTaskManagerService.startActivityAsUser(ActivityStarter.execute())
```

### 四、常见问题

- 为什么Application启动Activity不加NEW_TASK会发生崩溃？
- 旋转屏幕导致Activity重建问题的解决办法
