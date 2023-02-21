
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

