//
// Created by Bob on 2022/7/30.
//

#include "merge.h"

SurfaceFlinger {
    frameworks/native/services/surfaceflinger/MessageQueue.cpp
        ->init()
        ->waitMessage()
        ->invalidate()
}

#activity生命周期与视图的关系

class Activity {

    //AMS-创建实例对象后回调该方法
    void onCreate(){
    }

    //AMS-
    void onResume(){
    }

    void onPause(){
    }

    //AMS调用-同步给WMS
    void onStop(){
    }

    void onDestroy(){
    }

}

#setView流程

//三个阶段：创建APP进程、加载视图文件、将视图文件传递给为wms、sf进程的连接

/*

图形系统需要分为两个部分来看，静态和动态

静态部分，由厂家的GPU驱动和Google提供的hal、hwc、库等组成，作为整个图形系统存在的基石，为整个图形系统提供支撑
动态部分同样由两部分组成：
后台部门：由sf进程控制着内存的流动方向，APP进程向sf申请一块内存，绘制完成后还给sf进程，在整个流转过程中，system_server进程中的wms负责对View的操作进行封装，ams甚至可有可无
前台部分：由system_server进程管理，主要是ams、wms作为业务支撑部门，提供了多种添加视图的方式，toast、dialog等，wms封装了对view的操作，ams

文章的结构是先介绍了静态部分，动态部分以vsync到来前各个进程做了哪些准备和vsync到来后各个进程各自完成了哪些工作
正是因为有各个进程的互相配合，才能给

Activity对象的创建
视图对象的创建
ViewRootImpl的创建

在第一个阶段，ActivityThread.main()
ActivityThread，入口函数中初始化handler机制
ApplicationThread，ams传话筒
Activity，开发者的主战场
PhoneWindow，空壳子
WindowManager，wms的代理对象

在第二个阶段，Activity.setContentView
调用PhoneWindow设置视图
根据不同主题，为DecorView设置不同的子View（无论使用哪种主题视图，其中必然包含名为content的FrameLayout）
将开发者的布局文件添加到子View名为content的FrameLayout当中
为PhoneWindow设置DecorView

在第三个阶段，创建ViewRootImpl
1. Choreographer让它能够感知事件
2. 保存DecorView让它能够在事件来临时控制视图
3. Surface让它拥有绘图的能力
通过addToDisplay()方法推送到wms

App进程的Vsync信号由DisplayEventReceiver触发
大致的调用流程是
DisplayEventReceiver::sendEvents(native)
    DisplayEventDispatcher::handleEvent(native)
        DisplayEventReceiver.dispatchVsync(java)
            Choreographer.doFrame(java)
                ViewRootImpl.doTraversal(java)


sf进程和app进程的vsync各自管理

假设一个全屏的APP，那么它们的节奏是这样

app-请求
APP页面元素一旦发生变化，调用invalidate()方法请求下一次Vsync信号，此时sf什么都不做

app-vsync & sf-请求
app-vsync信号到来后，APP进程执行绘图三部曲，sf收到onFrameAvailable()，此时sf进程在请求vsync

sf-vsync
sf-vsync信号到来，sf进程执行合成五部曲，接着将layer交给hwc

hw-vsync
原始硬件的vsync一直在发生，接不接受取决于业务方的需求
hw-vsync信号到来，PageFlip，framebuffer发生切换，展示给用户

**/


#结语

/*

*/

/frameworks/base/core/java/android/app/ActivityThread.java
class ActivityThread {

    //zygote进程fork成功后调用入口函数
    void main(){
        Looper.prepareMainLooper();
        attach();//attach方法和ams建立连接，提供给ams控制四大组件的句柄
        Looper.loop();
    }

    //分两步解释更容易理解
    //1. 不管是从桌面点击图标进入还是adb命令启动，最终都交由ams发送启动请求给zygote进程，接着zygote孵化出该APP进程调用main方法
    //2. APP进程启动将创建ApplicationThread对象，并发起IPC把此对象传递给ams，此后四大组件相关回到都将有ApplicationThread对象负责，最终转发给H类执行
    void attach() {
        //获取ams代理并将ApplicationThread将给ams，这个对象以后将是ams的传声筒
        IActivityManager mgr = ActivityManagerNative.getDefault();
        mgr.attachApplication(new ApplicationThread());
    }

    //ApplicationThreadNative封装一系列的关于四大组件回调方法的跨进程通信命令
    //ApplicationThread对象所有操作几乎都由AMS发起调用
    class ApplicationThread extends ApplicationThreadNative {

        void scheduleLaunchActivity(){
            handleMessage(LAUNCH_ACTIVITY);
        }

    }

    class H extends Handler {

        //转发来自ApplicationThread的消息
        void handleMessage(Message msg) {
            case LAUNCH_ACTIVITY::handleLaunchActivity();
            case RESUME_ACTIVITY::handleResumeActivity();
        }

        //转发来自handleMessage的消息
        void handleLaunchActivity(){
            performLaunchActivity();
            handleResumeActivity();
        }

        //转发来自handleMessage的消息
        void handleResumeActivity(){
            performResumeActivity()
            activity.makeVisible();//调用此方法说明第二阶段视图加载已经完成，准备提交到wms服务
        }

        //执行创建Activity对象并回调生命周期
        Activity performLaunchActivity(){
            Activity activity = new Activity();
            activity.attach();//回调attach
            activity.onCreate();//回调Activity
            return activity;
        }

        //执行回调生命周期
        void performResumeActivity(){
            activity.onResume();
        }

    }

}

/frameworks/base/core/java/android/app/Activity.java
class Activity {

    View mDecor;//用户设置的跟视图，通常会在ActivityThread中被赋值
    Window mWindow;//Activity首次被创建调用attach()方法时同步创建，创建动作在Activity
    WindowManager  mWindowManager;//在attach方法中被创建

    //1. 创建PhoneWindow保存到变量mWindow，此时的Window还没有View视图
    //2. 获取wms代理对象，塞到刚刚创建的window对象当中，同时保存到本地mWindowManager变量
    void attach(Window window){
        mWindow = new PhoneWindow(this, window);
        mWindow.setWindowManager(getSystemService(Context.WINDOW_SERVICE));
        mWindowManager = mWindow.getWindowManager();//获取WindowManager动作在Activity中，获取完成接着设置给自己的局部变量，这我是真的没想到，找的好辛苦
    }

    //至此，APP进程启动成功，第一阶段结束，准备进入第二阶段
    void onCreate(){
        setContentView();
    }

    //第二阶段开始：加载视图文件并绑定到DecorView
    void setContentView(View view) {
        mWindow.setContentView(view);
    }

    //第二阶段已经完成，准备进入第三阶段
    void onContentChanged(){
    }

    //第三阶段开始：将视图传递给wms
    //makeVisible()在ActivityThread.H.handleResumeActivity()方法中被调用
    //此阶段完成后会请求vsync信号，并在下一次vsync到来时绘制View树，在下下次sf进程合成，在下下下次展示给用户，整个流程如下：
    //vsync->view.draw()
    //     vysnc->sf.compose()
    //          vsync->drm.flip() 用户可以看到
    void makeVisible() {
        mWindowManager.addView(mDecor);
    }

}

/frameworks/base/core/java/com/android/internal/policy/PhoneWindow.java
class PhoneWindow extends Window {

    DecorView mDecor;
    ViewGroup mContentParent;

    //1. 创建DecorView对象
    //2. 将开发者设置的视图文件作为子View添加到mContentParent
    //3. 通知Activity中onContentChanged方法
    void setContentView(View view) {
        mDecor = generateDecor();
        mContentParent = generateLayout();//看generateLayout方法的注释
        mContentParent.addView(view);//将开发者设置的视图添加为子View
        getCallback().onContentChanged();//回调Activity中onContentChanged()方法
    }

    //创建一个空的DecorView，也就是FrameLayout，里面啥也没有
    void generateDecor() {
        return new DecorView(this);
    }

    //1. 根据不同主题设置不同布局文件，加载该布局文件并设置成DecorView的子View
    //2. 返回子View中id为content的ViewGroup，通常还是个FrameLayout
    //以上两步执行完成以后，DecorView的布局变成：
    //<FrameLayout>//DecorView的根布局
    //  <LinearLayout>//开发者设置带有ActionBar的主题，注意，这里的视图可变的，根据主题来选择不同的视图
    //      <ActionBar/>
    //      <FrameLayout
    //      android:id="@android:id/content"/>//这里的FrameLayout才是最终包含开发者在setContentView中设置的布局
    //  </LinearLayout>
    //</FrameLayout>
    void generateLayout() {
        //加载不同的theme主题的布局文件，比如我们在xml中指定android:theme=@style/NoActionBar
        View root = inflater.inflate(layoutResource);
        //将上一步解析的视图作为根布局添加到DecorView，常见的比如垂直方向的LinearLayout，这样布局DecorView
        mDecor.addView(root);
        //找到用来装用户视图的ViewGroup，通常还是个FrameLayout
        ViewGroup contentParent = mDecor.findViewById(R.id.content);
        return contentParent;
    }

}

//自身无逻辑，可以跳过
/frameworks/base/core/java/com/android/internal/policy/DecorView.java
class DecorView extends FrameLayout {

    //DecorView和PhoneWindow互相持有，这代码写的，啧啧啧
    PhoneWindow mWindow;

    DecorView(PhoneWindow window){
        mWindow = window;
    }

}

//定义View操作接口，顶级接口
/frameworks/base/core/java/android/view/ViewManager.java
public interface ViewManager
{
    public void addView();
    public void updateViewLayout();
    public void removeView();
}

//啥也不是
/frameworks/base/core/java/android/view/WindowManager.java
public interface WindowManager extends ViewManager {

}

//WindowManager的最终实现
/frameworks/base/core/java/android/view/WindowManagerImpl.java
public class WindowManagerImpl implements WindowManager {

    WindowManagerGlobal mGlobal = WindowManagerGlobal.getInstance();

    void addView(View decorView) {
        mGlobal.addView(decorView);
    }

}

//全局单例，和WMS建立连接通信，也是APP进程中，所有窗口实际的管理者
//内部mViews和mRoots变量保存着所有创建的Activity对应的View和ViewRootImpl
/frameworks/base/core/java/android/view/WindowManagerGlobal.java
class WindowManagerGlobal {

	List<View> mViews;
	List<ViewRootImpl> mRoots;

	void addView(View decorView){
		ViewRootImpl root = new ViewRootImpl(decorView);
		mViews.add(decorView);
		mRoots.add(root);
		// do this last because it fires off messages to start doing things
		root.setView(view);
	}

}

//对应一个Activity，关于视图的事件触发都在此
//1. Choreographer让它能够感知事件
//2. 保存DecorView让它能够在事件来临时控制视图
//3. Surface让它拥有绘图的能力
/frameworks/base/core/java/android/view/ViewRootImpl.java
class ViewRootImpl {

    Choreographer mChoreographer;//构造函数中被创建
    View mView;//保存DecorView

    final Surface mSurface = new Surface();

    public ViewRootImpl(){
        //可以感知vsync的原因可以追溯到libgui库中的DisplayEventReceiver类
        mChoreographer = Choreographer.getInstance();
    }

    //1. 请求vsync信号，等待vsync来临后绘图
    //2. 创建binder代理对象传递给wms，此后wms将通过此代理对象来通知APP进程应该做什么事
    void setView(View decorView){
        mView = decorView;//将DecorView保存到ViewRootImpl的成员变量mView中
        requestLayout();//请求vsync信号
        //通过binder向wms添加窗口
      	res = mWindowSession.addToDisplay();
    }

    void requestLayout() {
        scheduleTraversals();
    }

    //请求vsync，等待刷新
    void scheduleTraversals() {
        //发送同步屏障消息的意义在于，保证vsync信号到来时，第一时间执行ViewRootImpl.doTraversal()方法
        mTraversalBarrier = mHandler.getLooper().getQueue().postSyncBarrier();//创建一个同步屏障（详见Android消息机制）
        mChoreographer.postCallback(Choreographer.CALLBACK_TRAVERSAL, mTraversalRunnable, null);//发送一条异步消息，mTraversalRunnable是处理这条消息的回调
        notifyRendererOfFramePending();
        pokeDrawLockIfNeeded();
    }

    final TraversalRunnable mTraversalRunnable = new TraversalRunnable();

    class TraversalRunnable implements Runnable {

        public void run() {
            doTraversal();
        }
    }

    //开始刷新
    void doTraversal() {
        if (mTraversalScheduled) {
            mTraversalScheduled = false;
            mHandler.getLooper().getQueue().removeSyncBarrier(mTraversalBarrier);//移除同步屏障
            //由于同步屏障消息被移除，所以view的绘制工作和主线程的消息处理是一起在执行的
            performTraversals();//View的绘制起点
        }
    }

    //绘图三部曲
    void performTraversals(){
        relayoutWindow();//向sf正式申请surface，在进入绘图之前为APP进程准备好一块surface内存
        mAttachInfo.mHardwareRenderer.initialize(mSurface);
        performMeasure();//Ask host how big it wants to be
        performLayout();
        performDraw();
    }

    void performMeasure(){
        mView.measure();
    }

    void performLayout(){
        mView.layout();
    }

    void performDraw(){
        mView.draw();
        mAttachInfo.mHardwareRenderer.draw(mView, mAttachInfo, this);
        ThreadedRenderer->draw()
            ->updateViewTreeDisplayList()
            ->View.
    }

    //创建surface
    //viewrootimpl持有的surface是Java对象，并没有在native创建对应的surface
    //不过这一些对于APP进程来说是无感的，APP->WMS->SF->WMS->APP，在这个过程中APP
    //在此方法中，调用wms为其创建native层的surface对象，在surface创建的过程中，会通知sf进程，sf进程为surface创建对应的layer，创建layer的过程中，又会初始化BufferQueue对象
    //surface中包含bufferqueue，所以sf进程除了为surface创建layer，还会为surface创建队列监听，当有新的视图变化，sf进程将会收到onFrameAvaliable()回调
    //该方法调用链稍微有点长，不在此展开讨论，大致流程是这样：
    //APP进程告知
    int relayoutWindow(){
    }

}

/frameworks/base/core/java/android/view/View.java
class View {

    /**
     * This method is called by ViewGroup.drawChild() to have each child view draw itself.
     *
     * This is where the View specializes rendering behavior based on layer type,
     * and hardware acceleration.
     */
    void draw(Canvas canvas, ViewGroup parent, long drawingTime) {
        final boolean hardwareAcceleratedCanvas = canvas.isHardwareAccelerated();
        /* If an attached view draws to a HW canvas, it may use its RenderNode + DisplayList.
         *
         * If a view is dettached, its DisplayList shouldn't exist. If the canvas isn't
         * HW accelerated, it can't handle drawing RenderNodes.
         */

    }

}
