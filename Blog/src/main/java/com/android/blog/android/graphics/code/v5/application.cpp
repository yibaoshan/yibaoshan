
/*

//frameworks/base/core/java/android/app/Activity.java
    - boolean dispatchTouchEvent(event); // 返回值表示是否继续下发
    - boolean onTouchEvent(event);

/frameworks/base/core/java/android/view/ViewGroup.java
    - boolean dispatchTouchEvent(event);
    - boolean onTouchEvent(event);
    - boolean onInterceptTouchEvent(event)
    - boolean requestDisallowInterceptTouchEvent(disallowIntercept)

/frameworks/base/core/java/android/view/View.java
    - boolean dispatchTouchEvent(event);
    - boolean onTouchEvent(event);
    - boolean dispatchPointerEvent(event); // 被 ViewRootImpl 调用，对 View 树来说，是事件分发的起点

/frameworks/base/core/java/com/android/internal/policy/DecorView.java
    - boolean dispatchTouchEvent(event) // 内部调用 Window.Callback ，为 Activity 提供优先拦截触摸事件的时机

在实际开发中，我们需要关心的重点是事件的分发与拦截

之前我们关心的都是：触摸事件怎么传递过来的？

却忽略了触摸事件所包含的内容，在了解分发流程前，我们来好好的认识一下触摸事件的本体：MotionEvent

我们本篇只分析单指触摸，也就是 ACTION_DOWN，ACTION_MOVE，ACTION_UP 这三个常用事件

多指触摸

TouchTarget

多指触摸触发的 ACTION_POINTER_DOWN/ACTION_POINTER_UP 事件不在本文讨论范围，感兴趣的同学可以在

ViewGroup 的 TouchTarget 这里也不展开

/frameworks/base/core/java/android/view/MotionEvent.java
    - ACTION_DOWN: 表示手指按下屏幕
    - ACTION_MOVE: 手指在屏幕上滑动时，会产生一系列的MOVE事件
    - ACTION_UP: 手指抬起，离开屏幕
    - ACTION_CANCEL: 等同于 ACTION_UP ，此时返回 true / false 无效，只能等待下一次 DOWN 事件
    - ACTION_POINTER_DOWN: 当已经有一个手指按下的情况下，另一个手指按下会产生该事件
    - ACTION_POINTER_UP: 多个手指同时按下的情况下，抬起其中一个手指会产生该事件

代码比较少，我们平时开发也会经常接触到，比如处理滑动冲突，禁用某个控件的点击事件等等

关键的方法非常少，搞懂每个方法的含义和使用场景，事件分发就会了

某个 View / ViewGroup 消费了 DOWN 事件以后，后续的 MOVE / UP / CANCEL 都交由该 View / ViewGroup 处理

我们也可以这么说，如果某个 View / ViewGroup 选择了不使用 DOWN 事件，那么，接下来 MOVE / UP / CANCEL 事件都将与该视图无关，除非等待再次发生 DOWN 事件。

一个view消费了某一个触点的down事件后，该触点事件序列的后续事件，都由该view消费

*/

//frameworks/base/core/java/com/android/internal/policy/DecorView.java
class DecorView extends FrameLayout {

    /*
    mWindow 对象说来话又长了，简单来说

    PhoneWindow#setContentView() -> installDecor() -> generateDecor() -> DecorView#setWindow()

    这里判断了 Window.Callback 是否为空，Activity 、Dialog 都实现了这个 Callback 接口

    所以，Activity 、Dialog 可以在执行事件分发之前，选择是否拦截事件
    */

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Window.Callback cb = mWindow.getCallback(); // 给 Activity 和 Dialog 拦截事件的机会
        return cb != null ? cb.dispatchTouchEvent(ev) : super.dispatchTouchEvent(ev);
    }
}

//frameworks/base/core/java/android/view/ViewGroup.java
class ViewGroup extends View {

    /**

    如果是 DOWN 事件，表示一个事件序列的开始，对于这个事件

    ViewGroup 要做的是

    1. 查看 disallowIntercept 标识，子视图有没有请求过它想要下一个事件？
    2. 通过 onInterceptTouchEvent() 方法询问自己要不要消费？
    3. 找到触摸位置的子视图，检查是否可以触摸，可以的话调用 dispatchTransformedTouchEvent() 询问它要不要消费，一旦确定消费，创建 TouchTarget 记录该子视图
    4. 找了一圈没人消费？丢给父视图，不管了

    如果不是 DOWN 事件，则表示是一个事件序列的延续，对于这个事件，ViewGroup 要做的是

    1. 查看保存的 mFirstTouchTarget 变量，为空表示这个事件序列是由 ViewGroup 自己消费的，不执行分发，直接调用 xxx 方法
    2. mFirstTouchTarget 变量不为空，表示之前有子视图已经在使用该序列了，但是 ViewGroup 可以选择是否要拦截，调用 onInterceptTouchEvent() 询问是否拦截消费？

    先看自己是否已经在消费触摸序列，拦截，后分发

    如果 mFirstTouchTarget 为空，并且事件类型不为 DOWN ，表示先前的事件也是 ViewGroup 自己消费的，无需执行分发，再次交给自己执行即可

    否则，只要 mFirstTouchTarget 不为空，并且事件类型为 DOWN 事件，进入考虑拦截环节

    此时，如果子视图调用了 requestDisallowInterceptTouchEvent(true) 请求不拦截，那么先不拦截

    如果子视图未发起请求，调用 onInterceptTouchEvent() 询问自身是否需要拦截消费该事件

    如果都没有，那么进入分发模式

    如果该view接收事件，则会为他创建一个TouchTarget，将该触控id和view进行绑定，之后该触控点的事件就可以直接分发给他了

    */

    // 虽然是复写 View 的 dispatchTouchEvent() 方法，但是，ViewGroup 并没有使用 super()，也就是说，ViewGroup 的 dispatchTouchEvent() 是个新方法，和 View 逻辑无关
    // DecorView 是 ViewGroup ，所以，在首次执行事件分发时会进入到 ViewGroup#dispatchTouchEvent() 方法中
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        int actionMasked = action & MotionEvent.ACTION_MASK;
        boolean intercepted = false;
        /*
            1. 如果是 DOWN 事件，说明是一个事件序列的开始，查询子视图是否请求父视图放行，然后询问 ViewGroup 自身是否拦截
            2. 如果 mFirstTouchTarget 不为空，说明是一个事件序列的延续，并且之前已经有子视图消费了事件序列的开始，此时，无视当前的事件类型，查询子视图是否请求放行，然后询问 ViewGroup 自身是否拦截

            以上条件都不满足

            表示上一个事件就是 ViewGroup 自身消费掉的，直接调用 dispatchTransformedTouchEvent() 分发给自己

        */
        if (actionMasked == MotionEvent.ACTION_DOWN|| mFirstTouchTarget != null) {
            // 检查子视图是否调用了 requestDisallowInterceptTouchEvent(true) 请求不拦截
            boolean disallowIntercept = (mGroupFlags & FLAG_DISALLOW_INTERCEPT) != 0;
            if (!disallowIntercept) intercepted = onInterceptTouchEvent(ev); // 子视图未请求不拦截，询问 ViewGroup 自身是否需要消费
        } else {
            intercepted = true; // 如果 mFirstTouchTarget 为空，并且事件类型不为 DOWN ，表示先前的事件也是 ViewGroup 自己消费的，无需执行分发，再次交给自己执行即可
        }
        // 子视图为请求放行，没人拦截，自己也不消费，进入分发流程
        if (!intercepted) {
            if (actionMasked == MotionEvent.ACTION_DOWN) {// 依旧先对 DOWN 做处理
                for (int i = mChildrenCount - 1; i >= 0; i--) {
                    ...// 检查子 View 是否可触摸，是否在触摸区域内等等，过程略
                    // 找到合适的子 View后，调用 dispatchTransformedTouchEvent() 执行事件分发
                    if (dispatchTransformedTouchEvent(ev, false, child, idBitsToAssign)) {
                        mFirstTouchTarget = addTouchTarget(child, idBitsToAssign); // 我把 mFirstTouchTarget 当变量用了
                        break;
                    }
                }
            }
        }
        // 有人拦截
        dispatchTransformedTouchEvent();// 给自己执行
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //询问 ViewGroup 自身是否需要处理事件
        return false;
    }

    private boolean dispatchTransformedTouchEvent(MotionEvent event, boolean cancel,View child, int desiredPointerIdBits) {
        boolean handled;
        if (child == null) {
            handled = super.dispatchTouchEvent(event);
        } else {
            handled = child.dispatchTouchEvent(event);
        }
        return handled;
    }
}



/*
    对于 View 来说，它在分发流程中的话语权是非常弱的，只能选择要不要消费 DOWN 事件，一旦选择了不消费，那以后这组事件就和它无缘了

    而 ViewGroup 不同，它在范围内
*/
//frameworks/base/core/java/android/view/View.java
class View {

    public boolean dispatchTouchEvent(MotionEvent event) {
        boolean result = onTouchEvent(event);
        return result;
    }


}