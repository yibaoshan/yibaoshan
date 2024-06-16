RV 提供的，为每个 Item 设置额外视图的功能，一般来说，常用的场景有：

- 添加分割线：这是 ItemDecoration 类最常用的功能之一。我们可以通过重写 getItemOffsets 方法来设置每个列表项的左右上下间距，并在 onDraw 方法中绘制分割线。
- 设置边距：我们可以通过重写 getItemOffsets 方法来设置每个列表项的边距。
- 添加阴影：我们可以通过在 onDraw 方法中绘制阴影来实现阴影效果。
- 创建粘性头部：我们可以通过重写 onDrawOver 方法来创建粘性头部，即当列表滚动时，头部始终悬浮在屏幕顶部的效果。

- onDraw(Canvas c, RecyclerView parent, State state)
    - item 绘图之前调用
- onDrawOver(Canvas c, RecyclerView parent, State state)
    - item 已经完成了绘制，我们可以在 item 上面绘图，也就是说，它可以完全覆盖 item
- getItemOffsets(Rect outRect, View view, RecyclerView parent, State state)
    - 计算每个 item 与它自己相邻的 item 的边距，并设置到 outRect 中。
    - outRect,你这个额外视图想要多大的空间
    - view 是当前的 item

注意事项：

- ItemDecoration 类会在绘制列表项之前调用。因此，如果我们在 onDraw 方法中绘制的内容覆盖了列表项，则会将其遮挡。为了避免这种情况，我们可以使用 onDrawOver 方法来绘制内容。
- ItemDecoration 类会影响列表项的布局。因此，我们需要在计算列表项的尺寸时考虑 ItemDecoration 造成的偏移量。
- ItemDecoration 类可能会降低列表的滚动性能。因此，在使用时需要注意性能优化。