//
// Created by Bob on 2022/8/22.
//

/*

首先我们要知道，measure这一步是为了确定所有view的大小

明确了目标以后，我们来大致需要做哪些事情？

measure的入口？
decorview是跟视图，decorview有多大？以及它是什么时候确定的？
margin和padding
执行测量的过程是怎样的？

1. 确定decorview的大小和测量模式

2. 测量view的大小
    1. 调用onMeasure()传递测量模式/大小执行实际的测量工作

*/

/**

测量是最复杂的一步，需要处理warp_content、match_content、padding、margin等各种情况

测量分为两种情况

在自定义View时，如果

我们知道DecorView最终是通过调用onMeasure()方法来完成测量工作，

对于自定义ViewGroup来说，另一个需要关心的参数是LayoutParams

*/

//View/ViewGroup绘制的入口，从DecorView开始向下调用
/frameworks/base/core/java/android/view/ViewRootImpl.java
class ViewRootImpl {

    DecorView mView;

    void performTraversals() {
        //最终DecorView拿到的屏幕宽高
        int desiredWindowWidth , desiredWindowHeight;
        measureHierarchy(desiredWindowWidth, desiredWindowHeight);
    }

    //确定APP的测量模式和大小
    //执行完这一步以后，DecorView的宽高和模式都确定下来了，MeasureSpec
    //DecorView是FrameLayout，宽高都是MATCH_PARENT，所以通常走第一项case
    void measureHierarchy(int desiredWindowWidth, int desiredWindowHeight){
        int childWidthMeasureSpec = getRootMeasureSpec();
        int childHeightMeasureSpec = getRootMeasureSpec();
        performMeasure(childWidthMeasureSpec,childHeightMeasureSpec);
    }

    //从DecorView开始执行子view的measure
    void performMeasure(int childWidthMeasureSpec, int childHeightMeasureSpec) {
        mView.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    int getRootMeasureSpec() {
        case ViewGroup.LayoutParams.MATCH_PARENT: return MeasureSpec.EXACTLY;//精确模式，大小为窗口宽高
        case ViewGroup.LayoutParams.WRAP_CONTENT: return MeasureSpec.AT_MOST; //最大模式，大小不定，不能超过窗口宽高
        return MeasureSpec.EXACTLY;//精确模式，窗口想要精确的大小，强制根视图为该大小
    }

}

/frameworks/base/core/java/android/view/View.java
class View {

    //http://www.aospxref.com/android-7.1.2_r39/xref/frameworks/base/core/java/android/view/View.java#19820
    //默认什么都不做的情况下，measure方法中会直接调用onMeasure()方法进行测量
    void measure(int widthMeasureSpec, int heightMeasureSpec) {
        onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    //onMeasure()方法中更直接，根据LayoutParams获取默认的MeasureSpec，接着设置View的大小
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    //记录测量的宽高
    void setMeasuredDimension(){
        mMeasuredWidth = measuredWidth;
        mMeasuredHeight = measuredHeight;
    }

    /**
    getDefaultSize()方法中 wrap_content 和 match_parent 属性的效果是一样的
    而该方法是 View 的 onMeasure()中默认调用的
    也就是说，对于一个直接继承自 View 的自定义 View 来说
    它的 wrap_content 和 match_parent 属性是一样的效果
    因此如果要实现自定义 View 的 wrap_content
    则要重写 onMeasure() 方法，对 wrap_content 属性进行处理
    */
    int getDefaultSize(int size, int measureSpec) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
        case MeasureSpec.UNSPECIFIED:
            result = size;
            break;
        case MeasureSpec.AT_MOST:
        case MeasureSpec.EXACTLY:
            result = specSize;
            break;
        }
        return result;
    }

    static class MeasureSpec {

        static int UNSPECIFIED;//未指定，不限制大小，
        static int EXACTLY;//精确模式，match_parent或者指定尺寸可用
        static int AT_MOST;//最大模式，warp_content可用

        public static int getMode(int measureSpec) {
            return (measureSpec & MODE_MASK);
        }

        public static int getSize(int measureSpec) {
            return (measureSpec &  ~MODE_MASK);
        }

    }

}

/frameworks/base/core/java/android/view/ViewGroup.java
class ViewGroup {

    void measureChildWithMargins(View child,int parentWidthMeasureSpec,int parentHeightMeasureSpec) {
        //确认子view的MeasureSpec
        int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec);
        int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec);
        //执行子view测量过程
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    int getChildMeasureSpec(int spec, int padding, int childDimension) {
        int specMode = MeasureSpec.getMode(spec);
        int specSize = MeasureSpec.getSize(spec);
        switch (specMode) {
        // Parent has imposed an exact size on us
        case MeasureSpec.EXACTLY:
            if (childDimension >= 0) {
                resultSize = childDimension;
                resultMode = MeasureSpec.EXACTLY;
            } else if (childDimension == LayoutParams.MATCH_PARENT) {
                // Child wants to be our size. So be it.
                resultSize = size;
                resultMode = MeasureSpec.EXACTLY;
            } else if (childDimension == LayoutParams.WRAP_CONTENT) {
                // Child wants to determine its own size. It can't be
                // bigger than us.
                resultSize = size;
                resultMode = MeasureSpec.AT_MOST;
            }
            break;

        // Parent has imposed a maximum size on us
        case MeasureSpec.AT_MOST:
            if (childDimension >= 0) {
                // Child wants a specific size... so be it
                resultSize = childDimension;
                resultMode = MeasureSpec.EXACTLY;
            } else if (childDimension == LayoutParams.MATCH_PARENT) {
                // Child wants to be our size, but our size is not fixed.
                // Constrain child to not be bigger than us.
                resultSize = size;
                resultMode = MeasureSpec.AT_MOST;
            } else if (childDimension == LayoutParams.WRAP_CONTENT) {
                // Child wants to determine its own size. It can't be
                // bigger than us.
                resultSize = size;
                resultMode = MeasureSpec.AT_MOST;
            }
            break;

        // Parent asked to see how big we want to be
        case MeasureSpec.UNSPECIFIED:
            if (childDimension >= 0) {
                // Child wants a specific size... let him have it
                resultSize = childDimension;
                resultMode = MeasureSpec.EXACTLY;
            } else if (childDimension == LayoutParams.MATCH_PARENT) {
                // Child wants to be our size... find out how big it should
                // be
                resultSize = View.sUseZeroUnspecifiedMeasureSpec ? 0 : size;
                resultMode = MeasureSpec.UNSPECIFIED;
            } else if (childDimension == LayoutParams.WRAP_CONTENT) {
                // Child wants to determine its own size.... find out how
                // big it should be
                resultSize = View.sUseZeroUnspecifiedMeasureSpec ? 0 : size;
                resultMode = MeasureSpec.UNSPECIFIED;
            }
            break;
        }
        return MeasureSpec.makeMeasureSpec(resultSize, resultMode);
    }

    class LayoutParams {
        static int MATCH_PARENT = -1;
        static int WRAP_CONTENT = -2;
    }

}