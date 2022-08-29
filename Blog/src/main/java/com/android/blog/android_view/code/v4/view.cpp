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

/**

首先我们要明确一点，由于我们的视图是绑定在DecorView下的FrameLayout

所以传递到我们的根布局时，测量模式为

*/

//View/ViewGroup绘制的入口，从DecorView开始向下调用
/**
并不是每次vsync信号时都会发生measure、layout
*/
/frameworks/base/core/java/android/view/ViewRootImpl.java
class ViewRootImpl {

    DecorView mView;

    void performTraversals() {
        //最终DecorView拿到的屏幕宽高
        int desiredWindowWidth , desiredWindowHeight;
        if(首次添加视图/视图尺寸发生变化/窗口未停止活动){
            measureHierarchy();
        }
        //执行测量
        performMeasure();
        //WindowManager.LayoutParams中的垂直/水平方向的权重是否大于0，这玩意不知道在哪可以设置
        //要使一个Window全屏，我们可以调用window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)方法
        //Window竟然还可以设置权重我还真没用过
        if (lp.horizontalWeight/lp.verticalWeight > 0.0f){
            measureAgain = true;
        }
        if(measureAgain)performMeasure();
        ...
        performLayout();
    }

    //确定APP的测量模式和大小
    //执行完这一步以后，DecorView的宽高和模式都确定下来了，MeasureSpec
    //DecorView是FrameLayout，宽高都是MATCH_PARENT，所以通常走第一项case
    void measureHierarchy(int desiredWindowWidth, int desiredWindowHeight){
        //如果DecorView是对话框或者是对话框形式的Activity，Android不希望它充满屏幕，所以在进入正式策略之前，需要先摸摸底，看看这个视图需要多大
        if (width == ViewGroup.LayoutParams.WRAP_CONTENT) {//Decor的宽度是warp，通常是Dialog或者是Dialog类型的Activity
            childWidthMeasureSpec = getRootMeasureSpec(baseSize, lp.width);
            childHeightMeasureSpec = getRootMeasureSpec(desiredWindowHeight, lp.height);
            //进行首次测量，我们可以利用Android Studio断点调试来验证
            performMeasure(childWidthMeasureSpec, childHeightMeasureSpec);
            //视图的期望宽度没有超过预置宽度，符合条件，进行下一步
            if ((host.getMeasuredWidthAndState() & View.MEASURED_STATE_TOO_SMALL) == 0) {
                goodMeasure = true;
            } else {
                //视图的期望宽度超过了预置宽度，比如我在xml写死"layout_width=10086dp"，那么更改baseSize再次测量试一试
                baseSize = (baseSize + desiredWindowWidth) / 2;
                childWidthMeasureSpec = getRootMeasureSpec(baseSize, lp.width);
                performMeasure(childWidthMeasureSpec, childHeightMeasureSpec);
                if ((host.getMeasuredWidthAndState() & View.MEASURED_STATE_TOO_SMALL) == 0) {
                    goodMeasure = true;
                }
            }
        }
        //如果是普通的Activity，那么DecorView的宽高都是match_parent，执行测量
        if (!goodMeasure) {
            childWidthMeasureSpec = getRootMeasureSpec(desiredWindowWidth, lp.width);
            childHeightMeasureSpec = getRootMeasureSpec(desiredWindowHeight, lp.height);
            performMeasure(childWidthMeasureSpec,childHeightMeasureSpec);
        }
    }

    //从DecorView开始执行子view的measure
    void performMeasure(int childWidthMeasureSpec, int childHeightMeasureSpec) {
        //宽高为屏幕宽高（减去状态栏导航栏），模式为EXACTLY（精确）
        mView.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    int getRootMeasureSpec() {
        case ViewGroup.LayoutParams.MATCH_PARENT: return MeasureSpec.EXACTLY;//精确模式，大小为窗口宽高
        case ViewGroup.LayoutParams.WRAP_CONTENT: return MeasureSpec.AT_MOST; //最大模式，大小不定，不能超过窗口宽高
        return MeasureSpec.EXACTLY;//精确模式，窗口想要精确的大小，强制根视图为该大小
    }

    void performLayout(){
        mView.layout(0, 0, mView.getMeasuredWidth(), mView.getMeasuredHeight());
    }

}

/frameworks/base/core/java/android/view/View.java
class View {

    void invalidate(){
        //View不可见、没有动画等情况跳过
        if (skipInvalidate()) {
            return;
        }
        //设置重绘区域
        if (p != null && ai != null && l < r && t < b) {
            p.invalidateChild(this, damage);
        }

    }

    //http://www.aospxref.com/android-7.1.2_r39/xref/frameworks/base/core/java/android/view/View.java#19820
    //默认什么都不做的情况下，measure方法中会直接调用onMeasure()方法进行测量
    //widthMeasureSpec和heightMeasureSpec是父视图传递过来的
    //依据是父视图自身的spec和子View的LayoutParams
    //所以说，如果不考虑父视图的spec的情况下，这两个参数就是由自身的LayoutParams属性决定的
    //widthMeasureSpec、heightMeasureSpec是父视图为自己生成的测量模式
    void measure(int widthMeasureSpec, int heightMeasureSpec) {
        if(一堆判断条件：是否重新布局/宽高是否改变/有无缓存等等){
            onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    //onMeasure()方法中更直接，根据LayoutParams获取默认的MeasureSpec，接着设置View的大小
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    protected int getSuggestedMinimumHeight() {
        return (mBackground == null) ? mMinHeight : max(mMinHeight, mBackground.getMinimumHeight());
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

    //viewrootimpl调用传递过来的是0,0,屏幕宽度,屏幕高度
    void layout(int left,int top,int right,int bottom){
        setFrame();
        if(尺寸/位置发生变化){
        }
    }

    void setFrame(){
        onSizeChanged();
    }

    //changed标识视图的尺寸大小或者位置发生变化
    void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //根据LayoutParams规则确定子View摆放的位置
        //如果是LinearLayout，将每个子View按照居上/居下/居左/居右摆摆好
    }

    //描述View/ViewGroup的测量模式和视图大小
    //根据自身的LayoutParams和父视图的MeasureSpec决定
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

    //描述View/ViewGroup的宽高值
    class LayoutParams {

        static int FILL_PARENT = -1;//已废弃，等同于MATCH_PARENT
        static int MATCH_PARENT = -1;
        static int WRAP_CONTENT = -2;

        //视图的宽高属性，小于0表示为WRAP_CONTENT/MATCH_PARENT，大于0表示为具体的尺寸
        public int width;
        public int height;
    }

    //在宽高值的基础上增加了上下左右间距值，因此，凡是支持设置margin的容器都是继承自MarginLayoutParams
    class MarginLayoutParams extends ViewGroup.LayoutParams {

        public int leftMargin;
        public int topMargin;
        public int rightMargin;
        public int bottomMargin;
        ...
    }


}