
我们项目一直使用的是 Glide 图片加载库，选用理由很简单

- 用户量大，大部分人都知道怎么用。上手开发维护成本低
- Glide 实现了 ComponentCallbacks2 接口，低内存通知自动清除缓存
  - clearMemory() 方法中执行了 bitmap、memoryCache、array 等内存缓存
- 唯一的缺点是不支持 webp 动图，后端可能会下发，不过也有三方库补上该缺点

Glide 源码保存在 library 目录下，以下基于 v4.11.0

## 基本用法

图片框架解耦：

- 我们在业务基建模块（business）定义了一系列的图片加载接口（ImageLoaderInterface），比如：圆形、圆角、毛玻璃
- 又定义了 ImageLoader 单例对象，初始化时要求传入 ImageLoaderInterface
- 接着在三方库模块（library）接入 glide ，创建 ImageLoaderImpl 实现 ImageLoaderInterface 接口
- 最后在 application 创建 ImageLoaderImpl 对象，并传入 ImageLoader 单例

AOP 框架：

- 我们的项目利用 BindingAdapter 实现 AOP，图片的网络加载只需在 xml 指定 url 即可，由 BindingAdapter 调用 ImageLoader 实现
- 另外，对于 drawable 类型资源，如果需要压缩（比如jpg格式），也可以在 xml 指定

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

绑定生命周期流程：

- Glide的请求通过requestmanager管理
- requestmanager创建时，会绑定一个requestfragment,该requestfragemnt为Glide.with(fragment)的儿子
- 同时requestmanager会持有requestframent的lificycle
- lificycle在构建requestfragment创建，为ActivityFragmentLifecycle
- 然后在进行请求或构建requestmanager时，会调用lifecycle.addListener，将自己作为observer添加进去，本身重写LifecycleListener的onStart,onStop,onDestoy,暂停请求或回复请求

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

另外，一般说起 Glide 缓存指的是 Resource 文件缓存，DrawableResource、BitmapResource、BytesResource等都是它的实现类

除了 Resource 缓存，Glide 还有另外两种缓存，或者说叫缓冲池，对象池：

- BitmapPool：用作 bitmap 复用
- ArrayPool：用作解码需求，避免大数组频繁分配造成的 GC 压力

比如 BitmapResource 就持有一个 BitmapPool 对象池成员

- Resource.recycle() 时使用的 Bitmap 不是被回收而是放回 BitmapPool
- 同时，Downsampler 并不是真的生成新的 Bitmap，有可能是从 BitmapPool 拿到的

再比如 StreamGifDecoder 和 StreamBitmapDecoder 都持有一个 ArrayPool 成员

- 解码过程中需要用到byte[]，但不是直接new byte[]，而是调用ArrayPool.get()从对象池中拿，用完了归还。

### 取逻辑

```
// com.bumptech.glide.load.engine.Engine.java
Engine#load() // 根据目标宽高、模式等生成 EngineKey
    
    // 优先从内存缓存查找
    -> synchronized loadFromMemory(key) 
           // 优先从存活对象查找
        -> loadFromActiveResources(key)
        
        -> loadFromCache(key) // 从内存查找
        
    // 从磁盘 I/O 或者网络 I/O 获取
    -> waitForExistingOrStartNewJob() 
        // 在此之前检查是否有相同 job 存在，有的话添加 callback 等回调，没有开启新任务
        -> EngineJob#start(new DecodeJob()) // 启动新线程
```

启动 DecodeJob 线程后，接下来会根据不同的 stage 执行不同操作，有点像 input 分发：

- INITIALIZE ：初始阶段，下一阶段是，磁盘 I/O 线程由 diskCacheExecutor 提供
  - RESOURCE_CACHE：磁盘获取转换后图片
  - DATA_CACHE：磁盘读取原始图片
- SOURCE：磁盘为空，从网络下载，这部分由 SourceGenerator.java 执行，网络 I/O 线程由 ActiveSourceExecutor 提供
  - ENCODE：下载完成，执行解码、转换
- FINISHED ：完成，可能是从磁盘读取，也可能是网络

磁盘缓存策略，可自由配置

- ALL 表示既缓存原始图片，也缓存转换过后的图片。
- NONE 不缓存任何内容。
- DATA 表示只缓存原始图片。
- RESOURCE 表示只缓存转换过后的图片。
- AUTOMATIC，自动选择，默认选项。可以理解为不支持转换后的资源缓存

### 存逻辑

ActiveResources 正在使用的图片，活跃内存缓存，有三个常用方法

- synchronized activate(key,resource)：弱引用保存到 hashmap ，调用时机：
  - loadFromCache()：从内存找到后，顺便放到活跃缓存
  - onEngineJobComplete()：走磁盘 I/O 或者网络 I/O 成功后，也加入
- synchronized deactivate(key)：从活跃缓存中删除
  - onResourceReleased()：释放内存时删除
- synchronized get(key)：从活跃缓存中获取 EngineResource

LruResourceCache 是 Glide 内存缓存实现类，它继承了 LinkedHashMap 完成 LRU 功能，并且实现了 Glide 的 MemoryCache 接口，默认容量为 100（v4.11版本）

- onResourceReleased()：在释放活跃内存的同时，也会将该 res 保存到 memoryCache

## 设计模式

- Glide 单例模式
- GlideBuilder 建造者模式
- ActivityFragmentLifecycle 生命周期监听，观察者模式
- BitmapPool、ArrayPool，缓存池，享元模式

## BitmapPool

Downsampler.java Glide 加载，解析 Bitmap 的实现类

## 参考资料

- [BitmapPool 了解吗？Glide 是如何实现 Bitmap 复用的？](https://juejin.cn/post/6951585628890857480)
- [Glide缓存分析](https://www.sunmoonblog.com/2018/07/27/glide-cache/)