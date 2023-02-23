
## HttpUrlConnection

## 底层网络支持

```
class RealConnection {
    // 所有 http / https 连接都被包装成 call 走该方法发起请求
    void connectSocket(timeout, readTimeout, call, eventListener){
        Platform.get().connectSocket(...);
    }
}
```

okhttp 的 Platform#get() 表示支持的平台，基本都是利用 java.net.socket 发起的请求，v3.9 版本支持的有：

- findPlatform()方法首先判断是不是 Android 平台，首选 AndroidPlatform
- 其次是：Jdk9Platform、JdkWithJettyBootPlatform、Platform

至于 java.net.socket，交由 Android 的 dalvik / art 虚拟机，或者 Java 的 Hotspot 虚拟机实现

## 架构设计

如果画张图的话，okhttp 的架构可以看做是 5 个拦截器

## 各个模块职责&细节处理