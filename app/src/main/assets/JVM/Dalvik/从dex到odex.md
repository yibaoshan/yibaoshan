
APK 在安装的时候，安装服务 PackageManagerService 会通过守护进程 installd 调用一个工具 dexopt 对打包在 APK 里面包含有 Dex 字节码的 classes.dex 进行优化

优化得到的文件保存在 /data/dalvik-cache 目录中，并且以 .odex为 后缀名，表示这是一个优化过的 Dex 文件

## 参考资料

- [Android上的Dalvik虚拟机](https://paul.pub/android-dalvik-vm/)