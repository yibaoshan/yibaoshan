
一般要配合 LiveData、DataBinding一起使用

- ViewModel不会随着Activity的屏幕旋转而销毁；
- 在对应的作用域内，保正只生产出对应的唯一实例，保证UI组件间的通信

## ViewModel&onSaveInstanceState

- 对于简单的数据，Activity 可以使用 onSaveInstanceState() 方法从 onCreate() 中的捆绑包恢复其数据，但此方法仅适合可以序列化再反序列化的少量数据，而不适合数量可能较大的数据，如用户列表或位图。
- ViewModel 存储大量数据，不用序列化与反序列化
- onSaveInstanceState 存储少量数据
- 相辅相成，不是替代
- 进程关闭是 onSaveInstanceState 的数据会保留，而 ViewModel 销毁

## 为什么不会随着Activity的屏幕旋转而销毁

- ViewModel 在 ViewModelStore 中的 map 中存着呢
- Android 提供 onRetainNonConfigurationInstance() 方法，用于处理配置发生改变时数据的保存
- ComponentActivity 重写了该方法
- 同样在 ComponentActivity 的 onDestroy() ，若不是配置更改，调用 onCleared() 方法

## 为什么在对应的作用域内，保正只生产出对应的唯一实例

ViewModelStore的 get方法，是根据 key 来取值的，如果 key相同，那取出来的ViewModel就是一个

## onCleared方法在什么调用

当 Activity 真正销毁的时候，而不是配置改变会调用ViewModelStore的 clear进而调用了ViewModel的onCleared，具体内容看第 9.2 小节

## 参考资料

- [Android Jetpack组件ViewModel基本使用和原理分析](https://github.com/jhbxyz/ArticleRecord/blob/master/articles/Jetpack/3ViewModel%E5%9F%BA%E6%9C%AC%E4%BD%BF%E7%94%A8%E5%92%8C%E5%8E%9F%E7%90%86%E5%88%86%E6%9E%90.md)
