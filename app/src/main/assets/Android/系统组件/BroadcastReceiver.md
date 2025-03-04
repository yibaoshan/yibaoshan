
## 使用方式

### 静态广播

1. 接收者：继承 BroadcastReceiver 类，重写 onReceive 方法
2. 接收者：AndroidManifest.xml中使用<receiver>标签注册，action-name为广播接受标识
3. 接收者：context#sendBroadcast(intent(action))

### 动态广播

1. 接收者：继承BroadcastReceiver类，重写onReceive方法
2. 接收者：context.registerReceiver(receiver,intentFilter(action))
3. 发送者：context.sendBroadcast(intent(action))

记得调用unregisterReceiver()解除注册，否则会造成内存泄漏

### 有序广播

参考静态广播和动态广播的使用流程，唯一有差别的地方在于：

1. 接收者：静态广播：xml中intent-filter设置priority的值
2. 接收者：动态广播：intentFilter设置priority的值
3. 发送者：context.sendOrderedBroadcast(intent,receiverPermission)

注意：

- priority值越大优先级越高
- receiverPermission和Activity的permission一样，在跨进程中，拥有相同权限AMS才会处理，防止其他APP攻击。同一进程无视
- 拦截广播：abortBroadcast()

### 本地广播

参考静态广播和动态广播的使用流程，唯一有差别的地方在于：

1. 不支持发送有序广播

### 广播的发送

1. Activity把广播发送到AMS中
2. AMS首先检测广播是否合法，然后根据IntentFilter规则，把所有符合条件的广播接收器整理成一个队列
3. 依次遍历队列中的广播接收器，判断是否拥有权限
4. 把广播发送到广播接收器所在进程，回调广播的onReceive方法

## 常见问题

- Q:广播的功能并不复杂，为什么被称为四大组件之一？

## 参考资料

- [Android S动态广播注册流程](https://blog.csdn.net/yun_hen/article/details/124415431)