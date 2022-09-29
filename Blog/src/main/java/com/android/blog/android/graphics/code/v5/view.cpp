//
// Created by Bob on 2022/9/13.
//

/frameworks/base/core/java/android/view/ViewRootImpl.java
class ViewRootImpl {

    InputChannel mInputChannel;

    void setView(){
        mInputChannel = new InputChannel();
        Session.addToDisplay(mInputChannel);//向wms添加窗口，最终调用到WindowManagerService#addWindow()方法
        //这里的调用链是这样的，native 层的 NativeInputEventReceiver 将 InputChannel的fd 加入到 Native Looper中进行监听
        mInputEventReceiver = new WindowInputEventReceiver(mInputChannel, Looper.myLooper());//客户端创建socket连接
    }

    class WindowInputEventReceiver extends InputEventReceiver {
        void onInputEvent(InputEvent event);
    }
}

/frameworks/base/services/core/java/com/android/server/wm/Session.java
class Session {

    void addToDisplay(InputChannel inputChannel){
        WindowManagerService.addWindow(inputChannel);
    }

}

/frameworks/base/services/core/java/com/android/server/wm/WindowManagerService.java
class WindowManagerService {

    int addWindow(InputChannel outInputChannel){
        WindowState win = new WindowState();//首次添加视图时创建，用于描述一个window
        win.openInputChannel(outInputChannel);
    }

}

/frameworks/base/services/core/java/com/android/server/wm/WindowState.java
class WindowState {

    void openInputChannel(InputChannel outInputChannel) {
        InputChannel[] inputChannels = InputChannel.openInputChannelPair();
        mInputChannel = inputChannels[0];
        mClientChannel = inputChannels[1];
        WindowManagerService.InputManager.registerInputChannel(mInputChannel);//通过wms持有的inputManager调用它的注册方法
    }
}

/frameworks/base/core/java/android/view/InputChannel.java
class InputChannel {

    static InputChannel[] openInputChannelPair() {
        return nativeOpenInputChannelPair();//native调用了socketpair()创建一对本地socket对象，被封装成两个 InputChannel
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

    void registerInputChannel(){
        //native层调用getDispatcher()方法获取 InputDispatcher 对象，然后，调用 registerInputChannel() 方法将此channel注册到 InputDispatcher，等到事件发生
        //此方法结束后，如果有触摸事件发生，ims会收到
        nativeRegisterInputChannel(mPtr, inputChannel, inputWindowHandle, false);
    }
}

//------------------------------------------------------------------------------native分界线----------------------------------------------------------------------------------------------------------

/frameworks/base/services/core/jni/com_android_server_input_InputManagerService.cpp
class NativeInputManager {

    NativeInputManager(){
        sp<EventHub> eventHub = new EventHub();
        mInputManager = new InputManager();
    }

    static nativeInit(){
        NativeInputManager im = new NativeInputManager();
    }

    void nativeRegisterInputChannel(){
        InputManager->getDispatcher()->registerInputChannel(
                inputChannel, inputWindowHandle, monitor);
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

    //注册监听，用于方法内部会创建一个新的connection连接，将 inputChannel 的fd 添加到 looper 监听
    int registerInputChannel() {
        sp<Connection> connection = new Connection();
        int fd = inputChannel->getFd();
        mConnectionsByFd.add(fd, connection);
        mLooper->addFd(fd, 0, ALOOPER_EVENT_INPUT, handleReceiveCallback, this);
    }

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
            InputChannel channel = inputTarget.inputChannel;//删减过的流程
            channel->sendMessage(&msg);//给能够被触摸的window发送跨进程消息
        }
    }

    bool threadLoop() {
        dispatchOnce();
    }
}

//-----------------------------------------------------------------------Linux分界线----------------------------------------------------------------

//drivers/input/input.c
//核心层，为驱动层提供设备注册，和操作接口，是 input 子系统的核心类
class input {

    //注册一个 input 设备到内核
    int input_register_device(input_dev *dev){
    }

    //从内核注销掉一个 input 设备
    void input_unregister_device(input_dev *dev){
    }

    int input_attach_handler(input_dev *dev, input_handler *handler){
        handler->evdev_connect();
    }

    //设备上报一个 input 事件，input 子系统对其处理
    void input_event(input_dev *dev, type, code, value){
        input_handle_event(dev, type, code, value);
    }

    void input_handle_event(input_dev *dev, type, code, value){
    }
}

//drivers/input/evdev.c
//事件层，和用户空间进行交互，提供事件监听功能。类似的类还有 mousedev.c 、keyboard.c
class evdev {

    //选择一个事件设备进行连接
    int evdev_connect(input_handler *handler, input_dev *dev, input_device_id *id){
    }

    //取消监听
    void evdev_disconnect(input_handle *handle){
    }
}