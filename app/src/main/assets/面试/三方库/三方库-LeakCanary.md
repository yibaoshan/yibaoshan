
2.0 以后通过 ContentProvider 执行初始化

LeakCanary 在 manifest 文件利用 activity-alias 设置了默认启动页

当你安装自己的 APP 时，LeakCanary 会再添加一个 icon 入口，有多个项目集成 LeakCanary 也会有多个小黄鸟 icon

LeakCanary 设置不同的 taskAffinity，以在最近任务中区分 APP 和 LeakCanary，格式如下：

```
com.squareup.leakcanary.${applicationId}
```

## manualInstall

初始化入口，默认安装

- ActivityWatcher
- FragmentAndViewModelWatcher
- RootViewWatcher
- ServiceWatcher

## ActivityWatcher

- 注册 ActivityLifecycleCallbacks 监听
- 有 Activity 执行 onDestroy() 交给 ReachabilityWatcher 追踪判断

## FragmentAndViewModelWatcher

兼容多个 FragmentManager

- android.app.FragmentManager (Deprecated)
- android.support.v4.app.FragmentManager
- androidx.fragment.app.FragmentManager

流程：

- onCreate() 注册 Fragment-Lifecycle 监听
- 在 onFragmentViewDestroyed() 与 onFragmentDestroyed() 对 view对象 与 fragment对象 进行了内存泄漏追踪。

## RootViewWatcher

- 使用 curtains 库监听所有根 View 的创建与销毁
- 初始化了 runable 用于监听视图是否泄漏
- 在当前view被添加到窗口时，则从handler中移除该 runable
- 如果当前view从窗口移除时，则触发该runable的执行。

## ReachabilityWatcher

最终实现类为 ObjectWatcher

- removeWeaklyReachableObjects() 清除引用队列
- 从 watchedObjects(key) 获取弱引用对象，若不为空
  - 利用 backgroundHandler 子线程执行检查任务
