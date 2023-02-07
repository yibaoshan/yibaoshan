
- Android 1.0 ~ Android 3.0 ：存放在 native。释放依赖 Java#finalize() 方法，缺点是不稳定，过多释放任务会导致 FinalizerDaemon 线程抛出 finalizerTimedOut 异常
- Android 3.0 ~ 7.0 ：存放在 Java 堆，由于堆区大小有限制，容易 OOM
- Android 8.0 + ：存放在 native ，引入 NativeAllocationRegistry 类释放数据
