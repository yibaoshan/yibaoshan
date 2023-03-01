
## 基本使用

- 创建 RecyclerView ：xml 创建，或者 new 对象。注意依赖 androidx
- 设置布局管理器：
  - 线性布局（LinearLayoutManager）：横向或者纵向显示
  - 网格布局（GridLayoutManager ）：一排 2 个，3 个等
  - 交错布局（StaggeredGridLayoutManager）：在上一个基础上，允许子 View 高度不一
- 设置 Adapter 适配器：
  - 自定义 ViewHolder 类，对应 xml 文件
  - onCreateViewHolder() ，创建 ViewHolder ，可根据自定义 ItemType 实现多布局
  - onBindViewHolder()，绑定数据
- 设置数据，notifyDataSetChanged()，刷新视图

## class LayoutManager

RecyclerView 的 onLayout() 委托给 LayoutManager 完成，因此

- LayoutManager 负责管理各个子 View 的布局显示、位置
- 当触摸事件发生，负责反复 layout 子视图，更新位置

自定义 LayoutManager 可以自由的定制视图展示方式，比如无限循环、滑动变大变小、路径变化啊等等，但我不会。。先不管

## class Adapter

管理 ViewHolder、数据和视图，设置点击回调等也都在这，是我们开发者打交道最多的地方

万能 Adapter：由于我们项目使用了 DataBinding ，万能 Adapter 部分用的是 binding-collection-adapter Pub 三方库的方案

- onAttachedToRecyclerView(RV)：当 Adapter 被添加到 RV 时
- onDetachedFromRecyclerView(RV)：当另一个 Adapter 被添加，旧的会收到该回调
- onViewAttachedToWindow(VH)：当 View 被添加到窗口，注意不是 added()
- onViewDetachedFromWindow(VH)：同上，注意不是 remove()

解释 onViewAttachedToWindow() 中的 Attached ：

- View 自身有 added 状态(调用 addView())和 removed(调用 removeView())
- RV 为 View 增加了 attached 和 detached 两种状态，分别表示该视图正在使用、已废弃
- 对应 ViewHolder 的，scrap（视图已废弃） 和 recycle（视图已回收）两种状态

## class Recycler

以 ViewHolder 为单位，为 RV 实现缓存回收机制的内部类

- mAttachedScrap（ArrayList/<ViewHolder/>）：
  - 临时缓存，最终被添加回屏幕。比如有 3 个item，删掉中间的，1和3会被临时保存到这。
  - 再或者，自然滑动的时候（其实就是在反复 layout 布局），会把所有的 item 都临时保存到这，计算好了每个子 View 的新位置后，会一个个取出来，当然了不一定是全部都会取出来的，因为有些已经滑出屏幕了
- mChangedScrap（ArrayList/<ViewHolder/>）：临时缓存，比如中间item被移除了，那它先添加到这，播放完动画丢进 pool。过渡用的
- mCachedViews（ArrayList/<ViewHolder/>）：存储刚被移出屏幕外的 View ，比如没有从 mAttachedScrap 取出来的就会被丢到这，默认上限为2 。无需重新 bind
- mViewCacheExtension(ViewCacheExtension)：不用管
- mRecyclerPool(RecycledViewPool)：缓存Item的最终站，用于保存那些Removed、Changed、以及mCachedViews满了之后更旧的Item，需要 bind

这块稍微有点绕，先有个基础概念，后面在深入

ViewHolder 状态：

- Inserted：如果刚好插入在屏幕可见范围内，会从RecycledViewPool中找一个相同类型的ViewHolder（找不到就create）来重新绑定数据并layout；
- Removed：会把对应ViewHolder扔到mAttachedScrap中并播放动画，动画播放完毕后移到RecycledViewPool里；
- Changed：先把旧的ViewHolder扔mChangedScrap中，然后像Inserted那样从RecycledViewPool中找一个相同类型的ViewHolder来重新绑定数据。旧ViewHolder对象用来播放动画，动画播完，同样会移到RecycledViewPool里；

滑动列表时查找顺序：

## 下拉刷新，上拉加载

GitHub 开源控件：SmartRefreshLayout

Google 控件：SwipeRefreshLayout

- 自定义 LinearLayout，支持 header，content，footer，三个布局
- 根据实现效果，使用 margin，或者 padding ，让 header、footer 超出屏幕之外
- 重写 onInterceptTouchEvent()，接管事件，判断 RV 到底或到顶，改变 RV 位置让它露出来
- 关闭下拉刷新，上拉加载窗口可使用属性动画

## 商品曝光

我们的需求，当前商品完成的在列表显示出来，2s 以上视为一次曝光

- 在 onScrolled() 滚动时，和 onScrollStateChanged() 停止滚动时（避免最后一次曝光的商品无法统计）执行可见性检测
- 根据 LayoutManager 不同，计算屏幕中显示的 item
- 把每个正在显示的 item 丢到延迟任务中（利用 map 去重）
  - 若 item 在新一轮的可见检测中被移除，删除对应的延迟任务
  - 若 item 延迟任务触发，该商品已展示了 2s ，根据可见范围拿到 Adapter 保存的数据，上报到神策统计，同时更改 flag ，防止重复上报
- 异常监听，发生锁屏、Activity 切换等事件取消上报任务

## vLayout

我们项目使用了阿里的 vLayout 完成电商首页展示，它的基本实现原理：

- 引入了 LayoutHelper 负责具体的布局逻辑
- 自定义 VirtualLayoutManager 继承自 LinearLayoutManager。它管理着一系列的 LayoutHelper
- 框架内置提供了几种常用的布局类型，包括：网格布局、线性布局、瀑布流布局、悬浮布局、吸边布局等
- 支持扩展外部，注册新的LayoutHelper，实现特殊的布局方式

## Q&A

- setHasStableIds 用处
  - 缓存相关，为 true 时，不走存入 mRecyclerPool 再取出的逻辑。因此，RV 所有 item 状态得以保存，比如焦点呐，图片呐（这也是 Glide 不会闪动的解决方案），同时也不会触发动画
  - 需要设置唯一 id
- View的onAttachedToWindow ,onDetachedFromWindow 在 RV 的对应关系
