## RecyclerView 类结构

### Interface

### Abstract

- AdapterDataObserver

### Private Class

### Public Class

private class

- ViewFlinger，滚动行为的核心组件，当用户快速滑动 RecyclerView 时，ViewFlinger 会计算出剩余的滚动距离并应用适当的动画，使得滚动可以逐渐减速至停止，这个过程也被称为“fling”动作。
- abstract AdapterDataObserver
- LayoutManager，负责确定 RecyclerView 的布局，包括确定每个 View 的位置和大小，以及确定 RecyclerView 的大小。