
基于 OkHttp 二次封装的网络库，解决了 OkHttp 使用不优雅（繁琐的模板代码）的 bug。

# 一、基础使用

常用注解

- @GET, @POST, @PUT, @DELETE
- @Path, @Query, @Field, @Body, @Header, @Url 等

都是 RUNTIME 类型，运行时动态代理生成对象

配置重试、超时、缓存

```
OkHttpClient.Builder()
    .retryOnConnectionFailure(true)
    .connectTimeout(10, TimeUnit.SECONDS)
    .cache(Cache(File(cacheDir, "http_cache"), 10 * 1024 * 1024))
```

接口在运行时由 Java 动态代理反射创建实例对象

整体结构 

- Retrofit 构建器模式 + 组合模式
- ServiceMethod：将接口转成实际请求逻辑
- OkHttpCall：封装真正的 HTTP 调用逻辑
- ExecutorCallAdapterFactory：管理线程调度（主线程切换）

| 场景                                | 使用模式              |
| ----------------------------------- | --------------------- |
| `Retrofit.Builder`                  | 建造者模式（Builder） |
| `create()` + 动态代理               | 代理模式（Proxy）     |
| `ServiceMethod` + Converter/Adapter | 策略模式（Strategy）  |
| `Retrofit` 封装外部复杂实现         | 外观模式（Facade）    |
| 注解解析 → ServiceMethod 调用链     | 模板方法 & 策略组合   |
| OkHttp 拦截器链                     | 责任链模式（Chain）   |
