

/**

Framework 是 Google 基于驱动层封装的一套框架，是一个中间层，它对接了底层实现，封装了复杂的内部逻辑，并提供供外部使用的接口。Framework层是应用程序开发的基础。

binder 在 framework 层的实现在 /frameworks/native/libs/binder/

头文件在 /frameworks/native/include/binder/

framework 层涉及到的类比较多，它们通常都会遵守下面的命名规则：

1. 服务的接口使用I字母作为前缀
2. 远程接口使用Bp作为前缀（binder proxy）
3. 本地接口使用Bn作为前缀（binder native）

Proxy 代表了调用方，通常与服务的实现不在同一个进程，因此下文中，我们也称Proxy端为“远程”端。

Native端是服务实现的自身，因此下文中，我们也称Native端为”本地“端。

类名            说明
----------------------------------------------------------------------------------
BpRefBase       RefBase的子类，提供remote()方法获取远程Binder
IInterface      Binder服务接口的基类，Binder服务通常需要同时提供本地接口和远程接口
BpInterface     远程接口的基类，远程接口是供客户端调用的接口集
BnInterface     本地接口的基类，本地接口是需要服务中真正实现的接口集

IBinder         Binder对象的基类，BBinder和BpBinder都是这个类的子类
BpBinder        Buffered Proxy Binder，远程Binder，这个类提供transact方法来发送请求，BpXXX实现中会用到,
BBinder         Buffered Binder，本地Binder，服务实现方的基类，提供了onTransact接口来接收请求

ProcessState    代表了使用Binder的进程
IPCThreadState  代表了使用Binder的线程，这个类中封装了与Binder驱动通信的逻辑
Parcel          在Binder上传递的数据的包装器

*/

//frameworks/native/include/binder/IBinder.h
class IBinder : {
    /*
    描述了所有在Binder上传递的对象，它既是Binder本地对象BBinder的父类，也是Binder远程对象BpBinder的父类
    方法名                   说明
    -------------------------------------------------------------------------------
    localBinder              获取本地Binder对象
    remoteBinder             获取远程Binder对象
    transact                 进行一次Binder操作
    queryLocalInterface      尝试获取本地Binder，如何失败返回NULL
    getInterfaceDescriptor   获取Binder的服务接口描述，其实就是Binder服务的唯一标识
    isBinderAlive            查询Binder服务是否还活着
    pingBinder               发送PING_TRANSACTION给Binder服务
    **/

    virtual BBinder*        localBinder(); // 获取本地Binder对象
    virtual BpBinder*       remoteBinder(); // 获取远程Binder对象
}

//frameworks/native/libs/binder/BpBinder.cpp
class BpBinder : public IBinder {

    /*
        BpBinder的实例代表了远程Binder，这个类的对象将被客户端调用。

        其中handle方法会返回指向Binder服务实现者的句柄

        这个类最重要就是提供了 transact()方法，这个方法会将远程调用的参数封装好发送的Binder驱动。
    */

    inline int32_t handle() const { return mHandle; }

    // code参数就是用来对服务接口进行编号区分的
    // Binder服务要保证自己提供的每个服务接口有一个唯一的code，例如某个Binder服务可以将：add接口code设为1，minus接口code设为2，multiple接口code设为3，divide接口code设为4，等等。
    status_t BpBinder::transact( code, const Parcel& data, Parcel* reply, uint32_t flags)
    {
        // Once a binder has died, it will never come back to life.
        if (mAlive) {
            status_t status = IPCThreadState::self()->transact(
                mHandle, code, data, reply, flags);
            if (status == DEAD_OBJECT) mAlive = 0;
            return status;
        }
        return DEAD_OBJECT;
    }
}

//frameworks/native/include/binder/Binder.h
class BBinder : public IBinder {

    /*
        Buffered Binder，本地Binder

        BBinder的实例代表了本地Binder，它描述了服务的提供方，所有Binder服务的实现者都要继承这个类（的子类）

        在继承类中，最重要的就是实现onTransact方法，因为这个方法是所有请求的入口。

        因此，这个方法是和BpBinder中的transact方法对应的，

        这个方法同样也有一个uint32_t code参数，在这个方法的实现中，由服务提供者通过code对请求的接口进行区分，然后调用具体实现服务的方法
    */

    status_t BBinder::onTransact( code, const Parcel& data, Parcel* reply, uint32_t /*flags*/){
        switch (code) {
            case INTERFACE_TRANSACTION:
                reply->writeString16(getInterfaceDescriptor());
                return NO_ERROR;
            case DUMP_TRANSACTION: {
            }
            case SHELL_COMMAND_TRANSACTION: {
            }

            case SYSPROPS_TRANSACTION: {
            }
        }
    }
}

//-------------------------------------------------------------------------我是分割线-------------------------------------------------------------------------------------

//frameworks/native/include/binder/Binder.h
class BpRefBase : public virtual RefBase {

    /*
        remote 方法可以获取到指向服务实现方的句柄
    */

    IBinder* const          mRemote;

    IBinder* remote() { return mRemote; }
}

//frameworks/native/libs/binder/IInterface.cpp
class IInterface {
    /*
        每个Binder服务都是为了某个功能而实现的，因此其本身会定义一套接口

        为了便于开发，这两中类里面的服务接口应当是一致的，例如：

        假设服务实现方提供了一个接口为add(int a, int b)的服务方法，那么其远程接口中也应当有一个add(int a, int b)方法。

        因此为了实现方便，本地实现类和远程接口类需要有一个公共的描述服务接口的基类

        这个基类通常是IInterface的子类，IInterface的定义如下
    */

    protected:
        // IInterface 类定义了onAsBinder让子类实现。
        // onAsBinder 在本地对象的实现类中返回的是本地对象，在远程对象的实现类中返回的是远程对象。
        // onAsBinder方法被两个静态方法 asBinder 方法调用。有了这些接口之后，在代码中便可以直接通过IXXX::asBinder方法获取到不用区分本地还是远程的IBinder对象。
        // 这个在跨进程传递Binder对象的时候有很大的作用（因为不用区分具体细节，只要直接调用和传递就好）
        virtual IBinder*            onAsBinder() = 0; // 要求子类实现
    };

    // static
    sp<IBinder> IInterface::asBinder(const IInterface* iface){
        if (iface == NULL) return NULL;
        return const_cast<IInterface*>(iface)->onAsBinder();
    }

    // static
    sp<IBinder> IInterface::asBinder(const sp<IInterface>& iface){
        if (iface == NULL) return NULL;
        return iface->onAsBinder();
    }
}

//frameworks/native/libs/binder/IInterface.cpp
class BnInterface : public INTERFACE, public BBinder {

    /*
        继承了BBinder类，由此可以通过复写onTransact方法来提供实现
    */
    virtual sp<IInterface> queryLocalInterface(const String16& _descriptor);
    virtual const String16& getInterfaceDescriptor() const;

    virtual IBinder* onAsBinder();
};

//frameworks/native/libs/binder/IInterface.cpp
class BpInterface : public INTERFACE, public BpRefBase {

    /*
        继承了BpRefBase，通过这个类的 remote 方法可以获取到指向服务实现方的句柄
    */
    virtual IBinder* onAsBinder();
};

//-------------------------------------------------------------------------我是分割线-------------------------------------------------------------------------------------

//frameworks/native/libs/binder/ProcessState.cpp
class ProcessState {

    /*
        ProcessState是一个Singleton（单例）类型的类，在一个进程中，只会存在一个实例。

        通过ProcessState::self()接口获取这个实例。一旦获取这个实例，便会执行其构造函数，由此完成了对于Binder设备的初始化工作。

        在 ProcessState 完成 binder 驱动的操作
    */

    sp<ProcessState> ProcessState::self(){
        if (gProcess != NULL) return gProcess;
        gProcess = new ProcessState;
        return gProcess;
    }

    ProcessState::ProcessState(): mDriverFD(open_driver()), mVMStart(MAP_FAILED){
        // 由于Binder的数据需要跨进程传递，并且还需要在内核上开辟空间，因此允许在Binder上传递的数据并不是无无限大的。mmap中指定的大小便是对数据传递的大小限制
        // 即 1M - 8k的大小。 因此我们在开发过程中，一次Binder调用的数据总和不能超过这个大小。
        mVMStart = mmap(0, (1*1024*1024) - (4096 *2), PROT_READ, MAP_PRIVATE | MAP_NORESERVE, mDriverFD, 0);
    }

    static int open_driver(){
        // 打开驱动
        int fd = open("/dev/binder", O_RDWR | O_CLOEXEC);
        status_t result = ioctl(fd, BINDER_VERSION, &vers); //获取binder驱动版本号
        result = ioctl(fd, BINDER_SET_MAX_THREADS, &maxThreads); //设置max threads
        return fd;
    }
}

//frameworks/native/libs/binder/IPCThreadState.cpp
class IPCThreadState {

    /*

    负责与Binder驱动通信的 IPCThreadState 。每一个线程有一个实例，单例类实现

    方法                     说明
    -----------------------------------------------------------------------
    transact                 公开接口。供Proxy发送数据到驱动，并读取返回结果
    sendReply                供Server端写回请求的返回结果
    waitForResponse          发送请求后等待响应结果
    talkWithDriver           通过ioctl BINDER_WRITE_READ来与驱动通信
    writeTransactionData     写入一次事务的数据
    executeCommand           处理binder_driver_return_protocol协议命令
    freeBuffer               通过BC_FREE_BUFFER命令释放Buffer
    */

    // 首先通过 writeTransactionData 写入数据，然后通过 waitForResponse 等待返回结果。TF_ONE_WAY 表示此次请求是单向的，即：不用真正等待结果即可返回。
    // 而 writeTransactionData 方法其实就是在组装 binder_transaction_data 数据，并没有与驱动进行交互进行发送：
    status_t IPCThreadState::transact(int32_t handle, uint32_t code, const Parcel& data, Parcel* reply, uint32_t flags){
        status_t err = writeTransactionData(BC_TRANSACTION, flags, handle, code, data, NULL);
        err = waitForResponse(reply);
        return err;
    }
}

//frameworks/native/libs/binder/Parcel.cpp
class Parcel {

    /*
        Parcel提供了所有基本类型的写入和读出接口 ，用作序列化
    */
}