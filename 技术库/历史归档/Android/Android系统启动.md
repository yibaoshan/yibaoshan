
Android 系统以Linux内核为基础，所以对于进程的管理自然离不开Linux本身提供的机制。例如：

- 通过fork来创建进行
- 通过信号量来管理进程
- 通过 proc 文件系统来查询和调整进程状态
- ...

本文基于 Android O (8.1.0_r81)

## init

init 是用户态的第一个进程，由 Linux 内核启动，进程号为 1，代码在 system/core/init/init.cpp

按照执行流程，init 实际上被执行了 4 次，分别是:

```
int main(int argc, char** argv) {
    if (!strcmp(basename(argv[0]), "ueventd")) {
        return ueventd_main(argc, argv);
    }

    if (!strcmp(basename(argv[0]), "watchdogd")) {
        return watchdogd_main(argc, argv);
    }
    bool is_first_stage = (getenv("INIT_SECOND_STAGE") == nullptr);

    if (is_first_stage) {
      	// ...
		setenv("INIT_SECOND_STAGE", "true", 1);
        char* path = argv[0];
        char* args[] = { path, nullptr };
        execv(path, args);
    }
  	// second stage ...
}
```

### watchdogd

watchdogd 是独立的看门狗进程，负责定期喂狗。核心代码:

```
int watchdogd_main() {
  int fd = open("/dev/watchdog", O_RDWR|O_CLOEXEC);
  ioctl(fd, WDIOC_SETTIMEOUT, &timeout);
  while (true) {
    write(fd, "", 1);
    sleep(interval);
  }
}
```

看门狗是一个独立的硬件，在嵌入式设备中通常用来检测设备存活状态，在一段时间超时后，看门狗硬件会认为系统已经跑飞，从而重启设备。

### first_stage

首先进行最基本的文件系统初始化:

```
mount("tmpfs", "/dev", "tmpfs", MS_NOSUID, "mode=0755");
mkdir("/dev/pts", 0755);
mkdir("/dev/socket", 0755);
mount("devpts", "/dev/pts", "devpts", 0, NULL);
...
```

然后，first stage 才正式开始。

第一步，挂载文件系统。需要挂载内容主要从 device_tree_fstab_ 而来

第二步，AVB(Android Verified Boot) 校验

第三步，初始化 SELinux。

注意此时我们还是在 kernel domain 中，加载完 sepolicy 之后，需要重新设置 /init 文件的 SELinux 属性(selinux_android_restorecon("/init", 0))，并以新的 init domain 重新执行 init 程序，进入第二阶段。

### second_stage

第二阶段的 init 进程，就是我们在 Android 用户态中见到的真正程序。

这一阶段主要包括对设备属性(property)相关操作以及 initrc 的处理，执行对应的开机启动进程；

其他还有比如 SELinux 的二次初始化，以及对子进程退出信号和 property_change 等事件的 epoll 循环监听和处理等。

首先说说 property，这是 Android 中一个简单的 key/value 内存数据库，可通过 getprop/setprop 来获取和设置。

除了属性服务，init 中另外一个重要的功能就是对 initrc 的处理

毕竟作为用户态的第一个进程，其肩负了启动其他进程和服务的使命。

在 Linux 中使用 init.rc 文件来描述各个启动项的启动属性和顺序

```
    std::string bootscript = GetProperty("ro.boot.init_rc", "");
    if (bootscript.empty()) {
        parser.ParseConfig("/init.rc");
        parser.set_is_system_etc_init_loaded(parser.ParseConfig("/system/etc/init"));
        parser.set_is_vendor_etc_init_loaded(parser.ParseConfig("/vendor/etc/init"));
        parser.set_is_odm_etc_init_loaded(parser.ParseConfig("/odm/etc/init"));
    } else ...
```

## zygote

zygote 是所有 Android 进程的父进程，因为 Java 虚拟机就是在该进程中初始化的。

其可执行文件名称为 app_process，入口代码在 frameworks/base/cmds/app_process/app_main.cpp 。

zygote 的代码分为两大部分，一部分在 native 层，另一部分则使用 Java 来编写。

### Native 层

native 层的入口就是上述 app_main 的入口，根据命令行参数执行不同的操作，在末尾进入了 AppRuntime 的main 函数:

AppRuntime 继承于 AndroidRuntime， start 函数的作用有两个:

1. 启动 Java 虚拟机；
2. 调用第一个参数对应 Java 类的 main 函数；

```
void AndroidRuntime::start( className, options, bool zygote)
{
    JNIEnv* env;
    if (startVm(&mJavaVM, &env, zygote) != 0) {
        return;
    }
    onVmCreated(env);
    // 启动线程将会成为 jvm 的主线程，执行结束后该 vm 也会被销毁
    jclass startClass = env->GetStaticMethodID(startClass, "main", "([Ljava/lang/String;)V");
    env->CallStaticVoidMethod(startClass, startMeth, strArray);
}
```

### Java 层

进入 Java 层之后，执行的是 com.android.internal.os.ZygoteInit 类的 main 函数。

代码在 frameworks/base/core/java/com/android/internal/os/ZygoteInit.java

zygote 初始化可以归纳如下

- 启动 DDMS，即 Dalvik Debug Monitor Server，在新版本中被 Android Profiler 代替；
- 预加载一些常用类和资源。由于 app 进程由 zygote fork 而来，因此子进程也继承了预加载的资源，从而加速应用的创建和初始化过程，子进程使用 copy-on-write 方式复用资源；
- unmount root 存储空间，设置 seccomp policy；
- 启动 system_server，这是咱们下一节的主角；
- zygoteServer.runSelectLoop: 监听创建的 socket 并循环等待连接，接收到子进程启动命令后则运行ZygoteConnection.processOneCommand 来 fork 并启动子进程。

```
Runnable processOneCommand(ZygoteServer zygoteServer) {
    pid = Zygote.forkAndSpecialize(...); // 两次返回
    if (pid == 0) {
        zygoteServer.setForkChild(); // in child
        zygoteServer.closeServerSocket();
        serverPipeFd = null;
        return handleChildProc(parsedArgs, descriptors, childPipeFd);
    } else {
        childPipeFd = null;
        handleParentProc(pid, descriptors, serverPipeFd); // In the parent. A pid < 0 indicates a failure and will be handled in
        return null;
    }
}
```

Zygote 进程创建 app 进程之后一步步进入到最终 Android 应用的 Activity .onCreate 函数中，最终执行应用程序的用户代码

在 app 进程启动前我们可以做很多有趣的事情

比如大家熟知的 Xposed 框架，原理上就是修改了 zygote 的代码

在应用启动前判断目标的包名是否需要 hook，如果匹配则 hook 对应的函数 (比如将函数 flag 修改为 native，增加注入回调处理等)。

## system_server

。。。未完待续

## 参考资料

- [Android 用户态启动流程分析](https://evilpan.com/2020/11/08/android-init/)
