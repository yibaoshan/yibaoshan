
#define LOG_TAG "IPCThreadState"

#include <binder/IPCThreadState.h>

#include <binder/Binder.h>
#include <binder/BpBinder.h>

/*线程单例，每个线程只有一个，保存在线程局部存储区域tls中*/
namespace android {

//处理发送的数据，BpBinder会调用此方法，最后会调用进程单例ProcessState来发送
status_t IPCThreadState::transact(int32_t handle,
                                  uint32_t code, const Parcel& data,
                                  Parcel* reply, uint32_t flags)
{
        // 把data数据整理进内部的mOut包中
        err = writeTransactionData(BC_TRANSACTION, flags, handle, code, data, NULL);
        //需要等待回复
    if ((flags & TF_ONE_WAY) == 0)
    {
        if (reply)
        {
            err = waitForResponse(reply);
        }
        else
        {
        //如果用户需要回复但是没有提供parcel对象，这里会自己伪造一个假的
            Parcel fakeReply;
            err = waitForResponse(&fakeReply);
        }
    }
    else
    {
    //不需要等待
        err = waitForResponse(NULL, NULL);
    }
    return err;
}

status_t IPCThreadState::waitForResponse(Parcel *reply, status_t *acquireResult)
{
    while (1)
    {
        // talkWithDriver()内部会完成跨进程事务
        if ((err = talkWithDriver()) < NO_ERROR)
            break;
        // 事务的回复信息被记录在mIn中，所以需要进一步分析这个回复
        cmd = mIn.readInt32();
        switch (cmd)
        {
        case BR_TRANSACTION_COMPLETE:
            if (!reply && !acquireResult) goto finish;
            break;

        case BR_DEAD_REPLY:
            err = DEAD_OBJECT;
            goto finish;

        case BR_FAILED_REPLY:
            err = FAILED_TRANSACTION;
            goto finish;
        default:
            // 注意这个executeCommand()噢，它会处理BR_TRANSACTION的。
            err = executeCommand(cmd);
            if (err != NO_ERROR) goto finish;
            break;
        }
    }
}

/*看到了吗？mIn和mOut的data会先整理进一个binder_write_read结构，然后再传给ioctl()函数。
而最关键的一句，当然就是那句ioctl()了。此时使用的文件描述符就是前文我们说的ProcessState中记录的mDriverFD，
说明是向binder驱动传递语义。BINDER_WRITE_READ表示我们希望读写一些数据。*/
status_t IPCThreadState::talkWithDriver(bool doReceive)
{
binder_write_read bwr;
    bwr.write_size = outAvail;
    bwr.write_buffer = (long unsigned int)mOut.data();
        bwr.read_size = mIn.dataCapacity();
        bwr.read_buffer = (long unsigned int)mIn.data();
    do
    {
    //这里里面会调用驱动层里面的binder_ioctl函数
        if (ioctl(mProcess->mDriverFD, BINDER_WRITE_READ, &bwr) >= 0)
            err = NO_ERROR;
    } while (err == -EINTR);
}

} // namespace android
