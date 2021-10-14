//
// Created by Bob on 2021/8/26.
//

#include <binder/ProcessState.h>

//构造函数
ProcessState::ProcessState(const char *driver)
    : mDriverName(String8(driver))
    , mDriverFD(open_driver(driver))//初始化时调用打开binder驱动
    , mVMStart(MAP_FAILED)
    , mThreadCountLock(PTHREAD_MUTEX_INITIALIZER)
    , mThreadCountDecrement(PTHREAD_COND_INITIALIZER)
    , mExecutingThreadsCount(0)
    , mMaxThreads(DEFAULT_MAX_BINDER_THREADS)
    , mStarvationStartTimeMs(0)
    , mBinderContextCheckFunc(nullptr)
    , mBinderContextUserData(nullptr)
    , mThreadPoolStarted(false)
    , mThreadPoolSeq(1)
    , mCallRestriction(CallRestriction::NONE)
{
}

//ServiceManager入口函数main会调用该方法来初始化binder
sp<ProcessState> ProcessState::initWithDriver(const char* driver){
    gProcess = new ProcessState(driver);
    return gProcess;
}

//成为上下文管理者
bool ProcessState::becomeContextManager(context_check_func checkFunc, void* userData)
{
    int result = ioctl(mDriverFD, BINDER_SET_CONTEXT_MGR_EXT, &obj);
    if (result != 0) {
        result = ioctl(mDriverFD, BINDER_SET_CONTEXT_MGR, &dummy);
    }
    return result == 0;
}

//打开binder驱动，谁调用的？
static int open_driver(const char *driver)
{
    int fd = open(driver, O_RDWR | O_CLOEXEC);
    return fd;
}
