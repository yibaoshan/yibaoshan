### LayoutInflater

LayoutInflater将xml布局解析为view对象，默认pull解析，读开始结束符

AppCompatActivity使用类似静态代理AppCompatDelegate来解决统一Material Design的支持[/mə'tɪrɪəl/](cmd://Speak/_us_/material)

### setContentView()方法解析

1. 实现类为Activity
2. getWindow().setContentView(id) 通过获取window对象去设置布局
3. window是抽象类,唯一实现类是PhoneWindow  所以是PhoneWindow。setContentView
4. 由于window不具备view得一些属性,所以具体的view操作属性是通过DecorView       
5. 而DeCorView实际的本质就是一个FrameLayout
6. FrameLayout包含title和Content布局,然后在PhoneWindow调用generateLayout()获取id为Content的控件对象
7. 然后把从业务Acitivyt的LayoutId通过inflate添加到content得控件对象中

### Activity的显示原理(Window/DecorView/ViewRoot)

##### performLaunchActivity()

`performLaunchActivity()` 方法里面执行了几个操作，创建 Activity 对象，调用 `Activity#attach()`，
 创建 PhoneWindow 对象，调用 `Activity#onCreate()`，初始化 DecorView ，添加布局到 DecorView 的 content ，调用 `Activity#onStart()` ；

Window->PhoneWindow

DecorView

### Activity的UI刷新机制(Vsync/Choreographer)

##### 单缓存(buffer)

CPU/GPU将数据处理完成后交给display显示

这样会有个缺点：当一帧数据还没有被显示完时，buffer已经第二帧数据更改，这样绘制的时候屏幕就会出现两帧重叠的问题

##### 双缓存(Frame buffer/Back buffer)

为了解决绘制重叠引入了双缓存，CPU/GPU和display各用个的

在Vsycn信号来临时进行数据交换，因为是直接交换内存地址，所以整个动作可以说是瞬间完成

在Android4.1之前，系统都是采用这种方式来更新UI

##### 三缓存(Graphic buffer)[/'græfɪk/](cmd://Speak/_us_/graphic)

结论：**三缓冲有效利用了等待vysnc的时间，减少了jank，但是带来了延迟。** 看不懂

##### 垂直脉冲信号Vsync

##### Choreographer[/ˌkɔri'ɔɡrəfə/](cmd://Speak/_us_/choreographer)

用于实现——"CPU/GPU的绘制是在VSYNC到来时开始"。

### UI的绘制原理(Measure/Layout/Draw)

##### ViewGroup绘制流程

1. onMeasure

   调用子view需要传入限制的宽高度，根据子view的xml属性和屏幕所剩空间决定

   1. 调用每个子view的measure方法，让子view进行自我测量
   2. 根据子view给出的尺寸计算自己的尺寸 

2. onLayout

tips：

1. ViewGroup 想要 onDraw 执行的时候得设置 setWillNotDraw(false)

##### View绘制流程

1. onMeasure
2. onLayout
3. onDraw

### Surface原理(Surface/SurfaceFlinger)

空

### Touch事件分发

从Activity.dispatchTouchEvent开始层层下发给ViewGroup的onInterceptTouchEvent，都没有处理则会从底往回调View的onTouchEvent方法。

ViewGroup拦截后交给ViewGroup的onTouchEvent方法

##### ViewGroup

public boolean onInterceptTouchEvent(){

​	return true;

}

##### View

public boolean onTouchEvent(MotionEvent event){

​	requestDisallowInterceptTouchEvent()

​	return true;

}

##### 问题分析

- 子线程绘制UI一定会崩溃吗？

  答：不一定，在ViewRootImpl没创建前随便玩，而且只要不调用requestLayout()方法只是invalidate()也是可以刷新UI的。简单来说，只要不调用checkThread的方法都可以

