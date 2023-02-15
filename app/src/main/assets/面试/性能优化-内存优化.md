
## Bitmap

- Android 1.0 ~ Android 3.0 ：存放在 native。释放依赖 Java#finalize() 方法，缺点是不稳定，过多释放任务会导致 FinalizerDaemon 线程抛出 finalizerTimedOut 异常
- Android 3.0 ~ 7.0 ：存放在 Java 堆，由于堆区大小有限制，容易 OOM
- Android 8.0 + ：存放在 native ，引入 NativeAllocationRegistry 类释放数据

## 低内存

- LMK 杀死进程后，AMS 可能保留有 Activity 信息，因此，再次打开 APP 可能不会走 Splash 页。若页面使用到静态变量，保险起见放在 Application 中初始化