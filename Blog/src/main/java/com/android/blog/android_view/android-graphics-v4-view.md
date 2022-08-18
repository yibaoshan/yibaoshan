
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