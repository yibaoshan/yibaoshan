HttpClient 已在 Android 6.0 中被删除

OkHttp 在 Android 4.4 及以后成为了 HttpURLConnection 的默认实现

OkHttp 本身是 Java 层框架，它负责：

- 构造请求（Request）
- 管理连接池和缓存
- 处理协议（HTTP/HTTPS、gzip 压缩等）
- 拦截器链处理

内部是通过 Java 的 `Socket` 或 `SSLSocket` 来完成网络 I/O 的：

```
Socket socket = new Socket(ip, port);
```

而 Android 的 `Socket` 实际是对 native 网络函数的封装，底层会通过 JNI 调用 C/C++ 层函数，即 **libc**（Android 的 C 标准库实现），最终由内核的 **Linux 网络协议栈** 接管

几个关于 OkHttp 重要的问题：

- 整体流程如下：核心是通过 `Interceptor.Chain` 串起了多个拦截器，最终由 `CallServerInterceptor` 发起真正的网络请求。
  ```
  OkHttpClient.newCall(Request)  
      → RealCall.execute()/enqueue()  
          → Dispatcher 管理线程执行  
              → 依次通过 Interceptor Chain（责任链）  
                  → 发起连接/查缓存/读写数据  
                      → 返回 Response
  ```
- OkHttp 分发器（Dispatcher）的工作：
  - `Dispatcher` 负责管理并发请求：
    - 有两个队列：`readyAsyncCalls` 和 `runningAsyncCalls`
    - 有最大并发数 `maxRequests` 和同一域名最大并发数 `maxRequestsPerHost`
  - 超出并发限制的请求进入等待队列，空闲时再调度执行
  - 小结，高并发控制和线程复用（默认使用线程池）

- 基于 **责任链模式** 拦截器，每个拦截器通过调用 `chain.proceed()` 将请求传递下去，同时可以在请求前后做处理，调用链如下：
  ```
  RealCall → InterceptorChain.proceed(request) → 
  |-- ApplicationInterceptor
  |-- RetryAndFollowUpInterceptor
  |-- BridgeInterceptor
  |-- CacheInterceptor
  |-- ConnectInterceptor
  |-- NetworkInterceptor(s)
  |-- CallServerInterceptor
  ```

# 一、常用 API

| 功能场景        | 示例方法                                           |
| ----------- | ---------------------------------------------- |
| 同步请求        | `OkHttpClient.newCall(request).execute()`      |
| 异步请求        | `enqueue(callback)`                            |
| 添加请求头       | `Request.Builder().header(...).build()`        |
| 添加请求体（POST） | `FormBody` / `RequestBody.create()`            |
| 文件上传 / 下载   | `MultipartBody`, `InputStream`                 |
| 设置超时、连接池    | `OkHttpClient.Builder().connectTimeout(...)`   |
| 拦截器使用       | `addInterceptor()` / `addNetworkInterceptor()` |
| 缓存设置        | `Cache(File, size)`                            |

## 自定义拦截器（Interceptor）

OkHttp 支持通过拦截器对请求和响应进行统一处理，分为：

- **Application Interceptor**：全局逻辑，不受重定向等影响
- **Network Interceptor**：在真正进行网络请求前后调用，能看到中间状态

| 功能场景           | 实现说明                                           |
| ------------------ | -------------------------------------------------- |
| **埋点日志**       | 请求开始/结束记录耗时、URL、状态码                 |
| **统一 Header**    | 给所有请求统一加上 `token`、`deviceId` 等          |
| **安全签名**       | 对请求参数按业务规则生成签名加密头部               |
| **Token 失效刷新** | 遇到 401 自动刷新 token 并重试请求（注意防止递归） |

举个刷新 Token 拦截器的例子

```kotlin
class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (response.code == 401) {
            // 同步刷新 token，再次请求
        }

        return response
    }
}
```

## 网络优化

1. **连接池复用优化**
   - `ConnectionPool(maxIdleConnections, keepAliveDuration)` 控制连接数量与空闲时长
   - 推荐配置：默认即可，关键是避免频繁创建新连接
2. **启用压缩（Gzip）**
   - 默认开启：通过 `BridgeInterceptor` 添加请求头 `"Accept-Encoding: gzip"`
   - 服务端返回 Gzip 响应体，`CallServerInterceptor` 自动解压
3. **缓存处理**
   - 配置磁盘缓存目录与大小：
    ```
    val cache = Cache(File(context.cacheDir, "http_cache"), 10 * 1024 * 1024)
    OkHttpClient.Builder().cache(cache)
    ```
   - 控制缓存行为的关键头部：
     - `Cache-Control`, `Expires`, `ETag`, `If-None-Match`
   - 推荐：结合 `CacheInterceptor`、设置合理的缓存策略（如图片、静态 JSON）
4. **减少主线程阻塞**
   - OkHttp 异步请求默认使用线程池，不要在主线程发同步请求。
    - 文件下载时使用流式处理避免内存占用高峰。

# 二、设计思想和基本原理

1. 拦截器链（核心）
   - `RealCall -> getResponseWithInterceptorChain`
   - 拦截器链结构类似责任链：每个拦截器调用 `chain.proceed()` 向后传递
   - 内部默认有 5 个核心拦截器（Retry、Bridge、Cache、Connect、CallServer）
2. 请求流程（同步 / 异步）
   - 同步：直接执行 `RealCall.execute()`
   - 异步：封装成任务加入 `Dispatcher` 线程池
3. Dispatcher 调度器
   - 控制并发数、请求队列
   - 支持按 Host 控制并发（默认每个 host 最多 5 个连接）
4. 连接管理：ConnectionPool
   - 支持 HTTP/1.1 的复用 & HTTP/2 的多路复用
   - 复用核心：`RealConnection` + `StreamAllocation`
5. 缓存机制
   - 基于响应头（Cache-Control）实现 HTTP 协议缓存策略
   - 通过 `DiskLruCache` 持久化缓存响应
   - 拦截器：`CacheInterceptor` 判断是否使用缓存
6. HTTPS 实现
   - 使用 Java 自带的 `SSLSocketFactory`
   - 支持证书校验、主机名验证，可配置自签名证书

## 设计思想

| 模块/功能      | 对应类                                | 模式           |
| -------------- | ------------------------------------- | -------------- |
| 请求构建       | `Request`, `Request.Builder`          | Builder        |
| 网络调度       | `Dispatcher`, `RealCall`              | 责任链、线程池 |
| 缓存策略       | `Cache`, `InternalCache`              | 策略模式       |
| 拦截器链       | `Interceptor`, `RealInterceptorChain` | 责任链         |
| 连接复用       | `ConnectionPool`, `RealConnection`    | 享元           |
| 异常重试与跟踪 | `RetryAndFollowUpInterceptor`         | 策略           |

# 三、常见问题

- DNS UnknownHostException 地址无法解析、无法访问等问题，引入 HttpDns，优先使用本地 SDK 缓存的 ip 
