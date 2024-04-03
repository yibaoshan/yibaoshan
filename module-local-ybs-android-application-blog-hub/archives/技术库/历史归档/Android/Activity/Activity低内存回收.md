
## 如何判断 APP 内存不足？

我们都知道 Android 虚拟机是有内存限制的，当 APP 使用内存达到某个阈值以后，系统会强制回收一些资源

其中就包括 Activity

那么，判断使用阈值的代码在哪呢？

```
class ActivityThread {
    void attach(boolean system, long startSeq) {
        ...
        // Watch for getting close to heap limit.
        BinderInternal.addGcWatcher({
            Runtime runtime = Runtime.getRuntime();
            long dalvikMax = runtime.maxMemory();
            long dalvikUsed = runtime.totalMemory() - runtime.freeMemory();
            // 当内存大于3/4的时候，启动回收策略
            if (dalvikUsed > ((3*dalvikMax)/4)) {
                mSomeActivitiesChanged = false;
                // 释放 Activity
                ActivityTaskManager.getService().releaseSomeActivities(mAppThread);
            }
        });
    }
```

通过上面源码我们可以看到，在 APP 的入口类 ActivityThread#attach() 方法中

通过 BinderInternal.addGcWatcher 进行了一个 gc 的监听

如果此时已用内存大于 runtime.maxMemory() 即当前进程最大可用内存的 3/4的时候，就会进入一个释放逻辑

## 释放非活跃 Activity

我们继续看 ActivityTaskManager.getService().releaseSomeActivities 中 releaseSomeActivities 函数的实现

```
class WindowProcessController {
    void releaseSomeActivities(String reason) {
        ArrayList<ActivityRecord> candidates = null;
        for (int i = 0; i < mActivities.size(); i++) {
            遍历所有的ActivityRecord
            final ActivityRecord r = mActivities.get(i);
            如果当前activity本来就处于finishing或者DESTROYING/DESTROYED状态，continue，即不加入activity的释放列表
            if (r.finishing || r.isState(DESTROYING, DESTROYED)) {
                if (DEBUG_RELEASE) Slog.d(TAG_RELEASE, "Abort release; already destroying: " + r);
                return;
            }
            // 如果处于以下状态，则该activity也不会被回收
            if (r.mVisibleRequested || !r.stopped || !r.hasSavedState() || !r.isDestroyable()
                    || r.isState(STARTED, RESUMED, PAUSING, PAUSED, STOPPING)) {
                if (DEBUG_RELEASE) Slog.d(TAG_RELEASE, "Not releasing in-use activity: " + r);
                continue;
            }
        }
        
        // 上面所以要释放的activityRecord信息都存在了candidates中
        if (candidates != null) {
            // Sort based on z-order in hierarchy.
            candidates.sort(WindowContainer::compareTo);
            // Release some older activities
            int maxRelease = Math.max(candidates.size(), 1);
            do {
                final ActivityRecord r = candidates.remove(0);
                // 回收
                r.destroyImmediately(reason);
                --maxRelease;
            } while (maxRelease > 0);
        }
    }
}
```

在上面的代码中，candidates 集合就包含了系统即将回收的 activity

添加到集合的 activity 需要满足：

- stopped 处于非stopped状态，就是当前 activity 必须是可见状态
- 或者是壁纸类、后台播放类的，mVisibleRequested = true

另外，如果当前的 activity 的 finishing 为true 或者 当前状态处于 DESTROYING, DESTROYED

那么这个 activity 就不会再被加入回收列表了，因为本来已经要被回收

## 参考资料

- [避免踩坑，内存不足时系统回收Activity的流程解析](https://juejin.cn/post/7162521119004557348)