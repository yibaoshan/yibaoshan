
# 聊聊 Glide | 不看源码，只聊设计

Glide 是一个非常优秀、高性能的开源图片加载框架，从我入行开始一直到现在，工程里面的图片库几乎都是用它。

截止到发稿，Glide 版本已经升到了 5.0，Java + Kotlin 代码总行数也膨胀到近 9w 行

![lines-code](imgs/glide-v5_0-code-lines.png)

Glide 的工程太庞大了，源码肯定是看不了一点，所以，我准备从最基础的使用代码入手，来尝试剖析 Glide 的功能与设计

```kotlin
Glide.with(context).load(url).into(imageView)
```

本文将从 Glide 链式调用的 3 个步骤展开，介绍 Glide 的缓存机制、生命周期集成、Bitmap 复用等

1. with(context)
   - 绑定生命周期，Glide 会在内部创建一个与该组件生命周期绑定的 RequestManager，这个 RequestManager 会监听传入的组件的生命周期事件
     - 在 onStop() 或 onPause() 时暂停请求。 
     - 在 onDestroy() 时取消请求并释放相关资源（如 Bitmap 内存）。
   - 为 Glide 提供上下文，如果你接下来传入的是资源文件，那 Glide 需要 Context 才可以访问 Res，另外，磁盘缓存涉及到的文件系统也需要 Context 才能工作。
2. load(x)
   - 入参可以是 String, Uri, File, DrawableRes, Bitmap 等 
   - 这一步的工作主要是定义请求和一些准备工作，构建和配置 RequestBuilder 类，告诉 Glide 你想要加载什么图片，以及用什么方式加载
   - load() 后面的 placeholder()、error()、centerCrop()、override() 等都属于配置阶段，这些选项会被封装到一个 RequestOptions 对象中，并关联到当前的 RequestBuilder。
3. into(imageView)
   - 这一步才是真正的请求触发和执行，常见的缓存处理、BitmapPool、图片的转换处理都在这个阶段 
   - 入参通常是 ImageView，也可以是自定义 Target，同时 Glide 内置了几个 Target 给开发者使用，比如 ViewTarget、SimpleTarget
   - 不同的 target 的核心加载流程（缓存、数据获取、解码、转换）是基本一致的，只是最终的 “结果消费” 和 “显示方式” 不一样，这一点我们在第三节会详细介绍。

# 一、Glide#with

Glide.with(context) 是我们使用 Glide 的第一步，传入 Context 的作用开头已经介绍过了

- 一是绑定生命周期，Glide 会在内部创建一个与该组件生命周期绑定的 RequestManager，这个 RequestManager 会监听传入的组件的生命周期事件，用来管理请求。
- 二是为 Glide 提供上下文，如果你接下来传入的是资源文件，那 Glide 需要 Context 才可以访问 Res，另外，磁盘缓存涉及到的文件系统也需要 Context 才能工作。

本小节还会来探讨

1. 多次调用 Glide#with() 传入同一个 Activity/Fragment 对象，Glide 会不会也创建多个 RequestManager，理论上应该共用一个 RequestManager ，不然就是资源浪费了
2. 传入 Application#Context 和 Activity/Fragment ，结果有什么不一样？

进入正文前，我们先来看 Glide 单例的初始化代码

## Glide 的初始化

首次加载图片会触发 Glide 单例对象的创建和初始化，Glide 的创建和初始化也是建造者模式，由 GlideBuilder 负责构建配置，然后通过 build() 方法创建 Glide 对象。

```java
public final class GlideBuilder {
    Glide build(context, manifestModules, annotationGeneratedGlideModule) {
        
        // step 1, init ThreadExecutor for Glide
        sourceExecutor = GlideExecutor.newSourceExecutor();
        diskCacheExecutor = GlideExecutor.newDiskCacheExecutor();
        animationExecutor = GlideExecutor.newAnimationExecutor();

        // step 2, init network monitor
        connectivityMonitorFactory = new DefaultConnectivityMonitorFactory();

        // step 3, init bitmapPool
        memorySizeCalculator = new MemorySizeCalculator.Builder(context).build();

        if (bitmapPool == null) {
            int size = memorySizeCalculator.getBitmapPoolSize();
            
            if (size > 0) bitmapPool = new LruBitmapPool(size);
            else bitmapPool = new BitmapPoolAdapter();
        }
        
        // step 4, init all of cache
        arrayPool = new LruArrayPool(memorySizeCalculator.getArrayPoolSizeInBytes());
        memoryCache = new LruResourceCache(memorySizeCalculator.getMemoryCacheSize());
        diskCacheFactory = new InternalCacheDiskCacheFactory(context); // 250 MB of cache

        // step 5, init engine
        engine = new Engine(memoryCache, diskCacheFactory, diskCacheExecutor, sourceExecutor, animationExecutor);

        return new Glide(context, engine, memoryCache, bitmapPool, arrayPool, requestManagerRetriever, connectivityMonitorFactory, logLevel, defaultRequestOptionsFactory, defaultTransitionOptions, manifestModules, annotationGeneratedGlideModule);
    }
}
```

在 build() 函数，我们会看到很多熟悉的老面孔

1. 初始化线程池
   - Glide 有好几个线程池，涉及网络、文件 I/O 和 Bitmap 解码/转换的操作都发生在这里的后台线程中。
   - 具体到初始化流程，有三个线程池被创建
     - sourceExecutor，用于执行 I/O 密集型任务，如网络下载、大型文件读取。 
     - diskCacheExecutor，专用于执行磁盘缓存的读写操作。 
     - animationExecutor，用于处理 GIF 或其他动画的解码和帧更新。
   - 我这里把判空逻辑删除了，实际开发中，你可以设置为自己管理的线程池，只需要在 AppGlideModule 中，拿到 GlideBuilder 对象以后，调用 setXXXExecutor 即可。
2. 初始化网络连接状态监视器，DefaultConnectivityMonitorFactory，感知网络连接，网络不可用时自动暂停网络请求，在恢复时自动重启，忽略。
3. MemorySizeCalculator 会根据设备内存大小和屏幕分辨率计算出，当前设备能够支持的 Bitmap 缓存池大小
   - 如果用户设备性能不是很高，那么 getBitmapPoolSize() 可能会返回 0
   - 一旦返回 0 ，bitmapPool 会使用 BitmapPoolAdapter，这是一个 “空操作” 的 BitmapPool，Glide 在这台设备上不会执行 Bitmap 缓存和复用
   - 如果 getBitmapPoolSize() 大于 0 ，启用 LruBitmapPool
   - LruBitmapPool 是个基于 LRU 策略的 Bitmap 复用池，如果你的应用有频繁创建/销毁 Bitmap 的场景，比如电商 APP，那复用 Bitmap 可以帮你减少 GC 次数，降低垃圾回收开销。
4. 第四步分别初始化了数组池，内存缓存和磁盘缓存
   - ArrayPool，与 BitmapPool 类似，减少小数组的频繁创建和回收，降低 GC 压力，大小由 memorySizeCalculator 决定
   - MemoryCache，缓存已经解码并准备好显示的图片资源，避免重复解码和转换，Glide 性能优化点之一，大小同样由 memorySizeCalculator 决定，性能差就不缓存/少缓存
   - DiskCacheFactory，磁盘缓存，避免重复下载，默认大小是 DEFAULT_DISK_CACHE_SIZE = 250MB，对你没看错，就是 250😂，当然你可以自定义 AppGlideModule 修改缓存大小
5. 初始化核心引擎 Engine
   - 类如其名，能用 Engine 命名的类都不一般
   - 它负责接收请求、管理缓存查找 (MemoryCache 和 DiskCache)、调度 DecodeJob 到不同的线程池、请求去重、解码回调 Request 等等工作
   - Engine 类跟 Glide 类的关系，比较像主力员工和老板的关系
   - 老板 Glide 负责对外开放接口，收集甲方各种各样的需求
   - 回家关起门要 Engine 类去做具体的事情，Engine 负责拆解任务然后去执行，中间还得上报工作进度

## 绑定组件生命周期

然后我们来看 Glide 是怎么利用入参的 Context 绑定组件的生命周期的

Glide#with() 是一个静态函数，并且有不同的重载版本，返回值是一个 RequestManager 对象

![](imgs/glide-with-method.png)

不管传入的 Context 是 Activity 还是 Fragment，最终都转交给 RequestManagerRetriever#get() 方法处理，得到两种类型的 RequestManager ：

1. Application 级别的 RequestManager
   - 传入的是抽象类 context 或者是 view，Glide 会遍历查找当前 view/context 所属的 act，如果 act 为空或者不是 fragmentAct，那么，使用 Application 的生命周期
   - 如果传入的是原生的 android.app.Activity 对象，也会使用  Application 的生命周期，原因未知，有知道的小伙伴可以在评论区留言
   - 另外，如果你是在非 UI 线程调用 Glide#with()，即使你的 context 是 Act 或 frag，也会返回 application 级别的 RequestManager
2. FragmentActivity 和 Fragment
   - 它俩都实现 lifecycle 接口，所以，Glide 的 LifecycleRequestManagerRetriever 类，负责管理和提供这些有 lifecycle 的 RequestManager 实例
   - 如果 lifecycle 通知销毁，LifecycleRequestManagerRetriever 也会清楚对应的 rm 和自己的映射
   - Map<Lifecycle, RequestManager> lifecycleToRequestManager 来保证，每个 Act/frag 对应一个 RequestManager
   - RequestManager 有了 lifecycle ，也就拥有感知生命周期的能力，这是它和 Application 级别的 RequestManager 最大的区别

一旦被认为需要返回 Application 级别的 RequestManager，那么，表示本次请求的生命周期和 Application 的生命周期一样长，而且，请求过程中不会自动暂停、恢复或取消，除非请求完成或者发生错误。

而 FragmentActivity 和 Fragment 的 RequestManager 因为有来自 Lifecycle 的回调，所以能够在 start、stop、destroy 这几个时机自动执行开始/恢复、暂停、取消等操作。

```java
  /**
   * Lifecycle callback that cancels all in progress requests and clears and recycles resources for
   * all completed requests.
   */
  @Override
  public synchronized void onDestroy() {
    targetTracker.onDestroy();
    clearRequests();
    requestTracker.clearRequests();
    lifecycle.removeListener(this);
    lifecycle.removeListener(connectivityMonitor);
    Util.removeCallbacksOnUiThread(addSelfToLifecycle);
    glide.unregisterRequestManager(this);
  }
```

### 1、无 UI Fragment 去哪了？

细心的朋友可能已经发现了，RequestManager 感知生命周期的能力，现在是由 Lifecycle 组件提供，原来的利用无 UI 的 Fragment 感知生命周期变化的那一套已经成为了过去式

![](imgs/glide-support-request-manager-fragment-deprecated.png)

查看 Git 提交记录发现，在2023年的3月10号，无 UI 的 SupportRequestManagerFragment 被标记为启用，并且，配合它执行生命周期调度的 ActivityFragmentLifecycle 类也被删除。

### 2、短暂的内存泄漏风险

最后还有个点需要注意，虽然 FragmentActivity/Fragment 有 Lifecycle 可以自动释放资源，但如果使用不当，还是有内存泄漏的风险

假设这么个场景，你在 Act/frag 中调用 Glide#with() 不小心传错 context 了，本来想传的是 this，结果传入了 application 的上下文，into 的 Target 又引用了自身，如果此时页面被关闭了，那么在 request 结束之前，Act/frag 是无法被正常回收的，会发生一个短暂的内存泄漏

# 二、RequestManager#load

with() 的工作是构建 RequestManager 并为它绑定组件的生命周期

load() 的主要工作是构建 RequestBuilder 对象，组装 Request，告诉 Glide 我要加载什么图片、用什么方式加载

## 设定图片数据源

Glide#load() 接受 9 种不同类型的参数：Bitmap、Drawable、String、Uri、File、Integer、URL、byte[]、Object

不管你传入的是什么，Glide 都只会调用 loadGeneric() 函数，把它们保存到 Object 类型的成员变量 model 中，不校验参数合法性

```java
private Object model; // 数据源

private RequestBuilder<TranscodeType> loadGeneric(@Nullable Object model) {
    this.model = model;
    isModelSet = true;
    return this;
}
```

以 String 举例，你传入的可能个是本地的文件路径，也可能是一个网络地址，又或者穿了个空字符

在组装请求的阶段， Glide 不会检查参数是否合法，具体的校验工作放在了后续的执行阶段里面（通过 ModelLoader 和 ResourceDecoder）

```java
public RequestBuilder<TranscodeType> load(@Nullable String string) {
  return loadGeneric(string);
}
```

如果你的数据源是 byte[] 数组、Bitmap 这种存在于内存中的资源，Glide 会将 DiskCacheStrategy 设置为 NONE，因为它们不需要磁盘缓存

```java
public RequestBuilder<TranscodeType> load(@Nullable Bitmap bitmap) {
    return loadGeneric(bitmap).apply(diskCacheStrategyOf(DiskCacheStrategy.NONE));
}
```

## 组装 Request

除了调用 load() 设定图片的数据源外，RequestBuilder 还提供的其他诸如占位符、预加载等功能，我们来快速的过一遍

### 1、占位符

Glide 目前支持三种占位符

- placeholder(int drawableRes)，开始加载、加载过程中显示的占位图片，一般是一个静态的资源
- error(int drawableRes)，加载失败/错误的时候显示的图片，比如图片数据源错误啊、网络错误、图片格式不对无法解析啊等等，一般也是静态资源
- fallback(int drawableRes)，优先级最高的占位符，如果图片的数据源是空的，比如用户头像的 URL 为空就会显示 fallback 的资源
    - 如果 fallback 被触发，那么，前面两个 placeholder 和 error 都不会显示，因为无法发起加载请求

### 2、预加载

预加载图片是项目中常用的优化用户体验的手段之一，先把图片加载到内存中，轮到这张图片显示的时候可以直接从内存中读取，避免了加载图片的等待时间，我们在多个项目的首页都有使用

preload() 有两个重载版本，一个是需要制定宽高，另一个无参函数则会缓存 SIZE_ORIGINAL 原图尺寸

```java
  public Target<TranscodeType> preload(int width, int height) {
    final PreloadTarget<TranscodeType> target = PreloadTarget.obtain(requestManager, width, height);
    return into(target);
  }

  public Target<TranscodeType> preload() {
    return preload(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
  }
```

不过，预加载的使用条件还是比较 苛刻 的，想要成功命中内存缓存，除了要求一样的链接地址，还必须保证宽高、转换信息都相同，否则，Glide 会认为这是两个不同的请求，不会命中内存缓存

接下来，我们通过一个小栗子来看看，应该如何正确使用预加载

#### 为什么我的预加载不生效？！！

Demo 条件如下图：

![](imgs/glide-preload-test-code.png)


- 第一个红色按钮，负责调用 Glide 无参的 preload() 方法，执行图片的预加载
- 第二个绿色按钮，调用了指定宽高的 preload() 方法，并且选择了 CenterCrop 转换，这是为了和 xml 的 ImageView 配置保持相同
- 最下面的蓝色按钮，调用 load.into 方法，以忽略磁盘缓存，尝试只从内存缓存中的方式加载图片，不会发起网络请求

理论上，如果红/绿的 preload() 方法执行成功，那点击蓝色按钮，应该可以成功显示图片

我们一起来看下结果

![](imgs/glide-preload-test-result.gif)

如图所示

先点击红色按钮执行无参预加载，再点蓝色按钮尝试从内存获取图片，ImageView 显示空白，说明此次未命中内存缓存，获取失败

然后再点击第二个绿色按钮执行 参数匹配的预加载，此时再点击蓝色按钮发现 ImageView 正常显示出头像照片，这说明此次成功命中内存缓存

为什么会出现这样的情况？答案藏在 Glide 的 EngineKey 类里

```java
class Engine {
    public <R> LoadStatus load(Object model, Key signature, int width, int height, Map<Class<?>, Transformation<?>> transformations,Class<?> resourceClass,Class<R> transcodeClass,Options options){
        // 1、根据数据源、宽高、转换信息、构建信息等等生成一个 key
        EngineKey key = keyFactory.buildKey(model, signature, width, height, transformations, resourceClass, transcodeClass, options);
        // 2、再根据 EngineKey 查询内存缓存
        memoryResource = loadFromMemory(key, isMemoryCacheable, startTime);
    }
}

class EngineKey implements Key {

    @Override
    public boolean equals(Object o) {
        if (o instanceof EngineKey) {
            EngineKey other = (EngineKey) o;
            return model.equals(other.model) && signature.equals(other.signature) && height == other.height && width == other.width && transformations.equals(other.transformations) && resourceClass.equals(other.resourceClass) && transcodeClass.equals(other.transcodeClass) && options.equals(other.options);
        }
        return false;
    }
}
```

在 Engine#load() 加载阶段，会先调用 loadFromMemory() 尝试从内存缓存中获取图片，获取成功则直接返回，否则才执行网络请求，重点来了：

- Glide 会根据数据源（我们这里是图片的 URL 链接）、宽高、转换信息等生成一个 EngineKey
- 这个 EngineKey 会作为查询缓存的唯一标识
  - 在 Glide 的内存缓存中，ActiveResources 和 LruResourceCache 内部都是使用 Map 存储的
  - Map 对比键值，调用的是对象的 equals() 方法，如果相同则返回 true
  - 再看 EngineKey 类，它重写了 equals() 方法，对比的就是 model、signature、width、height、transformations、resourceClass、transcodeClass、options 这些东西

所以，如果你在 preload() 设置的 model、width、height、transformations 等参数，和最终 load().into() 的时候不一样，那在 Glide 看来你这属于两个不同的请求，也就不会命中缓存了

我们来 debug 验证一下

![](imgs/glide-preload-test-debug-step1.png)

第一次使用无参 preload()，宽高是 Integer.MIN_VALUE 表示最大，按照原图尺寸加载，然后转换信息是空的

![](imgs/glide-preload-test-debug-step2.png)

预加载完成，第一次尝试读取缓存到 ImageView，获取缓存失败

这一步就能看出问题来了，本次请求的宽高是 ImageView 宽高的 px 值，转换列表中有 4 个元素，显然和预加载的配置是不一样的

![](imgs/glide-preload-test-debug-step3.png)

接着我们使用和 xml 中 ImageView 配置相同的参数，再来一次预加载请求，这次调用的是 preload(width, height) 方法

![](imgs/glide-preload-test-debug-step4.png)

第二次尝试从读取内存缓存中读取图片，这次成功命中缓存，ImageView 显示正常

本次 Demo 代码我放在 Github 仓库了，感兴趣的朋友可以点击链接查看：https://github.com/yibaoshan/yibaoshan/blob/master/application-android-ui-card-slide-sample/src/main/java/cn/ybs/card/slide/GlidePreloadTestActivity.kt

做个小结

如果我们想要在项目中使用 预加载 来提升用户体验，一定要注意 preload 的参数设置，尤其是 width、height、transformations 这三项，要和目标 ImageView 的配置保持一致

参数不一致是无法命中缓存的，只会白白浪费了 CPU、内存和流量资源。

### 3、裁剪和转换

继续来看 RequestBuilder 的其他功能

Glide 支持图片裁剪和转换功能，它提供了像圆形裁剪 CircleCrop()、高斯模糊 BlurTransformation、灰度处理 GrayscaleTransformation 等内置的转换，另外还提供了自定义转换 transform() 和制定尺寸 override() 的功能

像一般的 centerCrop、fitCenter 啥的无需单独设置，Glide 会自动读取 ImageView 的 ScaleType 信息，然后解析成对应的缩放效果保存到 requestOptions 对象

```java
class RequestBuilder {
    public ViewTarget into(ImageView view) {
        ...
        switch (view.getScaleType()) {
            case CENTER_CROP:
                requestOptions = requestOptions.clone().optionalCenterCrop();
                break;
            case CENTER_INSIDE:
                requestOptions = requestOptions.clone().optionalCenterInside();
                break;
            case FIT_CENTER:
            case FIT_START:
            case FIT_END:
                requestOptions = requestOptions.clone().optionalFitCenter();
                break;
            case FIT_XY:
                requestOptions = requestOptions.clone().optionalCenterInside();
                break;
            case CENTER:
            case MATRIX:
            default: // Do nothing.
        }
        ...
    }
}
```

### 4、缓存策略

Glide 提供了两种缓存策略，一是内存缓存，二是磁盘缓存，如果你不想使用缓存，或者只想使用缓存，可以使用下面的 API 进行自定义配置

- skipMemoryCache，默认 false，设为 true 的话，Glide 不会从内存缓存中加载图片，同时也不会将图片存入内存缓存
- diskCacheStrategy，指定磁盘的缓存策略，现在默认是 AUTOMATIC 智能模式，另外四个选项是：
  - ALL，缓存原图和转换后的图片、NONE，禁用磁盘缓存、DATA，只缓存原图、SOURCE，只缓存转换后的图片
- onlyRetrieveFromCache，只从缓存中加载图片，不发起网络请求，缓存范围包括内存缓存和磁盘缓存，默认 false

### 5、...

RequestBuilder 还支持设置监听、过渡动画、制定解码为 Bitmap、Gif、File 等功能，我这里就不一一展开了

更多关于 RequestBuilder 的信息可点击链接查看：https://github.com/bumptech/glide/blob/v5.0.0-rc01/library/src/main/java/com/bumptech/glide/RequestBuilder.java

# 三、RequestBuilder#into

into() 是 Glide 链式调用的终点，也是真正触发图片加载和显示的关键一步