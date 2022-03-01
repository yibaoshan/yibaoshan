### Overview
1. 并发为什么会出问题
2. Java如何解决并发问题
3. Happens-Before 规则

### 一、并发介绍
1. 可见性：CPU增加了缓存，以均衡与内存的速度差异；会造成CPU1更改值却没来得及同步主存，CPU2读主存的值操作时读的是旧的值
2. 原子性：CPU分时复用
3. 有序性：CPU指令优化

### 二、Java如何解决并发问题
1. 解决可见性：
使用volatile保证可见性，synchronized和lock释放锁后会将变量刷新到主存，所以同样也可以

2. 解决原子性：
使用synchronized和lock同步代码块

3. 解决有序性：
volatile、synchronized、lock都可以

### 三、 Happens-Before规则

