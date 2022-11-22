//
// Created by Bob on 2022/9/13.
//

//-----------------------------------------------------------------------Framework 汇总----------------------------------------------------------------

/*

frameworks/native/services/inputflinger/
  - InputDispatcher.cpp //
  - InputReader.cpp
  - InputManager.cpp //
  - EventHub.cpp // 事件中心，利用 epoll 监听设备文件
  - InputListener.cpp

frameworks/native/libs/input/
  - InputTransport.cpp
  - Input.cpp
  - InputDevice.cpp
  - Keyboard.cpp
  - KeyCharacterMap.cpp
  - IInputFlinger.cpp

frameworks/base/services/core/ //InputManagerService，分为Java层和Native层两部分。Java层负责与WMS的通信。而Native层则是InputReader和InputDispatcher两个输入系统关键组件的运行容器。
  - java/com/android/server/input/InputManagerService.java
  - jni/com_android_server_input_InputManagerService.cpp
    - class NativeInputManager

*/

/*

初始化流程：

IMS 作为运行在 SystemServer 进程中的服务，启动顺序依旧拍照

IMS分为Java层与Native层两个部分，其启动过程是从Java部分的初始化开始，进而完成 Native 部分的初始化

分为Java层和Native层两部分。

Java层负责与WMS的通信。

而Native层则是InputReader和InputDispatcher两个输入系统关键组件的运行容器。

简单来说，内核将原始事件写入到设备节点中，InputReader不断地通过EventHub将原始事件取出来并翻译加工成Android输入事件，然后交给InputDispatcher。InputDispatcher根据WMS提供的窗口信息将事件交给合适的窗口。窗口的ViewRootImpl对象再沿着控件树将事件派发给感兴趣的控件。控件对其收到的事件作出响应，更新自己的画面、执行特定的动作。所有这些参与者以IMS为核心，构建了Android庞大而复杂的输入体系。

Linux内核对硬件中断的处理超出了本书的讨论范围，因此本章将以IMS为重点，详细讨论除Linux内核以外的其他参与者的工作原理。

*/

/frameworks/base/services/java/com/android/server/SystemServer.java
class SystemServer {

    // 1.x 为初始化流程 2.x 为启动流程
    private void startOtherServices() {
        inputManager = new InputManagerService(context); // 创建 IMS 对象，详见【step 1.0】
        ServiceManager.addService(Context.INPUT_SERVICE, inputManager);
        ...
        //将InputMonitor对象保持到IMS对象
        inputManager.setWindowManagerCallbacks(wm.getInputMonitor());
        inputManager.start(); // 详见 【2.0】
    }
}

/frameworks/base/services/core/java/com/android/server/input/InputManagerService.java
class InputManagerService {
    // 【step 1.0】初始化流程
    public InputManagerService(Context context) {
       mPtr = nativeInit(this, mContext, mHandler.getLooper().getQueue());
       LocalServices.addService(InputManagerInternal.class, new LocalService());
    }

    // 【step 2.0】 启动流程
    public void start() {
        nativeStart(mPtr); // 详见 【2.1】
        Watchdog.getInstance().addMonitor(this);
    }
}

/frameworks/base/services/core/jni/com_android_server_input_InputManagerService.cpp
class NativeInputManager {

    // 【step 1.1】
    static jlong nativeInit(env, jclass, serviceObj, contextObj, messageQueueObj) {
        //获取 native 消息队列
        sp<MessageQueue> messageQueue = android_os_MessageQueue_getMessageQueue(env, messageQueueObj);
        ...
        NativeInputManager* im = new NativeInputManager(contextObj, serviceObj,messageQueue->getLooper()); // 详见 【step 1.2】
        im->incStrong(0);
        return reinterpret_cast<jlong>(im); //返回Native对象的指针
    }

    // 【step 1.2】
    NativeInputManager::NativeInputManager(contextObj, serviceObj, looper)  {
        JNIEnv* env = jniEnv();
        mContextObj = env->NewGlobalRef(contextObj); //上层IMS的context
        mServiceObj = env->NewGlobalRef(serviceObj); //上层IMS对象
        ...
        sp<EventHub> eventHub = new EventHub(); // 创建EventHub对象，详见 【step 1.3】
        mInputManager = new InputManager(eventHub, this, this); // 创建InputManager对象，详见 【step 1.4】
    }

    // 【step 2.1】
    static void nativeStart(env, jclass , ptr) {
        //此处ptr记录的便是NativeInputManager
        NativeInputManager* im = reinterpret_cast<NativeInputManager*>(ptr);
        status_t result = im->getInputManager()->start(); // 详见 【step 2.2】
        ...
    }
}

/frameworks/native/services/inputflinger/EventHub.cpp
class EventHub {

    // 【step 1.3】
    EventHub::EventHub(void)  {
        // 创建 epoll，用于监听设备文件是否有可读事件
        mEpollFd = epoll_create(EPOLL_SIZE_HINT);
        // 创建 inotify ，用于监听文件系统是否变化，有变化说明发生设备插拔
        mINotifyFd = inotify_init();
        ...
    }
}

/frameworks/native/services/inputflinger/InputManager.cpp
class InputManager {
    //【 step 1.4】
    InputManager::InputManager(eventHub, readerPolicy, dispatcherPolicy) {
        //创建 InputDispatcher 对象，详见 【step 1.5】
        mDispatcher = new InputDispatcher(dispatcherPolicy);
        //创建 InputReader 对象，详见 【step 1.6】
        mReader = new InputReader(eventHub, readerPolicy, mDispatcher);
        initialize(); // 详见 【step 1.7】
    }

    // 【step 1.7】
    void InputManager::initialize() {
        //创建线程 “InputReader”
        mReaderThread = new InputReaderThread(mReader);
        //创建线程 ”InputDispatcher“
        mDispatcherThread = new InputDispatcherThread(mDispatcher);
    }

    InputReaderThread::InputReaderThread(const sp<InputReaderInterface>& reader) :Thread(), mReader(reader);

    InputDispatcherThread::InputDispatcherThread(const sp<InputDispatcherInterface>& dispatcher) :Thread(), mDispatcher(dispatcher);

    // 【step 2.2】
    status_t InputManager::start() {
        result = mDispatcherThread->run("InputDispatcher", PRIORITY_URGENT_DISPLAY); // 启动线程“InputReader”
        result = mReaderThread->run("InputReader", PRIORITY_URGENT_DISPLAY); // 启动线程”InputDispatcher“
        ...
        return OK;
    }
}

/frameworks/native/services/inputflinger/InputDispatcher.cpp
class InputDispatcher {
    // 【step 1.5】
    InputDispatcher::InputDispatcher(const sp<InputDispatcherPolicyInterface>& policy) {
        //创建Looper对象
        mLooper = new Looper(false);

        mKeyRepeatState.lastKeyEntry = NULL;
        //获取分发超时参数
        policy->getDispatcherConfiguration(&mConfig);
    }
}

/frameworks/native/services/inputflinger/InputReader.cpp
class InputReader {

    // 【step 1.6】
    InputReader::InputReader(const sp<EventHubInterface>& eventHub) {
        // 创建输入监听对象
        mQueuedListener = new QueuedInputListener(listener);
        {
            AutoMutex _l(mLock);
            refreshConfigurationLocked(0);
            updateGlobalMetaStateLocked();
        }
    }
}