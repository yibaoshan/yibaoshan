
因为 fragment 不是由 ams 直接调度，不参与 binder 跨进程通信，仅有 activity 进行管理

所以，我个人其实一直把 fragment 看做是一个自带生命周期的特殊的 view 容器，它必须依赖 Activity 或者其他 fragment 才能显示，叠个甲，这是我个人看法。

首先来看 fragment 坎坷的一生

| 版本                   | Fragment 的状态                                                                              |
| -------------------- | ----------------------------------------------------------------------------------------- |
| Android 3.0（API 11）  | Fragment 首次引入，内置在 AOSP 的 `android.app.Fragment` 中                                         |
| Android 4.x \~ 8.x   | 系统内置 `android.app.Fragment`，Google 同时在 support 包中维护兼容版本 `android.support.v4.app.Fragment` |
| Android 9（API 28）    | Google 宣布迁移到 Jetpack 的 `androidx.fragment.app.Fragment`                                   |
| Android 10+（API 29+） | 官方弃用 `android.app.Fragment`，**Jetpack Fragment 完全独立于 AOSP**，成为 AndroidX 模块维护的一部分          |

- fragment 早期包含在 AOSP 中维护，是 AOSP 的内置类
- 9.0 以后基本算被移除了，目前 fragment 由 androidx 维护，是 jetpack 成员之一

原因我猜，可能是 androidx 可以更快的为 frag 支持新功能吧，androidx 的维护和更新次数更频繁

# Fragment 功能

fragment 具有生命周期的、可嵌套、可复用、支持状态保存的 view 容器类，几个核心功能：

| 功能类别                         | Fragment 提供的能力                                                                        |
| ---------------------------- | ------------------------------------------------------------------------------------- |
| 生命周期                         | 自带完整生命周期：从 `onAttach` 到 `onDetach`，包括 `onCreateView`、`onDestroyView`                  |
| 视图控制                         | `onCreateView()` 创建并绑定 UI，`getView()` 获取视图引用                                          |
| 与宿主交互                        | 可访问 `getActivity()`、`requireContext()`、`getParentFragment()`                          |
| 状态管理                         | 提供 `Bundle` 存储参数（`setArguments()` / `getArguments()`）和 SavedState 支持                  |
| Fragment 嵌套                  | 支持子 Fragment (`getChildFragmentManager()`)                                            |
| 动画与转场                        | 支持 `setEnterTransition()`、`setExitTransition()`、`setSharedElementEnterTransition()` 等 |
| Fragment 回退栈支持               | 通过 `FragmentTransaction` 实现添加、替换、删除并支持 `addToBackStack()`                             |

另外，androidx.fragment.app.Fragment 还实现了好多个接口，介绍几个常用的：

- ComponentCallbacks，响应 onConfigurationChanged 配置变化和 onLowMemory 低内存
- LifecycleOwner，响应生命周期能力，配合/管理 lifecycle 组件
- ViewModelStoreOwner、HasDefaultViewModelProviderFactory，创建、保存/管理 vm
- ActivityResultCaller，支持 jetpack 组件 ActivityResult

Fragment 的简单使用

1. xml 中使用 fragment 标签，静态添加，很少用，除非一些原来是 fragment 改为 activity 的页面
2. FrameLayout + FragmentManager，使用 FragmentTransaction 在容器中添加、替换、隐藏 Fragment，首页 Tab 切换经常用
3. ViewPager/ViewPager2，使用 FragmentPagerAdapter / FragmentStateAdapter 实现页面滑动，项目基本上绝大多数分类、滑动页面都是这种方案

# Fragment 生命周期

大概的生命周期如下，不同的使用方式，生命周期的顺序也会不同

| 生命周期方法            | 是否常重写  | 作用                                                                                |
| ----------------- | ------ |-----------------------------------------------------------------------------------|
| `onAttach()`      | ✅ 可选   | Fragment 绑定到宿主（Activity 或其他 Fragment），如果有接口通信，一般在这儿把 `context` 强转为回调接口，并且这时候可以调用 requireContext()、requireActivity() |
| `onCreate()`      | ✅ 可选   | 做初始化操作，比如读取 Bundle 参数、初始化成员变量，注意，此时的 view 还未创建。                                   |
| `onCreateView()`  | ✅ 必重写  | 创建并返回 Fragment 的 UI 视图（常用 `LayoutInflater.inflate()` 加载布局）。                       |
| `onViewCreated()` | ✅ 高频使用 | View 创建完毕后回调，适合做 View 绑定、UI 初始化、设置监听器等。                                           |
| `onStart()`       | ❌ 可选   | Fragment 可见但未获取焦点，可用于轻量数据刷新。                                                      |
| `onResume()`      | ✅ 常用   | Fragment 可见且获得焦点，适合开始动画、请求网络、开启监听。                                                |
| `onPause()`       | ✅ 常用   | Fragment 失去焦点但仍可见，适合释放动画、暂停请求。                                                    |
| `onStop()`        | ✅ 常用   | Fragment 不可见了，适合释放资源、停止耗时任务。                                                      |
| `onDestroyView()` | ✅ 高频使用 | Fragment 的 View 被销毁，适合释放 View 相关资源，避免内存泄漏（如 binding = null）。                      |
| `onDestroy()`     | ✅ 常用   | Fragment 完全销毁，释放所有资源（如取消协程、关闭线程）。                                                 |
| `onDetach()`      | ✅ 可选   | Fragment 与宿主解绑，释放与 Context 的引用。                                                   |

小 tips

- 正常情况下 onCreateView() 必须重写并返回一个 view，它没有 setContentView 函数，因为 fragment 不是窗口的拥有者，，它只是宿主的一个组件，由宿主通过 FragmentManager 控制它的 view 的显示

## FrameLayout

```kotlin
supportFragmentManager.beginTransaction()
    .replace(R.id.fragment_container, Fragment())
    .addToBackStack(null) // 可选：加入返回栈
    .commit()
```

beginTransaction 可选 api 

- add，添加 Fragment 到容器中，会触发 onAttach → onCreate → onCreateView → onViewCreated
- replace，先 remove() 当前 Fragment，再 add() 新 Fragment，被替换的 Fragment 执行 onDestroyView → onDetach（除非加栈）
- remove，从容器中移除 Fragment，onDestroyView → onDestroy → onDetach
- hide/show，隐藏或显示 Fragment，但不销毁	不触发生命周期，仅 onHiddenChanged()
- addToBackStack，把这次事务加入返回栈，用户可以通过返回键回退上一个 Fragment

## ViewPager/ViewPager2

ViewPager 需要 adapter 适配器，用来创建/管理 Fragment 实例对象，常用的 adapter 适配器有

- ViewPager#FragmentPagerAdapter，不销毁 Fragment 实例，只销毁视图，占内存多，适合页面少的场景
- ViewPager#FragmentStatePagerAdapter，页面切换回销毁 Fragment 实例，内存友好，适合页面多的场景
- ViewPager2#FragmentStateAdapter，类似 ViewPager#FragmentStatePagerAdapter，会销毁 Fragment 

| Fragment A（旧页） | Fragment B（新页）           |
| -------------- |--------------------------|
| `onPause()`    | `onAttach()`（first only） |
| `onStop()`     | `onCreate()`（first only） |
|                | `onCreateView()`         |
|                | `onViewCreated()`        |
|                | `onStart()`              |
|                | `onResume()`             |

这里有几点需要注意

1. 如果 A 被缓存，它不会 onDestroyView()，除非缓存数不足。
2. ViewPager 默认缓存左右各 1 个页面，可通过 setOffscreenPageLimit() 控制。
3. ViewPager2 也可以设置，它本质上使用的是 RecyclerView 的缓存机制（RecyclerView.RecycledViewPool）

ViewPager2 内部用的是 RecyclerView 实现，所以，frag 的创建流程和普通的 VH 差不太多

- 当用户滑动页面，RecyclerView 创建新 ViewHolder
- FragmentStateAdapter 创建新的 Fragment 
- Fragment 生命周期由 FragmentManager 调用 dispatchCreate()、dispatchStart() 等管理 
- Fragment 对应的 View 被添加到 FrameLayout 容器中（ViewHolder）

举个例子，在首次创建新 VH 的时候

```kotlin
override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val fragment = createFragment(position)
    fragmentManager.beginTransaction()
        .add(holder.container.id, fragment)
        .commitNow() // 立刻执行
}
```

# Fragment Manager

上面介绍使用方法的时候多次用到了 FM，它是管理操作 Fragment 的控制器，里面包含一系列的事务操作，通过 beginTransaction 对外提供

```kotlin
val transaction = supportFragmentManager.beginTransaction()
transaction.add(R.id.container, Fragment())             // 添加一个 Fragment 到容器
transaction.replace(R.id.container, AnotherFragment())  // 替换容器中 Fragment（内部是 remove + add）
transaction.remove(oldFragment)                         // 移除 Fragment，但实例对象仍可能存在
transaction.hide(fragment)                              // 隐藏/显示 Fragment，不会触发生命周期
transaction.show(fragment)
transaction.addToBackStack(null)                        // 加入到返回栈，按返回键可以回退事务
transaction.commit()/commitNow()                        // 提交事务，前者异步，后者同步立即执行
transaction.commitAllowingStateLoss()                   // 提交允许状态丢失，比如在 onSaveInstanceState 之后调用该函数
```

解释一下为什么 commit 怎么个异步法

1. beginTransaction() 只是创建一个 BackStackRecord 实例，表示一组待执行的操作 
2. add/replace/remove 等只会记录操作，不会立即执行，异步行为是为了合并多笔事务
3. commit() 把操作记录加入 FragmentManager.mPendingActions 队列，等到主线程空闲时（Handler -> Looper），FragmentManager 调用 executeOps() 真正执行这些操作

其他常用 API

- findFragmentById() / findFragmentByTag()	根据 ID / tag 查找 Fragment
- getFragments()，获取当前所有的 Fragment 列表，包括隐藏的
- popBackStack()，弹出回退栈
- registerFragmentLifecycleCallbacks()，类似 act 的监控，可以监听各个 Fragment 的生命周期变化

# 注意事项

- 禁止 fragment 使用带参构造函数，否则会出现 IllegalArgumentException，加入 lint 规则，因为这点小疏忽恢复 frag 导致崩溃，崩溃率上升就不好玩了
  - 传参使用 setArguments(Bundle)
- 通信方面
  - 同一个 act，共享 vm，不同 act，eventbus
  - frag call act，定义接口，act 实现接口，frag 在 onAttach 中把 context 强转为接口
  - act call frag，frag 公开方法
- commit 和 commitnow 都不允许在 onSaveInstanceState 之后调用，所以，所有涉及到 fragment 的操作，都必须检查宿主状态