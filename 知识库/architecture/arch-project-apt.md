
APT (Annotation Processing Tool) 是 Java 提供的一种编译器插件，用来在编译期分析注解、生成代码

比如，Hilt 自动生成依赖注入代码、ButterKnife、ARouter 生成绑定代码、EventBus 生成自动注册表

因为 apt 可以在编译期分析、执行检查，所以，从这个角度来看，apt 也可以看做是一种编译期 hook 的工具

- 收集并分析
- 生成，apt 非常适合用来生成静态的模板代码，比如 Hilt、ButterKnife、ARouter、EventBus 等都是在编译期生成新的代码

RetentionPolicy 的几个范围

- SOURCE，仅存在于源代码中，编译后被抛弃，不会进入 class 文件，常用于编译期校验（如 @Override, @SuppressWarnings）
- CLASS，注解信息本身编译后存在于 class 文件中，也会被打包进 jar 或者 dex，但在运行时无法读取注解信息
- RUNTIME，注解信息保留到运行时，比如 Retrofit 实在动态代理生成 impl 类的时候利用 反射 API 获取 @GET("/users") 注解信息，来组装实际的请求