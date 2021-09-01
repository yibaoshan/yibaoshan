#define LOG_TAG "ServiceManager"

#include <binder/IServiceManager.h>

sp<IServiceManager> defaultServiceManager()
{
    std::call_once(gSmOnce, []() {
        sp<AidlServiceManager> sm = nullptr;
        while (sm == nullptr) {
            sm = interface_cast<AidlServiceManager>(ProcessState::self()->getContextObject(nullptr));
            if (sm == nullptr) {
                ALOGE("Waiting 1s on context object on %s.", ProcessState::self()->getDriverName().c_str());
                sleep(1);
            }
        }
        gDefaultServiceManager = new ServiceManagerShim(sm);
    });
    return gDefaultServiceManager;
}

status_t ServiceManagerShim::addService(const String16& name, const sp<IBinder>& service,
                                        bool allowIsolated, int dumpsysPriority)
{

}

sp<IBinder> ServiceManagerShim::getService(const String16& name) const
{

}