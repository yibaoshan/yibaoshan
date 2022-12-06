
## Linux

## Android

### 系统组件

#### Activity

- Activity 创建、启动、低内存销毁、恢复流程、主/被动退出与销毁的生命周期变化；
- Activity 和 Window / View 的关系，View 可见性检测等
- 子类 ComponentActivity 继承的几个接口功能，LifecycleOwner、ViewModelStoreOwner、ActivityResultCaller 等等
- 子类 FragmentActivity 中的 FragmentController ，管理 Fragment 以及同步生命周期
- 子类 AppCompatActivity

#### Window / View

- View / ViewGroup 的绘制、渲染、事件分发流程
- policy 包下的 DecorContext、DecorView、PhoneWindow、PhoneLayoutInflater 等
- view 包下的 ContextThemeWrapper、ViewRootImpl

#### Context

- Context 的创建、传输与装饰者模式
- ContextWrapper 包装类和实现类 ContextImpl

## Java