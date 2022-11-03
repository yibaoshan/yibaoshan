书接上回

昨天我们说到，调用 `Fragment#startActivity()` 发生崩溃是因为 `mHost` 对象为空

接着，我们又提出两个猜想：

1. **在 `mHost` 对象赋值前，有没有可能调用 `startActivity()` 进行页面跳转？**
2. **在 `mHost` 赋值后，会不会因为发生一些事情，将 `mHost` 被置空，导致调用 startActivity() 时发生崩溃**



先来看第一个，有没有可能在 `mHost` 对象赋值前调用 startActivity() ？这个问题的关键点在于，`mHost` 对象什么时候会被赋值？



打开 FragmentManager 源码，搜索 `"mHost"` 关键字

``` java
class FragmentManager {

    FragmentHostCallback mHost; // 内部持有 Context 对象，其本质是宿主 Activity
    
    void moveToState(f,newState){
        switch(f.mState){
            case Fragment.INITIALIZING:
                f.mHost = mHost; // 赋值 Fragment 的 mHost 对象
                f.onAttach(mHost.getContext());
        }
        f.mState = newState;
    }
}
```

我们发现源码里只有一个地方会给 `mHost` 对象赋值，在 `moveToState()` 方法中

如果当前 Fragment 的状态是 `INITIALIZING` ，那么就把 FragmentManager 自身的 `mHos`t 对象，赋值给 Fragment 的 `mHost` 对象

这里多说一句，在 Android 中，一个 Activity 只会对应一个 FragmentManager 管理者。而 FragmentManager 中的 `mHost` ，其本质上就是 Activity 宿主。

所以，这里把 FragmentManager 的 `mHost` 对象，赋值给了 Fragment ，就相当于 Fragment 也持有了宿主 Activity

我们在 Fragment 调用的 `getResource()` 、`startActivity()` 等需要 `context` 的才能访问方法时，实际使用的就是 Activity 的上下文

废话说完了，我们来聊正事

`FragmentManager#moveToState()` 方法会先去判断 Fragment 的状态，那我们首先得知道 Fragment 有哪几种状态

```java
class Fragment {

    int INITIALIZING = 0;     // Not yet created.
    int CREATED = 1;          // Created.
    int ACTIVITY_CREATED = 2; // The activity has finished its creation.
    ... // 共6种标识
    
    int mState = INITIALIZING; // 默认为 INITIALIZING
}
```

看代码，Fragment 一共声明了6个标识符，各个标识符的含义看注释即可

这里重点关注 `mState` 变量，它表示的是 Fragment 当前的状态，默认为 `INITIALIZING `

好，Fragment 的状态介绍完了，我们回到 `FragmentManager#moveToState()` 方法

``` java
    void moveToState(f,newState){
        switch(f.mState){
            case Fragment.INITIALIZING:
                f.mHost = mHost; // 赋值 Fragment 的 mHost 对象
                f.onAttach(mHost.getContext());
        }
        f.mState = newState;
    }
```

在 `moveToState()` 方法中，只要当前 Fragment 状态为 `INITIALIZING` ，即执行 `mHost` 的赋值操作

巧了不是，前面刚说完，`mState` 默认值就是 `INITIALIZING` 

也就是说，在第一次调用 `moveToState()` 方法时，不管接下来 Fragment 要转变成什么状态（*根据 `newState` 的值来判断*）

首先，它都得从 `INITIALIZING` 状态变过去！那么，`case = Fragment.INITIALIZING` 这个分支必然会被执行！！`mHost` 也必然会被赋值！！！

再然后，才会有 `onAttach()` / `onCreate()` / `onStart()` 等等这些生命周期的回调！



我们回头看楼主的提问：**fragment 在点击按钮跳转一个新的 activity 的时候报错**

这里有个重点，fragment 能被点击，那说明 fragment 视图已经加载并且渲染完成，生命周期肯定是正常走的

而刚才通过源码跟踪我们发现，只有当 `mHost` 对象被赋值以后，才会执行 fragment 的生命周期

因此，我们的第一个猜想：**在 `mHost` 对象赋值前，有没有可能调用 startActivity() 方法执行页面跳转？**

答案显然是否定的




继续，来看第二个问题：**会不会因为发生一些事情，将 `mHost` 被置空？**

直接说答案，**会！**

置空 `mHost` 的操作，同样藏在 FragmentManager 的源码里：

``` java
class FragmentManager {
    
    void moveToState(f,newState){
        if (f.mState < newState) {
            switch(f.mState){
                case Fragment.INITIALIZING:
                    f.mHost = mHost; // mHost 对象赋值
            }
        } else if (f.mState > newState) {
            switch (f.mState) {
                case Fragment.CREATED:
                    if (newState < Fragment.CREATED) {
                        f.performDetach(); // 调用 Fragment 的 onDetach()
                      	if (!f.mRetaining) {
                          	makeInactive(f); // 重点1号，这里会清空 mHost
                         } else {
                            f.mHost = null; // 重点2号，这里也会清空 mHost 对象
                         }
                    }
            }
        }
        f.mState = newState;
    }
    
    void makeInactive(f) {
        f.initState(); // 此调用会清空 Fragment 全部状态，包括 mHost
    }
}
```

看上面的代码，执行 Fragment 的 `performDetach()` 方法后，紧接着就会把 `mHost` 对象置空！

标记为 "`重点1号`" 和 "`重点2号`" 的代码都执行了置空 `mHost` 对象的逻辑，两者的区别是：

Fragment 有一个保留实例的接口 `setRetainInstance(bool)` ，如果设置为 true ，那么在销毁重建 Activity 时，不会销毁该 Fragment 的实例对象

当然这不是本节的重点，我们只需要知道：执行完 `performDetach()` 方法后，无论如何，`mHost` 也都活不了了

那，什么动作会触发 `performDetach()` 方法？



**1、Activity 销毁重建**

不管因为什么原因，只要 Activity 被销毁，Fragment 也不能独善其身，所有的 Fragment 都会被一起销毁，对应的生命周期如下：

 `Activity#onDestroy()` -> `Fragment#onDestroyView(`) - > `Fragment#onDestroy()` - >`Fragment#onDetach()`

**2、调用 `FragmentTransaction#remove()` 方法移除 Fragment**

`remove()` 方法会移除当前的 Fragment 实例，如果这个 Fragment 正在屏幕上显示，那么 Android 会先移除视图，对应的生命周期如下：

`Fragment#onPause()` -> `onStop()` -> `onDestroyView()` - > `onDestroy()` - >`onDetach()`

**3、调用 `FragmentTransaction#replace()` 方法显示新的 Fragment**

`replace()` 方法会将所有的 Fragment 实例对象都移除掉，只会保留当前提交的 Fragment 对象，生命周期参考 `remove()` 方法



以上三种场景，是我自己做测试得出来的结果，应该还有其他没测出来的场景，欢迎大佬补充

另外，FragmentTransaction 中还有两个常用的 `detach()` / `hide()` 方法，只会将视图移除或隐藏，不会触发 `performDetach()` 方法



好了，现在我们知道了 `mHost` 对象置空的时机，答案已经越来越近了

我们先来汇总下已有的线索

从 FragmentManager 源码来看，只要我们的 `startActivity()` 页面跳转逻辑写在：

**`onAttach()` 方法执行之后 ，`onDetach()` 方法执行之前**

那结果一定总是能够跳转成功，不会报错！



那么问题就来了

**`onAttach()` 之前，视图不存在，`onDetach()` 之后，视图都已经销毁了，还点击哪门子按钮？**

这句话翻译一下就是：

**视图在，Activity 在，点击事件正常响应**

**视图不在，按钮也不在了呀，也就不存在页面跳转了**

这样看起来，似乎永远不会出现楼主说的异常嘛

除非。。。



**执行 `startActivity()` 方法的时候，视图已经不在了！！！**



这听起来很熟悉，ummmmmm。。这不就是异步调用吗？

```java
class Fragment {
    void onClick(){
      	//do something
        Handler().postDelayed(startActivity(),1000);
    }
}

```

上面是一段异步调用的演示代码，为了省事我直接用 Handler 提交了延迟消息

当用户点击跳转按钮后，一旦发生 Activity 销毁重建，或者 Fragment 被移除的情况

等待1s 执行 `startActivity()` 方法时，程序就会发生崩溃，这时候终于可以看到我们期待已久的异常：**Fragment not attached to Activity**

为什么会这样？熟悉 Java 的小伙伴这里肯定要说了，因为提交到 Handler 的 Runnable 会持有外部类呀，也就是宿主 Fragment 的引用。如果在执行 `Runnable#run()` 方法之前， Fragment 的 `mHost` 被清空，那程序肯定会发生崩溃的

那我们怎么样才能防止程序崩溃呢？

- **要么，同步执行，不要在异步中调用 Fragment 对象的方法**

- **要么，异步判空，用到上下文前调用 `isAdded()` 方法检查 Fragment 存活状态**

兜兜转转又回到了起点。。。不过好在，背后的逻辑算是理清了



好，最后来尝试回答楼主的问题：**发生 not attached to Activity，可能路径是怎样的？**

首先，必然存在一个异步任务调用了 `startActivity()` 方法。

在这个异步任务提交之后，执行之前，程序又发生了以下列表中，一个及以上的情况时，程序将会抛出  **not attached to Activity** 异常：

- **调用 `finishXXX()` 结束了 Activity，导致 Activity 为空**
- **手动调用 `Activity#recreate()` 方法，导致 Activity 重建**
- **旋转屏幕、键盘可用性改变、更改语言等配置更改，导致 Activity 重建**
- **向 FragmentManager 提交 `remove()` / `replace()` 请求，导致 Fragment 实例被销毁**
- **...**