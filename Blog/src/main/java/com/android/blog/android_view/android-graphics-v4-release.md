
Android图形系统（四）应用篇：描述下自定义View/ViewGroup的过程？

绝大多数 Android 开发者都有过自定义 View 的经历，以我个人的工作经验来看，自定义 View 大体可以分为三种：改装、组合和自定义

“改装”指的是继承自某个控件，在原有功能的基础上进行增删改，比如：基于 ViewPager 打造一个无限循环的轮播图控件

“组合”指的是将2个以上的控件组合成一个控件，比如：基于 RelativeLayout + 多个 EditText 组合成一个密码输入控件

“自定义”指的是当 Android 官方控件不足以满足业务需求（比如统计图表中的饼状图/折线图）时，继承 View/ViewGroup类，重写 onMeasure()、onLayout()、onDraw() 三大方法，从 0 到 1 创造一个新的控件

本篇是图形系列的第四篇文章，今天我们来聊聊自定义 View/ViewGroup 各个阶段发生的事情

我是概述图

### 一、测量阶段

在 Android 系统中，一个 View 的绘制显示要经过测量、布局和绘制这三个步骤

"测量"是为了计算每一个 View 需要的大小，View 和 ViewGroup 都需要重写 onMeasure() 方法来确定自己的尺寸

"布局"是为了计算每一个 View 的位置，通常只需要 ViewGroup 重写 onLayout() 方法，根据容器的属性合理的摆放子 View

"绘制"是最终绘制的阶段，所有的绘图操作都在 draw 阶段得到执行，通常需要 View 重写 onDraw() 方法

我们先来看第一步：测量流程

#### 初识MeasureSpec

上一篇文章我们介绍了测量流程是由 ViewRootImpl 类中的 performTraversals() 方法发起的，经过层层转发，最终会调用到每个 View 的 onMeasure() 方法

**View 真正的尺寸信息就是在 onMeasure() 方法中被确定的**

那么 View 是根据什么来确定自己应当具有多大的尺寸呢？不可能让子 View 自由地决定自己的大小吧

父 View 必然需要向子 View 传递信息来帮助子 View 来确定尺寸，查看 measure() 的方法签名：

```java
void measure(int widthMeasureSpec, int heightMeasureSpec)
```

widthMeasureSpec 和 heightMeasureSpec 就是帮助子 View 确定大小的参数

它们的类型是 int，内部以高两位来存储测量的模式，低三十位为测量的大小，计算中使用了位运算来提高并优化效率

当然，我们不必使用位运算来获得对应的数值，MeasureSpec 类为我们提供了对应的方法

```java
  class MeasureSpec {
    int UNSPECIFIED;//未指定，不限制大小
    int EXACTLY;//精确模式
    int AT_MOST;//最大模式
   
    int getMode(int measureSpec) return (measureSpec & MODE_MASK);
    int getSize(int measureSpec) return (measureSpec &  ~MODE_MASK);
  }
```

MeasureSpec 中声明了三种测量模式

- **UNSPECIFIED ：未指定模式，也可以称为无限制模式。当你收到此模式时，表明父视图不关心你的尺寸大小，你可以随意设置自己的尺寸信息。比如当你的父视图是可以纵向滚动的 ScrollView ，那子视图的高度大小对于父视图来说没有意义。无论你多高（即使超出屏幕），都可以通过滑动屏幕来查看**
- **EXACTLY ：精确模式。当你收到此模式时，表示父视图希望你就这么大（不要小于或大于给定大小），通常在 xml 中指定大小或者设为 match_parent 时会收到 EXACTLY 模式**
- **AT_MOST ：最大模式。当你收到此模式时，表示你可以在父视图给定的范围内随意发挥，但最好不要超过父视图给你的大小，通常在 xml 中设为 wrap_content 时会收到 AT_MOST 模式**

如果只是

#### MeasureSpec是由什么决定的？

既然，当然是由父视图的业务属性决定的！

聊到了 MeasureSpec ，就不得不提，表中的结论是怎么来的呢？

我们来看一段代码


#### View的测量过程

#### ViewGroup的测量过程

#### 聊聊onMeasure()多次执行的原因

##### 从ViewRootImpl 入口

##### ViewGroup 个人行为

除了 ViewRootImpl 入口处会发起多次调用外，各个 ViewGroup 自身的业务逻辑同样会导致

ViewGroup没有默认实现onMeasure()方法，这是因为不同的测量策略会产生不同的结果

拿 LinearLayout 来说，在高度设置为 wrap_content 的情况下

如果是横向布局，LinearLayout 的高度应该为最大子View的高度

如果是纵向布局，LinearLayout 的高度应该为所有子View的高度总和

#### 小结

为什么ViewGroup没有默认实现？

因为 ViewGroup 容器，它的职责是结合容器内的子View计算自己的高度，这是由ViewGroup的不同的属性决定的

这是由业务决定的

这张图的结论是怎么来的？

在测量阶段，View 和 ViewGroup 的侧重点完全不同

对于 View 来说，onMeasure() 是通知，我可以在父视图的规则下进行测量，也可以肆意妄为


### 二、布局阶段

### 三、绘制阶段

### 参考资料

- [Android measure过程 - 吴迪](https://www.viseator.com/2017/03/10/android_view_onMeasure/)
