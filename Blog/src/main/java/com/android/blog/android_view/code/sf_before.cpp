//
// Created by Bob on 2022/7/30.
//


//1. 创建flinger对象并调用init()方法执行初始化工作
//2. 注册sf服务和gpu服务到servicemanager
//3. 调用run()方法进入休眠
/frameworks/native/services/surfaceflinger/main_surfaceflinger.cpp
int main(int, char**) {

    // instantiate surfaceflinger
    sp<SurfaceFlinger> flinger = new SurfaceFlinger();

    // initialize before clients can connect
    flinger->init();

    // publish surface flinger
    sp<IServiceManager> sm(defaultServiceManager());
    sm->addService(String16(SurfaceFlinger::getServiceName()), flinger, false);

    // publish GpuService
    sp<GpuService> gpuservice = new GpuService();
    sm->addService(String16(GpuService::SERVICE_NAME), gpuservice, false);

    // run surface flinger in this thread
    flinger->run();

    return 0;
}

/frameworks/native/services/surfaceflinger/SurfaceFlinger.cpp

//利用RefBase首次引用机制来做一些初始化工作，这里是初始化消息机制
//消息队列在sf进程中一共提供两个功能
//1. 执行sf进程请求vsync的工作
//2. vsync-sf信号到来后，执行合成工作
void SurfaceFlinger::onFirstRef()
{
    mEventQueue.init(this);
}

//初始化-只截取了和vsync信号和图形合成有关的部分代码
void SurfaceFlinger::init() {
    {

        // start the EventThread
        //启动事件分发线程，提供给APP进程注册事件回调
        //mPrimaryDispSync是用来控制
        sp<VSyncSource> vsyncSrc = new DispSyncSource(&mPrimaryDispSync,
                vsyncPhaseOffsetNs, true, "app");
        mEventThread = new EventThread(vsyncSrc, *this);

        //又启动一个事件分发线程，并将自己注册到hwc中，用于sf进程监听vsync信号
        sp<VSyncSource> sfVsyncSrc = new DispSyncSource(&mPrimaryDispSync,
                sfVsyncPhaseOffsetNs, true, "sf");
        mSFEventThread = new EventThread(sfVsyncSrc, *this);
        mEventQueue.setEventThread(mSFEventThread);
    }

    // Drop the state lock while we initialize the hardware composer. We drop
    // the lock because on creation, it will call back into SurfaceFlinger to
    // initialize the primary display.
    //初始化HWC对象，加载hwcomposer.so的动作在HWComposer的初始化函数中
    mHwc = new HWComposer(this);
    //将自己注册到hwc的回调函数中，其内部分别调用registerHotplugCallback、registerRefreshCallback、registerVsyncCallback三个回调方法
    mHwc->setEventHandler(static_cast<HWComposer::EventHandler*>(this));

    mEventControlThread = new EventControlThread(this);
    mEventControlThread->run("EventControl", PRIORITY_URGENT_DISPLAY);

}

//进入消息轮询
void SurfaceFlinger::run() {
    do {
        waitForEvent();
    } while (true);
}

//等待消息唤醒
void SurfaceFlinger::waitForEvent() {
    do {
        IPCThreadState::self()->flushCommands();
        int32_t ret = mLooper->pollOnce(-1);
        } while (true);
}
