
/*

/frameworks/base/core/java/android/view/View.java
    - dispatchPointerEvent()

/frameworks/base/core/java/android/view/View.java
    - dispatchPointerEvent() // 被 ViewRootImpl 调用，对 View 树来说，是事件分发的起点
    - onInterceptTouchEvent()

/frameworks/base/core/java/com/android/internal/policy/DecorView.java
    - dispatchTouchEvent() // 为 Activity 提供拦截触摸事件的时机

之前我们关心的都是：触摸事件怎么传递过来的？

却忽略了触摸事件所包含的内容，在了解分发流程前，我们来好好的认识一下触摸事件的本体：MotionEvent

我们本篇只分析单指触摸，也就是 ACTION_DOWN，ACTION_MOVE，ACTION_UP 这三个常用事件

多指触摸触发的 ACTION_POINTER_DOWN/ACTION_POINTER_UP 事件不在本文讨论范围，感兴趣的同学可以在

/frameworks/base/core/java/android/view/MotionEvent.java
    - ACTION_DOWN: 表示手指按下屏幕
    - ACTION_MOVE: 手指在屏幕上滑动时，会产生一系列的MOVE事件
    - ACTION_UP: 手指抬起，离开屏幕
    - ACTION_POINTER_DOWN: 当已经有一个手指按下的情况下，另一个手指按下会产生该事件
    - ACTION_POINTER_UP: 多个手指同时按下的情况下，抬起其中一个手指会产生该事件

代码比较少，我们平时开发也会经常接触到，比如处理滑动冲突，禁用某个控件的点击事件等等

关键的方法非常少，搞懂每个方法的含义和使用场景，事件分发就会了

*/

//frameworks/base/core/java/com/android/internal/policy/DecorView.java
class DecorView extends ViewGroup {

    /*
    mWindow 对象说来话又长了，简单来说

    PhoneWindow#setContentView() -> installDecor() -> generateDecor() -> DecorView#setWindow()

    这里判断了 Window.Callback 是否为空，Activity 、Dialog 都实现了这个 Callback 接口

    所以，Activity 、Dialog 可以在执行事件分发之前，对事件进行处理
    */

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Window.Callback cb = mWindow.getCallback(); // 给 Activity 和 Dialog 拦截事件的机会
        return cb != null ? cb.dispatchTouchEvent(ev) : super.dispatchTouchEvent(ev);
    }
}

//frameworks/base/core/java/android/view/ViewGroup.java
class ViewGroup extends View {

    // 虽然是复写 View 的 dispatchTouchEvent() 方法，但是，ViewGroup 并没有使用 super()，也就是说，ViewGroup 的 dispatchTouchEvent() 是个新方法，和 View 逻辑无关
    // DecorView 是 ViewGroup ，所以，在首次执行事件分发时会进入到 ViewGroup#dispatchTouchEvent() 方法中
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.isFromSource(InputDevice.SOURCE_MOUSE)
                && ev.getAction() == MotionEvent.ACTION_DOWN
                && ev.isButtonPressed(MotionEvent.BUTTON_PRIMARY)
                && isOnScrollbarThumb(ev.getX(), ev.getY())) {
            return true;
        }
        return false;
    }
}

//frameworks/base/core/java/android/view/View.java
class View {

    // 事件起点
    public boolean dispatchPointerEvent(MotionEvent event) {
        if (event.isTouchEvent()) {
            return dispatchTouchEvent(event);
        } else {
            return dispatchGenericMotionEvent(event);
        }
    }

    /**
     * Pass the touch screen motion event down to the target view, or this view if it is the target.

     * @param event The motion event to be dispatched.
     * @return True if the event was handled by the view, false otherwise.
     */
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (!isAccessibilityFocusedViewOrHost()) { // 没有焦点，不处理
            return false;
        }

        boolean result = false;

        if (mInputEventConsistencyVerifier != null) {
            mInputEventConsistencyVerifier.onTouchEvent(event, 0);
        }

        final int actionMasked = event.getActionMasked();
        if (actionMasked == MotionEvent.ACTION_DOWN) {
            // Defensive cleanup for new gesture
            stopNestedScroll();
        }

        if (onFilterTouchEventForSecurity(event)) {
            if ((mViewFlags & ENABLED_MASK) == ENABLED && handleScrollBarDragging(event)) {
                result = true;
            }
            //noinspection SimplifiableIfStatement
            ListenerInfo li = mListenerInfo;
            if (li != null && li.mOnTouchListener != null
                    && (mViewFlags & ENABLED_MASK) == ENABLED
                    && li.mOnTouchListener.onTouch(this, event)) {
                result = true;
            }

            if (!result && onTouchEvent(event)) {
                result = true;
            }
        }

        if (!result && mInputEventConsistencyVerifier != null) {
            mInputEventConsistencyVerifier.onUnhandledEvent(event, 0);
        }

        // Clean up after nested scrolls if this is the end of a gesture;
        // also cancel it if we tried an ACTION_DOWN but we didn't want the rest
        // of the gesture.
        if (actionMasked == MotionEvent.ACTION_UP ||
                actionMasked == MotionEvent.ACTION_CANCEL ||
                (actionMasked == MotionEvent.ACTION_DOWN && !result)) {
            stopNestedScroll();
        }

        return result;
    }


}