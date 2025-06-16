
- ContentProvider 自动初始化，可在 manifest 删除使用 AppWatcher#manualInstall() 手动初始化
- 默认支持 act、frag、viewmodel 等，也可以调用 AppWatcher.objectWatcher.expectWeaklyReachable() 自定义监控对象

# 一、基本原理

- ActivityWatcher，ContentProvider 拿到 application 上下文，ActivityLifecycleCallbacks
- **Fragment**，Act 的 oncreate，获取 fm，Fragment-Lifecycle
- **ViewModel**，Act 的 oncreate，install 一个，在 vm 执行 oncleared 反射获取所有 viewmodel 并加入监听队列
- service，反射替换 ActivityThread 中的 mH handler 对象，自定义 callback，拦截来自 ams 的消息，同时 hook ams 拿到 server 对象

在所有组件应该结束时添加到弱引用对列，5 秒后检查引用是否为空，为空则使用 Debug.dumpHprofData() 保存内存快照，然后新开进程启用分析

| 模块                          | 作用                                       |
| ----------------------------- | ------------------------------------------ |
| **leakcanary-android**        | Android 入口，监控组件生命周期与触发分析   |
| **shark**                     | Hprof 文件分析器，解析内存泄漏路径         |
| **leakcanary-object-watcher** | 用于弱引用监控的核心组件                   |
| **plumber**（2.12+）          | 可插拔的内存分析流程，替换旧的泄漏检测流程 |