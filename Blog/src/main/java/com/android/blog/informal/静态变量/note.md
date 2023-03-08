
为什么 Android 中的静态变量会变空？

事情是这样的，前两天技术群的一个小伙伴抛出了一个问题，大家讨论的非常激烈

> 小 A：彦祖们，bugly 有个全局静态变量报空指针，是不是静态变量被系统回收了啊？
>
> 小 B：你怎么用的，是不是没赋值啊
>
> 小 A：赋值了啊，我在 SplashActivity 启动的时候已经初始化过了
>
> 小 C：会不会是静态变量被回收了？内存不够了系统会销毁 Activity，静态变量也会被清空吧
>
> 小 B：不可能，静态变量怎么可能会被回收呢
>
> 小 B：应用进程被杀掉了吧，静态变量也同时被清空了
>
> 小 A：那也不会啊，就算被杀了应用重启，默认启动页也还是 SplashActivity，也会重新走初始化流程的啊

页面使用的静态变量变空了？看到这儿，有经验的同学可能已经猜到发生的原因了：

**<font color=red>在系统内存不足的情况下，low memory killer 会根据进程优先级，直接干掉应用的进程</font>**

**<font color=red>并且，当应用程序再次激活时，它只会从它被杀死的地方恢复！</font>**

小 A 的应用进程肯定是被 LMK 干掉了，那不管什么类型的变量也都不存在了

而当用户再次打开 App 时，系统又从杀掉的页面开始恢复，小 A 同学的 SplashActivity 初始化逻辑没走

所以，在其他页面使用这个静态变量时，才会抛出 NullPointerException 空指针异常

**那这个问题应该怎么解决呢？**

既然静态变量存在 “未初始化就被使用” 的风险，那我们只需要保证在其他页面使用之前，完成初始化工作就行了，比如：

- 在 Application、ContentProvider 的 onCreate() 执行初始化
- 在静态变量所属类的 static 方法块执行初始化
- 页面使用静态变量前执行检查，为空的话执行初始化

## 思考环节🤔

虽然我们找到了问题发生的原因，并且给出了解决方案，但作为不好奇会死星人，总感觉不过瘾



## 系统内存和应用内存

一般我们说起 “内存不足”，指的是系统可用内存不足，和应用可用内存不足

在上一小节中我们知道了，当 “系统可用内存” 不足时，进程会被 LMK 干掉。那如果是 “应用可用内存” 不足，进程也会被杀死吗？

答案是：不会

当应用内存不足，也就是已使用的堆内存大于规定阈值的 3/4，那么 AMS 将启动回收策略（releaseSomeActivities），不在栈顶的 Activity 可能会被回收，即使此时的应用正在前台显示

此时释放的是进程的堆内存

当系统内存不足，LMK 守护进程启动清理工作，根据进程优先级，直接干掉进程

此时释放的是 Linux 用户空间内存



先说答案，在程序正常的运行过程中，已经被赋值的静态变量是不可能被回收清空的，除非：

- 这个类的类加载器被回收，且
- 静态变量所在的进程结束运行

虽然结论已经定下来了，但我们仍然可以

探究更多背后的细节

今天，我们以这个小伙伴的问题展开，

- 静态变量存在哪？为什么静态变量不会被清空回收掉？
- 在什么情况下，静态变量可能被回收？
- 如果静态变量所属的 ClassLoader 被卸载，那静态变量还在不在？
- 应用的类加载器会被卸载吗？如果会，什么时候执行？
- 最关键的，LMK 杀掉进程后，AMS 是如何恢复 Activity 栈的？

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