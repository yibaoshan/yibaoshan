
- 掌握各个hook点，插件化，热修复，换肤
- Bitmap 的加载和 Cache

## 系统组件

### Activity

- Activity 创建、启动、主被动销毁和恢复流程、启动模式及其生命周期变化
- Activity 和 Window / View 的关系，View 可见性检测等
- 子类 ComponentActivity 继承的几个接口功能，LifecycleOwner、ViewModelStoreOwner、ActivityResultCaller 等等
- 子类 FragmentActivity 中的 FragmentController ，管理 Fragment 以及同步生命周期
- 子类 AppCompatActivity

### Window / View

- View / ViewGroup 的绘制、渲染、事件分发流程
- policy 包下的 DecorContext、DecorView、PhoneWindow、PhoneLayoutInflater 等
- view 包下的 ContextThemeWrapper、ViewRootImpl
- View.post() 和 handler 的区别？

### 存储

- SharedPreferences
- MMKV

## 虚拟机

- 内存分配
- 垃圾回收器-守护线程
