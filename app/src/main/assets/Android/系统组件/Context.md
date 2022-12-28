
Context 使用了装饰者模式

ContextWrapper 继承自 Context ，内部功能都由 ContextImpl 来实现

```
- Context 
    - ContextImpl
    - ContextWrapper
        - Application
        - Service
        - ReceiverRestrictedContext // 在 ContextImpl 类中，ContextImpl
        - ContextThemeWrapper
            - Activity
```

在上面的继承图中，Application 和 Service 都直接继承 ContextWrapper  

只有 Activity 比较特殊，因为它是有界面的，所以他需要一个主题：Theme

ContextThemeWrapper 在 ContextWrapper 的基础上增加与主题相关的操作

## 创建时机

- **Application**
  - ActivityThread#makeApplication()
- **Activity**
  - ActivityThread#performLaunchActivity()
- **Service**
  - ActivityThread#handleCreateService()

## 几个没有自己 Context 的组件

- Provider，用的是 Application
- 动态广播用的是注册的 activity 的上下文

## 常用方法

### 资源管理

- **getAssets()**
  - 获取 AssetManager 。用于管理 assets 目录，通过 open() 方法打开文件
  - 在 ContextImpl中，本质是封装 getResources().getAssets()
  - apk 文件中，assets 是单独目录
- **getResources()**
  - 获取 Resources 资源大管家，负责 drawable、color、anim、layout、menu 等等
  - 管理 apk 文件中，res 目录下的所有文件
  - raw 是 res 的一个子目录
  - 除了 raw 保持原状，其他都打包成二进制文件
  - apk 文件的根目录中，resources.arsc 是资源索引文件，表示映射关系
- getPackageManager()，PackageManager
- getContentResolver()，ContentResolver
- getText()/getString()/getColor()/getDrawable()/getTheme()

### 系统管理

- getClassLoader()
- getPackageName() / getBasePackageName()
- getApplicationInfo()

### 操作四大组件

- startActivity()
- sendBroadcast() / registerReceiver()
- start/stopService() / bindService()
- getSystemService(name)

### Handler 相关

- getMainLooper()
- getMainExecutor()

### 文件 / 存储 / 数据库

- get/move/delete/reloadSharedPreferences()
- getDataDir() / getFilesDir()
- getNoBackupFilesDir()
- getExternalFilesDir()
- getObbDir()
- getCacheDir() / getCodeCacheDir() / getCodeCacheDir()
- openOrCreateDatabase() / get/move/deleteDatabase()

### 杂项 / 未分类

- **registerComponentCallbacks()**
  - 注册组件监听，分为 ComponentCallbacks 和 ComponentCallbacks2 ，通常用后者
  - onTrimMemory(level)，内存调整通知，共计 7 种 level，详见 Activity 生命周期
  - onConfigurationChanged()，配置发生变化
  - onLowMemory()，低内存通知，
- **getDisplay()**
- checkPermission()

## 常见问题

- 使用 Application 启动 Activity 会报错，因为 Application 没有 TaskRecord 任务栈，所以要么加一个任务栈要么使用 NEW_TASK flag

## 参考资料

- [广播onReceive()方法的context类型探究](https://blog.csdn.net/lyl0530/article/details/81105365)