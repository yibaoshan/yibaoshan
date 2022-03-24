## Overview
1. ContentProvider介绍
2. 启动流程
3. Uri介绍

## 一、ContentProvider介绍

在Android中常见的存储方式有：
- I/O文件
- SharePreferences
- SQLite
- 网络存储
- 还有一个就是ContentProvider了

### 1、ContentProvider生命周期

ContentProvider比较特殊，只有onCreate方法表示创建，一旦被创建，几乎除了内存不足外不会被销毁
其他的还有：
- onConfigurationChanged()
> 系统配置更改通知
- onLowMemory()
> 低内存
- onTrimMemory()
> 内存裁剪等级

### 2、ContentProvider创建方式

通常情况下，当前APP有大量数据需要提供给外部应用使用时，会使用的到ContentProvider
创建的方式也很简单，继承ContentProvider父类，实现query/delete/insert/update等方法就行了
通常内部也是由SQLite来实现，不过你也可以自己解析URI，自行实现内部方法

提供方注册流程：
1. 继承ContentProvider，重写onCreate()、query()、update()等方法，使用Android官方提供的UriMatcher来过滤意图
2. 在xml中注册，其中authorities为提供给访问者唯一标识，也可以使用readPermission、writePermission设置读/写权限

注意：

1. 当两个APP使用相同的authorities时，会导致第二个APP安装失败报错：INSTALL_FAILED_CONFLICTING_PROVIDER
2. 一般存储建议使用SQLite，因为增删改查都是返回影响的行号，其中query()方法返回值为Cursor，所以实现Cursor接口的类型也可以
3. 外部通过Uri调用ContentProvider访问数据时，若APP所在进程未创建，则创建该进程并初始化Application，但不会启动任何Activity

在系统中，相册、联系人等都是通过ContentProvider分享

### 3、ContentProvider使用方式

使用者调用流程：
1. 若调用的ContentProvider需要读写权限，在xml声明即可
2. 调用Context.getContentResolver()增删改查方法，并指定要操作的URI

注意：
1. 调用方可以使用Context.getContentResolver().registerContentObserver来观察监听数据变化

调用context的getContentResolver()方法可以获得ContentResolver实例对象，拥有该实例就可以调用query/delete/insert/update操作数据了

### 二、ContentProvider启动流程

AMS控制，随着Application创建而创建

### 三、URI介绍

> 是一种格式：content://authority/data_path/id


