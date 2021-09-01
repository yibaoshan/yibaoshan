package com.android.notebook.android.frameworks.base.core.java.android.os;

public class ServiceManager {

    private static IServiceManager getIServiceManager() {
        /*
        sServiceManager = ServiceManagerNative
                .asInterface(Binder.allowBlocking(BinderInternal.getContextObject()));
        * */
        return new IServiceManager();
    }

    static class IServiceManager {
        /*我是一个aidl文件*/
    }

}
