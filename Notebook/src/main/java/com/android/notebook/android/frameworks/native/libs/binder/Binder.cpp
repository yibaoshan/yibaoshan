//
// Created by Bob on 2021/8/26.
//

#include <binder/Binder.h>

/**
* BBinder也继承自IBinder，在头文件中已声明
* BBinder为binder实体类，也就是说所有想要公开给其他进程调用的目标端都需要继承此类，同样也是负责传输内容
*/

class BBinder::Extras
{

}

  virtual const String16&    getInterfaceDescriptor() const;

  virtual status_t    pingBinder();

  virtual status_t    transact(uint32_t code, const Parcel& data,
                                        Parcel* reply, uint32_t flags = 0);
