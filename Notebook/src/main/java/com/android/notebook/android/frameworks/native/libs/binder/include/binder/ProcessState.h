
#include <binder/IBinder.h>

#include <pthread.h>

namespace android {

class IPCThreadState;

//进程内的全局单例对象，用来调用binder驱动
class ProcessState : public virtual RefBase
{
public:
    static  sp<ProcessState>    self();
    static  sp<ProcessState>    selfOrNull();

    /* initWithDriver() can be used to configure libbinder to use
     * a different binder driver dev node. It must be called *before*
     * any call to ProcessState::self(). The default is /dev/vndbinder
     * for processes built with the VNDK and /dev/binder for those
     * which are not.
     */
    static  sp<ProcessState>    initWithDriver(const char *driver);

            sp<IBinder>         getContextObject(const sp<IBinder>& caller);

            void                startThreadPool();
                        
    typedef bool (*context_check_func)(const String16& name,
                                       const sp<IBinder>& caller,
                                       void* userData);

            bool                becomeContextManager(
                                    context_check_func checkFunc,
                                    void* userData);

            sp<IBinder>         getStrongProxyForHandle(int32_t handle);
            void                expungeHandle(int32_t handle, IBinder* binder);

            void                spawnPooledThread(bool isMain);
            
            status_t            setThreadPoolMaxThreadCount(size_t maxThreads);
            void                giveThreadPoolName();

            String8             getDriverName();

            ssize_t             getKernelReferences(size_t count, uintptr_t* buf);

                                // Only usable by the context manager.
                                // This refcount includes:
                                // 1. Strong references to the node by this and other processes
                                // 2. Temporary strong references held by the kernel during a
                                //    transaction on the node.
                                // It does NOT include local strong references to the node
            ssize_t             getStrongRefCountForNodeByHandle(int32_t handle);

            enum class CallRestriction {
                // all calls okay
                NONE,
                // log when calls are blocking
                ERROR_IF_NOT_ONEWAY,
                // abort process on blocking calls
                FATAL_IF_NOT_ONEWAY,
            };
            // Sets calling restrictions for all transactions in this process. This must be called
            // before any threads are spawned.
            void setCallRestriction(CallRestriction restriction);

private:
    friend class IPCThreadState;
    
            explicit            ProcessState(const char* driver);
                                ~ProcessState();

                                ProcessState(const ProcessState& o);
            ProcessState&       operator=(const ProcessState& o);
            String8             makeBinderThreadName();

            struct handle_entry {
                IBinder* binder;
                RefBase::weakref_type* refs;
            };

            handle_entry*       lookupHandleLocked(int32_t handle);

            String8             mDriverName;
            int                 mDriverFD;
            void*               mVMStart;

            // Protects thread count variable below.
            pthread_mutex_t     mThreadCountLock;
            pthread_cond_t      mThreadCountDecrement;
            // Number of binder threads current executing a command.
            size_t              mExecutingThreadsCount;
            // Maximum number for binder threads allowed for this process.
            size_t              mMaxThreads;
            // Time when thread pool was emptied
            int64_t             mStarvationStartTimeMs;

    mutable Mutex               mLock;  // protects everything below.

            Vector<handle_entry>mHandleToObject;

            context_check_func  mBinderContextCheckFunc;
            void*               mBinderContextUserData;

            String8             mRootDir;
            bool                mThreadPoolStarted;
    volatile int32_t            mThreadPoolSeq;

            CallRestriction     mCallRestriction;
};
    
} // namespace android

// ---------------------------------------------------------------------------

#endif // ANDROID_PROCESS_STATE_H
