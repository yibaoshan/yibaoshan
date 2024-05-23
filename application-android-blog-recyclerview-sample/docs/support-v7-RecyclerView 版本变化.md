[修订版 21，2014 年 10 月](https://developer.android.com/topic/libraries/support-library/rev-archive?hl=zh-cn#october-2014)

1. 发布 v7 `RecyclerView` 库，它提供灵活的列表视图，用于为大型数据集提供有限的窗口。

[修订版 21.0.2，2014 年 11 月](https://developer.android.com/topic/libraries/support-library/rev-archive?hl=zh-cn#november-2014)

- 向 `RecyclerView` 类添加了 `TOUCH_SLOP_DEFAULT` 和 `TOUCH_SLOP_PAGING` 常量，以支持用于分页的触摸溢出配置。

[修订版 22，2015 年 03 月](https://developer.android.com/topic/libraries/support-library/rev-archive?hl=zh-cn#march-2015)

- 向 `RecyclerView` 类添加了 `getLayoutPosition()` 和 `getAdapterPosition()` 方法。
- 废弃了 `RecyclerView` 类中的 `getChildPosition()` 和 `findViewHolderForPosition()` 方法。
- 废弃了 `RecyclerView.ViewHolder` 类中的 `getPosition()` 方法。
- 废弃了 `RecyclerView.LayoutParams` 类中的 `getViewPosition()` 方法。

[修订版 22.1.0，2015 年 04 月](https://developer.android.com/topic/libraries/support-library/rev-archive?hl=zh-cn#april-2015)

- 添加了 `SortedList` 类，可按列表顺序显示项，并提供列表更改通知。
- 添加了 `SortedListAdapterCallback` 类，可将已排序的列表绑定到 `RecyclerView.Adapter` 类。

[修订版 22.2.1，2015 年 07 月](https://developer.android.com/topic/libraries/support-library/rev-archive?hl=zh-cn#july-2015)

- 向 `RecyclerView` 添加了便捷方法，用于批量插入项。

[修订版 23.1.0，2015 年 10 月](https://developer.android.com/topic/libraries/support-library/rev-archive?hl=zh-cn#october-2015)

- 向 `ItemAnimator` 类添加了经过改进的动画 API，以更好地进行自定义：
    - 更改动画不再强制执行 `ViewHolder` 对象的两个副本，这样可以启用项内容动画。此外，`ItemAnimator` 对象决定是重复使用相同的 `ViewHolder` 对象还是创建新对象。
    - 新的信息记录 API 让 `ItemAnimator` 类能够在布局生命周期的正确时间点灵活地收集数据。此信息稍后会传递到动画回调。
- 为这一向后不兼容的 API 变更提供简单的过渡计划：
    - 如果您之前已经扩展了 `ItemAnimator` 类，则可以将基类更改为 `SimpleItemAnimator`，您的代码应该像以前一样工作。`SimpleItemAnimator` 类通过封装新 API 来提供旧 API。
    - 部分方法已从 `ItemAnimator` 类中移除。以下代码将不再编译：

```
recyclerView.getItemAnimator().setSupportsChangeAnimations(false)
```

您可以将其替换为：

```
ItemAnimator animator = recyclerView.getItemAnimator();
if (animator instanceof SimpleItemAnimator) {
   ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
}
```

[修订版 23.2.0，2016 年 02 月](https://developer.android.com/topic/libraries/support-library/rev-archive?hl=zh-cn#february-2016)

- `RecyclerView` 现在具有一项名为 `AutoMeasure` 的选择启用功能，借助此功能，`RecyclerView.LayoutManager` 可以轻松封装内容或处理 `RecyclerView` 的父级提供的各种测量规范。它支持 `RecyclerView` 的所有现有动画功能。
    - 如果您有自定义 `RecyclerView.LayoutManager`，请调用 `setAutoMeasureEnabled(true)` 以开始使用新的 `AutoMeasure` API。默认情况下，所有内置的 `RecyclerView.LayoutManager` 对象都会启用自动测量。
    - `RecyclerView.LayoutManager` 不再忽略某些 `RecyclerView.LayoutParams` 设置，例如滚动方向上的 `MATCH_PARENT`。
- 使用载荷信息更新 `RecyclerView.ViewHolder` 时，`DefaultItemAnimator` 现在会停用更改动画。
- 您现在可以修改 `ItemTouchHelper` 转义速度来控制滑动灵敏度。为便于您更轻松或更难以进行滑动，请替换 `getSwipeEscapeVelocity(float defaultValue)` 并修改 `defaultValue`。

[修订版 23.2.1，2016 年 03 月](https://developer.android.com/topic/libraries/support-library/rev-archive?hl=zh-cn#march-2016)

- 修复了与各种测量规范方法相关的错误。[（问题 201856）](https://code.google.com/p/android/issues/detail?id=201856&hl=zh-cn)
- 缩短了计算布局或滚动时 `RecyclerView` 不允许适配器更改的锁定时间段。[（问题 202046）](https://code.google.com/p/android/issues/detail?id=202046&hl=zh-cn)
- 修复了针对看不到的内容调用 `notifyItemChanged()` 时发生崩溃的问题。[（问题 202136）](https://code.google.com/p/android/issues/detail?id=202136&hl=zh-cn)
- 修复了 `RecyclerView.LayoutManager` 在同一测量传递中添加和移除视图时发生崩溃的问题。[（问题 193958）](https://code.google.com/p/android/issues/detail?id=193958&hl=zh-cn)

[修订版 23.3.0，2016 年 04 月](https://developer.android.com/topic/libraries/support-library/rev-archive?hl=zh-cn#april-2016)

- 修复了在可见项范围缩小时 `RecyclerView` 无法调用滚动回调的 bug。[（问题 200987）](https://code.google.com/p/android/issues/detail?id=200987&hl=zh-cn)
- 修复了 `RecyclerView` 在采用线性布局、采用加权和包含图片时会冻结的 bug。[（问题 203276）](https://code.google.com/p/android/issues/detail?id=203276&hl=zh-cn)
- 修复了 `OrientationHelper.getStartAfterPadding()` 中发生崩溃的问题。[（问题 180521）](https://code.google.com/p/android/issues/detail?id=180521&hl=zh-cn)
- 修复了使用 `android:nestedScrollingEnabled` 时发生崩溃的问题。[（问题 197932）](https://code.google.com/p/android/issues/detail?id=197932&hl=zh-cn)

[修订版 24.2.0，2016 年 08 月](https://developer.android.com/topic/libraries/support-library/rev-archive?hl=zh-cn#august-2016)

- 添加了 `RecyclerView.OnFlingListener`，以支持响应快滑的自定义行为。`SnapHelper` 类提供专门用于贴靠子视图的实现，`LinearSnapHelper` 类会扩展此实现，以提供类似于 `ViewPager` 的居中对齐的贴靠行为。

[修订版 25.0.0，2016 年 10 月](https://developer.android.com/topic/libraries/support-library/rev-archive?hl=zh-cn#october-2016)

- `android.v7.widget.RecyclerView.DividerItemDecoration` 类为项之间的垂直或水平分隔线提供了基本实现。

[修订版 25.0.1，2016 年 11 月](https://developer.android.com/topic/libraries/support-library/rev-archive?hl=zh-cn#november-2016)

- 如果布局管理器为 null，`RecyclerView` 会在预提取期间崩溃。
- 在回收视图持有者时，`RecyclerView` 发生崩溃。[（AOSP 问题 225762）](https://code.google.com/p/android/issues/detail?id=225762&hl=zh-cn)
- `RecyclerView`：Android Studio 中呈现问题。[（AOSP 问题 225753）](https://code.google.com/p/android/issues/detail?id=225753&hl=zh-cn)

[修订版 25.1.0，2016 年 12 月](https://developer.android.com/topic/libraries/support-library/rev-archive?hl=zh-cn#december-2016)

重要变更

- 通过提示内部 `RecyclerView` widget 的布局管理器，需要准备多少项才能在屏幕上滚动，嵌套 `RecyclerView` widget（例如，水平滚动列表的垂直滚动列表）的客户端可以显著提升性能。调用 `LinearLayoutManager.setInitialPrefetchItemCount(N)`，其中 `N` 是每个内部项可见的视图数。例如，如果您的内部水平列表一次显示至少 3 个半项视图，您可以通过调用 `LinearLayoutManager.setInitialPrefetchItemCount(4)` 提升性能。这样做可以让 `RecyclerView` 在外部 `RecyclerView` 滚动时尽早创建所有相关视图，从而显著减少滚动期间的卡顿现象。

API 变更

- `RecyclerView` RecyclerView 项预提取改进：
    - 通过嵌套 `RecyclerView` 预提取，可以从另一个滚动 `RecyclerView` 内的 `RecyclerView` 预提取内容，并通过 API 控制完成的预提取量：
        - `LinearLayoutManager.setInitialPrefetchItemCount()`
        - `LinearLayoutManager.getInitialPrefetchItemCount()`
    - 为自定义 LayoutManager 对象添加了 API，实现这些 API 即可在滚动和投掷期间启用预提取
        - `RecyclerView.LayoutManager.LayoutPrefetchRegistry()`
        - `RecyclerView.LayoutManager.collectAdjacentPrefetchPositions()`
        - `RecyclerView.LayoutManager.collectInitialPrefetchPositions()`
    - 改进了预提取，以在帧之间的时间内仅执行尽可能多的创建/绑定工作

修复的问题

- 向 `RecyclerView` 添加了焦点恢复机制。这还修复了在使用方向键导航（例如在 Android TV 设备上）时偏好设置 fragment 无法聚焦的支持。
- 在回收视图持有者时，`RecyclerView` 发生崩溃[（AOSP 问题 225762）](https://code.google.com/p/android/issues/detail?id=225762&hl=zh-cn)
- 为 `RecyclerView` 项添加动画效果，使其分离内部 `RecyclerView`，防止将来再次进行预提取
- 附加的 `RecyclerView` 项无法进行嵌套预提取
- 在第一次布局期间，系统会舍弃嵌套的 `RecyclerView` 项的预提取数据
- 如果两个拖动事件到达同一位置，`RecyclerView` 预提取会失败
- `RecyclerView` 应在 `RenderThread` 进行渲染时推测性布局

[修订版 25.1.1，2017 年 01 月](https://developer.android.com/topic/libraries/support-library/rev-archive?hl=zh-cn#january-2017)

- 带有 `StaggeredGridLayoutManager` 的 `RecyclerView` 在使用完整跨度项时发生崩溃[（AOSP 问题 230295）](https://code.google.com/p/android/issues/detail?id=230295&hl=zh-cn)
- `RecyclerViewFocusRecoveryTest` 在 API 15 上失败
- `RecyclerViewLayoutTest.triggerFocusSearchInOnRecycledCallback()` 在 API 15 上发生崩溃
- 在回收一些视图持有者时，`RecyclerView` 发生崩溃

[修订版 25.2.0，2017 年 02 月](https://developer.android.com/topic/libraries/support-library/rev-archive?hl=zh-cn#february-2017)

- `RecyclerViewLayoutTest.triggerFocusSearchInOnRecycledCallback()` 崩溃
- 在回收视图持有者时，`RecyclerView` 发生崩溃[（AOSP 问题 225762）](https://code.google.com/p/android/issues/detail?id=225762&hl=zh-cn)

[修订版 25.3.0，2017 年 03 月](https://developer.android.com/topic/libraries/support-library/rev-archive?hl=zh-cn#march-2017_2)

- `LinearLayoutManager.getInitialItemPrefetchCount()` 已重命名为 `LinearLayoutManager.getInitialPrefetchItemCount()`。旧名称仍然受支持，但将在未来的版本中移除。

[修订版 26.0.0 测试版1，2017 年 05 月](https://developer.android.com/topic/libraries/support-library/rev-archive?hl=zh-cn#may-2017)

- 在预提取期间，`RecyclerView.isComputingLayout()` 应返回 true

[修订版 26.0.0，2017 年 07 月](https://developer.android.com/topic/libraries/support-library/revisions?hl=zh-cn#july-2017)

- `RecyclerView` 的新 `fastScrollEnabled` 布尔标记。如果启用，则必须设置 `fastScrollHorizontalThumbDrawable`、`fastScrollHorizontalTrackDrawable`、`fastScrollVerticalThumbDrawable` 和 `fastScrollVerticalTrackDrawable`。
- 修复 `RecyclerView.toString()` 中出现无限循环错误。

[修订版 27.0.0，2017 年 10 月](https://developer.android.com/topic/libraries/support-library/revisions?hl=zh-cn#october-2017)

- `RecyclerView.AttachViewToParent()` 出现 `IllegalArgumentException`
- 解决 焦点卡在 `RecyclerView` 中的错误。

[修订版 27.0.1，2017 年 11 月](https://developer.android.com/topic/libraries/support-library/revisions?hl=zh-cn#november-2017_1)

- 修复用户滚动后，无法点击 `RecyclerView` 中的项目。[（AOSP 问题 66996774）](https://issuetracker.google.com/66996774?hl=zh-cn)

[修订版 27.1.0，2018 年 02 月](https://developer.android.com/topic/libraries/support-library/revisions?hl=zh-cn#february-2018)

- `RecyclerView` 的 `ListAdapter`（以及 `AsyncListDiffer`）可以更轻松地在后台线程上计算列表差异。它们可以帮助您的 `RecyclerView` 动画内容自动更改，只需在界面线程上完成极少的工作。在后台，它们使用的是 `DiffUtil`。
- 由于未在 `setAdapter` 中清除 `State.mPreviousLayoutItemCount` 而出现 `RecyclerView` `IndexOutOfBoundsException`[（AOSP 问题 37657125）](https://issuetracker.google.com/issues/37657125?hl=zh-cn)

[修订版 28.0.0 Alpha 版1，2018 年 03 月](https://developer.android.com/topic/libraries/support-library/revisions?hl=zh-cn#march-2018)

- 使用 `androidx` 作为软件包前缀
- 新增 `recyclerview-selection` 为 `RecyclerView` 提供内容选择支持。
- 修复在某些情况下，`RecyclerView` 的 `LinearLayoutManager` 的 `smoothScrollToPosition()` 显示不稳定（来回）移动[（AOSP 问题 71567765）](https://issuetracker.google.com/issues/71567765?hl=zh-cn)

[修订版 27.1.1，2018 年 04 月](https://developer.android.com/topic/libraries/support-library/revisions?hl=zh-cn#april-2018)

- 修复即使适配器为 null，`RecyclerView.setRecycledViewPool()` 也会增加 `attachmentsCount`
- 修复 `SmoothScroller.onStop` 调用 `stop()` 或 `startSmoothScroller()` 时会出现 `RecyclerView` `NPE`

[修订版 28.0.0 RC1，2018 年 08 月 06 日](https://developer.android.com/topic/libraries/support-library/revisions?hl=zh-cn#august-6,-2018)

- 修复当数据集随着移除选择而发生更改时 `RecyclerView` 选择库中出现 `ConcurrentModificationException`

[修订版 28.0.0，2018 年 09 月 21 日](https://developer.android.com/topic/libraries/support-library/revisions?hl=zh-cn#september-21,-2018)

- 最后一个 `support` 库，RV 没有任何更新。
