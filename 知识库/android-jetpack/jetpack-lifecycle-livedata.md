
拥有生命周期感知能力的数据持有类，遵循观察者模式，并提供 getVal 方法

- 观察者调用 observe 监听数据变化
- 观察者使用 set/post 更新数据，一个同步一个异步
- 仅在 started、resumed 时活跃，可以规避内存泄漏和一些 bug，如果异步有结果但页面 stop 时，数据暂存直到重新 started 时才触发


| 方法名                           | 作用                         |
| ----------------------------- |----------------------------|
| `observe()`                   | 注册观察者（生命周期感知）              |
| `observeForever()`            | 注册非生命周期感知观察者，注意需要手动释放      |
| `setValue()`                  | 主线程更新数据                    |
| `postValue()`                 | 子线程更新数据                    |
| `onActive()` / `onInactive()` | 用于子类监听激活/不激活状态（比如自动开始网络请求） |

LiveData 特点和注意事项

- 值没有变化时，指的是对象引用是否相同，LiveData 不会主动通知相同的值
- 粘性事件问题（Sticky），observe() 会立刻触发一次 dispatchingValue()，然后因为 mLastVersion < mVersion，所以，新注册的观察者会立即收到最新数据
  - 从设计角度来说，粘性事件是正常的
  - 如果想解决，可以自定义 SingleLiveEvent 继承自 MutableLiveData，增加布尔值决定是否调用 onChanged
- Fragment 记得使用 viewLifecycleOwner，因为 Fragment 和 View 的生命周期不一致，有些 Fragment 甚至没有 UI，可以避免 Fragment 视图销毁后仍接收更新

# 基本实现

核心字段

```java
private static final Object NOT_SET = new Object(); // 表示尚未设置值
private T mData; // 当前值
private int mVersion; // 当前数据版本
private final Map<Observer<? super T>, ObserverWrapper> mObservers; // 观察者列表
```

分发过程

```java
private void dispatchingValue(ObserverWrapper initiator) {
    for (Map.Entry<Observer<? super T>, ObserverWrapper> entry : mObservers) {
        ObserverWrapper wrapper = entry.getValue();
        
        if (!wrapper.mActive) continue; // 不活跃的不通知

        if (wrapper.mLastVersion < mVersion) {
            wrapper.mLastVersion = mVersion;
            wrapper.mObserver.onChanged(mData);
        }
    }
}
```

post 分发，ArchTaskExecutor 中默认的 DefaultTaskExecutor 也还是使用 Handler 切换到 UI 线程

```java
protected void postValue(T value) {
    boolean postTask = mPendingData == NOT_SET;
    mPendingData = value;

    if (postTask) {
        ArchTaskExecutor.getInstance().postToMainThread(mPostValueRunnable);
    }
}
```

生命周期感知，内部类 LifecycleBoundObserver 监听组件状态，如果生命周期变化，触发 shouldBeActive() 检查，决定是否通知回调。

```java
class LifecycleBoundObserver extends ObserverWrapper implements LifecycleEventObserver {
    @Override
    public void onStateChanged(LifecycleOwner source, Lifecycle.Event event) {
        boolean currentlyActive = shouldBeActive();
        activeStateChanged(currentlyActive);
    }

    @Override
    boolean shouldBeActive() {
        return mOwner.getLifecycle().getCurrentState().isAtLeast(STARTED);
    }
}
```


