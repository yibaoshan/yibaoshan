
Android图形系统（四）应用篇：描述下自定义View/ViewGroup的过程？

对于应用开发工程师来说，相信

绝大多数 Android 开发者都有过自定义 View 的经历，以我个人的工程经验来看，自定义 View 大体可以分为三种：组合、改造和自定义

组合指的是将2个以上的控件组合在封装

流传比较广MeasureSpec的介绍流传比较广的说法是来自《Android开发艺术探索》

接收到AT_MOST时如何处理？

什么情况下会接收到xx模式呢？

接收到什么类型的测量模式和LayoutParams没有必然联系，子View/ViewGroup的策略应该由父视图的业务来决定

比如我们自己自定义一个ViewGroup，完全无视子View的LayoutParams属性，子View的宽高mode全部指定为AT_MOST也可以，指定为

哪怕不指定子View的MeasureSpec属性，通过现有的ViewGroup们同样可以推翻上图罗列的几种规则

只是想说明子视图得到的测量模式不一定和LayoutParams有关

所以我们这里只讨论当我们作为子View或者子ViewGroup时，父视图传递的是AT_MOST时我们应该怎么办？

LayoutParams是ViewGroup的静态类，我们只需要知道它是用来描述View/ViewGroup的宽高就可以了，更详细的作用我们会在layout阶段介绍

onMeasure()为什么会执行多次？

我在刚学习自定义View时就发现onMeasure方法会被回调多次，那时候没有深究内部原因，能跑就行

写文章的话就有必要搞懂这个问题了

layout阶段唯一的职责是确定每个子View的位置，非常简单

### 一、测量阶段

#### 一直以来MeasureSpec的误解

#### 为什么ViewGroup没有默认实现？

因为 ViewGroup 容器，它的职责是结合容器内的子View计算自己的高度，这是由ViewGroup的不同的属性决定的

拿 LinearLayout 来说，在高度设置为 wrap_content 的情况下

如果是横向布局，LinearLayout 的高度应该为最大子View的高度

如果是纵向布局，LinearLayout 的高度应该为所有子View的高度总和

这是由业务决定的

这张图的结论是怎么来的？

在测量阶段，View 和 ViewGroup 的侧重点完全不同

对于 View 来说，onMeasure() 是通知，我可以在父视图的规则下进行测量，也可以肆意妄为

#### onMeasure()多次执行的原因



### 二、布局阶段

### 三、绘制阶段

