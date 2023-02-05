## 编译 / 打包流程


- **Javac**：Java 文件经过编译器（Java compiler）编译后，得到 class 字节码（Java bytecode）
- **Desugar**：D8 编译器优化 class 字节码，使低版本系统兼容 Java 高版本特性，如 JVM 加入 invokedynamic 支持 Java8 的 Lambda 表达式

## Gradle 自动构建



## JIT



## 基于栈 or 基于寄存器

- JVM 和 ART 都是基于栈的指令集
- Dalvik 虚拟机是基于寄存器的指令集实现