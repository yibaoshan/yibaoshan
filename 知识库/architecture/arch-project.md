
# Hook

从时机上可以分为编译时 hook 和 运行时 hook 两大类。

编译时 hook 主要以构建过程中的字节码修改为主，代表有 Lancet、ByteX 等

运行时 hook 可以细分为 Java层 和 Native 层 hook

Java 层常用的有 反射替换、动态代理 等 , Native 层主要就是以 PLT 和 inline hook 为主，比如字节的 ShadowHook

Clean Architecture
   - 分层设计（Entities → Use Cases → Interface Adapters → Frameworks），依赖规则（内层不依赖外层）
   - 中心思想，分层、依赖倒置原则、边界划分、数据流转
   - 结合 Dagger/Hilt 实现依赖注入，业务逻辑与框架解耦

# 动态化与组件化

1. 组件化
   - 解决模块间通信（ARouter原理）、资源冲突（资源命名规范）、独立调试（Gradle动态切换Application/Library）。
   - 架构图：分层设计（App壳工程 → 业务组件 → 功能层 → 基础库）。
2. 动态化方案
   - 热修复（Tinker/Sophix）、插件化（RePlugin/Shadow），重点理解类加载机制与资源 Hook。
   - 动态换肤方案。

#  IoC/AOP 实践

1. 依赖注入（IoC）
   - Dagger2/Hilt的核心：@Component、@Module、@Inject，解决依赖图的构建与生命周期管理。
2. AOP 切面编程
   - 使用 AspectJ 或 ASM 实现无侵入埋点、性能监控（如方法耗时统计）。
