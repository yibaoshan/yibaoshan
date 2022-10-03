### Overview
1. 作用域
2. 升级过程
3. 底层实现
4. 常见问题

### 一、作用域

### 二、升级过程
无锁 → 偏向锁 → 轻量级锁 → 重量级锁
1. 无锁
2. 偏向锁
3. 轻量级锁
4. 重量级锁

### 三、底层实现
管程：monitor
monitorenter和monitorexit指令
1. A线程执行同步代码时monitor为0，那么monitor加1占用锁
2. A线程执行同步代码时B线程已经抢占了，那么等monitor为0

### 四、常见问题

1. synchronized代码块中发生异常如何保证释放锁？
答：javap查看class可以发现，代码块中隐式加入try finally，在finally中会调用monitorexit命令释放锁
2. 锁升级后可以降级吗？
答：在safepoint中可以，jdk8中本身也有个MonitorInUseLists的开关

