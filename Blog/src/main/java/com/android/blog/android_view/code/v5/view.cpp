//
// Created by Bob on 2022/9/13.
//

/frameworks/base/core/java/android/view/ViewRootImpl.java
class ViewRootImpl {
    void addView(){
        mInputEventReceiver = new WindowInputEventReceiver(mInputChannel, Looper.myLooper());
    }

    class WindowInputEventReceiver extends InputEventReceiver {
        void onInputEvent(InputEvent event);
    }
}

/frameworks/base/services/java/com/android/server/SystemServer.java
class SystemServer {

    void startOtherServices() {
        InputManagerService inputManager = new InputManagerService(context);
        WindowManagerService wm = new WindowManagerService(context);
        wm.start(inputManager);//window持有input
        inputManager.setWindowManagerCallbacks(wm);
        inputManager.start();
    }
}

/frameworks/base/services/core/java/com/android/server/input/InputManagerService.java
class InputManagerService {

    public InputManagerService() {
        nativeInit();
    }
}

/frameworks/base/services/core/jni/com_android_server_input_InputManagerService.cpp
class NativeInputManager {

    NativeInputManager(){
        sp<EventHub> eventHub = new EventHub();
        mInputManager = new InputManager();
    }

    static nativeInit(){
        NativeInputManager im = new NativeInputManager();
    }

}

/frameworks/native/services/inputflinger/InputManager.cpp
class InputManager {

    //创建读线程和分发线程
    InputManager(){
        mDispatcher = new InputDispatcher();//内部会创建 InputDispatcherThread 线程
        mReader = new InputReader(eventHub, mDispatcher);//内部创建 InputReaderThread 线程
    }
}

/frameworks/native/services/inputflinger/InputReader.cpp
class InputReader {

    void InputReader::loopOnce() {
        int count = mEventHub->getEvents();//读消息，有消息返回，没消息阻塞到epoll()
        if(count) processEventsLocked();//分发原始事件
    }

    void processEventsLocked(RawEvent ev){
        InputDevice:process();//解析原始事件，得到 NotifyMotionArgs 类型消息
        InputDispatcher::notifyMotion();//分发输入事件
    }
}

/frameworks/native/services/inputflinger/InputDispatcher.cpp
class InputDispatcher {
    const nsecs_t DEFAULT_INPUT_DISPATCHING_TIMEOUT = 5000 * 1000000LL; // 5 sec
    const nsecs_t APP_SWITCH_TIMEOUT = 500 * 1000000LL; // 0.5sec
    const nsecs_t STALE_EVENT_TIMEOUT = 10000 * 1000000LL; // 10sec
    const nsecs_t STREAM_AHEAD_EVENT_TIMEOUT = 500 * 1000000LL; // 0.5sec

    void notifyMotion(const NotifyMotionArgs* args) {
        MotionEntry* newEntry = new MotionEntry(args);//封装成entry
        enqueueInboundEventLocked(newEntry);//入列一个节点，等待分发被执行，逻辑在 dispatchOnce()
    }

    void dispatchOnce() {
        if(!queue.isEmpty()) dispatchOnceInnerLocked();//有消息就分发
    }

    //判断消息的类型：配置更改、插拔消息、key事件、触摸事件
    void dispatchOnceInnerLocked() {
        if(EventEntry::TYPE_MOTION) dispatchMotionLocked();//我们这只关注触摸事件
    }

    void dispatchMotionLocked() {
        if(isPointerEvent) findTouchedWindowTargetsLocked();//找到需要分发触摸事件的window
        else findFocusedWindowTargetsLocked();//轨迹球，查了一下是一种特殊的鼠标，忽略
        dispatchEventLocked();//分发事件
    }

    int findTouchedWindowTargetsLocked(){//获取正在被触摸的window，触摸事件用
        size_t numWindows = mWindowHandles.size();
        for (size_t i = 0; i < numWindows; i++);//从前向后遍历所有的window以找出触摸的window，将满足条件的放入inputTargets，没找到返回第一个前台window
    }

    int findFocusedWindowTargetsLocked();//获取获取焦点的window，鼠标事件用

    void dispatchEventLocked(Vector<InputTarget>& inputTargets) {
        for (size_t i = 0; i < inputTargets.size(); i++) {
            InputChannel channel = inputTarget.inputChannel;//删减流程
            channel->sendMessage(&msg);//发送跨进程消息
        }
    }

    bool threadLoop() {
        dispatchOnce();
    }
}
