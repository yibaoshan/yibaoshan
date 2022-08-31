
- Android View体系从DecorView开始，把每个View/ViewGroup可以看做一个节点的话，Android View就是一颗多叉树，执行测量、布局、渲染时，从根节点开始，深度优先遍历整个多叉树

### 一、Measure

#### 1、Notes

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
- measure是最复杂的一步，我们需要理解MeasureSpec和LayoutParams的含义才能
- 调用setMeasuredDimension()方法确定mMeasuredWidth和mMeasuredHeight变量的值

#### 2、MeasureSpec

每个View和ViewGroup都有MeasureSpec属性，决定了View/ViewGroup的大小

##### AT_MOST

如果你的LayoutParams设置的是warp_content，那么通常会收到来自父视图返回的AT_MOST模式，加上父视图的宽高

AT_MOST表示最大模式，无论是View还是ViewGroup，在onMeasure()方法接受到此模式时，表示你可以在在父视图的大小下你可以随意设定宽高，但最好不要超过父视图的宽高，否则视图会显示不完整

如果是View接收到AT_MOST模式时，在父视图的宽高下自由发挥即可

如果是ViewGroup接受到此模式，稍微有点麻烦，我需要计算

##### EXACTLY

如果你的LayoutParams设置的是match_parent或者具体的值，比如200dp，那么绝大多数情况下你讲收到来自父视图返回的EXACTLY模式，同时加上父视图的宽高

EXACTLY表示精确模式，在onMeasure()方法接受到此模式时，你最好就这么大

AT_MOST和EXACTLY只是父视图给子View的参考，子View无视MeasureSpec当然也可以，当然这只是约定俗成的规矩，我自定义ViewGroup，无视子View的LayoutParams统统返回AT_MOST，你拿我也没辙

##### UPSPECIFIED

EXACTLY表示未确定模式，比较特殊，一般和你的LayoutParams关系不大

当你收到UPSPECIFIED时，通常是需要自己计算大小的，因为此模式下父视图不会携带宽高信息给你，你爱多大多大

什么情况会使用UPSPECIFIED模式呢？

滚动模式，比如横向/纵向滚动的ScrollView、RecyclerView等等

在滚动模式下，子View/ViewGroup的大小对于父视图来说没有意义，就算超过屏幕大小我滑动查看就行了，如何处理滑动事件才是父视图需要考虑的重点

以ScrollView举例，无论ScrollView的父视图的LayoutParams如何设置，ScrollView都会将子View的高度mode设置为未指定模式

所以如果你在ScrollView中自定义View，LayoutParams设置为warp_content或者match_parent，在屏幕上找不到你的自定义View，因为此时接收到父视图的高度为0（ScrollView重写了计算高度方法）

哪怕指定了自定义View的宽高，没有指定min_height或者背景的情况下，收到的依然是高度0

但是，如果你在自定义View外面再套一层FrameLayout、LinearLayout、RelativeLayout等容器，就可以在屏幕上看到自己写的View了

为什么会这样呢？

因为父视图为了挡了一刀，ScrollView变成你的爷爷了，爷爷下发的UPSPECIFIED模式给了你爸爸，你爸爸收到UPSPECIFIED模式后根据你的LayoutParams来测量，发现你是

#### 3、LayoutParams

#### 4、Others

##### View执行onMeasure()

View的职责是根据父视图传递的SpecMode和SpecSize测量自己所需要的大小

##### ViewGroup执行onMeasure()

ViewGroup的职责是调用measureChild()或者measureChildWithMargins()完成子View的测量工作

接着，拿到子View的宽高

##### 为什么onMeasure执行多次?

ViewRootImpl中有5个地方进行方法调用，他们分别是

##### onMeasure()阶段的目的

### 二、Layout

如果说measure是最复杂的，那么layout阶段可能是三个阶段中最简单的一步

它的任务是根据上一步测量的结果把每个View/ViewGroup摆摆好

- 对于View来说，执行完onLayout以后，我们就可以调用getWidth()/getHeight()方法获取View的实际宽高，因为到了这一步，View的尺寸大小就已经确定了下来
- 在自定义View过程中，如果我们将宽高设置为自适应大小，那么我们只需要关心onSizeChanged()方法，此方法调用后，新的视图尺寸会确定下来，我们可以根据这个尺寸来绘图
- 对于ViewGroup来说，重点需要关注LayoutParams参数，几乎每个ViewGroup都有自己的LayoutParams属性，比如LinearLayout有权重、居上/下/左右中等，RelativeLayout有marginTop等等
- ViewGroup的职责就是依据自身的规则来排兵布阵
- View/ViewGroup的layout方法都没有默认实现，我们自己来写个demo玩一玩

在layout阶段最重要的是理解各个ViewGroup的LayoutParams属性，比如在线性布局LinearLayout中，排在前面的View属性如果是match_parent

那么线性布局就会按照测量结果传递给这个子View全屏，在此容器的其他视图就会被排挤出屏幕外

这时候你就会发现虽然调用getWidth方法有值，但它就是不显示，这种情况要么是被覆盖，要么是被隐藏，要么就是像这种情况，它的坐标被绘制到屏幕外了

在屏幕外的View的宽高，可能会发生的一件事情是

### 三、Draw

Draw阶段调用流程有点特殊，measure和layout都是以DecorView作为根节点深度优先遍历的多叉树，Draw不同

Draw的调用链稍微有点绕，对于开发者来说，只需要关注在onDraw方法中做些什么事情

绝大多数自定义View需要实现该方法，

Draw的调用链之所以这么绕，是因为Android的绘制是有顺序的

比如一个ViewGroup的背景是蓝色，子View是一个红色的圆形，用户希望看到的肯定是这样：

我是图片

的小伙肯定是绘制了ViewGroup的背景