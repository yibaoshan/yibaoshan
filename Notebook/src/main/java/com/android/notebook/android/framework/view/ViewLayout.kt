package com.android.notebook.android.framework.view

class ViewLayout {

    /**
     * 带着问题来思考整个layout过程
     *
     * 1. 系统为什么要有layout过程？
     * 答：View框架在经过第一步的measure过程后，成功计算了每一个View的尺寸。
     * 但是要成功的把View绘制到屏幕上，只有View的尺寸还不行，还需要准确的知道该View应该被绘制到什么位置。
     * 除此之外，对一个ViewGroup而言，还需要根据自己特定的layout规则，来正确的计算出子View的绘制位置，已达到正确的layout目的。
     * 这也就是layout过程的职责。
     * 该位置是View相对于父布局坐标系的相对位置，而不是以屏幕坐标系为准的绝对位置。
     * 这样更容易保持树型结构的递归性和内部自治性。而View的位置，可以无限大，超出当前ViewGroup的可视范围，这也是通过改变View位置而实现滑动效果的原理。
     *
     * 2. layout过程都干了点什么事？
     * 由于View是以树结构进行存储，所以典型的数据操作就是递归操作，所以，View框架中，采用了内部自治的layout过程。
     * 每个叶子节点根据父节点传递过来的位置信息，设置自己的位置数据
     * 每个非叶子节点，除了负责根据父节点传递过来的位置信息，设置自己的位置数据外（如果有父节点的话
     * 还需要根据自己内部的layout规则（比如垂直排布等），计算出每一个子节点的位置信息，然后向子节点传递layout过程。
     * 对于ViewGroup，除了根据自己的parent传递的位置信息，来设置自己的位置之外
     * 还需要根据自己的layout规则，为每一个子View计算出准确的位置（相对于子View的父布局的位置）。
     * 对于View，根据自己的parent传递的位置信息，来设置自己的位置。
     *
     * 总结，简而言之，layout过程是为了确认如何摆放/排列子view的
     *
     * 布局流程实际上是一个复杂的过程，整个流程主要逻辑顺序如下：
     * 1. 决定是否需要重新进行测量流程onMeasure()；
     * 2. 将自身所在的位置信息进行保存；
     * 3. 判断本次布局流程是否引发了布局的改变；
     * 4. 若布局发生了改变，令所有子控件重新布局；
     * 5. 若布局发生了改变，通知所有观察布局改变的监听发送通知。
     * 整个布局过程中，除了4是ViewGroup自身需要做的，其它逻辑对于View和ViewGroup而言都是公共的——这说明单个View也是有布局流程的需求的。
     *
     * 问题：
     * 1. android:clipChildren属性实现原理？
     * 2. 既然布局流程是针对ViewGroup的，那么view为什么也会有？
     * 答：决定是否要重新测量以及之后的重新保存坐标信息等
     * 3. getWidth 和 getMeasuredWidth区别以及为什么两次的值可能不一样？
     * 答：getWidth是父布局确定大小后调用setFrame保存的值，getMeasuredWidth是view自身觉得自己要多大。
     * 那么为什么可能会不一样呢？因为假设子view想要100dp，但是在同一个前面有个更大的抢占了整个空间，那我自个只能小一点或者压根就没有布局空间了
     * 这样调用getWidth得到的大小肯定小于自身需求
     *
     * 注意：
     * 1. 为了保证'内部自制性'，子view的位置坐标是基于父ViewGroup，而非屏幕坐标系
     * 2. getWidth 和 getMeasuredWidth 的区别在于setFrame之后才会有getWidth，setMeasuredDimension之后有getMeasuredWidth
     * */

    abstract class View {

        fun layout(left: Int, top: Int, right: Int, bottom: Int) {
//            // 1.决定是否需要重新进行测量流程（onMeasure）
//            if(needMeasureBeforeLayout) {
//                onMeasure(mOldWidthMeasureSpec, mOldHeightMeasureSpec)
//            }
//
//            // 先将之前的位置信息进行保存
//            int oldL = mLeft;
//            int oldT = mTop;
//            int oldB = mBottom;
//            int oldR = mRight;
//            // 2.将自身所在的位置信息进行保存；
//            // 3.判断本次布局流程是否引发了布局的改变；
//            boolean changed = setFrame(l, t, r, b);
//
//            if (changed) {
//                // 4.若布局发生了改变，令所有子控件重新布局；
//                onLayout(changed, l, t, r, b);
//                // 5.若布局发生了改变，通知所有观察布局改变的监听发送通知
//                mOnLayoutChangeListener.onLayoutChange(this, l, t, r, b, oldL, oldT, oldR, oldB);
//            }
        }

        protected open fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {

        }

        //保存坐标信息
        protected open fun setFrame(left: Int, top: Int, right: Int, bottom: Int): Boolean {
            var changed = false;
            //save and do something
            return changed
        }

    }

    class ViewGroup : View() {

        override protected fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {

        }

    }

}