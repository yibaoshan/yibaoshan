
按官网对 rv 的介绍，recyclerview 是一种能够在有限区域内，展示大量数据的视图，它由三个部分组成

1. Adapter，负责数据的管理，以及完成数据和视图（ViewHolder）的绑定
2. LayoutManager，负责排兵布阵，摆放 item 的位置，并且响应/处理 rv 水平、垂直方向的滚动
    - rv 本质是个自定义 ViewGroup，需要经历测量、布局、绘制三部曲
    - rv 的 onLayout 布局的部分，实际是委托给 LayoutManager 完成的
3. Recycler，rv 的缓存实现，判定 vh 是否要回收，然后管理回收、复用
   - Recycler 类内部有 5 个和缓存相关的成员变量
     - ArrayList<ViewHolder> mAttachedScrap
       - 这个集合的作用会让人比较困惑，因为它的大小是变化的
       - 如果屏幕静止，什么都没发生，那它的大小就是 0
       - 如果用户开始滑动屏幕，需要重新 layout 布局，那调用 scrapView() 把当前屏幕上正在显示的 vh 保存在 mAttachedScrap 中，等待复用（ps：也有可能因为不能被回收丢到 mChangedScrap 里，暂不考虑这种情况，大概率是丢到 attached 里面）
       - 一次 layout 完成以后，或者停止滚动以后，mAttachedScrap 集合中所有元素会被移出，此时，大小又是 0
       - 所以，mAttachedScrap 是临时的缓存集合，仅存在于一次 layout，用户滑动屏幕期间，可能会产生多次 layout，每次都会把屏幕上的 vh 加入到mAttachedScrap，layout 完成就清空
     - ArrayList<ViewHolder> mChangedScrap
       - 抛开屏幕滑动期间 notifyData 的场景，这个集合也是种临时缓存，存活于一次 layout 周期
       - 调用 notifyData 以后，rv 会把需要更新范围内的 vh 丢到 mChangedScrap 中
       - 然后很快会被取出来复用，因为这块的视图是不需要改变的，只需要重新绑定数据
       - 数据变更 layout 完成后，mChangedScrap.clear() 集合被清空
     - ArrayList<ViewHolder> mCachedViews
       - mCachedViews 非常好理解，保存屏幕刚刚不可见的 vh
       - 比如你刚把某个 item 划出屏幕一点点，随时可能再划回来，这种 vh 保存在 mCachedViews，默认缓存 2 个
       - 重新划回来 vh 的视图和数据都不需要更新，直接用
     - ViewCacheExtension mViewCacheExtension，开发者自定义缓存实现，我没用过，忽略
     - RecycledViewPool mRecyclerPool
       - 长期缓存持，mCachedViews 放不下的，每种 vh 最大缓存数为 5
       - 可以通过 setMaxRecycledViews(viewType, max) 方法来自定义上限
       - 重新从 mRecyclerPool 取出来的 vh 需要走 bind 绑定数据
       - 如果有需要，可以多个 RecyclerView 共享一个缓存池

补充一下上面这几个缓存的查找顺序

1. 入口在 tryGetViewHolderForPositionByDeadline() 函数，先查 mChangedScrap，因为可能是 notifyItemChanged
2. 再查 mAttachedScrap
3. 然后查 mCachedViews
4. 如果 ViewCacheExtension 存在，尝试 getViewForPositionAndType
5. 最后查 RecycledViewPool
6. 都找不到，调用 onCreateViewHolder 创建新 ViewHolder

vh 的回收也是实时判定的，在滑动/layout的过程中实时更新，大概过程是这样的：

```
屏幕滑出 → 尝试放入 mCachedViews（临时缓存） → 满了 → 放入 RecycledViewPool（永久缓存）
```

也就是说，一次 layout 过程中就会把无法复用的 vh 逐步转移到 cache 或 pool 中。滑动不停，layout 不止，缓存也持续更新。

# 基本使用

想要使用 rv 控件，最少要设置 adapter 和 LayoutManager 才能正常工作，其中，分割线和动画则是可选项

| 组件               | 是否必须         | 作用                           |
| ---------------- | ------------ | ---------------------------- |
| `LayoutManager`  | ✅ 必须         | 控制 RecyclerView 中 item 的排列方式 |
| `Adapter`        | ✅ 必须         | 提供 item 的数量、视图和数据绑定逻辑        |
| `ItemDecoration` | ❌ 可选         | 分割线等视觉效果                     |
| `ItemAnimator`   | ❌ 可选         | 插入、删除时的动画                    |
| `ViewHolder`     | ✅（Adapter 中） | 复用机制，缓存 item view，提高性能       |

## 1、Adapter

adapter 是 RV 的两大金刚之一，负责提供数据，以及完成数据和视图的绑定

1. 第一步，创建 VH 类继承自 ViewHolder
   - VH 内部持有 itemView，用来表示列表中每个 item 的视图
   - 每个 VH 都和一个 item 的布局对应，所以，如果需要展示有多个不同的视图，需要创建对应数量的 VH
   - recyclerview 的缓存机制，缓存的最小单位就是 viewholder
2. 然后，创建 Adapter，并要求传入数据，重写 onCreateViewHolder 和 onBindViewHolder 等方法

| 方法                             | 作用                                                                       |
| ------------------------------ |--------------------------------------------------------------------------|
| `onCreateViewHolder()`         | 创建 ViewHolder，负责 item 的布局加载和初始化（只创建，不绑定数据）                               |
| `onBindViewHolder()`           | 将数据绑定到 ViewHolder 中的 View 上（重复调用）                                        |
| `getItemViewType()`            | 返回 item 的类型，用于支持多布局                                                      |
| `getItemCount()`               | 返回列表数据的总个数                                                               |
| `onViewRecycled()`             | 当 View 被回收时触发，常用于清理资源（如取消动画）                                             |
| `onAttachedToRecyclerView()`   | Adapter 绑定到 RecyclerView 时回调，一般用于设置 LayoutManager 的 spanSizeLookup（Grid） |
| `onDetachedFromRecyclerView()` | Adapter 被 RecyclerView 解绑时触发                                             |
| `hasStableIds() + getItemId()` | 用于开启稳定 ID 支持，便于动画和 DiffUtil 更准确识别 item 变化，也可以解决图片闪烁问题                    |

## 2、LayoutManager

LayoutManager 是 RV 核心之一，告诉

| LayoutManager                | 简介       | 特点                             |
| ---------------------------- | -------- |--------------------------------|
| `LinearLayoutManager`        | 线性布局管理器  | 垂直/水平滚动，默认纵向，是最常见的布局           |
| `GridLayoutManager`          | 网格布局管理器  | 多列/多行布局，支持跨列，适合宫格、表格类 UI       |
| `StaggeredGridLayoutManager` | 瀑布流布局管理器 | 不规则 item 高度，支持 item 高度错落，适合图片流 |
| `FlexboxLayoutManager` | flex-wrap 弹性布局 | 适用于标签展示、自动换行、多尺寸 item 动态排布的场景  |

如果需要自定义布局，比如探探卡片滑动效果

```kotlin
class LayoutManager : RecyclerView.LayoutManager() {

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        // 核心方法：如何摆放所有的子 View
    }

    override fun canScrollVertically(): Boolean {
        return true
    }

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        // 控制滑动行为
        return dy
    }
}
```

# 常见问题

- 如果 item 大小固定，比如向我们 IM 应用的聊天列表，可以使用 recyclerView.setHasFixedSize(true) 告诉 rv 我们的 item 尺寸不变，这样可以减少不必要的 layout 以及 measure，提高性能。
- recyclerView.itemAnimator = null，关闭默认动画以避免视觉闪烁
- 如果有多个 rv 可以复用同一个 rv 类型，可以创建一个共享的 RecycledViewPool，还是比如我们的 IM 聊天页表，它有多个 tab，最近聊天、新打招呼、群聊 等，item 都是一样的
```kotlin
recyclerView.setRecycledViewPool(sharedPool)
```
- 开启稳定 ID 可以减少刷新次数
```kotlin
adapter.setHasStableIds(true)
override fun getItemId(position: Int): Long = data[position].id
```
- 强烈要求所以 adapter 都必须使用 DiffUtil，如果有 baseAdapter ，记得封装函数
- rv 嵌套 rv 发生滑动冲突的时候
  - 嵌套时，内层要设置
    ```kotlin
      layoutManager.isItemPrefetchEnabled = false
      setNestedScrollingEnabled(false)
    ```
  - 外层使用 LinearLayoutManager，另外，能减少嵌套就减少
- 避免高度wield wrap_content 的 item 配合图片加载，容易造成 item 高度跳动，看起来很奇怪，建议使用固定高度或比例布局（ConstraintLayout）。
- 实现滑到底加载更多，可以结合 LayoutManager.findLastVisibleItemPosition() 判断是否滑到最后一项。