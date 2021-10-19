package com.android.notebook.android.framework.view


class ViewEventDispatch {

    /**
     * Android的事件分发机制总的来说是监听I/O变化，经过筛选后再派发给各个子系统
     * 本文重点讨论Android View中的事件分发机制，大致流程如下：
     * 1. InputManager收到事件后分发给当前激活的window
     * 2. window拿到事件传递给ViewRootImpl
     * 3. ViewRootImpl再传递给DecorView
     * 4. 接下来是双重职责，DecorView给把事件传递给Activity，若Activity不消费，再传递回DecorView，然后，DecorView再从root向下询问
     *
     * 事件序列：由按下down事件为开始，以up或者cancel事件为结束，避免每次询问浪费性能
     * 分发链：每个ViewGroup持有的mFirstTouchTarget对象
     * */

    var hasChild = false;

    open fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        var consume = false
        // 1.将事件分发给Child
        if (hasChild) {
//            consume = child.dispatchTouchEvent()
        }
        // 2.若Child不消费该事件,或者没有child，判断自身是否消费该事件
        if (!consume) {
//            consume = super.dispatchTouchEvent()
        }
        // 3.将结果向上层传递
        return consume
    }

    class MotionEvent {}

    /**
     * DecorView的双重职责：
     * 1.在接收到输入事件时，DecorView不同于其它View，它需要先将事件转发给最外层的Activity，使得开发者可以通过重写Activity.onTouchEvent()函数以达到对当前屏幕触摸事件拦截控制的目的
     * 这里DecorView履行了自身（根节点）特殊的职责；
     * 2.从Window接收到事件时，作为View树的根节点，将事件分发给子View，这里DecorView履行了一个普通的View的职责。
     * */

    /*
    public class DecorView extends FrameLayout {

        // 1.将事件分发给Activity
        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            return window.getActivity().dispatchTouchEvent(ev)
        }

        // 4.执行ViewGroup 的 dispatchTouchEvent
        public boolean superDispatchTouchEvent(MotionEvent event) {
            return super.dispatchTouchEvent(event);
        }
     }

    // 2.将事件分发给Window
    public class Activity {
        public boolean dispatchTouchEvent(MotionEvent ev) {
            return getWindow().superDispatchTouchEvent(ev);
        }
    }

    // 3.将事件再次分发给DecorView
    public class PhoneWindow extends Window {
        @Override
        public boolean superDispatchTouchEvent(MotionEvent event) {
            return mDecor.superDispatchTouchEvent(event);
        }
    }
    * */

}