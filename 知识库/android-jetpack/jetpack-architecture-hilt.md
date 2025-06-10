
聊聊 Hilt：业务架构如何更优雅的解耦

hilt 是 jetpack 团队为 Android 应用提供的一个 依赖注入（DI）框架，内置了对组件（如 Activity、Fragment、ViewModel 等）的依赖注入支持，属于业务架构框架之一

依赖注入（Dependency Injection）是面向对象设计的 解耦 思想，最早应用于 Java 服务端，比如大名鼎鼎的 string 框架，它的核心思想是：

- 把某个类所 “**依赖**” 的对象，不由它自己创建，而是由外部提供并 “**注入**” 给它。

# 一、什么是 DI

如果你是第一次接触依赖注入的框架，上面关于 hilt/依赖注入的介绍可能不是很好理解，接下来我将通过一个 🌰 来尝试解释一下下：什么是依赖注入 DI？

> Android 官网有对 DI 的介绍，我这里偷懒直接搬运过来了😁

类通常需要引用其他的类，才能完成自己的工作，比如，小车车 Car 类需要引用引擎 Engine 类，这些个必需的类称为 依赖项。

```kotlin
class Car {

    private val engine = Engine()

    fun start() {
        engine.start()
    }
}

fun main() {
    val car = Car()
    car.start()
}
```

在下面的示例中，Car 类必须拥有 Engine 类的一个实例才能运行。Car 可通过以下三种方式获取所需的 Engine 对象：

1. 自己 new 一个出来
2. 从其他地方获取，比如从父类或全局静态，静态单例就属于这
3. 以构造参数形式，要求外部提供

第三种方式就是依赖项注入，使用这种方法，你就不需要自己 new 出来或者从其他地方获取实例，并且，虽然你写在了构造函数中要求使用方提供实例，但在实际使用中，使用方也不需要传入实例对象，DI 框架会自动完成这个过程。

```kotlin
class Car @Inject constructor (val engine: Engine){ // @Inject 注解，表示这个构造函数需要依赖注入

    fun start() {
        engine.start()
    }
}
```

或者，也可以将 @Inject 注解用到公开的成员属性上

```kotlin
class Car{

    // 表示这个成员属性需要注入
    @Inject
    lateinit var engine: Engine

    fun start() {
        engine.start()
    }
}
```

## Clean Arch

将自己 new 对象，改为用构造函数传参，或者公开成员属性由外部传参，依赖注入的基础功能就是这么简单，这看似简单的一小步，却能帮助业务架构实现 更优雅的 “解耦”

举例说明

我们很早就把项目架构往 Google 推荐的 Clean Architecture 上去靠，现在各个组件的依赖关系大概是这样的：

```text
UI（Activity/Fragment/Compose） → ViewModel → UseCase → Repository（接口） ← Impl → ApiService
```

- clean arch 每一层的职责边界非常的清晰
  - UI 就是用来展示数据和处理交互的，ViewModel 用来组织页面的业务逻辑（提供数据/响应 UI 层事件等）
  - UseCase 负责一个特定的业务功能（登录注册/音视频通话啥的），Repository/Impl 负责抽象数据源以及和数据源交互（依赖倒置）
  - 最后的 ApiService 负责提供数据源，可以是 Database，也可以是 network，总之，它们依赖 Retrofit、OkHttp、Room 等库来完成数据访问工作
- 在 clean 的依赖关系中，有个很重要的原则：内层不依赖外层，最外层只需要干好自己的事儿就行了（单一职责），内层的行为和它无关
- 这种依赖关系，就非常适合使用 DI 框架，内层所需要的实例对象由 DI 框架自动完成注入，最起码，可以减少我们写模板代码的时间，不用你一个个去 new 了。

总之

如果你的业务分层不是很清晰，DI 框架的确只能帮你解决不用手动 new 对象的问题

如果业务分层比较清晰，类似官网的 clean arch，从业务架构角度来看，其实已经完成了解耦，那 DI 框架在这里主要起到了 “粘合剂” 的作用，帮助业务架构更优雅的解耦

# 二、基本使用

接下来我将分 2 部分快速介绍 hilt 的基本使用

1. 依赖方（消费者），怎么样才能获得自己想要的对象？
2. 生产者，怎么对外提供自己的服务？

忽略引入 gradle 依赖步骤，我们以一个典型的 clean arch 项目举例

## 消费者（Consumer） 

### 1、HiltAndroidApp

首先需要在 Application 上增加 @HiltAndroidApp 注解

```java
@HiltAndroidApp
public class xxApplication extends Application { ... }
```

这一步告诉 hilt，这个类是 Android 应用的入口，在编译期间 hilt 会自动扫描这个类，并生成一个 Hilt_Application 类，启用启动后去启动依赖注入容器

简单来说，@HiltAndroidApp 是个开关，不加 @HiltAndroidApp，其他所有的 hilt 注解都不会起作用

### 2、AndroidEntryPoint

从名称就可以看出，这是个入口类注解，是告诉编译器，我这个类里面有用到 @Inject 注解的成员变量，你需要帮我完成注入哦

@AndroidEntryPoint 注解目前支持的入口类有： Activity、Fragment、Service、BroadcastReceiver 和 View

```kotlin
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject // Hilt 会注入这个UserRepository实例
    lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 现在你可以直接使用 userRepository 了
        userRepository.fetchUserData()
    }
}
```

为啥需要单独拎出来一个 AndroidEntryPoint 注解呢？

因为 activity、server、广播的实例对象是由 ams 通知 activityThread 创建管理的，我们无法自行 new 一个对象出来，那么，hilt 也就无法为我们完成里面成员属性的注入工作了

以 activity 举例，加入了 AndroidEntryPoint 注解以后，在编译期会生成 hilt _MainActivity 类，在新类的 onCreate 方法中，会调用 generatedComponent()#inject() 完成注入，并把  userRepository 对象注入进去

简单来说，在无法手动创建对象的 Android 组件中，你需要显示的告诉 hilt，我有成员属性需要被注入

### 3、HiltViewModel

@HiltViewModel 是专门为 ViewModel 提供依赖注入的注解

不管是 act 还是 frag，带参数的 vm 如果希望被正常创建，需要手动实现 ViewModelProvider.Factor，@HiltViewModel 注解就是告诉 hilt，vm 的构造函数需要依赖注入，你来帮我完成

```kotlin
@HiltViewModel
class MySavedStateViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle // Hilt 会自动注入 savedStateHandle 对象
) : ViewModel() {
    val userId = savedStateHandle.get<String>("userId")
}
```

act 使用层面上

```kotlin
@AndroidEntryPoint // 1. Activity 必须被 @AndroidEntryPoint 注解
class xxActivity : AppCompatActivity() {

  // 2. 使用 by viewModels() 委托获取 ViewModel 实例
  private val viewModel: MySavedStateViewModel by viewModels()
}
```

### 4、Inject

@Inject 注解使用起来比较简单，它可以作用在类成员变量上（要求公开），可以作用在构造函数上，也可以作用在公开方法上，目的是告诉 hilt ：我需要这个依赖这个对象

## 生产者（Provider）

### 1、Module

被 @Module 修饰的类，通常是作为多个服务方的集合，同时，Module 一般配合 @InstallIn 注解使用

```kotlin
@Module // 标记这是一个 Hilt 模块
@InstallIn(SingletonComponent::class) // 告诉 Hilt 这个 module 里面的服务方的生命周期。
object NetworkModule { // 通常使用 object (Kotlin) 或 companion object + @JvmStatic 来创建单例模块
    // 里面会包含 @Provides 或 @Binds 方法
}
```

Module 使用上比较自由，没啥要求，你可以按照业务模块来组织 module，也可以按照功能模块来组织 module ，比如 DataModule、OSSModule 等

@InstallIn 注解用来表示不同的作用域，我一般只用 @SingletonComponent ，其他没怎么用过，这里忽略

### 2、Provides/Binds

@Provides 和 @Binds 都是告诉 hilt，这个方法会返回一个对象实例，这个对象实例会作为 @Inject 注入的成员变量或者构造函数参数

它俩的相同点是都只能存在于 @Module 修饰的类里面，不同的是：

- binds 只能存在于抽象的 @Module 类，并且修饰的方法也必须是抽象的
- binds 返回类型必须是接口类型，并且修饰的方法必须只有一个参数，这个参数就是接口的具体实现类。

@Binds 注解的要求听起来比较抽象，但它是完成 SOLID 原则中的 DIP 依赖倒置原则的必要条件

- 不要在类内部 new 出它依赖的类（这是依赖具体实现）。
- 应该依赖于接口或抽象类，由外部 “注入” 具体实现。
- 这样可以在不修改类的情况下，无缝不同的依赖实现，例如图片框架、权限处理、日志库、埋点库等等

我们一般用在 clean arch 的 Repository 层

- 创建一个 @Binds 注解修饰的函数，要求传入 Repository 的实现类 RepositoryImpl
- 对外提供 Repository 的接口，即返回值是 Repository
- 如果需要切换实现，替换实现类即可

做个小结，@Provides 和 @Binds 的作用是告诉 hilt：用 @Inject 注解标注的对象，可以在我这被创建

如果项目中使用了 @Inject 作用在某个成员变量上，但这个对象的创建没有使用 @Provides 和 @Binds 提供给 hilt，编译期间 hilt 会报错

# 三、基本原理

hilt 是基于 Dagger2 实现，Dagger2 我也没用过，基本原理肯定是注解处理器在编译期生成代码，Java 肯定是 APT，kotlin 不清楚是 KAPT 还是已经升级到了 KSP

小结下使用流程

```
@HiltAndroidApp Application
        ↓
@AndroidEntryPoint Activity/Fragment/ViewModel
        ↓
@Inject 注入依赖
        ↓
@Module + @Provides/@Binds 提供依赖
        ↓
@InstallIn 定义作用域（如 SingletonComponent）
```

整个编译过程大概是这样的：

1. Hilt（实际上是底层的 Dagger）的注解处理器扫描项目代码，寻找所有的 Hilt 相关的注解，HiltViewModel、Module、Provides 啥的
2. 根据收集到的注解，构建出一个依赖图
   - 比如要创建 xxViewModel，需要 xxxUseCase
   - 要创建 xxxUseCase，又需要 xxxRepository
   - 创建 xxxRepository，又需要 xxApiService
   - 以上，hilt 会验证这个图是否完整，即所有 @Inject 的依赖是否都有对应的提供方式，如果缺少，编译就会报错
3. 开始生成代码
   - 为 AndroidEntryPoint 创建  hilt_xxActivity 类，为 HiltViewModel 创建 ViewModelProvider.Factory 类等

# 四、常见问题

- ViewModel 注入失败，老熟人了，改为 by viewModels() 或者 by activityViewModels()
- 工程中已有的单例有没有必要改成 DI 实现？我个人不建议，尤其是 core 层的，因为你不知道哪里的 aar 会用到导致 class not found

# 五、参考资料

- [Android - Hilt 官方文档](https://developer.android.com/training/dependency-injection/hilt-android)
- [Android 中的依赖项注入 官方文档](https://developer.android.com/training/dependency-injection)
