//
// Created by Bob on 2022/7/30.
//

/*

SystemServer进程中，ActivityManagerService通常是最忙的服务，所有的组件都是ams在管理

但在图形系统中，WindowManagerService地位更重要一些，我们甚至可以忽略ams直接和wms通信来进行视图的展示

wms比ams有用，这一点在Vsync信号到来后更为明显，工具人属性拉满

启动ams服务，并将ams注册到servicemanager
启动wms
拉起launcher桌面
Looper.loop();
SystemServer进程的任务已经完成，剩下的工作都跑在各个服务的各自的线程里面

*/

/frameworks/base/services/java/com/android/server/SystemServer.java
class SystemServer {

    /**
    * The main entry point from zygote.
    */
    public static void main(String[] args) {
        new SystemServer().run();
    }

    private void run() {
        Looper.prepareMainLooper();
      	startBootstrapServices();
    	startOtherServices();
      	// Loop forever.
        Looper.loop();
    }

    //启动AMS
    private void startBootstrapServices() {
         mActivityManagerService = mSystemServiceManager.startService(ActivityManagerService.Lifecycle.class).getService();
         // Set up the Application instance for the system process and get started.
         mActivityManagerService.setSystemProcess();
    }

    //启动WMS
    private void startOtherServices() {
      	WindowManagerService wm = WindowManagerService.main();
      	//将wms注册到servicemanager
      	ServiceManager.addService(Context.WINDOW_SERVICE, wm);
      	mActivityManagerService.systemReady();//
    }

}

/frameworks/base/services/core/java/com/android/server/am/ActivityManagerService.java
class ActivityManagerService {

    //将自己注册到servicemanager中去
    public void setSystemProcess() {
        ServiceManager.addService(Context.ACTIVITY_SERVICE, this, true);
    }

    //启动launcher桌面
    public void systemReady() {
        //aosp版本不同代码也不同，在7.0中最终调用startHomeActivityLocked()方法唤起launcher
    }

}

/frameworks/base/services/core/java/com/android/server/wm/WindowManagerService.java
class WindowManagerService {

    public static WindowManagerService main(){
        return new WindowManagerService();
    }

}
