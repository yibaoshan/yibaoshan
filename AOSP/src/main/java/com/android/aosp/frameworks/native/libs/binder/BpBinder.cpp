//
// Created by Bob on 2021/8/26.
//

#include <binder/BpBinder.h>

/**
* BpBinder继承自IBinder，在头文件中已声明
* BpBinder为binder实体的代理类，负责传输内容
*/

  virtual const String16&    getInterfaceDescriptor() const;

  virtual status_t pingBinder();

  virtual status_t transact(uint32_t code, const Parcel& data,
                                        Parcel* reply, uint32_t flags = 0){
       //实际通过调用IPCThreadState的transact来完成传输数据动作
      status_t status = IPCThreadState::self()->transact(
                  mHandle, code, data, reply, flags);
              if (status == DEAD_OBJECT) mAlive = 0;
              return status;
  }
