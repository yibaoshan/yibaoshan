
SharedPreferences 是 Android 平台上 轻量级的存储类，用来保存App的各种配置信息

其本质是一个以 键值对（key-value）的方式保存数据的 xml 文件，保存在 /data/data/package/shared_prefs 目录下。

## 使用方式

### 读数据

读数据只需要获取 SP 对象，然后调用 getXXX 方法即可

```
SharedPreferences sp = Context#getSharedPreferences(key,Context.MODE_PRIVATE);
int age = sp.getInt("age",0);
```

### 修改数据

```
// 获取编辑器，优化写操作，一次 I/O
SharedPreferences.Editor editor = sp.edit(); 

editor.putInt("age", 28); // 修改 / 保存数据
editor.remove("age"); // 删除数据
editor.clear(); // 清空数据
// 同步提交保存，不建议使用
editor.commit();
// 异步保存文件
editor.apply();
```

## 源码实现

### 读优化与读锁

sp 初始化时，对 xml 文件进行一次读取，并将文件内所有内容（即所有的键值对）缓到内存的一个 Map 中

```
class SharedPreferencesImpl implements SharedPreferences {
  private final File mFile;             // 对应的xml文件
  private Map<String, Object> mMap;     // Map中缓存了xml文件中所有的键值对
  
  public String getString( key, defValue) {
    synchronized (mLock) { // 加锁是为了防止发生，读的时候 apply / commit 未完成
        return mMap.get(key);
    }
  }
}
```

另外，读操作是加锁的

防止发生：在读的时候，其他线程的 apply / commit 未完成

### 写优化与写锁

sp 使用了 mEditorMap 来保存写入或修改的数据，只有 commit() / apply() 时，才会将数据合并到主 map

```
class EditorImpl implements Editor {
  public Editor putString(String key, String value) {
      synchronized (mEditorLock) {
          mEditorMap.put(key, value);
          return this;
      }
  }
}
```

同样的，写操作也是加锁的。防止发生多线程同时写数据丢失的问题

### 数据合并与文件锁

在 mEditorMap 合并到主 map 的瞬间，为了保存数据安全，此时的 sp 是不能读也不能写的

- 不能读是因为，数据还没合并完，可能读不到最新的
- 不能写是因为，数据在合并，需要原子操作

所以，apply / commit 时需要先持有 mLock 和 mEditorLock 两把锁

等合并完成以后，执行 I/O 也需要一把文件锁：mWritingToDiskLock

最终，我们一共通过使用了3把锁，对整个写操作的线程安全进行了保证。

### 进程安全

SharedPreferences 默认不支持多进程，在多进程下可能会发生数据丢失

```
// This class does not support use across multiple processes.
public interface SharedPreferences {
  // ...
}
```

## ANR 问题

除了初始化和 commit 可能因为文件过大导致超时以外，apply 异步也会

ActivityThread#handleStopActivity() 方法中

在 Activity onStop 以及 Service 处理 onStop，onStartCommand 时

都会执行 QueuedWork.waitToFinish() 等待所有的等待锁释放。

```
private void handleStopActivity(IBinder token, boolean show, int configChanges, int seq) {
    // Make sure any pending writes are now committed.
    if (!r.isPreHoneycomb()) {
        QueuedWork.waitToFinish();
    }
    // 省略无关。。
}
```

## 参考资料

- [Android SharedPreferences的设计与实现](https://juejin.cn/post/6884505736836022280)
- [剖析 SharedPreference apply 引起的 ANR 问题](https://mp.weixin.qq.com/s?__biz=MzI1MzYzMjE0MQ==&mid=2247484387&idx=1&sn=e3c8d6ef52520c51b5e07306d9750e70)