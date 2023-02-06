
## 常见异常

- GC 超时：多个对象重写 finalize() 方法却没有在 10s 内完成，FinalizerDaemon 线程抛出 finalizerTimedOut 异常

