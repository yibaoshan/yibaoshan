
## 前置知识点(TaskRecord)

我们的 Activity 都是以 ActivityRecord 的形式保存在 AMS 中

- ActivityRecord 的最小管理单位是 **TaskRecord** ，除了 singleInstance 外，其他启动模式，都在同一个 TaskRecord 中操作
- 像 singleTask 清空栈内该 Activity 之上的所有 Activity，操作的就是 **TaskRecord**

包含关系如下：

- ActivityStack #1（Launcher APP）
  - TaskRecord #1
    - ActivityRecord #A
- ActivityStack #2（Focused My APP）
  - TaskRecord #1
    - ActivityRecord #A
    - ActivityRecord #B
    - ActivityRecord #A
  - TaskRecord #2 （singleInstance）
    - ActivityRecord #C

关于最近任务（RecentTasks），以及 **TaskRecord** 和 taskAffinity 之间的关系补充：

- **TaskRecord** 的 taskAffinity 是由 Task 的 root activity决定的
- 同一个应用程序中默认所有 activity 的 taskAffinity 是一样的
- 而按下多任务键显示的最近任务窗口，是根据 taskAffinity 的值来定的
- 因此：
  - 若 APP 不同页面都想在最近任务展示，可以通过 "taskAffinity = '.xxx'" 设置
  - 若未重新设置 taskAffinity ，即使模式为 singleInstance ，最近任务中，依然只有一个 APP 窗口

## 正常回收

- 主动调用 finish() 方法
- 按下返回键
- 启动 singleTask 的 Activity，及栈中其他 Activity 不需要了被回收

## 系统内存不足

Low Memory Kill 根据进程优先级，直接干掉进程

## 应用内存不足

进程在，而且 App 在前台显示，只是不在栈顶的 Activity 被回收

- 单进程应用，ActivityThread#attach() 中添加了 GC 监听
- 若应用使用内存大于3/4的时，启动回收策略（releaseSomeActivities）
- 非可见 Activity 都会加入回收列表（后台播放类 Activity、壁纸类除外），等待被回收
- 回收的 Activity 会收到 onSaveInstanceState() 回调，返回到该页面时，重走 onCreate()
- 若是 FragmentActivity，系统还会帮助恢复 fragment。fragment无默认构造发生崩溃，重影等bug就是因为这个。。

补充，复现条件很简单，打开多个 Activity，在当前页面一点点申请内存（避免无法分配大内存触发 OOM）