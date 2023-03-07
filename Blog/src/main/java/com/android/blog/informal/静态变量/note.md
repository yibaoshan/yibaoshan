
为什么 Android 中的静态变量会变空？

事情是这样的，最近在技术群有个小伙伴抛出了一个问题，大家讨论的非常火热

A：佬们，我的一个全局静态变量报空指针，是不是静态变量被回收了啊？

B：静态的不会被回收吧，是不是没赋值啊

A：不可能，我在 SplashActivity 已经初始化过了

看到这，有经验的小伙伴已经知道问题的答案了

先说答案，在程序正常的运行过程中，已经被赋值的静态变量是不可能被回收清空的，除非：

- 这个类的类加载器被回收，且
- 静态变量所在的进程结束运行

面对 bugly 上报的空指针，陷入了沉思

## 静态变量存在哪？

## 什么情况下静态变量会被回收？

作为一名资深的八股文选手，

### ClassLoader 被卸载

我们需要去验证，首先得把我们的 java 编译成 dex 文件，一共分为三步：

1. 先把 java / kotlin 文件通过 javac 命令编译为 .class 文件
2. 接着利用 jar 命令将 class 文件成 jar 包
3. 最后用 Google 提供的 sdk 工具，把 jar 包编译为我们的 dex 文件

1、java 编译为 class

javac Dream.java

2、class 编译为 jar

jar cvf xxx.jar xxx.class

3、jar 编译为 dex

dx --dex --output=xxx_dex.jar xxx.jar

理论上是不可能出现 static 修饰的变量

class 对象都没了

好，到这里结论就很清晰了，当 ClassLoader 被卸载时，它

### 应用的类加载器会被卸载吗？

类回收的重要的条件之一，是类加载器被卸载，那

系统类加载器会被回收吗？若是会，什么时候执行？

这里我们就不绕弯子了，答案是在初始化 Application 时完成的创建

```
ActivityThread#handleBindApplication() // 创建应用的 Application
    -> ContextImpl#createAppContext(LoadedApk packageInfo) // 创建 APP 的上下文
        -> PackageInfo#getClassLoader(LoadedApk packageInfo)
            -> LoadedApk#getClassLoader()
                -> ApplicationLoaders#getClassLoader(zip)
```

链接放在这里了，感兴趣的朋友点击进入搜索 "PathClassLoader" 一探究竟

## Activity 恢复流程

作为资深八股文选手，理论上是不可能出现 static 修饰的变量

静态变量为空，只有

进程 kill

相信大多数 Android 开发在成长过程中，

综上所述