ChildHelper mChildHelper;
RecyclerView mRecyclerView;

@Nullable
SmoothScroller mSmoothScroller;

## call super recyclerview

- requestLayout()
- assertInLayoutOrScroll() / assertNotInLayoutOrScroll()
    - 如果在 / 不在滑动或布局中，抛出异常
- supportsPredictiveItemAnimations()
    - 是否支持 item 预测动画？暂时没搞懂干嘛用的
- getClipToPadding()
    - 获取爸爸的 mClipToPadding 属性

## abstract

- generateDefaultLayoutParams()
    - 生成默认的 layout params，子类必须实现

## public

- onAttachedToWindow() / onDetachedFromWindow()
    - 添加到屏幕 / 从屏幕移除
- onLayoutChildren()
    - 关键函数，布局子视图，要求子类实现
- generateLayoutParams()
    - 生成 lp，没啥说的
- scrollHorizontallyBy() / scrollVerticallyBy()
    - 水平 / 垂直方向滚动
- canScrollHorizontally() / canScrollVertically()
    - 是否可以水平 / 垂直方向滚动，需子类实现，默认为 false
- scrollToPosition(pos) / smoothScrollToPosition(pos)
    - 滚动到指定位置，需子类实现
- startSmoothScroll(smoothScroller)
    - 如果滚动器为空，赋值给本地并开始滚动
    - 如果滚动器不为空，先停止，然后走为空逻辑
- int getLayoutDirection()
    - 获取爸爸的视图方向
- endAnimation(view)
    - 调用 ItemAnimator 结束动画
- addView() / addDisappearingView()
    - 添加视图，主要逻辑在私有函数 addViewInt(child, index, disappearing) 中
    - disappearing 暂时不知道干啥的，从注释来看，只参与 onLayoutChildren 阶段的绘制，调用 getChildAt() / getChildCount() 无效，猜测应该不走缓存。
- removeView() / removeViewAt() / removeAllViews()
    - 删除视图，内部全部交由 ChildHelper 类实现
- getPosition(view)
    - 获取视图在 RecyclerView 中的位置
- getItemViewType(view)
    - 获取视图类型
- findViewByPosition(pos) / getChildAt(index) / getChildCount()
    - 查找子视图

https://blog.csdn.net/sinat_33585352/article/details/78941169