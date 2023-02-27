
大体流程

```
RealCall->excute()->Dispatcher->excute()->intereceptors()
```

## 实际请求示例

```
OkHttpClient client = OkHttpClient().Builder().addInterceptor(...).build();
Request request = new Request.Builder().url("https://www.google.com").build();
Response response = client.newCall(request).execute();
return response.body().string();
```

## 关键类支持

### OkHttpClient

- 是整个 OkHttp 的核心管理类，所有的内部逻辑和对象归 OkHttpClient 统一来管理，它通过 Builder 构造器生成

### Request / Response

这两个类的定义是完全符合 Http 协议所定义的请求内容和响应内容

- Request 构建请求封装类，内部有 url, header , method，body 等常见的参数
- Response 是请求的结果，包含 code, message, header,body

### RealCall

肩负了调度和责任链组织的两大重任

- 负责请求的调度（同步（execute）的话走当前线程发送请求，异步（enqueue）的话则使用OkHttp内部的线程池进行）
- 负责构造内部逻辑责任链，并执行责任链相关的逻辑，直到获取结果

## 架构设计

如果画张图的话，okhttp 的架构可以看做是 5 个拦截器

OkHttp 将整个请求的复杂逻辑切成了一个一个的独立模块并命名为拦截器(Interceptor)，通过责任链的设计模式串联到了一起，最终完成请求获取响应结果。

## 责任链&拦截器

几个关键的拦截器：

- RetryAndFollowUpInterceptor：负责请求失败 的 重试 工作以及 重定向
- CacheInterceptor：负责缓存处理
- ConnectInterceptor：请求，复用处理

### 责任链拼装

```
class RealCall implements Call {
    
  	//1. 调用execute发起请求
    @Override
    public Response execute() throws IOException {
        client.dispatcher().executed(this);
        return getResponseWithInterceptorChain();
    }

  	//2. 获取响应报文，基于责任链模式
    Response getResponseWithInterceptorChain() throws IOException {
        // 拼装拦截器
        List<Interceptor> interceptors = new ArrayList<>();
        interceptors.addAll(client.interceptors());
        interceptors.add(new RetryAndFollowUpInterceptor(client));
        interceptors.add(new BridgeInterceptor(client.cookieJar()));
      	//最终调用发起
        interceptors.add(new CallServerInterceptor(forWebSocket));
        
        Interceptor.Chain chain = new RealInterceptorChain(...);
        Response response = chain.proceed(originalRequest);
        return response;
    }
}
```

### 自定义拦截器

```
class MyInterceptor implements Interceptor {
  @Override public Response intercept(Chain chain) throws IOException {
    //处理request请求，报文加密之类就是在这一步
    Request request = chain.request();
    //处理返回结果，比如统一报错拦截
    Response response = chain.proceed(request);
    return response;
  }
}
```

### interceptors&networkInterces

- interceptors，应用拦截器
- networkInterceptors，网络拦截器，网络请求相关

没用过，先不管

## 连接复用 DNS&Socket

由 ConnectInterceptor 拦截器完成，实现在 ExchangeFinder#findConnection() 方法中

其内部保存了一个 ConnectionPool 连接池