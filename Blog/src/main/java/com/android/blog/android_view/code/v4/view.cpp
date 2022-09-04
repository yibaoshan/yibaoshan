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

//描述View/ViewGroup的宽高值
class LayoutParams {

    int MATCH_PARENT = -1;
    int WRAP_CONTENT = -2;

    //视图的宽高属性，小于0表示为WRAP_CONTENT/MATCH_PARENT，大于0表示为具体的尺寸
    int width;
    int height;
}

//在宽高值的基础上增加了上下左右间距值，凡是支持设置margin的容器都是继承自MarginLayoutParams
class MarginLayoutParams extends ViewGroup.LayoutParams {

    leftMargin;
    topMargin;
    rightMargin;
    bottomMargin;
    ...
}


/frameworks/base/core/java/android/view/ViewGroup.java
class ViewGroup extends View {

    void measureChild(View child, int parentWidthMeasureSpec,int parentHeightMeasureSpec) {
        LayoutParams lp = child.getLayoutParams();//普通
        int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec, lp.width);
        int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec,lp.height);
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    void measureChildWithMargins(View child,int parentWidthMeasureSpec,int parentHeightMeasureSpec) {
        MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
        int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,lp.width);
        int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec, lp.height);
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    int getChildMeasureSpec(int spec, int padding, int childDimension) {
        //获取子View的MeasureSpec
    }

    //描述View/ViewGroup的宽高值
    class LayoutParams {

        int MATCH_PARENT = -1;
        int WRAP_CONTENT = -2;

        //视图的宽高属性，小于0表示为WRAP_CONTENT/MATCH_PARENT，大于0表示为具体的尺寸
        int width;
        int height;
    }

    //在宽高值的基础上增加了上下左右间距值，凡是支持设置margin的容器都是继承自MarginLayoutParams
    class MarginLayoutParams extends ViewGroup.LayoutParams {

        leftMargin;
        topMargin;
        rightMargin;
        bottomMargin;
        ...
    }

}

int getDefaultSize(int size, int measureSpec) {
    int result = getSuggestedMinimumWidth();//默认为android:minHeight属性的值或者View背景图片的大小值
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

//
protected int getSuggestedMinimumWidth() {
   return (mBackground == null) ? mMinWidth : max(mMinWidth, mBackground.getMinimumWidth());
}

void measureChild() {
    ViewGroup.LayoutParams lp = child.getLayoutParams();
    int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,lp.width);
    int childHeightMeasureSpec = MeasureSpec.makeSafeMeasureSpec(parentSize,MeasureSpec.UNSPECIFIED);

    child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
}
/frameworks/base/core/java/android/view/ViewRootImpl.java
class ViewRootImpl {


    DecorView mView;

    void performTraversals() {
        //最终Window的尺寸大小
        int desiredWindowWidth , desiredWindowHeight;
        if (mFirst) {
            if (shouldUseDisplaySize(lp)) {
                // NOTE -- system code, won't try to do compat mode.
                //默认为屏幕的尺寸
                Point size = new Point();
                desiredWindowWidth = size.x;
                desiredWindowHeight = size.y;
            } else {
                Configuration config = mContext.getResources().getConfiguration();
                desiredWindowWidth = dipToPx(config.screenWidthDp);
                desiredWindowHeight = dipToPx(config.screenHeightDp);
            }
        } else {
            //取缓存大小
            desiredWindowWidth = frame.width();
            desiredWindowHeight = frame.height();
        }
        if(layoutRequested){//APP发起requestLayout请求
            //执行测量工作，确定Window的尺寸大小，如果是Dialog或者Dialog形式的Activity，那么该window最好不要铺满全屏
            measureHierarchy();
        }
        //Window大小确定下来以后，去sf申请对应大小的surface
        relayoutWindow()
        if(首次添加视图/视图尺寸发生变化){
            //surface申请成功以后，再次执行测量工作
            //此方法结束后，该window的大小将会被确定，除非window的尺寸发生改变，否则不会再次执行该方法
            //在阅读源码的过程中，看到performTraversals()方法中调用了performMeasure()执行了测量
            //会认为最终调用到onMeasure方法的就是由它源头就在这
            //实际上，每当我们调用requestLayout方法请求重新测量时，发起者是上面的measureHierarchy()方法
            //除非首次添加视图或者Window的尺寸发生变化，否则该方法不会再次执行
            performMeasure();
        }

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
    boolean measureHierarchy(int desiredWindowWidth, int desiredWindowHeight){
        //如果DecorView是对话框或者是对话框形式的Activity，Android不希望它充满屏幕，所以在进入正式策略之前，需要先摸摸底，看看这个视图需要多大
        if (width == ViewGroup.LayoutParams.WRAP_CONTENT) {//Decor的宽度是warp，通常是Dialog或者是Dialog类型的Activity
            childWidthMeasureSpec = getRootMeasureSpec(baseSize, lp.width);//注意这里，屏幕的宽度被设置为了预设宽度
            childHeightMeasureSpec = getRootMeasureSpec(desiredWindowHeight, lp.height);
            //进行首次测量，我们可以利用Android Studio断点调试来验证
            performMeasure(childWidthMeasureSpec, childHeightMeasureSpec);
            //视图的期望宽度没有超过预置宽度，符合条件，进行下一步
            //MEASURED_STATE_TOO_SMALL表示测量尺寸小于视图想要的空间
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
        if (!goodMeasure) {
            //方法能走到这有两种情况：
            //1. 该视图是普通的Activity，DecorView宽高为match_parent
            //2. 该视图是Dialog，且Dialog想要的宽度很大，上面的预设宽度不满足，扩容以后还不满足，索性直接给它屏幕的宽度让它自己折腾去
            childWidthMeasureSpec = getRootMeasureSpec(desiredWindowWidth, lp.width);
            childHeightMeasureSpec = getRootMeasureSpec(desiredWindowHeight, lp.height);
            performMeasure(childWidthMeasureSpec,childHeightMeasureSpec);
        }
        return 和缓存窗口大小相比尺寸是否发生变化;
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
        //DecorView作为多叉树的根节点，向下深度优先遍历整棵树
        mView.layout(0, 0, mView.getMeasuredWidth(), mView.getMeasuredHeight());
    }

    void performDraw(){
    }

    void draw() {
        if(硬件绘制){
            mAttachInfo.mThreadedRenderer.draw(mView, mAttachInfo, this);
        } else {
            drawSoftware();
        }
    }

    void drawSoftware(){
        mView.draw();
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
        setFrame();//保存left，top，right，bottom，并回调onSizeChanged方法
        if(尺寸/位置发生变化){
            onLayout();
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

    //调用到各个View的onDraw()，接着，我们
    void draw(){
        drawBackground();//画背景，如果有的话
        onDraw();//
        dispatchDraw();//空方法，由ViewGroup实现
        onDrawForeground();
    }

    /**
     * This method is called by ViewGroup.drawChild() to have each child view draw itself.
     *
     * This is where the View specializes rendering behavior based on layer type,
     * and hardware acceleration.
     */
    boolean draw(ViewGroup parent){
        draw()
    }

    //描述View/ViewGroup的测量模式和视图大小
    //根据自身的LayoutParams和父视图的MeasureSpec决定
    static class MeasureSpec {

        static int UNSPECIFIED;//未指定，不限制大小
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

    void dispatchDraw(){
        if (clipToPadding) {
            clipSaveCount = canvas.save();
            //处理clipToPadding标签
        }
        //遍历绘制
        for (int i = 0; i < childrenCount; i++) {
            child.draw();
        }
        //绘制完毕，还原canvas
        if (clipToPadding) {
            canvas.restoreToCount(clipSaveCount);
        }
    }

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