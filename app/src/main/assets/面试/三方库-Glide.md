
我们项目一直使用的是 Glide 图片加载库，选用理由很简单

- 用户量大，大部分人都知道怎么用。上手开发维护成本低
- Glide 实现了 ComponentCallbacks2 接口，低内存通知自动清除缓存
  - clearMemory() 方法中执行了 bitmap、memoryCache、array 等内存缓存
- 唯一的缺点是不支持 webp 动图，后端可能会下发，不过也有三方库补上该缺点

Glide 源码保存在 library 目录下，以下基于 v4.11.0

## 基本用法

我们的项目使用了 BindingAdapter 实现 AOP ，图片加载也在基建模块抽了接口出来，在业务模块由 Glide 实现

```
ImageLoader.loadXX() // 多个重载方法
 - Glide.with(context).load(url).apply(options).into(iv)
```

## with 阶段（Lifecycle）

with() 是多个重载方法，返回值是 RequestManager，我们可以传入 Context、Activity、FragmentActivity、Fragment、View 等不同的上下文

它一共做了两件事：

- 绑定页面生命周期
  - ApplicationLifecycle
  - ActivityFragmentLifecycle
- 创建 / 初始化 RequestManager 请求管理者
  - asBitmap() / asDrawable() / asGif() / as File()
  - load(Bitmap / Drawable / String / Uri / File / byte[])

大致的方法调用：

```
Glide#with()
    -> getRetriever().get()
        -> checkAndInitializeGlide()
            -> initializeGlide()
```

## load 阶段（RequestBuilder）

load() 方法同样是重载方法，支持 bitmap、drawable、file等，以 string url 举例，load 方法主要干一件事：

**使用 RequestManager 创建 / 初始化 RequestBuilder**

确定加载的参数类型，以及参数的初始化，为下一步的 into 阶段做准备

这部分的源码调用很长，先忽略掉

## into 阶段（RequestBuilder）

into 阶段主要干了三件事：

- 封装 Request（requestOptions、监听等） 并发起请求
- request 获取图片原始数据
  - loadFromMemory()：首先尝试从内存加载
    - loadFromActiveResources()：尝试从正在使用的缓存获取
    - loadFromCache()：从内存 LruCache 中获取
  - waitForExistingOrStartNewJob()：尝试从磁盘获取，若为空，启动从网络下载
  - onResourceReady()：图片准备完成
- 将图片显示到 View 上

源码 load 目录下的 Encoder、Transformation等完成了图片的解码、转换等功能

load.engine 目录完成图片的下载和缓存功能

## AppGlideModule

Glide 为开发者提供了 AppGlideModule / LibraryGlideModule 类，我们可以在构建 Glide 之前，修改 Glide 的默认配置

两者的区别是：

- AppGlideModule 提供全局默认配置，有且只能有一个，由 GlideAnnotationProcessor 生成 Impl 类文件
  - 在 applyOptions() 方法中拿到 GlideBuilder 修改配置
- LibraryGlideModule 可以有多个，没用过先不管
  - 在 registerComponents() 方法中利用 Registry 来修改

继承 AppGlideModule 后，我们可以拿到 GlideBuilder 来修改 Glide 的默认配置：

- setBitmapPool()：bitmap 缓存池，默认 LruBitmapPool.java
- setArrayPool()：未知，默认 LruArrayPool.java
- setMemoryCache()：内存缓存策略，默认是 LruResourceCache.java
- setDiskCache()：磁盘策略，默认 InternalCacheDiskCacheFactory.java
- setDecodeFormat()：解码模式，4.11.0 版本默认 PREFER_ARGB_8888

## 三重缓存

Glide 的加载 / 缓存代码都在 com.bumptech.glide.load.engine 包下，我们只需要了解 Glide 缓存的取出逻辑，和缓存保存的时机