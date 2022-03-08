### Overview
1. 并发为什么会出问题
2. Java如何解决并发问题
3. Happens-Before 规则

### 一、并发介绍
多线程切换本身会引发**原子性**，即：当CPU时间片执行完了就会保存当前线程状态，去执行其他线程
CPU增加了缓存，在多线程会环境中又会引发**可见性**，即主存和线程拿到的值不一致
CPU指令优化会导致**有序性**，这个我个人没碰到过
1. 可见性：CPU增加了缓存，以均衡与内存的速度差异；会造成CPU1更改值却没来得及同步主存，CPU2读主存的值操作时读的是旧的值
2. 原子性：CPU分时复用
3. 有序性：CPU指令优化

### 二、Java如何解决并发问题
1. 解决可见性：volatile禁用CPU缓存
使用volatile保证可见性，volatile关键字保证用到时去主存读最新的，修改完后立即更新到主存
synchronized和lock释放锁后会将变量刷新到主存，所以同样也可以

2. 解决原子性：
使用synchronized和lock同步代码块

3. 解决有序性：
volatile、synchronized、lock都可以

### 三、 Happens-Before规则
- volatile 变量规则：对一个 volatile 变量的写操作先行发生于后面对这个变量的读操作。
- 线程启动规则：Thread 对象的 start() 方法调用先行发生于此线程的每一个动作。

