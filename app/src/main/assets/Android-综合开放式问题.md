### 打包流程

### Android混淆机制

### Android类加载机制

几种类加载器：

- PathClassLoader: 主要用于系统和app的类加载器,其中optimizedDirectory为null, 采用默认目录/data/dalvik-cache/
- DexClassLoader: 可以从包含classes.dex的jar或者apk中，加载类的类加载器, 可用于执行动态加载,但必须是app私有可写目录来缓存odex文件. 能够加载系统没有安装的apk或者jar文件， 因此很多插件化方案都是采用DexClassLoader;
- BaseDexClassLoader: 比较基础的类加载器, PathClassLoader和DexClassLoader都只是在构造函数上对其简单封装而已.
- BootClassLoader: 作为父类的类构造器。

热修复核心逻辑：在DexPathList.findClass()过程，一个Classloader可以包含多个dex文件，每个dex文件被封装到一个Element对象，这些Element对象排列成有序的数组dexElements。当查找某个类时，会遍历所有的dex文件，如果找到则直接返回，不再继续遍历dexElements。也就是说当两个类不同的dex中出现，会优先处理排在前面的dex文件，这便是热修复的核心精髓，将需要修复的类所打包的dex文件插入到dexElements前面。

类加载器：

1. PathClassLoader
2. DexClassLoader

####Android安全

1. 可外部启用的activity需要注意拒绝服务漏洞
2. 尽量不暴露activity，必须暴露的加权限控制

字节码增强

ASM

 Javassist

Instrument

JVM提供的一个可以修改已经加载类的类库，专门为Java语言编写的插桩服务提供支持

**如何快速定位崩溃**

1. 崩溃的基本信息：

   进程名、线程名。前台进程还是后台进程，是不是UI线程

   Java类型还是native类型，还是anr

   自己的代码问题还是系统的代码问题

2. 系统和应用的运行日志

   系统的event logcat会记录APP的一些基本情况，记录在system/etc/event-log-tags中

3. 机型、系统、厂商、CPU等

4. 设备状态，是否root，是否是模拟器

5. 内存信息，考虑一些内存不足的崩溃是否多发生在低内存的手机上

6. 自定义的应用信息

**解决崩溃**

1. **优先级:** 优先解决Top 崩溃或者对业务有重大影响;其次简单易排查的java崩溃,疑难杂症放到最后

2. **尝试复现:**针对疑难杂症,可以通过早先在日志中自定义的应用信息来复现崩溃过程

3. **着眼硬件:**排查是否是手机系统,内存,机型引起的。

4. **系统错误:**有些问题是Android系统不同版本之间存在的问题。

####问题分析

**虚拟机相关**

1. JVM、DVM、ART的区别？

   答：jvm基于栈，dvm基于寄存器

   JIT和AOT的区别

   jvm执行class，dvm执行dex

   dvm只支持32，art支持64

   art4.0可选项，5.0全替换，art安装时会进行一次aot，将字节码编译成机器码

   7.0改为运行过得代码编译为机器码

2. JIT和AOT的概念

   答：JIT全称just-in-time，在dalvik虚拟机中，通过jit把字节码文件编译成机器码执行

   AOT全称ahead-of-time，安装时提前将字节码编译为机器码

3. Java反射慢在哪里？

   答：相对于直接执行字节码指令来说会慢一些，首先编译器无法优化，其次每次都需要查找消耗时间

**资源/文件相关**

1. Android中assets和raw目录的区别？

   答：相同点：原封不动的打包到apk

   不同点：raw归res管，获取方法是getResources().openRawResource(R.raw.xxx)

   assets获取方法时getAssets().open("xxx")

2. XML解析的三种方式？

   答：Dom解析，Sax解析，Pull解析

**屏幕分辨率/适配相关**

1. 你们的屏幕是如何适配的？

   答：我们利用DataBinding的BindingAdapter机制

2. 如何获取屏幕帧率？

   答：getWindowManager().getDefaultDisplay().getRefreshRate()

**打包/混淆相关**

1. Java和kotlin的方法签名有何不同？

   答：kotlin会有metadata元数据的签名，记得在proguard混淆时要保存签名

   keep class kotlin.Metada{*;}

2. 如何获取泛型类型？

   答：通过class的getGenericTypeArguments()获取实际编译后类型

**ANR相关**

- 什么是ANR？

  答：input响应大于5s，前台service大于20s，后天服务200s，前台广播10s，后台广播60s
  
- anr是如何产生的？

  答：以service举例，在创建service过程中最后会调用schedulesServiceTimeoutLocked，该方法会发送一个延迟消息，如果service正常执行完成，将会移除消息

- 如何自己实现一套handler框架？

- 如何访问系统api？

  答：下载framework.jar欺骗IDE

- 如何规避Android P对私有api限制？

  答：使用FreeRefection，1. classloader置null