
配合 UI 组件，比如 act、frag 的使用，生命周期比 act、frag 更长，适合处理配置变化时的数据保存，MVVM/MVI UI 架构的核心成员之一

View = ViewBinding 或 finViewById 的 MVVM

- Model 是 Java bean + 数据的逻辑处理的数据类功能，在 ViewModel 中通过 Repository 层（Room、Retrofit、MMKV）获取到数据源，返回给 View 展示
- ViewModel 是组装厂，负责发起请求、处理、丢给 LiveData
- View 是 act、frag 中的 ViewBinding 对象，或者是 findViewById 获取的一个个 View 对象，观察 VM 的 liveData，完成数据绑定，以及负责处理用户的交互，调用 VM 

View = DataBinding 时的 MVVM

- 因为 xml 可以直接处理简单的数据，完成数据绑定，此时的 View 层被一分为二，xml 对象和 viewBinding 对象
  - xml 中一般也会持有两个对象
    - xml 持有 vm 时，控件的属性值（比如 text、src 等）直接绑定到 liveData 对象，vm 中修改 liveData 值自动更新 View
    - xml 持有 act、frag，绑定行为操作，比如点击事件、长按事件等
  - viewBinding 通常在 act、frag 中使用，通过它拿到控件，完成复杂的事件监听、UI 交互，比如滑动监听、frag 管理等
- ViewModel 和 Model 的职责保持不变

View = Compose 时的 MVVM

- ViewModel 和 Model 的职责还是不变
- View 变成了 Compose UI 组件，依旧持有 ViewModel 完成逻辑处理和数据绑定

# 基本使用

首先创建一个类，继承自 ViewModel

在 act 或 frag 中，通过 ViewModelProvider 或 viewModels 委托获取实例，直接实例化会导致 vm 的生命周期管理失效

如果需要向 vm 传递参数，可以使用 ViewModelProvider.Factory，当然，现在基本都是 @HiltViewModel，不需要手动写 Factory

在同一 Activity 下的多个 Fragment 可共享同一个 ViewModel，委托方式，activityViewModels 创建的 ViewModel 是同一个实例

# 基本原理

vm 的保存与恢复

- androidx.activity.ComponentActivity 和 androidx.fragment.app.Fragment 中持有 ViewModelStore 对象 
- ViewModelStore 是对 HashMap<String key, ViewModel> 的封装，对外提供 put、get 和 clean 方法
- 配置发生变化时，onRetainNonConfigurationInstance() 保存 ViewModelStore
- 重建后，getLastNonConfigurationInstance() 恢复 ViewModelStore

ViewModelProvider 的创建

- 从 ViewModelStoreOwner（如 Activity）获取 ViewModelStore。 
- 每个 ViewModel 都有有个唯一的 key，这也是能保证 ViewModel 实例唯一的原因
- 默认反射创建 ViewModel 实例，也可自定义 Factory

# 注意事项

- 屏幕旋转后 LiveData 重复触发	
- 未实现自定义 Factory 导致 ViewModel 初始化报错 No default constructor	
- 异步任务配合 viewModelScope，禁止使用 GlobalScope，因为 viewModelScope 本质是 CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)，绑定到 onCleared() 自动取消。
- vm 禁止持有 Context 或任何 UI 组件