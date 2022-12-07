package com.android.notebook.backup.android.framework.view

class ViewMeasure {

    /**
     * View布局测量三部曲：
     * 1. measure()
     * 父布局调用measure并传入宽高布局要求
     * 对于View的测量流程，其必然包含了2部分：公共逻辑部分 和 开发者自定义测量的逻辑部分
     * 开发者不能重写measure()函数，并将View自定义测量的策略通过定义一个新的onMeasure()接口暴露出来供开发者重写
     *
     * 2. onMeasure()：子类自定义实现
     *
     * 3. setMeasuredDimension()：标志测量的完成
     *
     * */

    /**
     *
     * ViewGroup布局测量三部曲：
     * 1. measure()：同上
     *
     * 2. onMeasure()：此处增加调用measureChild/measureChildWithMargins重新计算子控件布局要求
     *
     * 3. setMeasuredDimension()：同上
     *
     * */

    open class View {

        //执行测量的函数，参数中32位的int里包含测量模式，以及测量的大小
        final fun measure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            //为了保证公共部分的安全，此方法使用final修饰
            //在这里执行完公共部分的代码后，再调用onMeasure进行用户层级的测量
            onMeasure(widthMeasureSpec, heightMeasureSpec)
        }

        //真正执行测量的函数，开发者需要自己实现自定义的测量逻辑
        open fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            setMeasuredDimension(0, 0)
        }

        //完成测量的函数
        open fun setMeasuredDimension(measuredWidth: Int, measuredHeight: Int) {
            //至此，已经可以获取view的测量大小了
            //int width = view.getMeasuredWidth();
            //int height = view.getMeasuredHeight()
        }

    }

    class ViewGroup : View() {

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            // 1.通过遍历，对每个child进行测量
            var getChildCount = 0;
            for (i in 0..getChildCount) {
                var child: View? = null;
                // 2.计算新的布局要求，并对子控件进行测量
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
            }
            // ...
            // 3.所有子控件测量完毕...
            // ...
        }

        fun measureChildren(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        }

        //计算子控件的新的布局要求，让子控件根据新的布局要求进行测量
        fun measureChild(child: View?, parentWidthMeasureSpec: Int, parentHeightMeasureSpec: Int) {

        }

        fun measureChildWithMargins() {

        }

        //根据父布局的MeasureSpec和padding值，计算出对应子控件的MeasureSpec
        fun getChildMeasureSpec() {

        }

    }

    class MeasureSpec(mode: Mode, size: Int) {
        var size // 测量大小
                : Int = size
        var mode // 测量模式
                : Mode = mode

        enum class Mode {
            UNSPECIFIED, //父元素不对子元素施加任何束缚，子元素可以得到任意想要的大小；日常开发中自定义View不考虑这种模式，可暂时先忽略；
            EXACTLY, //父元素决定子元素的确切大小，子元素将被限定在给定的边界里而忽略它本身大小；这里我们理解为控件的宽或者高被设置为 match_parent 或者指定大小，比如20dp；
            AT_MOST//子元素至多达到指定大小的值；这里我们理解为控件的宽或者高被设置为wrap_content
        }

    }


}