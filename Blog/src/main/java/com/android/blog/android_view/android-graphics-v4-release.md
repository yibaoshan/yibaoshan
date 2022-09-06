
Android图形系统（四）应用篇：描述下自定义View/ViewGroup的过程？

相信大多数的 Android 开发者都有过开发自定义 View 的经历，以我个人的工程经验来看，自定义 View 大体可以分为三种：改装、组合和自定义

**改装**指的是继承自某个控件，在原有功能的基础上进行增删改，比如：基于 **`ViewPager`** 打造一个无限循环的轮播图控件

**组合**指的是将2个以上的控件组合成一个控件，比如：基于 **`RelativeLayout`** + 多个 **`EditText`** 组合成一个密码输入控件

**自定义**指的是当 Android 官方控件不足以满足业务需求（比如统计图表中的饼状图/折线图）时，继承 View / ViewGroup 类，重写 **`onMeasure()`**、**`onLayout()`**、**`onDraw()`** 三大方法，从 0 到 1 创造一个新的控件

本篇是图形系列的第四篇文章，今天我们来聊聊在自定义 View / ViewGroup 过程中各个阶段发生的事情

![android_graphic_v4_overview](/Users/bob/Desktop/Bob/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/v4/android_graphic_v4_overview.png)



# 一、测量阶段

在 Android 系统中，一个 View 的绘制显示要经过测量、布局和绘制这三个步骤

**测量是为了计算每一个 View 需要的大小，View 和 ViewGroup 都需要重写 `onMeasure()` 方法来确定自己的尺寸**

**布局是为了计算每一个 View 的位置，通常只需要 ViewGroup 重写 `onLayout()` 方法，根据容器的属性合理的摆放子 View**

**绘制是最终绘制的阶段，所有的绘图操作都在 draw 阶段得到执行，通常需要 View 重写 `onDraw()` 方法**

我们先来看第一步的测量流程

![android_graphic_v4_measure_process](/Users/bob/Desktop/Bob/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/v4/android_graphic_v4_measure_process.GIF)

*图片来源：自己画的*

在[上一篇](https://juejin.cn/post/7132777622487957517#heading-39)文章中，我们介绍了测量流程是由 ViewRootImpl 类中的 **`performTraversals()`** 方法发起的，经过层层转发，最终会调用到每个 View 的 **`onMeasure()`** 方法

**View 真正的尺寸信息就是在 `onMeasure()` 方法中被确定的**

那么 View 是根据什么来确定自己应当具有多大的尺寸呢？不可能让子 View 自由地决定自己的大小吧，父 View 必然需要向子 View 传递信息来帮助子 View 来确定尺寸

## 什么是 MeasureSpec

查看 **`measure()`** 的方法签名：

```java
void measure(int widthMeasureSpec, int heightMeasureSpec)
```

**`widthMeasureSpec`** 和 **`heightMeasureSpec`** 就是帮助子 View 确定大小的参数

它们的类型是 **int**，内部以高两位来存储测量的模式，低三十位为测量的大小，计算中使用了位运算来提高并优化效率

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

MeasureSpec 声明了三种测量模式：

- **`UNSPECIFIED` ：未指定模式，也可以称为无限制模式。当你收到此模式时，表明父视图不关心你的尺寸大小，你可以随意设置自己的尺寸信息。什么情况下可能收到 `UNSPECIFIED` 呢？比如当你的父视图是可以纵向滚动的 `ScrollView` ，那子视图的高度大小对于父视图来说没有意义。无论你多高（即使超出屏幕），都可以通过滑动屏幕来查看（同理，如果是横向滚动那么宽度就没有意义）**
- **`EXACTLY `：精确模式。当你收到此模式时，表示父视图希望你就这么大（不要小于或大于给定的大小），通常在 xml 中指定大小或者设为 `match_parent` 时会收到 `EXACTLY` 模式**
- **`AT_MOST `：最大模式。当你收到此模式时，表示你可以在父视图给定的范围内随意发挥，但最好不要超过父视图给你的大小，通常在 xml 中设为 `wrap_content` 时会收到 `AT_MOST` 模式**

看到这里或许已经有同学发现了，View 的测量模式好像和我们在 xml  中指定的宽高有关：

> 1. **View 设置为` match_parent` 或固定值时，将会收到 `EXACTLY` 精确模式**
> 2. **View 设置为 `wrap_content` 时，将会收到 `AT_MOST` 最大模式**

事实真的如此吗？我们接着往下看

## MeasureSpec 是由什么决定的

关于 View 测量模式的创建规则，网上有张图流传的比较广

![android_graphic_v4_measure_spec_book](/Users/bob/Desktop/Bob/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/v4/android_graphic_v4_measure_spec_book.JPG)

*图片来源：自己拍的*

上图是任主席在2015年出版的**《Android开发艺术探索》**第四章的拍摄图，图中的表格非常详细了展示了一个 **“普通 View”** 的 MeasureSpec 的创建规则

**如果你的自定义 View 的父视图是 FrameLayout 、LinearLayout 、RelativeLayout，恭喜你，这些规则适用于你**

**但如果你的父视图是 ScrollView、RecyclerView 等支持滚动的 ViewGroup，那么你收到的测量模式多半和上图描述的不符**

接下来通过几组测试来验证一下我们的想法

**我们用 FrameLayout 和 ScrollView 作为测试的父视图，TextView 作为测试的子 View**

**然后对父视图和子 View 分别设置不同的 LayoutParams 属性，观察它们在不同宽高模式下的表现：**

![android_graphic_v4_measure_spec_test](/Users/bob/Desktop/Bob/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/v4/android_graphic_v4_measure_spec_test.png)

*图片来源：自己截图 测试的代码可以点击[这里](https://github.com/yibaoshan/Blackboard/blob/master/Blog/src/main/java/com/android/blog/android_view/demo/v4/MeasureTestActivity.java)查看，测试的详细数据可以点击[这里](https://www.yuque.com/docs/share/c2296195-b016-49d3-a7d4-30775b2c0f3f)查看*

从测试的结果来看，我们可以得出几个比较有趣的结论：

- **FrameLayout + TextView 的组合，子 View 得到的测量模式和预期表现相同**
- **FrameLayout 为 `wrap_content` 时（*第二轮第1组*），如果子 View 设置为 `match_parent`，那子 View 将会收到两次测量，最终的 MeasureSpec 固定为 `EXACTLY`**
- **在 FrameLayout 中，如果子 View 宽高设置为固定值，且期望大小超过了父视图（*第三轮第3组测试*）时，父视图返回的宽高是我们设定的值。同理，如果子 View 的设置的大小超过屏幕实际宽高系统，父视图返回的依旧是设定的值，即使它非常不合理**
- **在 ScrollView 中，无论子 View 是 `math_parent` 、`wrap_content` 还是固定值，或者说不管子 View 申请的高度是大于父视图还是小于父视图的高度，ScrollView 统统指定为自身高度，Mode 为 `UNSPECIFIED`**

我们发现一旦使用 ScrollView 作为父视图，那么 **“`match_parent` / `wrap_conent` 对应 `EXACTLY`/ `AT_MOST`”** 这套规则将不再适用

**为什么 ScrollView 会如此特殊呢？**

原因也很简单，因为它不是调用 **`getChildMeasureSpec()`** 方法为子 View 生成的测量结果，而是直接调用 **`makeSafeMeasureSpec()`** 方法为子 View 指定了测量模式与大小

```java
void measureChild() {
    ViewGroup.LayoutParams lp = child.getLayoutParams();
  
    int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,lp.width);
    int childHeightMeasureSpec = MeasureSpec.makeSafeMeasureSpec(parentSize,MeasureSpec.UNSPECIFIED);//高度指定为parentSize

    child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
}
```

到这里，我们就可以来尝试回答小标题的问题：MeasureSpec 是由什么决定的？

**大多数情况下，父视图通过调用 `getChildMeasureSpec()` 方法为子 View 生成测量结果，那么子 View 的MeasureSpec 是由父视图的 SpecMode 和 子 View 自身的 LayoutParams 共同决定的**

**如果父视图是调用 `makeSafeMeasureSpec()` 方法为子 View 指定测量结果时（*比如示例中的 ScrollView*），子 View 的MeasureSpec 是由父视图的业务属性决定的！**

关于 MeasureSpec 的介绍暂时告一段落，接下来进入本小节正题：View 的测量

## View 的测量过程

 View 的测量过程是自顶向下的过程，我们可以把 View 当做一棵以 DecorView 作为 **`root`** 节点的多叉树，测量事件会以深度优先的顺序遍历整棵树

View 的测量工作是在 **`onMeasure()`** 方法中进行的，如果自定义 View 没有重写 **`onMeasure()`** 方法，别担心，Android 系统为我们提供了默认实现

```java
/frameworks/base/core/java/android/view/View.java
class View {

    void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
      	int width = getDefaultSize(widthMeasureSpec);
      	int height = getDefaultSize(heightMeasureSpec);
        setMeasuredDimension(width,height);//保存宽高值
    }
    
    int getDefaultSize(int size, int measureSpec) {
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
}
```

在默认实现的 **`onMeasure()`** 方法中，会先调用 **`getDefaultSize()`** 方法为自己生成宽高信息，接着会调用 **`setMeasuredDimension()`** 方法将刚刚获取的宽高值保存起来

重点在于 **`getDefaultSize()`**方法：

> 1. **如果你的 MeasureSpec 是 `AT_MOST` 或者 `EXACTLY`，那么你得到的 `size` 和父视图相同**
> 2. **如果你的 MeasureSpec 是 `UNSPECIFIED`，那么你的尺寸将会取背景尺寸或者最小宽高中最大的那个值**

从 **`getDefaultSize()`** 的代码逻辑来看，如果你的 MeasureSpec 被指定为 **`UNSPECIFIED`**，并且你的自定义 View 没有设置最小宽高，那么你将无法在屏幕上看到它，因为它的高度或者宽度为 0 ！

因此，建议大家在自定义 View 时最好重写 **`onMeasure()`** 方法，最起码要处理MeasureSpec 为 **`UNSPECIFIED`** 的情况

总结一下 View 在测量流程完成的事情：

1. **根据父视图传递的 MeasureSpec，合理的计算自己所需的尺寸大小**
2. **在确定了自身尺寸后，调用 `setMeasuredDimension()` 方法保存尺寸信息**

## ViewGroup 的测量过程

当 ViewGroup 中的所有子 View 全部测量完以后，接下来就该轮到 ViewGroup 为自己计算高度了

Android 并没有为 ViewGroup 的 **`onMeasure()`** 方法提供默认实现，这是因为不同的容器的业务逻辑都不一样，即使同一个容器，也可能因为适用场景的差别而使用不同的测量策略

拿 LinearLayout 来举例，在高度设置为 **`wrap_content`** 的情况下：

- **如果是横向布局，LinearLayout 的高度应该是最大子 View 的高度**

- **如果是纵向布局，LinearLayout 的高度应该为所有子View的高度总和**

虽然 ViewGroup 自身的宽高是由业务决定的，但是，容器内的子 View 的测量方式大多数情况下是通用的，所以 Android 为 ViewGroup 准备了几个测量子 View 的方法，一起来看看

```java
/frameworks/base/core/java/android/view/ViewGroup.java
class ViewGroup extends View {

  	//测量子视图
    void measureChild() {
        LayoutParams lp = child.getLayoutParams();
        int childWidthMeasureSpec = getChildMeasureSpec();
        int childHeightMeasureSpec = getChildMeasureSpec();
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

  	//测量子视图并计算其Margin
    void measureChildWithMargins() {
        MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
        int childWidthMeasureSpec = getChildMeasureSpec();
        int childHeightMeasureSpec = getChildMeasureSpec();
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

  	//获取子View的MeasureSpec，对于ViewGroup来说，这是非常重要的一个方法！！！
    int getChildMeasureSpec(int spec, int padding, int childDimension) {
        ...
    }

}
```

View 类中一共提供了3个测量子 View 的方法：

> 1. **`measureChild()`：调用了 `getChildMeasureSpec()` 方法，根据父视图的 MeasureSpec 和子视图的 LayoutParams 来为子视图生成 MeasureSpec ，成功后通知子视图执行 `measure()` 方法**
> 2. **`measureChildWithMargins() `：调用了 `getChildMeasureSpec()` 来为子视图生成 MeasureSpec ,生成成功后通知子视图执行 `measure()` 方法**
> 3. **`getChildMeasureSpec()`：根据父视图的 MeasureSpec 和 子视图的 LayoutParams 属性来生成 子视图的 MeasureSpec**

**`measureChild()`** 和 **`measureChildWithMargins()`** 都调用了 **`getChildMeasureSpec()`** 方法来为子 View 生成 MeasureSpec，两个方法唯一区别是后者使用了 MarginLayoutParams，在计算子视图可用空间时会考虑到 Margin 的部分

**什么是 MarginLayoutParams 呢？**

### 1、初识 LayoutParams

View 有 **`top`** 、**`bottom`** 、**`left`** 、**`right`** 和 **`padding`** 这几个属性，记录的是这个 View 在屏幕坐标系中的绝对位置，但它这几个属性只有在 **`layout `** 阶段以后才会有具体的值

**在没有确定 View 的绝对位置之前，Android 用 LayoutParams 来描述一个 View / ViewGroup 的宽高值**

```java
//描述View/ViewGroup的宽高值
class LayoutParams {

    int MATCH_PARENT = -1;
    int WRAP_CONTENT = -2;

    //视图的宽高属性，小于0表示为WRAP_CONTENT/MATCH_PARENT，大于0表示为具体的尺寸
    int width;
    int height;
}
```

LayoutParams 使用 **`width`** 和 **`height`** 属性来描述宽高，如果小于0表示为 **`WRAP_CONTENT`** / **`MATCH_PARENT`**，大于0则表示为具体的尺寸

ViewGroup 中还有一个叫 MarginLayoutParams 的静态内部类，它继承自 LayoutParams 并在其基础上增加了上下左右间距值

我们日常使用的 ViewGroup 中凡是支持设置 **`margin`** 属性的都是继承自 MarginLayoutParams，如果你想要在代码中动态的修改 View 的 **`margin`** 属性，记得强转为 MarginLayoutParams 再进行操作

```java
//在宽高值的基础上增加了上下左右间距值，凡是支持设置margin的容器都是继承自MarginLayoutParams
class MarginLayoutParams extends ViewGroup.LayoutParams {

    leftMargin;
    topMargin;
    rightMargin;
    bottomMargin;
    ...
}
```

MarginLayoutParams 虽然是 View 的属性之一，但它描述的间距并不算在 View 的可用空间，在计算视图大小时，**`margin`** 是算到父视图的可用空间里，只有 **`padding`** 才是算到自己的可用空间

### 2、生成 ChildView MeasureSpec

关于 LayoutParams 暂时先告一段落，在后面 **`layout`** 阶段会再次介绍，它是 ViewGroup 能够正确摆放子视图的重要依据

**接下来我们开始 ViewGroup 最核心的逻辑，如何为子 View 生成 MeasureSpec ？**

前面说了 Android 虽然没有为 ViewGroup 提供 **`onMeasure()`** 方法的默认实现，但 Android 为 ViewGroup 准备了几个测量子 View 的方法，其中最重要的当属 **`getChildMeasureSpec()`** 方法

```java
/frameworks/base/core/java/android/view/ViewGroup.java
class ViewGroup extends View {
  
    int getChildMeasureSpec(int spec, int padding, int childDimension) {
        int specMode = MeasureSpec.getMode(spec);//父视图的 SpecMode
        int specSize = MeasureSpec.getSize(spec);//父视图的大小
        int size = Math.max(0, specSize - padding);//子视图可用空间大小为：父视图大小 - 父视图 padding - 子视图 margin
    
        int resultSize = 0;
        int resultMode = 0;
    
        switch (specMode) {
            case MeasureSpec.EXACTLY: // 父视图为 EXACTLY 时
                if (childDimension >= 0)
                    resultSize = childDimension;// 子视图设置的大小为固定值时，听子视图的，我不管你，爱多大就多大，模式也给你精确模式 EXACTLY
                    resultMode = MeasureSpec.EXACTLY;/
                if (childDimension == LayoutParams.MATCH_PARENT) {
                    resultSize = size;// 子视图为 MATCH_PARENT ，把你的大小设置和父视图相同，值固定了，模式给你精确模式 EXACTLY
                    resultMode = MeasureSpec.EXACTLY;
                if (childDimension == LayoutParams.WRAP_CONTENT) {
                    resultSize = size;// 子视图为 WRAP_CONTENT，爸爸也不知道你要多大，把我的全部都给你，模式指定为最大模式，你在这个范围内随意发挥
                    resultMode = MeasureSpec.AT_MOST;
            case MeasureSpec.AT_MOST: // 父视图为 AT_MOST 时
                if (childDimension >= 0) {
                    resultSize = childDimension;// 同上，子视图设置的大小为固定值时，听子视图的，我不管你，爱多大就多大，模式也给你精确模式 EXACTLY
                    resultMode = MeasureSpec.EXACTLY;
                if (childDimension == LayoutParams.MATCH_PARENT) {
                    resultSize = size;// 虽然子视图是 MATCH_PARENT 但父视图我自个是 AT_MOST，那怎么办？我多大你多大呗，将模式指定为最大模式，在我给你这个范围内随意发挥好了
                    resultMode = MeasureSpec.AT_MOST;
                if (childDimension == LayoutParams.WRAP_CONTENT) {
                    resultSize = size;// 同上，把父视图的全部都给你，模式指定为最大模式，你在这个范围内随意发挥
                    resultMode = MeasureSpec.AT_MOST;
        case MeasureSpec.UNSPECIFIED: // 父视图为 UNSPECIFIED 时
                if (childDimension >= 0) {
                    resultSize = childDimension;// 不管父视图我是什么模式，只要你设置了具体值，你爱多大多大
                    resultMode = MeasureSpec.EXACTLY;
                if (childDimension == LayoutParams.MATCH_PARENT) {
                    resultSize = sUseZeroUnspecifiedMeasureSpec ? 0 : size;// 子视图想和我一样大？ 满足你，模式给你 UNSPECIFIED，ps：sUseZeroUnspecifiedMeasureSpec 默认为false
                    resultMode = MeasureSpec.UNSPECIFIED;  //mode为
                if (childDimension == LayoutParams.WRAP_CONTENT) {
                    resultSize = sUseZeroUnspecifiedMeasureSpec ? 0 : size;// 你也不知道自己多大，那我就把自己的尺寸给你，模式也给你我的模式 UNSPECIFIED
                    resultMode = MeasureSpec.UNSPECIFIED;
        }
        return MeasureSpec.makeMeasureSpec(resultSize, resultMode);
    }
}
```

**`getChildMeasureSpec()`** 中的逻辑是根据父视图的 MeasureSpec 和 子 View 的 LayoutParams 来生成 MeasureSpec，规则如下：

> - **父视图为 `EXACTLY` 时**
>   - **子视图设置的大小为固定值，听子视图的，我不管你，爱多大就多大，模式也给你精确模式 `EXACTLY`**
>   - **子视图为 `MATCH_PARENT` ，把你的大小设置和父视图相同，值固定了，模式给你精确模式 `EXACTLY`**
>   - **子视图为 `WRAP_CONTENT`，爸爸也不知道你要多大，把我的全部都给你，模式指定为最大模式，你在这个范围内随意发挥**
> - **父视图为 `AT_MOS`T 时**
>   - **子视图设置的大小为固定值，听子视图的，我不管你，爱多大就多大，模式也给你精确模式 `EXACTLY`**
>   - **子视图为 `MATCH_PARENT`，虽然子视图是 `MATCH_PARENT` 但父视图我自个是 `AT_MOST`，那怎么办？我多大你多大呗，将模式指定为最大模式，在我给你这个范围内随意发挥好了**
>   - **子视图为 WRAP_CONTENT，把父视图的全部都给你，模式指定为最大模式，你在这个范围内随意发挥**
> - **父视图为 `UNSPECIFIED` 时**
>   - **子视图设置的大小为固定值，不管父视图我是什么模式，只要你设置了具体值，你爱多大多大**
>   - **子视图为 `MATCH_PARENT`，子视图想和我一样大？ 满足你，模式给你 `UNSPECIFIED`**
>   - **子视图为 `WRAP_CONTENT`，你也不知道自己多大，那我就把自己的尺寸给你，模式也给你我的模式 `UNSPECIFIED`**

我们将 **`getChildMeasureSpec()`**  方法中 ChildView MeasureSpec 的生成规则统计成表格的话就是**《Android开发艺术探索》**中表格的内容

![android_graphic_v4_measure_spec_book_excel](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/v4/android_graphic_v4_measure_spec_book_excel.jpg)

*图片来源：https://www.yuque.com/docs/share/c2296195-b016-49d3-a7d4-30775b2c0f3f*

子 View 的 MeasureSpec 生成以后，ViewGroup 便可以通知子 View 执行测量工作，当所有的子 View 全部完成测量工作以后，就轮到 ViewGroup 计算自身大小了

接下来我们通过一段示例代码来总结下 ViewGroup 的测量过程

```java
/frameworks/base/core/java/android/widget/LinearLayout.java
class LinearLayout extends ViewGroup {
  
  	int mTotalLength = 0;
  	
    void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxWidth =0;
        for (int i = 0; i < count; ++i) {
            measureChildWithMargins(child);//为每个子 View 创建 MeasureSpec并通知执行 measure
            maxWidth = Math.max(maxWidth,child.getWidth());// 更新 LinearLayout 的最大宽度
            mTotalLength += child.getHeight();// 获取每个View高度，累加结果 LinearLayout 的高度
        }
        setMeasuredDimension(maxWidth,mTotalLength);//设置自身尺寸
    }

    void measureChildWithMargins(View child) {
        int width = getChildMeasureSpec();
        int height = getChildMeasureSpec();
        child.measure(width, height);// 通知子 View 执行 measure
    }

    void getChildMeasureSpec();//为子 View 生成 MeasureSpec

}
```

示例代码演示的是一个纵向的 LinearLayout 测量过程：

> 1. **调用了 `measureChildWithMargins()` 方法为每个子 View 创建 MeasureSpec 并通知其执行 `measure`**
> 2. **子 View 测量完成后，获取每个子 View 的高度累加到成员变量 `mTotalLength` ，同时不断更新最大宽度的子 View，它的宽度就是 LinearLayout 将来的宽度**
> 3. **循环结束表示所有子 View 全都测量完成，调用 `setMeasuredDimension()` 方法保存自身尺寸信息**

**当一个 ViewGroup 的测量工作结束，会开始执行它的父视图的测量流程，直到最顶部的 DecorView 的 `measure` 方法执行结束，到那时，整个视图所依附的 Window 的大小才可以确定下来**

## onMeasure() 执行多次的原因

有过自定义 View / ViewGroup 开发经验的小伙伴肯定知道，View 的 **`onMeasure()`** 方法可能会存在被多次调用的情况

尤其在首次加载 Activity 时，每个 View 最少也会执行2次 **`onMeasure()`** 方法

这2次调用都发生在 ViewRootImpl 类的 **`performTraversals()`** 方法中，其他的几次调用有的也还是发生在 ViewRootImpl 中，有的则是 ViewGroup 的个人行为，我们接着往下看

### 1、创建 Surface 引起的二次调用

[上一篇](https://juejin.cn/post/7132777622487957517#heading-14)文章中我们知道了 ViewRootImpl 里面管理着一个 Surface 对象

**View 必须依托 Surface 对象才能够显示绘制，所以，在 View 执行测量工作之前，Surface 的大小必须先确定下来**

Surface 的大小什么时候确定的呢？我们来看 **`ViewRootImpl#performTraversals()`** 方法中的逻辑

```java
/frameworks/base/core/java/android/view/ViewRootImpl.java
class ViewRootImpl {
    
    void performTraversals() {
        if(layoutRequested){//APP发起requestLayout请求
            boolean 视图尺寸发生变化 = measureHierarchy();//① 执行日常测量工作
        }
        if(首次添加视图/视图尺寸发生变化等){
            relayoutWindow();//Window大小确定下来以后，去sf申请对应大小的surface
            performMeasure();//② 申请到surface后再次测量，此方法结束后，该window的大小将会被确定，除非window的尺寸发生改变，否则不会再次执行该方法
        }
    }
    
    boolean measureHierarchy(){//执行测量，并返回和缓存的窗口大小比，新的测量尺寸是否发生变化
      performMeasure();
    }
    
    void performMeasure() {
        mView.measure();
    }
}
```

在 **`performTraversals()`** 方法中可以看到，当我们发起 **`requestLayout`** 请求时，最终是由 **`measureHierarchy()`** 方法来执行测量工作

测量工作结束后，接着判断是不是首次添加视图或者测量得到的结果与缓存窗口尺寸不符

1. **如果是首次添加视图，说明 Surface 之前不存在，需要重新创建**
2. **如果调用 `measureHierarchy()` 方法得到的测量结果与缓存不符，说明 Window 的大小发生变化，需要重新申请Surface**

只要满足以上条件，ViewRootImpl 便会携带测量得到的宽高值去调用 **`relayoutWindow()`** 方法，请求 SF 进程创建一个符合尺寸要求的 Surface

等到 **`relayoutWindow()`** 方法返回，一个和 DecorView 大小相同的 Surface 便创建成功。

在创建 Surface 的过程中，一共执行了两次测量（代码中标记为1/2号）：

> 1. **第一次是在 `measureHierarchy()` 方法中调用了 `performMeasure()` 执行测量工作**
>
> 2. **第二次是在 `relayoutWindow()` 方法申请到 Surface 以后，再次调用 `performMeasure()` 发起的测量**

**这两次测量就是首次加载 Activity 时，View 都会执行2次 `onMeasure()` 方法的原因！**

### 2、Dialog 可能会引发多次调用

在了解创建 Surface 会导致二次调用 **`onMeasure()`** 方法的原因后，我们来看下面的这张表格：

![android_graphic_v4_measure_count_test](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/v4/android_graphic_v4_measure_count_test.jpg)

*图片来源：自己截图 测试代码点击[这里](https://github.com/yibaoshan/Blackboard/blob/master/Blog/src/main/java/com/android/blog/android_view/demo/v4/MeasureTestActivity.java)查看，详细数据点击[这里](https://www.yuque.com/docs/share/c2296195-b016-49d3-a7d4-30775b2c0f3f)查看*

这张表格的数据来自我自己做的一个小测试，测试的步骤很简单：**自定义一个 ViewGroup 继承自 FrameLayout，在不同的 Activity 主题中，记录 `onMeasure()` 方法的执行次数**

从测试的结果来看，普通的 Activity + FrameLayout 的组合，在首次加载 Activity 时 **`onMeasure()`** 方法被调用了2次，在我们手动请求 **`requestLayout()`** 以后 **`onMeasure()`** 方法执行了一次，测试结果符合我们的预期

**但是，当我们把 Activity 的主题为 Dialog 以后，`onMeasure()` 方法的调用次数竟然达到了6次！**

**再次修改测试条件，当把根视图的宽度改为和屏幕实际宽度相同以后，`onMeasure()` 方法的调用次数更是达到了惊人的12次！！**

为什么会发生这么多次的调用？跟踪 **`performMeasure()`** 方法的调用链可以发现，多出来的几次调用都是在 **`measureHierarchy()`** 方法内发起，那接下来我们就一起来看看 **`measureHierarchy()`** 方法里面都做了些什么

```java
/frameworks/base/core/java/android/view/ViewRootImpl.java
class ViewRootImpl {

    boolean measureHierarchy(int desiredWindowWidth, int desiredWindowHeight){
        //宽度为 WRAP_CONTENT，通常表示是 Dialog 或者是 Dialog 主题的 Activity
        if (width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            int baseSize = 读取系统预置的Dialog宽度;
            //既然是Dialog，Android不希望它充满屏幕，所以这里的宽度被设置为了预设宽度
            childWidthMeasureSpec = getRootMeasureSpec(baseSize, lp.width);
            childHeightMeasureSpec = getRootMeasureSpec(desiredWindowHeight, lp.height);//高度依旧为屏幕高度，你想要多高自己定
            //①进行首次测量
            performMeasure(childWidthMeasureSpec, childHeightMeasureSpec);
            //测量结束后，发现视图期望的宽度没有超过预设宽度，goodMeasure设为true，在后续流程中不再执行测量工作，MEASURED_STATE_TOO_SMALL这个flag表示测量尺寸小于视图想要的空间
            if ((host.getMeasuredWidthAndState() & View.MEASURED_STATE_TOO_SMALL) == 0) {
                goodMeasure = true;
            } else {
                //视图期望的宽度超过了预置宽度，比如我在xml写死"layout_width=10086px"，那么把baseSize改大一些再次测量试一试
                baseSize = (baseSize + desiredWindowWidth) / 2;
                //②再次执行测量
                performMeasure(childWidthMeasureSpec, childHeightMeasureSpec);
                //同样的，在测量执行结束后查看宽度是否满足预期，满足则将goodMeasure设为true，在后续流程中不再执行测量工作
                if ((host.getMeasuredWidthAndState() & View.MEASURED_STATE_TOO_SMALL) == 0) {
                    goodMeasure = true;
                }
            }
        }
        //方法能走到这有两种情况：
        //1. 该视图是普通的Activity，DecorView宽高不是 WRAP_CONTENT
        //2. 该视图是Dialog类型，且Dialog想要的宽度很大，系统预设的宽度不满足，再次扩容以后同样不满足，没办法，索性直接给它屏幕的实际宽高让它自己折腾去
        if (!goodMeasure) {
            //③这里的测量方法如果得到执行，要么是第一次调用，要么是第三次调用
            performMeasure(childWidthMeasureSpec,childHeightMeasureSpec);
        }
        return windowSizeMayChange;//返回和缓存的window相比，测量下来的视图大小是否发生变化
    }
}
```

在 **`measureHierarchy()`** 方法中，首先会判断 DecorView 的宽度是不是 **`WRAP_CONTENT`**，是的话表示该视图可能是个 Dialog 或者是 Dialog 主题的 Activity

如果是 Dialog 类型的视图，Android 不希望它充满屏幕，因此，视图的宽度将会从**`屏幕实际宽度`**更改为 Dialog 的**`系统预设宽度`**，然后执行视图的测量工作

**第一次测量完成后，如果该视图期望的宽度比`系统预设宽度`大，那就用`系统预设宽度` + `屏幕实际宽度` / 2算出来的值当做新的宽度要求，再次执行测量工作（*比如屏幕实际宽度为100，系统预设宽度为60，新的宽度为(100 + 60) / 2 = 80*）**

**第二次测量完成后，如果扩容后的宽度大小仍然不满足该视图期望值，Android 系统将不再尝试再次测量，等待第三次的最终测量**

**第三次测量执行之前会先判断 `goodMeasure` 变量的值，只有当 `goodMeasure` 为 false 的情况下才会执行测量，什么时候值会为 false 呢？两种情况（看代码注释啊喂）：**

> 1. **视图是普通的 Activity，DecorView 宽不为 `WRAP_CONTENT` 的情况，那么标记为3号的 `performMeasure()` 方法将会被执行（*第一次调用*）**
>2. **视图是 Dialog 类型，且 Dialog 想要的宽度很大，`系统预设宽度`不满足，再次扩容以后同样不满足，那么标记为3号的 `performMeasure()` 方法将会被执行（*第三次调用*）**

在测试中我发现如果把 Activity 设置为 Dialog 主题后，每次 **`requestLayout()`** 都会执行3次 **`performTraversals()`** 方法，导致同一个调用链也会重复3遍，发生这种情况的原因我暂时不清楚，有了解这块的老哥可以在评论区留言

我们把数据手动去重以后，会发现去重后的调用链会完全符合 **`measureHierarchy()`** 方法中的代码逻辑

### 3、Window 权重导致多次调用

在 ViewRootImpl 的 **`performTraversals()`** 方法中，还有一种不怎么常见的场景同样会触发执行 **`onMeasure()`** 方法，直接来看代码

```java
/frameworks/base/core/java/android/view/ViewRootImpl.java
class ViewRootImpl {
    void performTraversals() {
        if (lp.horizontalWeight/lp.verticalWeight > 0.0f){///WindowManager.LayoutParams中的垂直/水平方向的权重是否大于0，这玩意不知道在哪可以设置
            measureAgain = true;
        }
        if(measureAgain)performMeasure();
    }
}
```

在 **`performTraversals()`** 方法中，会检查 **`WindowManager.LayoutParams`** 是否设置权重，如果设置了则会重新执行一次测量

我在源码中全局搜索后，发现只有 Toast 类中会设置 **`horizontalWeight`** / **`verticalWeight`** 属性，具体代码逻辑我没有深究，感兴趣的同学可以自己点击[这里](http://androidxref.com/7.0.0_r1/xref/frameworks/base/core/java/android/widget/Toast.java#415)查看源码

到这里，ViewRootImpl 类中所有发起测量的地方我们都分析完了，**`performMeasure()`** 方法在源码中一共有5处调用

![android_graphic_v4_measure_viewrootimpl](/Users/bob/Desktop/Bob/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/v4/android_graphic_v4_measure_viewrootimpl.GIF)

*图片来源：自己截图*

在 **`ViewRootImpl#measureHierarchy()`** 方法中被调用3次：

1. **在1331行，视图为Dialog 类型时，将视图宽度设置为 Dialog 预置宽度，发起首次测量**
2. **在1344行，视图为 Dialog 类型时，预置宽度不满足视图期望值，扩容宽度后再次发起测量**
3. **在1358行，`goodMeasure` 变量为 false 的情况下，两种情况：**
   1. **该视图是普通的 Activity，发起第一次测量**
   2. **该视图是 Dialog，且 Dialog 想要的宽度很大，预设宽度和扩容宽度均不满足，给它屏幕实际尺寸发起第三次测量**

在 **`ViewRootImpl#performTraversals()`** 方法中被调用了2次：

1. **在2024行，每次申请到 Surface 以后，发起一次测量**
2. **在2050行，为 Window 设置权重时，再次发起一次测量**

*注：Android 不同版本代码行数也不同，笔者源码环境是：[Android 7.0](http://androidxref.com/7.0.0_r1/xref/frameworks/base/core/java/android/view/ViewRootImpl.java)*

### 4、由 ViewGroup 自身发起调用

除了 ViewRootImpl 入口处会发起多次调用外，各个 ViewGroup 自身的业务逻辑同样会导致 View 的 **`onMeasure()`** 方法被多次调用

这部分内容我们可以去看缘佬在[ 玩 Android 每日一问 onMeasure()多次执行原因？](https://www.wanandroid.com/wenda/show/17920)这个问题下的回答

搬一部分缘佬的回答：**`onMeasure` 方法的回调次数，主要取决于它所在的容器的 `onMeasure` 逻辑，搭配不同 ViewGroup 和设置不同属性都会有影响**

比如 LinearLayout 在设置权重属性后就会多执行一次测量流程，在整个测量过程中，LinearLayout 最少会经历1次测量，最多会经历3次

所以由 ViewGroup 自身发起调用次数很难有一个标准、统一的答案，我们这里就不展开讨论了



# 二、布局阶段

最难的测量任务在上一章节已经结束了，接下来的内容会轻松很多

如果说测量阶段的学习难度是100分的话，那布局阶段可以直接降一个数量级，降到10分（10分我都觉得高了）

布局阶段的任务量主要是在 ViewGroup 一侧，子 View 不参与布局过程，父视图负责把子 View 们按照LayoutParams 规则摆放好

![android_graphic_v4_layout_process](/Users/bob/Desktop/Bob/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/v4/android_graphic_v4_layout_process.GIF)

*图片来源：自己画的*

## 再谈 LayoutParams

在 ViewGroup 的测量阶段我们认识了 LayoutParams 和 MarginLayoutParams，这里再来简单复习一下

**LayoutParams 是 ViewGroup 的内部类，里面有 `width` / `height` 两个属性，用来描述一个 View 的宽高**

**MarginLayoutParams 同样是 ViewGroup 的内部类，继承自LayoutParams，增加了上下左右四个方向的 `margin` 属性**

LayoutParams 是每个 ViewGroup 能够正确摆放子视图的重要依据，除了Android 为 ViewGroup 提供的这两个默认的 LayoutParams 外，每个 ViewGroup 也都会重写一个属于自己的 LayoutParams：

> - **FrameLayout.LayoutParams 继承自 MarginLayoutParams，在此基础上增加了 `gravity` 属性**
> - **LinearLayout.LayoutParams 继承自 MarginLayoutParams，在此基础上增加了 `weight`、`gravity` 等属性**
> - **RelativeLayout.LayoutParams 继承自 MarginLayoutParams，在此基础上增加了 `above`、`below`、`alignXxx`、`toXxxOf` 等属性**
> - **GridLayout.LayoutParams 继承自 MarginLayoutParams，在此基础上增加了 `row`、`column`、`gravity` 等属性**

**从几个常用布局的 LayoutParams 可以看到，凡是支持设置 `margin` 属性的都会选择继承自 MarginLayoutParams**

想要了解某个 ViewGroup 支持哪些属性，我们可以在 xml 布局文件中直接点击 **`layot_xx`** 属性跳转到 **`attrs.xml`** 文件，在 **`attrs.xml`** 文件中，保存着所有 Android 官方控件支持的属性列表

## ViewGroup 的布局过程

**`layout()`** 事件的起点和测量事件一样，都在 ViewRootImpl 类中触发，布局流程执行的顺序也和测量流程相同，以 DecorView 作为 View 多叉树的根节点，深度优先遍历整棵树

来看入口函数

```java
/frameworks/base/core/java/android/view/ViewRootImpl.java
class ViewRootImpl {

    void performTraversals() {
        if(发起requestLayout请求){
            performLayout();
        }
    }
    void performLayout(){
        mView.layout(mView.getMeasuredWidth(), mView.getMeasuredHeight());//以DecorView的宽高作为起始坐标
    }
}
```

ViewRootImpl 中判断是否有任一 View 发起过 **`requestLayout()`** 请求，有的话执行 **`performLayout()`** 方法，从 DecorView 开始向下分发 **`layout()`** 事件

接着看 ViewGroup 类收到 **`layout()`** 事件后的处理逻辑

```java
/frameworks/base/core/java/android/view/ViewGroup.java
class ViewGroup extends View {

  	//View.java中的逻辑，被我挪到这了
    void layout(int l, int t, int r, int b){
        setFrame();
        if(该视图执行了requestLayout()请求，或者视图的位置发生变化){
            onLayout();
        }
    }
    
    boolean setFrame(int left, int top, int right, int bottom) {
        //如果视图位置发生变化，保存新的上下左右坐标点位置
    }

    abstract void onLayout(boolean changed,int l, int t, int r, int b);
}
```

ViewRootImpl 在向下分发布局事件的过程中，会通知到每个 View 的 **`layout()`** 方法，所以在 **`layout()`** 方法中，会先判断自己有没有发起过 **`requestLayout()`** 请求，没有发起请求说明不需要更新，就不执行 **`onLayout()`** 方法了

**如果自己调用过 `requestLayout()` 请求，或者视图的位置发生变化，会将新的位置信息保存下来，然后调用自身的 `onLayout()` 方法执行布局**

在 ViewGroup 中，**`onLayout()`** 方法是由 **`abstract`** 关键字修饰的空方法，这表示 Android 要求所有继承 ViewGroup 的类必须自行实现 **`onLayout()`** 方法

到这里，对于 Android 系统来说，整个布局流程已经执行完成了（*开头说的布局很简单没骗你吧*），接下来调用的 **`onLayout()`** 方法是各个 ViewGroup 自身的业务逻辑

## 手写一个斜着的线性布局

由于布局流程过于简单，接下来我准备手写一个可以斜着摆放子 View 的**`线性布局`**，在一步步实现功能的过程中理解布局阶段在三部曲中的意义

先看效果

![android_graphic_v4_layout_slant](/Users/bob/Desktop/Bob/workspace/androidstudio/Blackboard/Blog/src/main/java/com/android/blog/android_view/imgs/v4/android_graphic_v4_layout_slant.GIF)

*图片来源：自己录的*

动图演示的是一个纵向居中排列的**`线性布局`**，容器内包含5个 TextView 和 1个 ImageView，点击“**`启用倾斜属性`**”按钮后，ViewGroup 会按照一定的偏移量重新布局，把容器内所有的子 View 以左上到右下对角线的方式斜着摆放

SlantLinearLayout 实现的功能非常简单，代码量也不多，关键代码只有3行：

```java
class SlantLinearLayout extends LinearLayout {

    void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
           int childLeft = (childSpace - childWidth) / 2;//根据父视图的padding和子View的margin、自身测量的宽高，计算每个子View起始位置
           int childTop += lp.topMargin;//起始位置加上子View设定的顶部距离
           int childOffset = offset * i - (count - i - 1) * offset;//如果用户启用倾斜属性，那么计算每个子View在原先的基础上的偏移量
           
           child.layout(left, top, left + width, top + height);//通知子View执行布局
        }
    }
}
```

SlantLinearLayout 的自身宽高和子 View 们的宽高都在测量阶段就已经确定下来了，所以我们直接根据测量的结果来计算每个子 View 所在的位置

- **`childTop` 表示每个 View 距离顶部的值，由于容器是纵向的线性排列，所以在确定了第一个距离顶部的高度后，接下来每次累加子 View 自身的高度作为下一个子 View 的起始高度就行了**

- **`childLeft` 表示每个 View 距离左边的值，由于每个 View 宽度都不一样，所以 `childLeft` 要动态计算**

- **`childOffset` 表示每个 View 距离左边的偏移量，如果用户启用倾斜属性，那么计算每个子 View 时需要在原先的基础上的加上设定的偏移量**

SlantLinearLayout 的布局过程就是在不停的计算这3个变量的值，然后调用子 View 的 **`layout()`** 方法，将计算得到的上下左右位置信息（屏幕坐标系的绝对值）传递给子 View，点击[[这里]](https://github.com/yibaoshan/Blackboard/blob/master/Blog/src/main/java/com/android/blog/android_view/demo/v4/SlantLinearLayout.java)查看源码

布局阶段结束后，开发者可以调用 **`getWidth()`** / **`getHeight()`** 方法来获取视图的宽高



# 三、绘制阶段

绘制流程和之前的测量/布局相比会显得有些特殊，体现在两个方面

**一是绘制流程执行的顺序，测量和布局阶段都是先执行子 View 再执行 ViewGroup 自身，而绘制是先执行 ViewGroup 绘制流程，再执行子 View 的绘制流程**

**二是绘制流程涉及到其他硬件（GPU），启用硬件加速和关闭硬件加速方法走的是两条完全不同的路线，这就会导致绘制阶段的调用链要更复杂一些，在后续分析方法调用的时候要有心理准备**

按照惯例，先来看绘制流程的入口：

```java
/frameworks/base/core/java/android/view/ViewRootImpl.java
class ViewRootImpl {

    void performTraversals() {
        performDraw();
    }

    void performDraw(){
        draw();
    }

    void draw() {
        if(是否启用硬件绘制) ThreadedRenderer.draw(mView);
        else drawSoftware();
    }
}
```

依旧是 ViewRootImpl 作为绘制事件的最初发起者，经过 **`performDraw()`** 转发后，最终由 **`draw()`** 来执行绘制事件的分发

在 **`draw()`** 方法中，先判断应用是否了启用硬件加速，如果启用硬件加速的话就走 **`ThreadedRenderer#draw()`** 方法

如果没有启用硬件加速，调用 **`drawSoftware()`** 方法执行绘制事件分发

我们先来看没有启用硬件加速的情况

## ViewGroup 的绘制过程

本章节的开头就提到了绘制流程的执行顺序问题，接下来我们就一起来看看 **`ViewGroup#draw()`** 方法的实现，从源码中找答案

```java
/frameworks/base/core/java/android/view/ViewGroup.java
class ViewGroup extends View {

    void draw(Canvas canvas) {
        drawBackground();//先画背景
        onDraw(canvas);//不管是View还是ViewGroup，先把自己画出来
        dispatchDraw(canvas);//通知子视图执行绘制，如果是视图是View，这个方法默认为空，只有ViewGroup有实现
        onDrawForeground(canvas);//最后画前景
    }
    
    void dispatchDraw(){
        for (int i = 0; i < childrenCount; i++) {
            draw();//简化过的路径
        }
    }
}
```

ViewGroup 继承自 View ，所以我把 **`View#draw()`** 方法中的逻辑挪到了在 ViewGroup 中，在 **`ViewGroup#draw()`** 方法中一共完成了4件事：

> 1. **调用 `drawBackground()` 方法画 ViewGroup 的背景**
> 2. **调用自身的 `onDraw()` 方法执行 Canvas 绘图逻辑**
> 3. **调用 `dispatchDraw()` 通知子 View 执行 `draw()`**
> 4. **调用 `onDrawForeground()` 方法画视图的前景**

第1步和第4步的画背景/前景的方法实现我们这里就不展开了（但是方法执行顺序我们一定要记住），重点关注第2步和第3步

在第2步中，ViewGroup 会先执行自己的 **`onDraw()`** 方法执行绘图，之后才会执行第3步调用 **`dispatchDraw()`** 方法通知子 View 执行绘制流程

**在 Android 系统中，先绘制的内容会被后绘制的内容覆盖掉，所以我们在自定义 View / ViewGroup 的时，了解绘制的前后调用顺序非常重要！**

## View 的绘制过程

View 的绘制流程和 ViewGroup 的绘制流程几乎一模一样，唯一的区别是 View 中的 **`dispatchDraw()`** 是空实现，因为它没有子视图

```java
/frameworks/base/core/java/android/view/View.java
class View {
  
    void draw(Canvas canvas) {
        drawBackground();//先画背景
        onDraw(canvas);//不管是View还是ViewGroup，先把自己画出来
        dispatchDraw(canvas);//空方法
        onDrawForeground(canvas);//最后画前景
    }
    
    void dispatchDraw();//空方法
}
```

View 的绘制流程其实也是这4个步骤，只不过 **`dispatchDraw()`** 是方法罢了，没有实际作用

到这里，整个绘制流程已经执行完成了，接下来调用的 **`onDraw()`** 方法是各个 View 自身的业务逻辑

Android 通常在 **`onDraw()`** 方法中会执行绘图操作，但当我们选择开启硬件加速之后，实际的绘制操作就不在 **`onDraw()`** 方法中执行了，接着往下看

## 启用硬件加速

Android 4.0 以后对所有应用程序都默认开启硬件加速，再加上现在的 GPU 驱动对绘图指令支持的非常全面，所以，在绝大多数情况下，应用都是走硬件绘制这条路线的

启用硬件加速后，绘制的入口就会发生变化，主要逻辑在 ThreadedRenderer 类，它的任务是收集开发者在 **`onDraw()`** 方法中调用的绘制指令，然后启用 RenderThread 线程，在渲染线程中再调用 GPU 执行真正的绘图操作

```java
/frameworks/base/core/java/android/view/ThreadedRenderer.java
class ThreadedRenderer {

    void draw(DecorView view){
      	view.updateDisplayListIfDirty();//经过精简后的方法调用，直接调用View记录绘图指令
        int syncResult = nSyncAndDrawFrame();//切换到render thread 执行真正的绘图指令！
    }

}
```

ThreadedRenderer 类中的 **`draw()`** 方法一共完成了两件事：

> 1. **调用 View 的 `updateDisplayListIfDirty()` 方法，收集绘制指令**
> 2. **调用 `nSyncAndDrawFrame()` 方法，将绘图指令同步给 RenderThread 线程去执行**

渲染线程的绘图工作我们暂时不关心，本小节我们需要了解的是 **`View#updateDisplayListIfDirty()`** 方法是如何收集绘制指令的，来看 View 的源码：

```java
/frameworks/base/core/java/android/view/View.java
class View {

    RenderNode updateDisplayListIfDirty(){
        RenderNode renderNode;
        DisplayListCanvas canvas = renderNode.start();//开始记录绘制节点
        draw(canvas);//执行常规的draw阶段，注意这里使用的canvas类型是DisplayListCanvas
        renderNode.end();//View 树遍历完成，结束记录
        return renderNode;//将记录的节点返回给 ThreadedRenderer.java
    }

}
```

在 View 的 **`updateDisplayListIfDirty()`** 方法中，首先调用了 **`RenderNode#start()`** 方法获取一个 DisplayListCanvas 对象，并同时开始记录绘制指令

接着调用 View 自身的 **`draw()`** 方法执行常规的绘制流程（*注意这里使用的 Canvas 类型是 DisplayListCanvas*），**`draw()`** 方法结束以后，将收集到的绘图指令返回给 ThreadedRenderer 执行实际的绘图操作

**其中，DisplayListCanvas 对象是硬件绘制中最重要的知识点！！！注意看接下来的分析**

```c++
/frameworks/base/libs/hwui/DisplayListCanvas.cpp
class DisplayListCanvas {

    //以画圈指令为例，当开发者调用Canvas.drawCircle()方法绘图时，这个指令实际上被转发到addDrawOp()保存了起来
    void DisplayListCanvas::drawCircle(float x, float y, float radius, const SkPaint& paint) {
        addDrawOp(new (alloc()) DrawCircleOp(x, y, radius, refPaint(&paint)));
    }

    size_t DisplayListCanvas::addDrawOp(DrawOp* op) {
        //保存指令，等待切换到 RenderThread 渲染线程时再取出执行
    }

}
```

**DisplayListCanvas 继承自 Canvas ，当我们使用 DisplayListCanvas 进行一系列绘图操作时，这些绘图指令会通过重定向的方式保存到 DisplayList 集合中，并不会实际执行**

比如上面的代码块演示了普通的画圈指令，当我们调用 **`Canvas#drawCircle()`** 方法画圈时，这个指令实际上被转发到 **`addDrawOp()`** 方法保存了起来，直到 ThreadedRenderer 对象调用了 **`nSyncAndDrawFrame()`** 方法后才真正得到执行

**总结一下，应用启用硬件加速以后，`onDraw()` 方法中的指令将不再被执行，而是被收集到 DisplayList 集合中，等到所有需要绘制的 View 的 `draw()` 方法执行结束后，这些指令将会被同步到 RenderThread 渲染线程执行真正的绘图工作**

> *ps：启用硬件加速和关闭硬件加速对于开发者来说是无感的，**`onDraw()`** 方法的收集工作依旧是在 UI  线程中执行，代码写的垃该卡还是会卡*



# 四、结语

本篇文章介绍了自定义 View / ViewGroup 最重要的三个流程：测量、布局、绘制

**对于测量阶段，我们需要理解三种 MeasureSpec 的含义、ChildView MeasureSpec 的默认生成规则，以及了解 `onMeasure()` 方法会执行多次的原因**

**对于布局阶段，我们需要了解布局阶段在整个绘制流程中的作用以及 LayoutParams 对于 ViewGroup 的意义**

**对于绘制阶段，我们需要了解 View / ViewGroup 的绘制顺序，以及当应用启用硬件加速后 Android 系统都做了些什么**

最后，自定义 View 需要掌握一些基础的 Canvas API，出于篇幅考虑这部分内容不再展开讨论，有需要的同学可以查看 [GcsSloop](https://www.gcssloop.com/) 的[[Android自定义View系列]](https://www.gcssloop.com/category/customview.html)文章



本篇文章到这里就全部结束了，希望能对大家有帮助



**全文完**



# 五、参考资料

- [《Android开发艺术探索》- 任玉刚](https://book.douban.com/subject/26599538/)
- [Android measure过程 - 吴迪](https://www.viseator.com/2017/03/10/android_view_onMeasure/)
- [Android View的绘制流程 - Kelin](https://www.jianshu.com/p/5a71014e7b1b)
- [View的测量布局绘制过程 - 奔波儿灞取经](https://juejin.cn/post/6984417565279797262)
- [玩Android 每日一问 | onMeasure()多次执行原因？](https://www.wanandroid.com/wenda/show/17920)
- [玩Android 每日一问 详细的描述下自定义 View 测量时 MesureSpec.UNSPECIFIED](https://www.wanandroid.com/wenda/show/8613)
- [玩Android 每日一问 | 自定义控件测量模式真的和 match_parent，wrap_content 一一对应吗？](https://www.wanandroid.com/wenda/show/12489)
