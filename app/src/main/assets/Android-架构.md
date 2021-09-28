##### Android系统架构

- Linux内核
- 硬件抽象层HAL(Haraware Abstraction Layer)
- 运行库
  - 系统native库
  - Android运行时环境(虚拟机在这)
- 应用框架层Framework
- 应用层System Apps

#####Android启动

1. 开机上电，板子ROM代码拉起BL，BL拉起kernel
2. kernel初始化进程/内存/驱动/低内存等，退化为0进程，启动init和kthreadd
3. init启动adbd/logd等用户守护进程，并且启动ServiceManger(binder管家)，最后解析init.rc文件，创建zygote进程和servicemanager进程(binder)
4. zygote加载虚拟机，启动SystemServer，SystemServer是启动/管理所有系统服务的

###Android系统进程

#### ServiceManager

ServiceManager启动流程：

1. 启动进程：init.rc文件拉起
2. 启用binder机制：调用binder_open和mmap申请128k控件
3. 发布自己的服务：
4. 等待并相应请求：

以finger举例：在main方法中，调用defaultServiceManager()获取SM，再调用addService(String name,IBnder service)将自己注册到系统服务

####Zygote

Zygote的作用主要是三个：

一是加载虚拟机，包括常用类、JNI、主题资源等

二是启动SystemServer，管理所有系统服务

三是孵化应用进程

Zygote启动流程：同样也是由init.rc拉起

####SystemServer

负责启动系统服务

```
public static void main(String[] args) {
    new SystemServer.run();
}

private void run(){
    Looper.prepareMainLooper();
    System.loadLibrary("android_servers");
    createSystemContext();
    startBootServices();
    startCoreServices();
    startOtherServices();
    Looper.loop();
}

public void systemReady(){

}
```

#### FinalizerWatchdogDaemon

守护进程：finalize方法10s结束进程

##### Android系统服务

- ActivityManagerService(AMS)：Activity管理
- ActivityTaskManagerService(ATM)：Android10中引入，管理/调度Activity
- WindowManagerService(WMS)：窗口管理
- PackageManagerService：包管理
- PowerManagerService：电源管理
- Installer：安装服务
- DisplayManagerService：显示挂历
- BatteryService：电源管理

##### 问题分析

- 为什么SystemServer和zygote之间通信要采用socket？

  答：主要为了解决fork问题，Unix设计守则3：多线程不准使用fork

- 为什么一个Java程序对应一个虚拟机？

  答：稳定性：独立环境，安全性：独立环境不共享内存