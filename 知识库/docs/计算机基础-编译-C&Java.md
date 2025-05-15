
### C/C++编译全链路


```c
预处理 → 词法分析 → 语法分析 → 语义分析 → 中间代码生成 → 目标代码生成 → 链接
```

1. **预处理阶段**：gcc -E展开宏、头文件，可插入自定义宏定义或头文件替换（如-D定义条件编译）
2. **编译优化**：-O2等优化级别设置，LLVM Pass可插入自定义优化逻辑
3. **目标文件生成**：objcopy工具可修改.o文件符号表，readelf分析段结构
4. **动态链接**：LD_PRELOAD环境变量劫持动态库函数（如内存分配监控）

关键干预点：

- 编译器插件：Clang/LLVM插件实现AST级别代码改写（如安全检测规则注入）
- 链接脚本：自定义.ld文件控制内存布局，重定向符号地址
- 二进制修补：patchelf修改ELF文件动态链接器路径或段属性

### Java编译全链路

```c
.java → .class（javac）→ JAR/WAR → JVM加载（类加载器）→ JIT/AOT编译
```

1. **字节码生成**：javac -parameters保留参数名，-g生成调试信息
2. **字节码增强**：ASM/Javassist在编译期或类加载时修改字节码（如APM探针注入）
3. **动态代理**：java.lang.reflect.Proxy实现运行时接口拦截

关键干预点：

- Annotation处理器：AbstractProcessor实现编译期代码生成（Lombok原理）
- Instrumentation API：javaagent机制实现类加载时的字节码改写（应用性能监控）
- GraalVM Native Image：AOT编译时将字节码转为本地机器码，可定制反射配置

### Python编译/解释过程

```c
.py → 词法分析 → 语法树 → 字节码（.pyc）→ Python虚拟机执行
```
