

从 Android 5.0（Lollipop）开始，Android Runtime（下文简称ART）就彻底代替了原先的 Dalvik，成为 Android 系统上新的虚拟机。


## ART VS. Dalvik

Dalvik 虚拟机是 2008 年跟随 Android 系统一起发布的。

当时的移动设备的系统内存只有 **64M** 左右，CPU 频率在 **250~500MHz** 之间。

这些年移动芯片的性能每年都有大幅提升。如今的智能手机内存已经有6G甚至8G至多。

CPU 也已经步入了 64 位的时代，频率高达 2.0 GHz甚至更高。

硬件的更新，常常也伴随着软件的换代。

因此，Dalvik 虚拟机被淘汰也是情理之中的事情。

Dalvik 之所以要被 ART 替代包含下面几个原因：

- Dalvik 是为 32位 设计的，不适用于 64位 CPU。
- 单纯的 "**字节码解释**" 加 "**JIT编译**" 的执行方式，性能要弱于本地机器码的执行。
- 原先的 "**垃圾回收机制**" 不够好，会导致卡顿。

## 参考资料

- [Android运行时ART简要介绍和学习计划](https://blog.csdn.net/Luoshengyang/article/details/39256813)
- [ART 在 Android 安全攻防中的应用](https://evilpan.com/2021/12/26/art-internal/#java-vm)
