
## 编译 / 打包流程

- **Javac**：Java 文件经过编译器（Java compiler）编译后，得到 class 字节码（Java bytecode）
- **Desugar**：D8 编译器优化 class 字节码，使低版本系统兼容 Java 高版本特性，如 JVM 加入 invokedynamic 支持 Java8 的 Lambda 表达式
- **ProGuard / R8**：代码混淆，代码压缩（提出无用的 Java 代码并且做一些优化）
- **DX / D8**：将所有的 Java 代码转换为 DEX 格式

Android Studio 3.4 版本后，加入 D8 R8 变更

R8 把 desugaring、shrinking、obfuscating、optimizing 和 dexing 都合并到一步进行执行

## DEX 文件

- 文件头
- 索引区
- 数据区

## Gradle 自动构建
