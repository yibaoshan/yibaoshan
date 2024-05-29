Adapter 类是 RV 的数据和视图的适配器，左手控制数据，右手控制视图。

Abstract class，要求必须传入继承自 ViewHolder 的泛型类

## abstract

- VH onCreateViewHolder(parent, viewType)
    - 创建一个 ViewHolder，viewType 用于区分不同的 item
- onBindViewHolder(vh, position)
    - 执行绑定数据到视图的操作
- int getItemCount()
    - 获取需要展示多少个 item

## public

- int getItemViewType()
    - 如果你需要显示不同类型的 item，需要重写这个方法，返回不同的 viewType
- setHasStableIds(bool)
    - 设置稳定 ID，默认为 false，如果设为 true，需要重写 getItemId() 方法
    - 简单来说，如果为 true，调用 notifyDataSetChanged() 方法刷新后，RV 虽然会走 onBindViewHolder() 方法，但会尽最大可能不清除视图，而是从 mCachedViews 中获取。
    - 应用的话，很多防止刷新图片闪烁会用到这种方式，大部分情况下是可以解决闪烁问题的，闪烁根本原因是 remove 后重新 add bind 导致的，后面再展开。
- bool hasStableIds()
- long getItemId()
- onViewRecycled(vh)
    - 当 vh 被回收时调用，发生在 RecyclerView#dispatchViewRecycled() 函数，仅做通知。
- onViewAttachedToWindow(vh)
    - 视图被添加到窗口或者被窗口释放（下面的方法），由 RV 调用。
- onViewDetachedFromWindow(vh)

## final

- createViewHolder(vh, position)
- bindViewHolder(vh, position)

## 数据监听器

- registerAdapterDataObserver()
- unregisterAdapterDataObserver()
- hasObservers()
- notifyDataSetChanged()
- notifyItemChanged(position)
- notifyItemRangeChanged(start, cnt)
- notifyItemInserted(position)
- notifyItemMoved(from, to)
- notifyItemRangeInserted(start, cnt)
- notifyItemRemoved(position)
- notifyItemRangeRemoved(start, cnt)