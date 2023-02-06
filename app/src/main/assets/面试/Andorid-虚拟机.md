
## JIT 与 AOT

- **解释执行**：JVM / Dalvik / ART 从磁盘加载 class / dex 到内存，将代码解释成目标平台指令集，等待 OS 调度执行
- **JIT (Just-in-time compilation)**：实时收集热点代码，满足阈值动态编译为 machine code，等待 OS 调度执行
- **Ahead of time**：提前编译为本地机器码，格式为 oat（遵循 ELF 格式），缺点是增加安装耗时和占用更多本地存储

## Dalvik VS ART

- Android 1.0，Dalvik 作为 Android 虚拟机运行环境
- Android 2.2，Dalvik 加入 JIT 动态编译
- Android 4.4，加入 ART 虚拟机，此时的 ART 只支持 AOT
- Android 5.0，Dalvik 退出舞台
- Android 7.0，ART 混合使用 AOT 编译，解释和 JIT 三种运行时
- **指令集**：JVM 和 ART 都是基于栈的指令集实现（stack-based），而 Dalvik 是基于寄存器的指令集实现（register-based）
- 基于栈：指令简单；
- 基于寄存器：执行速度更快（因为内存访问次数会减少），代价是更大的代码长度

## 方法执行

ART 对于 Java 方法实现了两种执行模式 ArtMethod#Invoke()

- 解释执行字节码，入口是 EnterInterpreterFromInvoke，该函数定义在 art/runtime/interpreter/interpreter.cc
- 快速执行，即直接调用通过 OAT 编译后的本地代码，在 ARM64 中的定义在 art/runtime/arch/arm64/quick_entrypoints_arm64.S

## ART 内存分配

APP 运行时，虚拟机的内存分布：

- 256m ~ 512m 不等的 heap 空间，供

## ART 垃圾回收

art 支持的回收算法

```
enum CollectorType {
  kCollectorTypeMS,   mark-sweep 回收算法
  kCollectorTypeCMS, 并行mark-sweep 算法  
  kCollectorTypeSS,  // semi-space和mark-sweep 混合算法
  kCollectorTypeGSS,  分代kCollectorTypeSS.
  kCollectorTypeMC,  mark-compact算法
  kCollectorTypeCC,  concurrent-copying 算法
};
```

## GC Roots 与 Stop-The-World

safepoint

手动调用 System.gc() 时，由于安全点的缘故，可能在自动GC原本不会进入 GC 的位置上进入 GC