

## 启动方式

### Context#startService()

```
onCreate():创建service时调用，仅调用一次
    ->onStartCommand():重复调用startService时调用
        ->onDestroy():调用stopService关闭服务时调用
```

- 首次启动会创建一个 Service 实例
- 依次调用 onCreate() 和 onStartCommand() 方法
- 再次启动调用它的 onStartCommand() 方法
- Service 与它的调用者无关，只要不调用 stopService，Service 会继续运行的
- 无论启动了多少次 Service ,只需调用一次 StopService 即可停掉Service

### Activity#bindService()

```
onCreate():创建service时调用，仅调用一次
    ->onBind():每调用一次bindService时调用
        ->onUnbind():手动调用unbindService()解绑或绑定的Activity执行到onDestroy()时调用
            ->onDestroy():所有Activity都解绑时调用
```

- 当首次 bindService 绑定一个 Service 时,系统会实例化一个 Service 实例
- 并调用其 onCreate() 和 onBind()方法
- 接着调用者就可以通过 IBinder 和 Service 进行交互
- 此后如果再次使用 bindService 绑定 Service，系统不会创建新的 Service 实例,也不会再调用 onBind() 方法，只会直接把 IBinder 对象传递给其他后来增加的客户端
- 注意，基于以上，Service 的 IBinder 可以被抢占
- 解除绑定调用 unbindService()，onUnbind 和 onDestroy 方法将会被调用
- 这是一个客户端的情况，假如是多个客户端绑定同一个 Service 的话,情况如下
  - 当一个客户完成和 service 之间的互动后，它调用 unbindService() 方法来解除绑定。
  - 当所有的客户端都和 service 解除绑定后，系统会销毁service。（除非service也被startService()方法开启）
- bindService 模式下的 Service 是与调用者相互关联的，在 bindService 后，一旦调用者销毁,那么 Service也立即终止

## 生命周期

### onStartCommand(intent,flag,startId)

**flag 含义如下：**

- 0：正常创建时的默认值
- START_FLAG_REDELIVERY：系统重建并且返回值START_REDELIVER_INTENT为时的带参
- START_FLAG_RETRY：重试启动时带参，搞不懂什么场景下会发生？

**返回值含义：**

- START_STICKY：默认，如果Service启动后被清理，系统重启服务时，intent为空
- START_STICKY_COMPATIBILITY：默认，START_STICKY兼容版本
- START_NOT_STICKY：可选，如果Service被清理，系统将不会尝试重新启动
- START_REDELIVER_INTENT：可选，如果Service被清理，系统会尝试重启，并带入把最后一次的intent带过来

**startId 含义：**

定向关闭

你可以 activity 启动 service 之后将改值返回出去，这样 activity 想要关闭 service 时就可以根据 startId 来关闭了

### bindService(intent,serviceConnection,flag)

**flag含义：**

- Context.BIND_AUTO_CREATE：表示收到绑定请求的时候，如果服务尚未创建，则即刻创建，在系统内存不足需要先摧毁优先级组件来释放内存，且只有驻留该服务的进程成为被摧毁对象时，服务才被摧毁
- Context.BIND_DEBUG_UNBIND：通常用于调试场景中判断绑定的服务是否正确，但容易引起内存泄漏，因此非调试目的的时候不建议使用
- Context.BIND_NOT_FOREGROUND：表示系统将阻止驻留该服务的进程具有前台优先级，仅在后台运行，该标志位位于Froyo中引入

## IntentService

```
IntentService是Android为了后台线程任务设计的service
不需要开发者自己维护线程池，当所有任务完成后，Service会自动结束
继承IntentService需要实现两个方法：
1. 构造函数super(name)，name为线程名称
2. onHandleIntent(intent) 相当于提交任务，有多个任务提交时，IntentService会按顺序依次执行，不会并行
执行流程:startService()->onStartCommand()->onStart()->Handler.sendMessage()->onHandleIntent()->stopSelf()

```

## 常见问题

- 多个Activity绑定同一个service的生命周期是怎样的
- 为什么使用bindService的方式时Activity销毁后service也会销毁？
  - 答：在activity执行onDestroy()方法时在H里会调用removeContextRegistrations来清除注册的service
- stopSelf()/stopSelf(startId)/stopSelfResult(startId)和stopService()的区别？
  - stopSelf(startId)若没有相同startId，则不会执行停止服务操作，stopService会关闭整个服务
- 远程服务不可使用startService方式启动，只允许bindService。多个APP调用bindService时只会有一个service实例，且service只会响应最后一个client的动作
- Service允许使用Toast，但不允许弹窗Dialog，否则会收到以下错误：Unable to add window -- token null is not valid; is your activity running?