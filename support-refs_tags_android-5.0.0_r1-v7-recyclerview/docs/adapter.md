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
- bool hasStableIds()
- long getItemId()
- onViewRecycled(vh)
- onViewAttachedToWindow(vh)
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