
Android图形系统（四）应用篇：描述下自定义View/ViewGroup的过程？

相信大多数的 Android 开发者都有过自定义 View 的经历，以我个人的工程经验来看，自定义 View 大体可以分为三种：改装、组合和自定义

改装指的是继承自某个控件，在原有功能的基础上进行增删改，比如：基于 `ViewPager` 打造一个无限循环的轮播图控件

组合指的是将2个以上的控件组合成一个控件，比如：基于 `RelativeLayout` + 多个 `EditText` 组合成一个密码输入控件

自定义指的是当 Android 官方控件不足以满足业务需求（比如统计图表中的饼状图/折线图）时，继承 View / ViewGroup 类，重写 **`onMeasure()`**、**`onLayout()`**、**`onDraw()`** 三大方法，从 0 到 1 创造一个新的控件

本篇是图形系列的第四篇文章，今天我们来聊聊自定义 View/ViewGroup 各个阶段发生的事情

我是概述图

# 一、测量阶段

在 Android 系统中，一个 View 的绘制显示要经过测量、布局和绘制这三个步骤

测量是为了计算每一个 View 需要的大小，View 和 ViewGroup 都需要重写 onMeasure() 方法来确定自己的尺寸

布局是为了计算每一个 View 的位置，通常只需要 ViewGroup 重写 onLayout() 方法，根据容器的属性合理的摆放子 View

绘制是最终绘制的阶段，所有的绘图操作都在 draw 阶段得到执行，通常需要 View 重写 onDraw() 方法

我们先来看第一步的测量流程

![android_graphic_v4_measure_process](/Users/bob/Desktop/Bob/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/v4/android_graphic_v4_measure_process.GIF)

*图片来源：自己画的*

在[上一篇](https://juejin.cn/post/7132777622487957517#heading-39)文章中，我们介绍了测量流程是由 ViewRootImpl 类中的 **`performTraversals()`** 方法发起的，经过层层转发，最终会调用到每个 View 的 **`onMeasure()`** 方法

View 真正的尺寸信息就是在 **`onMeasure()`** 方法中被确定的

那么 View 是根据什么来确定自己应当具有多大的尺寸呢？不可能让子 View 自由地决定自己的大小吧，父 View 必然需要向子 View 传递信息来帮助子 View 来确定尺寸

## 什么是 MeasureSpec

查看 **`measure()`** 的方法签名：

```java
void measure(int widthMeasureSpec, int heightMeasureSpec)
```

**`widthMeasureSpec`** 和 **`heightMeasureSpec`** 就是帮助子 View 确定大小的参数

它们的类型是 **int**，内部以高两位来存储测量的模式，低三十位为测量的大小，计算中使用了位运算来提高并优化效率

当然，我们不必使用位运算来获得对应的数值，**MeasureSpec** 类为我们提供了对应的方法

```java
  class MeasureSpec {
    int UNSPECIFIED;//未指定，不限制大小
    int EXACTLY;//精确模式
    int AT_MOST;//最大模式
   
    int getMode(int measureSpec) return (measureSpec & MODE_MASK);
    int getSize(int measureSpec) return (measureSpec &  ~MODE_MASK);
  }
```

MeasureSpec 声明了三种测量模式：

- **`UNSPECIFIED` ：未指定模式，也可以称为无限制模式。当你收到此模式时，表明父视图不关心你的尺寸大小，你可以随意设置自己的尺寸信息。什么情况下可能收到 `UNSPECIFIED` 呢？比如当你的父视图是可以纵向滚动的 `ScrollView` ，那子视图的高度大小对于父视图来说没有意义。无论你多高（即使超出屏幕），都可以通过滑动屏幕来查看（同理，如果是横向滚动那么宽度就没有意义）**
- **`EXACTLY `：精确模式。当你收到此模式时，表示父视图希望你就这么大（不要小于或大于给定大小），通常在 xml 中指定大小或者设为 `match_parent` 时会收到 `EXACTLY` 模式**
- **`AT_MOST `：最大模式。当你收到此模式时，表示你可以在父视图给定的范围内随意发挥，但最好不要超过父视图给你的大小，通常在 xml 中设为 `wrap_content` 时会收到 `AT_MOST` 模式**

看到这里或许已经有同学发现了，View 的测量模式好像和我们在 xml  中指定的宽高有关

- View 设置为` match_parent` 或固定值时，将会收到 `EXACTLY` 精确模式

- View 设置为 `wrap_content` 时，将会收到 `AT_MOST` 最大模式

事实真的如此吗？我们接着往下看

## MeasureSpec 是由什么决定的

关于 View 测量模式的创建规则，网上有张图流传的比较广

![android_graphic_v4_measure_spec_book](/Users/bob/Desktop/Bob/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/v4/android_graphic_v4_measure_spec_book.JPG)

*图片来源：自己拍的*

上图是任主席在2015年出版的《Android开发艺术探索》第四章的拍摄图，图中的表格非常详细了展示了一个 **“普通 View”** 的 MeasureSpec 的创建规则

如果你的自定义 View 的父视图是 FrameLayout 、LinearLayout 、RelativeLayout，那么这些规则适用于你

但如果你的父视图是 ScrollView、RecyclerView 等支持滚动的 ViewGroup，那么你收到的测量模式多半和上图描述的不符

接下来通过几组测试来验证一下我们的想法

我们用 FrameLayout 和 ScrollView 作为测试的父视图，TextView 作为测试的子 View

然后对父视图和子 View 分别设置不同的 LayoutParams 属性，观察它们在不同宽高模式下的表现：

![android_graphic_v4_measure_spec_test](/Users/bob/Desktop/Bob/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/v4/android_graphic_v4_measure_spec_test.png)

*图片来源：自己截图 测试的代码可以点击[这里](https://github.com/yibaoshan/Blackboard/blob/master/Blog/src/main/java/com/android/blog/android_view/demo/v4/MeasureTestActivity.java)查看，测试的详细数据可以点击[这里](https://www.yuque.com/docs/share/c2296195-b016-49d3-a7d4-30775b2c0f3f?# 《Android Graphics NO.4  MeasureSpec》)查看*

从测试的结果来看，我们可以得出几个比较有趣的结论：

- FrameLayout + TextView 的组合，子 View 得到的测量模式和预期表现相同
- FrameLayout 为 wrap_content 时（第二轮第1组），如果子 View 设置为 match_parent，那子 View 将会收到两次测量，最终的 MeasureSpec 固定为 EXACTLY
- 在 FrameLayout 中，如果子 View 宽高设置为固定值，且期望大小超过了父视图（第三轮第3组测试）时，父视图返回的宽高是我们设定的值
- 接上一条，如果子 View 的设置的大小超过屏幕实际宽高系统会如何处理呢？答案是：父视图返回的依旧是我们设定的值，即使它非常不合理
- 在 ScrollView 中，无论子 View 是 math_parent 、wrap_content 还是固定值，在高度上，ScrollView 永远会把字 View 设置为 UNSPECIFIED，高度值为父视图的高度
- 在 ScrollView 中，不管子 View 申请的高度是大于父视图还是小于父视图的高度，ScrollView 统统指定为自身高度，Mode 为 UNSPECIFIED

我们发现一旦使用 ScrollView 作为父视图，那么 “match_parent / wrap_conent 对应 EXACTLY/ AT_MOST” 这套规则将不再适用

为什么 ScrollView 如此特殊呢？

原因也很简单，因为它不是调用 getChildMeasureSpec() 方法为子 View 生成的测量结果，而是直接调用 makeSafeMeasureSpec() 方法为子 View 指定了测量模式与大小

```java
void measureChild() {
    ViewGroup.LayoutParams lp = child.getLayoutParams();
  
    int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,lp.width);
    int childHeightMeasureSpec = MeasureSpec.makeSafeMeasureSpec(parentSize,MeasureSpec.UNSPECIFIED);//高度指定为parentSize

    child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
}
```

到这里，我们就可以来尝试回答小标题的问题：MeasureSpec 是由什么决定的？

大多数情况下，父视图通过调用 getChildMeasureSpec() 方法为子 View 生成测量结果，那么子 View 的MeasureSpec 是由父视图的 SpecMode 和 子 View 自身的 LayoutParams 共同决定的

其他情况下，父视图直接调用 makeSafeMeasureSpec() 方法为子 View 生成测量结果的（比如示例中的 ScrollView），那么子 View 的MeasureSpec 是由父视图的业务属性决定的！

## View 的测量过程

关于 MeasureSpec 的介绍暂时告一段落，接下来进入 View 的测量过程

 measure 过程是自顶向下的过程，我们可以把 View 当做一棵以 DecorView 作为 root 节点的多叉树，测量工作会以深度优先的顺序遍历整棵树，所以，子 View 的测量工作总是会在 ViewGroup 的测量工作之前发生

对于一个正经的 View 来说，在 onMeasure() 阶段要完成两件事

- 一是根据父视图传递的 MeasureSpec，合理的计算自己所需的尺寸大小
- 二是在确定了尺寸后，调用 setMeasuredDimension() 方法进行保存

当子 View 的测量工作完成以后，父视图就可以通过调用 getMeasureWidth() / getMeasureHeight() 方法来获取子 View 的宽高

如果自定义 View 没有重写 onMeasure() 方法，Android 系统为我们有默认实现

```java
void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
  	int width = getDefaultSize(widthMeasureSpec);
  	int height = getDefaultSize(heightMeasureSpec);
    setMeasuredDimension(width,height);//保存宽高值
}

int getDefaultSize(int size, int measureSpec) {
    int result = getSuggestedMinimumWidth();//默认为android:minHeight属性的值或者View背景图片的大小值
    int specMode = MeasureSpec.getMode(measureSpec);
    int specSize = MeasureSpec.getSize(measureSpec);
    switch (specMode) {
        case MeasureSpec.UNSPECIFIED://注意，如果接收到未指定模式，一定要为View设置背景或者设置最小高度
            result = size;
            break;
        case MeasureSpec.AT_MOST:
        case MeasureSpec.EXACTLY:
            result = specSize;
            break;
    }
    return result;
}
```

getDefaultSize() 方法会根据 View 自身属性来返回尺寸信息，从代码逻辑来看，正常情况下，在 xml 指定 View 为 match_parent 或者 wrap_content ，得到的效果是一样的

另外，如果你的父视图把你的 MeasureSpec 指定为 UNSPECIFIED，并且你的自定义 View 没有设置最小高度，那么在默认模式中你将无法在屏幕上看到它，因为它的高度为 0 ！

因此，建议大家在自定义 View 时最好重写 onMeasure() 方法，最起码要处理父视图可能为 ScrollView 的情况

## ViewGroup 的测量过程

当一个 ViewGroup 中的所有子 View 全部测量完以后，接下来就该轮到 ViewGroup 为自己计算高度了

Android 并没有为 ViewGroup 的 onMeasure() 方法提供默认实现，这是因为不同的容器的业务逻辑都不一样，即使同一个容器，也可能因为适用场景的差别而使用不同的测量策略

拿 LinearLayout 来举例，在高度设置为 wrap_content 的情况下

- 如果是横向布局，LinearLayout 的高度应该是最大子 View 的高度

- 如果是纵向布局，LinearLayout 的高度应该为所有子View的高度总和

ViewGroup 自身的宽高是由业务决定的，但是，容器内的子 View 的测量方式大多数情况下是通用的，所以 Android 为 ViewGroup 准备了几个测量子 View 的方法

```java
/frameworks/base/core/java/android/view/ViewGroup.java
class ViewGroup extends View {

  	//测量子视图
    void measureChild(View child, int parentWidthMeasureSpec,int parentHeightMeasureSpec) {
        LayoutParams lp = child.getLayoutParams();
        int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec, lp.width);
        int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec,lp.height);
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

  	//测量子视图并计算其Margin
    void measureChildWithMargins(View child,int parentWidthMeasureSpec,int parentHeightMeasureSpec) {
        MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
        int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,lp.width);
        int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec, lp.height);
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

  	//获取子View的MeasureSpec，对于ViewGroup来说，这是非常重要的一个方法！！！
    int getChildMeasureSpec(int spec, int padding, int childDimension) {
        ...
    }

}
```

- measureChild() 中方法调用了 getChildMeasureSpec() 方法，根据父视图的 MeasureSpec 和子视图的 LayoutParams 来为子视图生成 MeasureSpec ，生成成功通知子视图执行 measure() 方法
- measureChildWithMargins() 方法中同样调用了 getChildMeasureSpec() 来为子视图生成 MeasureSpec ,生成成功同志子视图执行 measure() 方法
- getChildMeasureSpec() 方法内部逻辑是根据父视图的 MeasureSpec 和 子视图的 LayoutParams 来生成 子视图的 MeasureSpec，这个方法对于 ViewGroup 来说极为重要！

measureChild() 和 measureChildWithMargins() 都调用了 getChildMeasureSpec() 方法来为子 View 生成 MeasureSpec，两个方法唯一区别是后者使用了 MarginLayoutParams，在计算子视图可用空间时会考虑到 Margin 的部分

什么是 MarginLayoutParams 呢？

### 初识 LayoutParams

在 ViewGroup 中有两个静态类

我们平时开发时设置 View 的宽高

```java
//描述View/ViewGroup的宽高值
class LayoutParams {

    int MATCH_PARENT = -1;
    int WRAP_CONTENT = -2;

    //视图的宽高属性，小于0表示为WRAP_CONTENT/MATCH_PARENT，大于0表示为具体的尺寸
    int width;
    int height;
}

//在宽高值的基础上增加了上下左右间距值，凡是支持设置margin的容器都是继承自MarginLayoutParams
class MarginLayoutParams extends ViewGroup.LayoutParams {

    leftMargin;
    topMargin;
    rightMargin;
    bottomMargin;
    ...
}
```



关于 LayoutParams 后面布局阶段的时会再次介绍，它是 ViewGroup 能够正确摆放子视图的重要依据

当 ViewGroup 测量工作结束，会开始执行 ViewGroup 的父视图的测量流程，直到最顶部的 DecorView measure 方法执行结束，该视图所依附的 Window 的大小才可以确定下来

## 聊聊 onMeasure() 多次执行的原因

View 和 ViewGroup 的测量过程介绍完了，接下来我们来聊聊绘制阶段老生常谈的话题，为什么onMeasure方法会执行多次

首先设计了

### ViewRootImpl 调用次数

我们都知道onMeasure方法的源头是由viewrootimpl的preform发起的，点开viewrootimpl类发现，pre方法在里面有多处调用，我们怎么才能知道当前的onMeasure是哪里调用的呢？

很简单，我们再来设计一个测试



通过跟踪方法调用链可以发现，

### ViewGroup 个人行为

除了 ViewRootImpl 入口处会发起多次调用外，各个 ViewGroup 自身的业务逻辑同样会导致 View 的 onMeasure() 被调用多次

这部分内容我们可以去看缘佬在[ 玩 Android 每日一问 onMeasure()多次执行原因？](https://www.wanandroid.com/wenda/show/17920)这个问题下的回答

照搬两句缘佬的回答

ViewGroup没有默认实现onMeasure()方法，

## 小结

为什么ViewGroup没有默认实现？

因为 ViewGroup 容器，它的职责是结合容器内的子View计算自己的高度，这是由ViewGroup的不同的属性决定的

这是由业务决定的

这张图的结论是怎么来的？

在测量阶段，View 和 ViewGroup 的侧重点完全不同

对于 View 来说，onMeasure() 是通知，我可以在父视图的规则下进行测量，也可以肆意妄为

# 二、布局阶段

如果说测量阶段的学习难度是100分的话，那布局阶段可以直接降一个数量级，降到10分（我觉得10分都高了！）

最难的测量任务在上一阶段已经结束了，

布局阶段，这就大大减少了，

## 再谈 LayoutParams

## ViewGroup 的布局过程

子视图怎么摆放完全由 ViewGroup 的业务决定，接下来我们一起手写一个斜着的线性布局，来感受一下布局阶段到底在布局些什么？

## 手写一个斜着的线性布局

先看效果

![android_graphic_v4_layout_slant](/Users/bob/Desktop/Bob/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/v4/android_graphic_v4_layout_slant.GIF)

*图片来源：自己录的*

可以想象成一个纵向居中的 LinearLayout 内部有几个子 View ，点击“启用倾斜属性”后，这个 ViewGroup 会更改这几个子视图的左间距

# 三、绘制阶段

绘制在三部曲中的地位有些特殊，从

# 四、参考资料

- [Android measure过程 - 吴迪](https://www.viseator.com/2017/03/10/android_view_onMeasure/)
