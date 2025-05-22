
# Wakelocks（唤醒锁）机制

- PowerManager.WakeLock，用来阻止系统进入休眠，比如后台播放 MP3，刷新位置啥的
- Android 4.4 已放弃 wake lock，迁移到 Linux 3.4 主线的 autosleep+wakeup_sources
- 非主流服务，忽略

# CPUFreq Governor（CPU 频率调节）

- 性能、功耗、温度控制
- 非主流服务，忽略