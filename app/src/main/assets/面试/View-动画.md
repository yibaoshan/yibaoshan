
## View 动画

###  1、补间动画

- 视图动画的一种，通常用于 Activity、Dialog 弹窗等过渡动画
- 支持位移（TranslateAnimation）、缩放（ScaleAnimation）、透明度（AlphaAnimation）、旋转（RotateAnimation）

2种使用方法：

1. 代码实现，创建 XXXAnimation 对象，设置动画参数
2. xml 实现，调用 AnimationUtils#loadAnimation() 解析获取 Animation 对象

播放动画：调用 View#startAnimation(animation)

实现原理：动画在 View#draw() 方法中执行，将动画变换矩阵运用到 canvas 上

### 2、帧动画

和补间动画一样，属于视图动画一种，实现原理是相同的

使用方式：xml 指定帧动画图片

播放动画：代码中拿到 AnimationDrawable 对象，调用 start() 方法

**实际开发尽量少的使用帧动画**，理由如下：

1. 32 位 / 小内存手机可能会引发 OOM
2. 帧动画在未停止的情况想被其他 View 覆盖，只会白白浪费宝贵的内存资源（某些下拉刷新框架就是如此，动画一直偷偷在背后播放）

### 3、属性动画

Android 3.0 以后加入的，项目中用的最多的动画实现方案（ValueAnimator，ObjectAnimator）

经常跟估值器（Evaluator）、插值器（Interpolator）搭配实现动画效果

## 属性 / 方法 / 接口

View / ViewGroup 测量、布局、绘制的触发时机

主动触发：

- 手动调用 requestLayout()
  - 若视图的位置需要发生变化，则重新执行测量、布局、绘制三步曲
  - 重新测量可能会影响到其他 View / ViewGroup 的位置大小，随即触发其他视图的测量绘制流程
- 手动调用 invalidate()
  - View：若内容需要更新，重新执行绘制流程（onDraw）
  - ViewGroup：触发自身和子视图的重新绘制

被动触发：

- 被动 invalidate()
  - View 的状态改变，如 enable、background、visibility 等
  - View 的可见性改变，如解锁开屏、返回键退回到上一个页面
  - Activity 的 focus 变化，如下拉查看状态栏、Dialog 弹窗等
- 被动 requestLayout() -- 暂无发现

## View 常见回调函数

- **onFinishInflate()**，由 LayoutInflater 解析 xml 而来时调用，比如 setContentView() 解析完成并添加到 DecorView 时，此时视图的测量/绘制还未开始
- **onVisibilityChanged()**，View 状态可见性发生变化，比如 GONE
- **onWindowVisibilityChanged()**，依附的 Window 可见性发生变化
- **onSizeChanged()**，大小首次确认，或者发生修改的回调通知
- **onAttachedToWindow()**，被添加到 Window 的回调通知，如手动 addView()。同一 View 对象可以被添加/删除多次，只是不能被重复添加
- **onDetachedFromWindow()**，被 removeView()，一般在此执行释放工作，如解除动画、回收 Bitmap 等
- **onFocusChanged()**，焦点变化通知
- **onSaveInstanceState()**，参考 Activity，内存紧张时可能会被调用，通知保存信息

## View 常用属性

- mTop、mBottom、mLeft、mRight 分别表示 View 上下左右四个坐标点相对 "父布局" 的值
- mPaddingTop、Bottom、Left、Right 内边距的值，算作 View 自身大小。margin 是外边距，没有属性值，算作是父布局的大小
- mScrollX、mScrollY，视图内容相对之前的滚动偏移量，作用于 onDraw 中，left = mScrollX + paddingLeft
- mContext，上下文环境，类型为 ContextThemeWrapper（因为要操作主题资源），可以递归调用 Context#getBaseContext() 再强转获取到 Activity
