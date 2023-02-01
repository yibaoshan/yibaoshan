
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