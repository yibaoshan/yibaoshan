# RecyclerView 类结构

## Interface

- OnItemTouchListener
    - 提供给外部的接口，用于拦截处理 RecyclerView 的触摸事件，拦截后，RecyclerView 会将事件传递给该监听器。
    - boolean onInterceptTouchEvent(rv, e)，要求返回布尔值，是否要消费触摸事件。
    - void onTouchEvent(rv, e)，如果上一步返回 true，则该函数会收到事件调用。
    - 用途：item 的点击事件、长按事件；item 的侧滑菜单实现。
- RecyclerListener
    - onViewRecycled(vh)，当 RecyclerView 的 item 被回收时，会调用该函数。

## Abstract

- ViewCacheExtension
- Adapter
    - [点这](adapter.md)
- LayoutManager
    - [点这](LayoutManager.md)
- ItemDecoration
- OnScrollListener
- ViewHolder
- AdapterDataObserver
- SmoothScroller
- ItemAnimator

## Private Class

- ViewFlinger
    - 滚动行为的核心组件，当用户快速滑动 RecyclerView 时，ViewFlinger 会计算出剩余的滚动距离并应用适当的动画，使得滚动可以逐渐减速至停止，这个过程也被称为“fling”动作。
- RecyclerViewDataObserver
- ItemAnimatorRestoreListener
- ItemHolderInfo

## Public Class

- RecycledViewPool
- Recycler
- LayoutParams
- AdapterDataObservable
- SavedState
- State