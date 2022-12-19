
自 Android 5.0 开始，Dalvik 虚拟机已经被废弃，其源码也已经被从 AOSP 中删除

因此想要查看其源码，需要获取 Android 4.4 或之前版本的代码。

本小节接下来贴出的源码取自 AOSP 代码 TAG android-4.4_r1

app_process.cpp main 方法调用 runtime.start 启动虚拟机，接着

- 通过startVm方法启动虚拟机
- 通过startReg方法注册Android Framework类相关的JNI方法
- 查找入口类的定义
- 调用入口类的main方法
- 处理虚拟机退出前执行的逻辑