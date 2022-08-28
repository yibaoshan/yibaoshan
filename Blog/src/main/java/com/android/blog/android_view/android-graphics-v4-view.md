> 1. 开篇，介绍View常用的使用方式
>    1. 控件组合，频率最高的方式之一，比如倒计时就是linearlayout+textview
>    2. 基于控件二次改装，频率最高的方式之一，比如拦截滑动事件/自动计算高度的ViewGroup
>    3. 自定义View/ViewGroup，虽然写过不少自定义View，还没有从0到1的写过ViewGroup，很遗憾。目前的ViewGroup几个布局完全够用
>    4. 自定义View还是很酷的，记得刚学会自定义View那会，感觉天下都是我的，恨不得整个页面的控件都用自定义一遍，比较深的一次是当时公司在做区块链钱包
>    5. 当时有一个助记词的需求，刚开完ui评审会我就自告奋勇的和tl接下了这个页面，然后
>    6. 年龄上来了以后，想要一个，github搜，点击下载库，找到控件类，删作者名称，改一改添加到项目，完事
>    7. 在《图形系统三》这篇文章中详细介绍了View绘制的执行入口，我们来复习一遍：
> 2. View绘制三部曲，说明自定ViewGroup场景较少，所以measure/layout过程我们就以常用的几个布局来介绍他们的原理和区别，
>    1. measure，多次调用的意义
>    2. layout，
>    3. draw
> 3. Window与View，介绍在开发中，我们常用的两种方式，二是Activity，Activity本身也是包含了Window
>    1. Window添加View，注意事项，Window类型
>    2. Activity本身包含Window，这个Window包含了一个DecorView

Android图形系统（四）应用篇：描述下自定义View/ViewGroup的过程？



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


#### invalidate会触发其他view的重绘吗？

#### View绘制过程用的是同一个canvas吗？

#### 1、measure

为了加深对MeasureSpec的理解，接下来我们通过几个小栗子来感受下：

- 每个View和ViewGroup都有MeasureSpec和LayoutParams两个参数，决定了View/ViewGroup的大小
- 每个View/ViewGroup的MeasureSpec属性大多数参考父视图的MeasureSpec，当然，你可以更改自身的MeasureSpec属性
- 每个View/ViewGroup的LayoutParams属性通常是由开发者/业务决定的，基本上都继承自MarginLayoutParams
- 每个View在measure阶段要做的事情就是合理的计算自身所需的宽和高，不重写onMeasure()方法也行，根据父视图的MeasureSpec，将会有以下几种组合
  - 不固定大小，match_parent或者warp_content
    - 父视图为Exactly或者AT_Most时，当前View宽高为父视图宽高
    - 父视图为UPSPECIFIED未确定时，当前View宽高为0，在页面中看不见它

  - 确定大小，比如写死layout_width为200dp，和上面一样的
    - 父视图为Exactly或者AT_Most时，当前View宽高为父视图宽高
    - 父视图为UPSPECIFIED未确定时，当前View宽高为0，在页面中看不见它

- 每个ViewGroup在measure阶段必须要重写onMeasure()方法，否则，在页面中看不到它
  - 重写onMeasure()，调用measureChild()或者measureChildWithMargins()方法让子View自己完成测量
  - 所有子View测量工作完成后，我们可以获取每个子View的宽高信息，相加得到自身宽高

- 每个View/ViewGroup在onMeasure()结束以后，都会调用setMeasuredDimension()方法来保存测量的宽高值
- View的大小是由父视图的MeasureSpec和自身的LayoutParams参数决定的
- ViewGroup的大小同样是由父视图的MeasureSpec和自身的LayoutParams决定的
- MeasureSpec是父布局传递过来的，目的是限制子View的大小，LayoutParams是View/ViewGroup自身属性
- measure阶段要把View和ViewGroup分开来看，它俩在measure完成的事情不同


##### 什么是MeasureSpec

- UPSPECIFIED : 父容器对于子容器没有任何限制,子容器想要多大就多大，你爱多大就多大
- 父控件对子控件不加任何束缚，子元素可以得到任意想要的大小，这种MeasureSpec一般是由父控件自身的特性决定的
- 比如ScrollView，它的子View可以随意设置大小，无论多高，都能滚动显示，这个时候，size一般就没什么意义
- 这个模式用于可以滚动的容器，比如如果你的父视图是ScrollView或者RecyclerView，你在自定义View设置的是wrap_content或者match_parent
- 本来应该收到的是AT_MOST，却受到了UNSPECIFIED
- EXACTLY: 精确模式，View设置了确切的大小，或者设置了match，对其父视图，总之大小都是确定的，对应 MATCH_PARENT 和确定的值
- AT_MOST：子容器可以是声明大小内的任意大小，当View收到此模式时，意味着你可以自己在父视图的这块小天地随意实战，通常情况下，你在xml指定宽高为WRAP_CONTENT，在measure阶段，将会收到AT_MOST

- 在此模式下，如果你自己写的自定义View/ViewGroup没有重写onMeasure()方法来确定View大小，并且LayoutParams不是指定的值（math或者warp），那么你将不会在屏幕上看到它，高度为0

什么时候不会收到AT_MOST

##### 什么是LayoutParams

LinearLayout继承自MarginLayoutParams，新增了gravity和权重weight属性

RelativeLayout继承自MarginLayoutParams，新增了above、below、alignxxx等一系列属性

在自定义ViewGroup时，如果对布局有要求，在layout阶段同样也会使用到

比较重要的是MarginLayoutParams，我们常用的LinearLayout、RelativeLayout都是继承于MarginLayoutParams

这里有同学可能要问了：为什么没有PaddingLayoutParams呢？

因为padding是View的属性呀，小傻瓜

##### View#measure()

View的任务是在此阶段计算自己的大小

默认情况

在父视图MeasureSpec没有确定的情况下，除了

如何重写measure()

如果要重写的话内部的逻辑是业务逻辑，比如助记词那篇文章中就是计算每行的高度

##### ViewGroup#measure()


伪代码：


onMeasure(){  
    for(int i = 0;i<count;i++){
        child.measure();
        child.margin();
    }
}

ViewGroup的任务是先调用child.measure计算子View的大小

ViewGroup在测量阶段只需要考虑两种情况，如果LayoutParams宽高都为match_parent

根据自身的LayoutParams属性，来计算自身的高度

ScrollView就重写了measureChildWithMargins方法，然后将子视图的MeasureSpec改成了MeasureSpec.UNSPECIFIED

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

不按照MeasureSpec可以吗？父容器限制我的大小为10*10，我可以申请20*20的大小吗？

这里需要注意measure是一个final方法，在内部它通过调用onMeasure来完成实际的测量工作

如果我们需要自定义view，就需要覆盖onMeasure在其中完成view大小的测量

我们看下默认的onMeasure实现：

##### 示例

padding和margin的影响

MeasureSpec决定什么？

MATCH_PARENT和WRAP_CONTENT会如何处理？


#### 2、layout

