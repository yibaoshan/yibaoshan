

## 生命周期

### attachBaseContext(context)

- 在 Activity#attach() 方法中，被第一个调用的用户方法
- 该方法被调用，说明 Activity 对象创建成功
- 之后是创建 Window 对象和保存 WindowManager 的过程

### onCreate(savedInstanceState)

- 由 Activity#performLaunchActivity() 发起调用
- 如果 savedInstanceState 不为空，表示 Activity 被重建
- 此时 Window 已准备好，在此方法中，或之后都可以调用 setContentView() 方法设置视图
- 如果调用 setContentView，之后是创建 DecorView 对象，然后把开发者视图添加到 DecorView 的过程，最后回调 onContentChanged()，通知视图添加完成
- 注意，接收到 onContentChanged 通知，此时并没有添加到 Window ，更没有绘制，所以视图是不可见的。只是表示 DecorView 对象已创建，视图解析完成、添加完成

### onStart()

- 和 onCreate()一样，由 Activity#performLaunchActivity() 发起调用
- 调用顺序在 onCreate 之后
- 没什么鸟用，不用管

### onResume()

- 由 Activity#handleResumeActivity() 发起调用
- 先执行 onResume() 回调，然后执行 makeVisible() 方法
- 在 makeVisible() 方法中，创建 ViewRootImpl 对象，然后是 ViewRootImpl 通过 binder 向 wms 添加窗口的过程
- 接着，第一次 vsync 请求发生在 ViewRootImpl#setView() 方法
- vsync 信号来临，所有 View 执行绘制，此时视图对用户可见
- 另外，它还表示该 Activity 被重新激活，活跃在栈顶，能够获取焦点，接收 key/touch 事件
- 通常在这重新检查各种权限，再次确认用户状态，恢复动画等等

### onPause()

- Another activity comes in front of the activity
- window 层级发生变化，不是顶层 Window 时被调用，此时无法获取焦点，无法接收 key/touch 事件
- 另一个 Activity 如果是投屏或者非全屏，那么该 Activity 可能是部分可见
- 在此方法执行耗时，会影响下一个 Activity 的启动
- 调用 finish() 方法后，很快会回调该方法

### onStop()

- The activity is no longer visible，Activity 完全不可见时调用
- AMS 回调时间不确定，由 IdleHandler 执行，但有保底 10s
- 停止播放动画等资源

### onDestroy()

- 死了
- 释放资源，解除各种注册，防止内存泄漏
- 回调时机同样不确定，由 IdleHandler 执行，保底 10s
- Android 12 以后，杀后台不会回调该方法，所以如果是需要关闭蓝牙之类的操作，要另外想办法

### onSaveInstanceState()

Activity 的 onSaveInstanceState 回调时机，取决于 app 的 targetSdkVersion：

- targetSdkVersion 低于11的app，onSaveInstanceState方法会在Activity.onPause之前回调；
- targetSdkVersion低于28的app，则会在onStop之前回调；
- 28之后，onSaveInstanceState在onStop回调之后才回调。

### Activity 跳转生命周期

```
FirstActivity->onPause()

SecondActivity->onCreate()
SecondActivity->onStart()
SecondActivity->onResume()

FirstActivity->onStop()
```

### 横竖屏切换

#### **Android 8.0及以上**

没配置configChanges属性，切到横屏和再切到竖屏，生命周期调用顺序都表现为：

```
onPause
onSaveInstanceState
onStop
onDestroy
onCreate
onStart
onRestoreInstanceState
onResume
```

配置了configChanges属性，切到横屏和再切到竖屏，生命周期调用顺序都表现为：

```
onConfigurationChanged
```

#### **Android7 .0、7.1.1**

没配置 configChanges 属性和没配置全 configChanges = orientation|keyboardHidden|screenSize

切到横屏和再切到竖屏，生命周期调用顺序都表现为：

```
onConfigurationChanged
onPause
onSaveInstanceState
onStop
onDestroy
onCreate
onStart
onRestoreInstanceState
onResume
```

和 8.0 以上相比，先回调了 onConfigurationChanged() 方法，再执行一系列的生命周期

配置了configChanges属性是orientation|keyboardHidden|screenSize，切到横屏和再切到竖屏，生命周期调用顺序都表现为：

```
onConfigurationChanged
```

#### **Android6.0 及以下**

未配置 configChanges 属性和未配置全 configChanges 属性是 orientation|keyboardHidden|screenSize

切到横屏和再切到竖屏，生命周期调用顺序都表现为：

```
onPause
onSaveInstanceState
onStop
onDestroy
onCreate
onStart
onRestoreInstanceState
onResume
```

和 8.0 是相同的结果

配置了 configChanges 属性是 orientation|keyboardHidden|screenSize，切到横屏和再切到竖屏，生命周期调用顺序都表现为：

```
onConfigurationChanged
```

#### **小结**

从打印顺序来看，大多数情况下，如果 orientation|keyboardHidden|screenSize 这三个属性缺少任意一个

Activity 都会重新走生命周期，并且走 onSaveInstanceState / onRestoreInstanceState 保存恢复数据

否则就只走 onConfigurationChanged() 回调

## 其他 / 杂项

### 低内存通知 onLowMemory / onTrimMemory

onLowMemory / onTrimMemory 是系统低内存通知的回调函数

除了 Activity 外，Application、Fragment、Service、ContentProvider 也可以接受该回调通知。其中，Fragment 由 Activity 负责通知

onTrimMemory(int level) 共计有 7 种 level，重点关注以下 3 种类型即可

- TRIM_MEMORY_RUNNING_CRITICAL：内存严重不足，此时应该释放一些非关键性资源，否则会导致其他后台进程被杀死
- TRIM_MEMORY_RUNNING_LOW：内存不足，此时应该释放一些不需要的资源
- TRIM_MEMORY_RUNNING_MODERATE：内存不太充足，此时应该释放一些不需要的资源

onLowMemory() 暂时没搞清楚触发时机，以及出发后应该做些什么

onTrimMemory() / onLowMemory() 是组件的方法，使用 ComponentCallbacks2 也可以达到相同效果，适用于在非组件监听内存变化。

- 组件方法适用于，当前组件内有缓存需要释放。我们可以重写方法实现释放逻辑
- ComponentCallbacks2 适用于，全局的资源释放，比如 glide 图片缓存，加载到内存的 h5 资源包等等