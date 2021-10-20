package com.android.notebook.android.framework.view

class ViewEventIntercept {

    /**
     * 问题：
     * 1. 为什么需要拦截机制？dispatch返回true或false不满足业务条件吗？
     * 2. dispatchTouchEvent()/onTouchEvent()/onInterceptTouchEvent()的先后顺序
     *
     * View
     * # onTouchEvent()
     * # dispatchTouchEvent()
     * ViewGroup
     * # onInterceptTouchEvent()
     * # override dispatchTouchEvent()
     *
     * 自问自答：
     * 1. 不需要向下分发时的场景
     * 2. 先后顺序：ViewGroup.onInterceptTouchEvent()->ViewGroup.dispatchTouchEvent()->View.onTouchEvent()
     * */

    class ViewGroup : View() {

        fun onInterceptTouchEvent(event: MotionEvent) {

        }

        fun requestDisallowInterceptTouchEvent(flag: Boolean) {

        }

        override fun dispatchTouchEvent(event: MotionEvent) {
            //每次接收到down事件，将之前子view请求不拦截的状态重置
            if (event.action == "down") {
                requestDisallowInterceptTouchEvent(false)
            }
            super.dispatchTouchEvent(event)
        }

    }

    open class View {

        var parent: ViewGroup? = null

        fun onTouchEvent(event: MotionEvent) {
            if (event.action == "down") {
                parent?.requestDisallowInterceptTouchEvent(true)
            }
        }

        open fun dispatchTouchEvent(event: MotionEvent) {

        }

    }

    class MotionEvent {
        var action = ""
    }
}