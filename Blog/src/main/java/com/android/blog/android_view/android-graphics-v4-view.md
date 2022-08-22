> 1. 开篇，介绍View常用的使用方式
>    1. 控件组合，频率最高的方式之一，比如倒计时就是linearlayout+textview
>    2. 基于控件二次改装，频率最高的方式之一，比如拦截滑动事件/自动计算高度的ViewGroup
>    3. 自定义View/ViewGroup，虽然写过不少自定义View，还没有从0到1的写过ViewGroup，很遗憾。目前的ViewGroup几个布局完全够用
>    4. 自定义View还是很酷的，记得刚学会自定义View那会，感觉天下都是我的，恨不得整个页面的控件都用自定义一遍，比较深的一次是当时公司在做区块链钱包
>    5. 当时有一个助记词的需求，刚开完ui评审会我就自告奋勇的和tl接下了这个页面，然后
>    5. 年龄上来了以后，想要一个，github搜，点击下载库，找到控件类，删作者名称，改一改添加到项目，完事
> 2. View绘制三部曲，说明自定ViewGroup场景较少，所以measure/layout过程我们就以常用的几个布局来介绍他们的原理和区别，
>    1. measure，多次调用的意义
>    2. layout，
>    3. draw
> 3. Window与View，介绍在开发中，我们常用的两种方式，二是Activity，Activity本身也是包含了Window
>    1. Window添加View，注意事项，Window类型
>    2. Activity本身包含Window，这个Window包含了一个DecorView

开发方式，在XML文件中使用控件

好看的UI设计是留住用户的第一步

1. 组合已有控件

项目中这类组合使用的频率挺高的，比如电商活动通常都是倒计时，我们就可以要求UI设计师统一风格，我们写一个

View绘制的原理以及注意事项

自定义View不是一件很复杂的事情，几乎

基于已有控件二次开发

比如UI设计师觉得Android的switch控件又丑又不统一，看看人家ios的toggles，多么精致

得，又得抽时间写个自定义，虽然网上有同款，但不能为了个开关控件导入整个包吧

自定义viewgroup好像没有使用过

相信大多数读者对自定义View多少有些了解，如果您还没有动手写过，那么可以点击这里https://www.gcssloop.com/进行学习

自定义View

自定义Window

不考虑surfaceview和textureview

View体系是Google为Android开发者准备的一套描述视图的API

我们可以利用它写文字，展示图片，显示边框等等

现有的控件没法满足邀请的话，还可以选择自定义View

View绘制三部曲背后的原理

#### 1、measure

View 的工作原理中最重要的就是测量、布局、绘制三大过程，而其中测量是最复杂的

ViewRootImpl#performTraversals()：确定DecorView的MeasureSpec
    ViewRootImpl#performMeasure()：执行DecorView的measure()方法
        View#measure()：

##### 问题和笔记

- measure在第一次进行测量，后续应该只有在Window窗口大小发生变化时才会重新执行测量，否则，跳过这一步，有哪几种情况跳过或执行测量呢？
  - Activity执行了onStop回调，Window的状态为stopped，即mStopped=true时不执行测量
  - 

##### 目的

需要处理warpcontent，matchcontent

测量DecorView下所有View和ViewGroup的大小

如果你需要自定义View，那么在这一步可以根据父view传递的MeasureSpec来确定自己的大小

哪怕你的子View比父view要大，这一步也都会记录下来

##### 基本原理

入口？

在ViewRootImpl#performMeasure()之前会通过getRootMeasureSpec获取根view的MeasureSpec，它默认为屏幕的大小

触发条件？

所有的View都会执行measure过程吗？

- UPSPECIFIED : 父容器对于子容器没有任何限制,子容器想要多大就多大，你爱多大就多大
- 父控件对子控件不加任何束缚，子元素可以得到任意想要的大小，这种MeasureSpec一般是由父控件自身的特性决定的
- 比如ScrollView，它的子View可以随意设置大小，无论多高，都能滚动显示，这个时候，size一般就没什么意义
- EXACTLY: 精确模式，对应 MATCH_PARENT 和确定的值
- AT_MOST：子容器可以是声明大小内的任意大小，对应 WRAP_CONTENT

不按照MeasureSpec可以吗？父容器限制我的大小为10*10，我可以申请20*20的大小吗？

这里需要注意measure是一个final方法，在内部它通过调用onMeasure来完成实际的测量工作

如果我们需要自定义view，就需要覆盖onMeasure在其中完成view大小的测量

我们看下默认的onMeasure实现：

##### 示例

padding和margin的影响

MeasureSpec决定什么？

MATCH_PARENT和WRAP_CONTENT会如何处理？


