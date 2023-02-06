
## 卡顿监控

微信和 BlockCanary 三方库使用的方案：利用 Handler#setMessageLogging() 方法设置 printer

拿到 dispatchMessage() 方法执行的时间，发现耗时异常调用 Thread#getStackTrace() 保存方法堆栈

几种无法被 Handler 机制覆盖的 case：

- 事件分发：通过 PLT Hook（Native Hook的一种方式），去 Hook 这对 Socket 的 send 和 recv 方法，进而来监控 Touch 事件耗时
- IdleHandler：反射替换 MessageQueue 中的 mIdleHandlers，在自定义的 List 添加耗时监控

## 常见卡顿



## 卡顿治理

- 原则上尽量减少 GC（STW耗时），比如监听内存变化，及时释放资源