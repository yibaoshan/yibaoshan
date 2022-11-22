//
// Created by Bob on 2022/9/13.
//

/*

frameworks/native/services/inputflinger/
  - InputDispatcher.cpp // 在初始化 InputManager 时被创建
    - class InputDispatcherThread
  - InputReader.cpp // 系统启动后，运行在 Reader 线程，在初始化 InputManager 时被创建
    - class InputReaderThread
  - InputManager.cpp // 管理 reader 和 dispatcher 线程 ，没有业务逻辑，不重要。在初始化 NativeInputManager 时，被同步创建
  - EventHub.cpp // 事件中心，利用 epoll 监听设备文件，创建时机和 InputManager 相同，在初始化 NativeInputManager 时，被同步创建
  - InputListener.cpp

frameworks/native/libs/input/
  - InputTransport.cpp
    - class InputChannel
    - class InputMessage
    - class InputPublisher
    - class InputConsumer
  - Input.cpp
  - InputDevice.cpp
  - Keyboard.cpp
  - KeyCharacterMap.cpp
  - IInputFlinger.cpp

frameworks/base/services/core/
  - java/com/android/server/input/InputManagerService.java
  - jni/com_android_server_input_InputManagerService.cpp
    - class NativeInputManager

岗位职责，创建时机，所在进程

*/

//frameworks/native/services/inputflinger/InputManager.cpp
class InputManager {

    /*任务非常简单，创建、启动、停止 reader 和 dispatcher 线程*/

    void InputManager::initialize() {
        mDispatcher = new InputDispatcher();//内部会创建 InputDispatcherThread 线程
        mReader = new InputReader(eventHub, mDispatcher);//内部创建 InputReaderThread 线程
    }

    void start(); // 启动 reader 和 dispatcher 线程

    void stop(); // 停止 reader 和 dispatcher 线程
}

//frameworks/native/services/inputflinger/EventHub.cpp
class EventHub {

    /*
    利用 INotify 监听设备数量变化
    利用 Epoll 监听设备文件读写状态变化

    *** 重要 *** 负责设备的管理，和事件的监听

    岗位职责，管理输入设备，包括扫描、打开、读取、卸载设备的过程
    */

    // 在创建 EventHub 对象时
    EventHub::EventHub(void)  {
        // 创建 epoll，用于监听设备文件是否有可读事件
        mEpollFd = epoll_create(EPOLL_SIZE_HINT);
        // 创建 inotify ，用于监听文件系统是否变化，有变化说明发生设备插拔
        mINotifyFd = inotify_init();
    }

    size_t EventHub::getEvents( timeoutMillis, buffer, bufferSize) {
        //getEvents() 是 IMS 的核心，该方法一共做了两件事
        // 1. 监听设备插拔动作，执行对应的设备的打开/卸载操作，并生成 RawEvent 结构通知调用者
        // 2. 监听输入设备文件的事件变化
        //如果没有任何事件发生，调用 epoll_wait() 函数执行等待
        return event - buffer; // 返回读取到的事件数量
    }


}

//frameworks/native/services/inputflinger/InputReader.cpp
class InputReader {

    /*

    无限循环读取事件，读到了以后解析原始事件，拆解为按键、触摸屏、轨迹球等等

    总结，从 InputReader 的命名来看，解析、转换、生成 Android Motion 事件，原始输入事件的整合、变换与 Android 输入事件的生成才是它的主要工作
    */

    class InputReaderThread : Thread {

        /*
        InputReaderThread 线程启动后，循环将不断地执行 InputReader#loopOnce()函数
        */

        bool InputReaderThread::threadLoop() {
            mReader->loopOnce();
            return true;
        }
    }

    // 读取事件，解析事件
    void InputReader::loopOnce() {
        int count = mEventHub->getEvents(); // 读消息，有消息返回，没消息阻塞到 epoll()。由于事件分发需要时间，所以单次读取的事件可能是多个
        if(count) processEventsLocked();//分发原始事件
    }

    void InputReader::processEventsLocked(rawEvents, count) {
        // 遍历所有事件，解析、分发
        for (const RawEvent* rawEvent = rawEvents; count;) {
            int32_t type = rawEvent->type; // 获取事件类型
            switch (type){ // 源码不包含此 switch 逻辑，这是 InputDevice 中的内容，为了方便理解我才搬了过来
                case EV_KEY; // 按键类型的事件。能够上报这类事件的设备有键盘、鼠标、手柄、手写板等一切拥有按钮的设备（包括手机上的实体按键）
                case EV_ABS; // 绝对坐标类型的事件。这类事件描述了在空间中的一个点，触控板、触摸屏等使用绝对坐标的输入设备可以上报这类事件
                case EV_REL; // 相对坐标类型的事件。这类事件描述了事件在空间中相对于上次事件的偏移量。鼠标、轨迹球等基于游标指针的设备可以上报此类事件
                case EV_SW; // 开关类型的事件。这类事件描述了若干固定状态之间的切换。手机上的静音模式开关按钮、模式切换拨盘等设备可以上报此类事件
                ...
            }
            // 我们只专注 touch 触摸事件
            dispatchTouches(when, policyFlags);
        }
    }

    void dispatchTouches(){
        // 判断是否只是单指移动事件，还是换了个重新按下的事件，或是多指触摸等等等
        // 解析完成后，调用 dispatchMotion() 分发
        dispatchMotion();
    }

    void dispatchMotion(){
        // 最终生成 NotifyMotionArgs 结构，交给 InputDispatcher 执行最后的分发
        NotifyMotionArgs args;
        InputDispatcher::notifyMotion(args);//分发输入事件
    }

}


//frameworks/native/services/inputflinger/InputDispatcher.cpp
class InputDispatcher {

    /*
    到了这里，原始事件已经被封装成 Key 、Motion 等事件
    接下来的任务是怎么把这些事件分发给有需要的窗口

    InputDispatcher 可以分为两步来看

    第一步是，InputReader 调用的 notifyMotion() 做了什么
    第二步是，InputDispatcher 同样作为单独的线程，循环体中（dispatchOnce()）里面做了些什么

    派发流程非常长，我这里只展示了一两个关键方法，主要目标是了解流程，和 Framework 的各个类打个照面，不至于以后碰到了太陌生

    */

    class InputDispatcherThread : Thread {

        bool InputDispatcherThread::threadLoop() {
            mDispatcher->dispatchOnce();
            return true;
        }
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
        // 如果派发队列为空，则会使派发线程陷入无限期休眠状态。
        // 即将被派发的事件从派发队列中取出并保存在mPendingEvent成员变量中。
        // 事件有可能因为某些原因而被丢弃，被丢弃的原因保存在dropReason中。
        // 不同类型的事件使用不同的派发函数进行实际的派发动作。如本例中的Motion事件 使用dispatchMotionLocked()函数进行派发。
        // 派发一个事件至少需要一次线程循环才能完成。是否在下次循环继续尝试此事件的派发由派发函数的返回值决定。
        // 事件的派发是串行的，在排在队首的事件完成派发或被丢弃之前，不会对后续的事件进行派发。理解InputDispatcher的这一特点非常重要。
        if(EventEntry::TYPE_MOTION) dispatchMotionLocked();//我们这只关注触摸事件
    }

    // 专门用来为Motion事件寻找合适的目标窗口
    void dispatchMotionLocked() {
        // 如果成功地找到了可以接收事件的目标窗口，则通过dispatchEventLocked()函数完成实际的派发工作
        int32_t injectionResult = findTouchedWindowTargetsLocked();//找到需要分发触摸事件的window
        dispatchEventLocked();//实际的分发事件
    }

    // 我知道大家可能对怎么找到目标窗口的逻辑非常感兴趣，这个函数 400 多行代码，内部还调用了其他方法
    // 这个是以窗口为单位的，对于 ui boy 来说，Activity 、Dialog 等就是一个窗口
    int findTouchedWindowTargetsLocked(){//获取正在被触摸的window，触摸事件用
        /*
            // 1. 根据窗口的点击区域与事件发生的坐标点选取合适的目标窗口。注意其遍历顺序是沿
                         Z-Order由上至下进行遍历，因此Z-Order越靠上，则拥有获取事件的优先权。另外，
                         如果窗口没有在其LayoutParams.flag中指明FLAG_NOT_TOUCH_MODAL选项，说
                         明是一个模式窗口。模式窗口将会阻止点击事件被派发给位于其下的窗口，无论点击
                         事件是否发生在它的可点击区域内。
             // 2. 检查所找到的窗口是否可以接收新的按键事件。这个检查工作是由isWindowReadyFo
                         rMorelnputLocked()函数完成的。如果窗口尚无法接收事件，则说明此窗口有可能发
                         生ANR。handleTargetsNotReadyLocked()会记录下这一事实，并将injectionResult设
                         置为PENDING，要求下次派发线程的循环中重试此事件的派发。因为检查窗口是否
                         可以接收新的输入事件需要我们清楚地理解向窗口派发事件以及窗口对事件做出反馈
                         的过程，因此这里暂不做深入讨论。
             // 3. 如果找到的窗口可以接收新的事件，则由addWindowTargetLocked()生成一个
                         InputTarget，并放入参数inputTargets列表中。由于InputTarget中几乎所有字段都可
                         以从InputWindowHandle中找到，所以其生成过程就不再赘述。

        */

        size_t numWindows = mWindowHandles.size();
        for (size_t i = 0; i < numWindows; i++);//从前向后遍历所有的window以找出触摸的window，将满足条件的放入inputTargets，没找到返回第一个前台window
    }

    // 合适的目标窗口被确定下来之后，便可以开始将实际的事件发送给窗口了
    void dispatchEventLocked(Vector<InputTarget>& inputTargets) {
        for (size_t i = 0; i < inputTargets.size(); i++) {
            InputChannel channel = inputTarget.inputChannel;//删减过的流程
            channel->sendMessage(&msg);//给能够被触摸的window发送跨进程消息
        }
    }

    //注册监听，用于方法内部会创建一个新的connection连接，将 inputChannel 的fd 添加到 looper 监听
    int registerInputChannel() {
        sp<Connection> connection = new Connection();
        int fd = inputChannel->getFd();
        mConnectionsByFd.add(fd, connection);
        mLooper->addFd(fd, 0, ALOOPER_EVENT_INPUT, handleReceiveCallback, this);
    }

}

/*

IMS 和 WMS 的通信同步问题

在 InputDispatcher 执行事件派发的过程中，调用了 findTouchedWindowTargetsLocked() 函数来寻找能接收事件的目标窗口

我们都知道，Android 系统的窗口的创建与销毁是由 WindowManagerService 在管理，而 InputManagerService 中的 InputDispatcher 却持有窗口集合

显然，WMS 和 IMS 存在某种联系，

解决两个问题

1. IMS 持有的窗口集合从哪里来？WMS 是什么时候将 Window 信息同步到 IMS

回到 InputDispatcher 源码，搜索 mWindowHandles 关键字，很容易就找到了 mWindowHandles 集合是在 setInputWindows() 函数中被赋值

那么 setInputWindows() 是谁调用的？

接着搜索 Framework ，结果如图

我是图片

我们发现，Java 层的 IMS 也有一个 setInputWindows() 方法，并通过 JNI 指向了 native 中的 InputDispatcher#setInputWindows()

最终的调用动作发生在 InputMonitor 类的 updateInputWindowsLw() 方法中

InputMonitor 类好像没见过，它是连接 WMS 和 IMS 的枢纽

WMS 通过 InputMonitor.java 持有了 IMS 的引用，当窗口信息发生变化后，通过 InputMonitor#updateInputWindowsLw() 方法，将新的窗口集合更新到 IMS 中

2. IMS 如何将触摸事件通知到 APP 进程的

因为涉及到跨进程，常用的通信方式也就那么几个，IMS 这里选用的是 socket ，在 Java 层被包装为 InputChannel

InputChannel 是分发 input 事件的通道，driver 产生 input 事件，发送到 system_server 后通过 socket 发送到 app 进程。

每次创建 window 时会创建一对 InputChannel 对象，一个存到 app 进程的 ViewRootImpl 的 mInputChannel 变量，一个存到 system_server 的 WindowState 的 mInputChannel 字段。

*/

//frameworks/native/services/inputflinger/InputDispatcher.cpp
class InputDispatcher {

    void InputDispatcher::setInputWindows(Vector<sp<InputWindowHandle> >& inputWindowHandles) {
        mWindowHandles = inputWindowHandles;
    }
}

//------------------------------------------------------------------------------native分界线----------------------------------------------------------------------------------------------------------


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

/frameworks/base/services/core/java/com/android/server/input/InputManagerService.java
class InputManagerService {

    // 启动 IMS 服务，不同版本代码细节或许有差别
    public InputManagerService() {
        mPtr = nativeInit();
        LocalServices.addService(InputManagerInternal.class, new LocalService());
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


