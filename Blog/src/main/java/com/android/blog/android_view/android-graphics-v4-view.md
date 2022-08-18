> 1. 开篇，介绍View常用的使用方式
>    1. 控件组合，频率最高的方式之一，比如倒计时就是linearlayout+textview
>    2. 基于控件二次改装，频率最高的方式之一，比如拦截滑动事件/自动计算高度的ViewGroup
>    3. 自定义View/ViewGroup，虽然写过不少自定义View，还没有从0到1的写过ViewGroup，很遗憾。目前的ViewGroup几个布局完全够用
>    4. 记得刚学会自定义View那会，感觉天下都是我的，恨不得整个页面的控件都用自定义一遍
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

measure

padding和margin的影响

MeasureSpec决定什么？

MATCH_PARENT和WRAP_CONTENT会如何处理？

measure

入口

过程

performMeasure