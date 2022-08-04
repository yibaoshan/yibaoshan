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

#setView流程

/frameworks/base/core/java/android/app/ActivityThread.java
class ActivityThread {

    void main(){
        Looper.prepareMainLooper();
        Looper.loop();
    }

    class H extends Handler {
        void handleMessage(Message msg) {
            case LAUNCH_ACTIVITY::handleLaunchActivity();
            case RESUME_ACTIVITY::handleResumeActivity();
        }

        void handleLaunchActivity(){
            performLaunchActivity();//创建Activity实例对象并调用onCreate()
            handleResumeActivity();
        }

        void handleResumeActivity(){
            performResumeActivity()
            activity.makeVisible();
        }

        Activity performLaunchActivity(){
            Activity activity = new Activity();
            activity.attach();
            activity.onCreate();
            return activity;
        }

        void performResumeActivity(){
            activity.onResume();
        }

    }

}

/frameworks/base/core/java/android/app/Activity.java
class Activity {

    View mDecor;//用户设置的跟视图，通常会在ActivityThread中被赋值
    Window mWindow;//Activity首次被创建调用attach()方法时同步创建，创建动作在Activity
    WindowManager  mWindowManager;

    void onCreate(){
        setContentView();
    }

    void setContentView(View view) {
        mWindow.setContentView(view);
    }

    void attach(){
        mWindow = new PhoneWindow(this, window);//window参数从AMS创建传递到ActivityThread接着到Activity
        mWindow.setWindowManager(getSystemService(Context.WINDOW_SERVICE));
        mWindowManager = mWindow.getWindowManager();//获取WindowManager动作在Activity中，获取完成接着设置给自己的局部变量，这我是真的没想到，找的好辛苦
    }

    void makeVisible() {
        mWindowManager.addView(mDecor, getWindow().getAttributes());
    }

}

/frameworks/base/core/java/com/android/internal/policy/PhoneWindow.java
class PhoneWindow extends Window {

    // This is the top-level view of the window, containing the window decor.
    DecorView mDecor;//View的最底层，本质是个FrameLayout，内部包含title和content
    // This is the view in which the window contents are placed. It is either mDecor itself, or a child of mDecor where the contents go.
    ViewGroup mContentParent;//保存着我们设置的视图

    void setContentView(View view) {
        mDecor = new DecorView(this);
        mContentParent = findViewById(R.id.content);//获取title下面的那个
        mContentParent.addView(view);
        getCallback().onContentChanged();//回调Activity中onContentChanged()方法
    }

}

/frameworks/base/core/java/com/android/internal/policy/DecorView.java
/** @hide */
class DecorView extends FrameLayout {

    //DecorView和PhoneWindow互相持有，啧啧啧
    PhoneWindow mWindow;

    DecorView(PhoneWindow window){
        mWindow = window;
    }

}

/frameworks/base/core/java/android/view/ViewManager.java
public interface ViewManager
{
    public void addView(View view, ViewGroup.LayoutParams params);
    public void updateViewLayout(View view, ViewGroup.LayoutParams params);
    public void removeView(View view);
}

/frameworks/base/core/java/android/view/WindowManager.java
public interface WindowManager extends ViewManager {

}

/frameworks/base/core/java/android/view/WindowManagerImpl.java
public class WindowManagerImpl implements WindowManager {

    WindowManagerGlobal mGlobal = WindowManagerGlobal.getInstance();

    void addView(View view) {
        mGlobal.addView(view);
    }

}

/frameworks/base/core/java/android/view/View.java
class View {

    class AttachInfo {

        ViewRootImpl mViewRootImpl;

        AttachInfo(ViewRootImpl viewRootImpl){
            mViewRootImpl = viewRootImpl;
        }

    }
}

//全局单例，和WMS建立连接通信
class WindowManagerGlobal {

	List<View> mViews;
	List<ViewRootImpl> mRoots;

	void addView(View view){
		ViewRootImpl root = new ViewRootImpl(view);
		mViews.add(view);
		mRoots.add(root);
	}

}

//对应一个Activity
class ViewRootImpl {

    View mView;

    final Surface mSurface = new Surface();

    void setView(View view){
        mView = view;//将DecorView保存到ViewRootImpl的成员变量mView中
        requestLayout();//请求vsync信号
      	res = mWindowSession.addToDisplay();//背后又是老长一串调用链，就不展开跟了，大致流程是在wms服务中创建了WindowState对象
    }

    void requestLayout() {
        scheduleTraversals();
    }

    void scheduleTraversals() {
        if (!mTraversalScheduled) {
            mTraversalBarrier = mHandler.getLooper().getQueue().postSyncBarrier();//创建一个同步屏障（详见Android消息机制）
            mChoreographer.postCallback(Choreographer.CALLBACK_TRAVERSAL, mTraversalRunnable, null);//发送一条异步消息，mTraversalRunnable是处理这条消息的回调
            notifyRendererOfFramePending();
            pokeDrawLockIfNeeded();
        }
    }

    void doTraversal() {
        if (mTraversalScheduled) {
            mTraversalScheduled = false;
            mHandler.getLooper().getQueue().removeSyncBarrier(mTraversalBarrier);//移除同步屏障

            if (mProfile) {
                Debug.startMethodTracing("ViewAncestor");
            }

            performTraversals();//View的绘制起点

            if (mProfile) {
                Debug.stopMethodTracing();
                mProfile = false;
            }
        }
    }

}
