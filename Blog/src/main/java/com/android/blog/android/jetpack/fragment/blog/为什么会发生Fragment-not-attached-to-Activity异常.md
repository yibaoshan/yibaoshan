
为什么会发生 Fragment not attached to Activity 异常？

事情是这样的，前两天有位大佬在群里提了个问题，原文如下

我是图片

一个 Fragment 在点击按钮跳转一个新的 Activity 的时候，报崩溃异常：**Fragment not attached to Activity**

问：**复现路径可能是什么样的呢？**

# 一、回答问题前先审题

我们把这个问题的几个关键词圈出来

首先，可以点击 Fragment 上的按钮，证明这个 Fragment 是可以被看到的，那肯定是处于存活的状态的

其次，在跳转到新的 Activity 的时候发生崩溃，证明 Fragment 调用的是 **`startActivity()`** 方法

最后，来看异常信息：**Fragment not attached to Activity**

这个报错我们都已经很熟悉了，在 **`onAttach()`** 之前，或者 **`onDetach()`** 之后，调用任何和 Context 相关的方法，都会抛出 " **not attached to Activity** " 异常

发生的原因往往是因为异步任务导致的，比如一个网络请求回来以后，再调用了 **`startActivity()`** 进行页面跳转，或者调用 **`getResources()`** 获取资源文件等等

解决方案也非常简单：在 Fragment 调用了 Context 相关方法前，先通过 **`isAdded()`** 方法检查 Fragment 的存活状态就完事了

到这里，崩溃产生的原因找到了，解决方案也有了，似乎整篇文章就可以结束了

但是，楼主问的是：**复现路径可能是什么样的呢？**

这勾起了我的好奇心，我也想知道可能的路径是怎样的

于是，在接下来的两个晚上，笔者开始了一场源码之旅..

# 二、大胆假设，小心求证

审题结束我们就可以开始动手解答了，以下是群里的完整对话

大佬：一个 Fragment 在点击按钮跳转一个新的 Activity 的时候，报崩溃异常：**Fragment not attached to Activity** 。复现路径可能是什么样的呢？

我：这个问题之前在项目中也有碰到过，当时的解决方案是，通过调用 **`isAdded()`** 来检查 Fragment 是否还活着，来避免因为上下文为空导致的崩溃

当时忙于做业务没有深入研究，现在趁着晚上有时间来研究一下下

首先，打开 Fragment 源码，路径在：frameworks/base/core/java/android/app/Fragment.java

用 “**not attached to Activity**” 作为关键字搜索，可以发现 **`getResources()`** 、**`getLoaderManager()`** 、**`startActivity()`** 等等共计 **6** 处地方，都可能抛出这个异常

题目明确提到，是跳转 Activity 时发生的错误，那我们直接来看 **`startActivity()`** 方法

```java
class Fragment {
    void startActivity(){
        if (mHost == null)
            throw new IllegalStateException("Fragment " + this + " not attached to Activity");
    }
}
```

从上面代码可以看出，当 **`mHost`** 对象为空时，程序抛出 **Fragment not attached to Activity** 异常

好，现在我们的问题转变为:

1. **`mHost` 对象什么时候会被赋值？**

很显然，如果在赋值前调用了 `startActivity()` 方法，那程序必然会崩溃

2. **`mHost` 对象赋值以后，可能会被置空吗？如果会，什么时候发生？**

我们都知道，Fragment 依赖 Activity 才能生存，那我们有理由怀疑：

当 Activity 执行 `stop` / `destroy` ，或者，配置发生变化（比如屏幕旋转）导致 Activity 重建，会不会将 **`mHost`** 对象也置空呢？

## mHost 对象什么时候会被赋值？

先来看第一个问题，**`mHost` 对象什么时候会被赋值？**

平时我们使用 Fragment 开发时，通常都是直接 **new** 一个对象出来，然后再提交给 FragmentManager 去显示

创建 Fragment 对象的时候，不要求传入 **`mHost`** 参数，那 **`mHost`** 对象只能是 Android 系统帮我们赋值的了

得，又得去翻源码

打开 FragmentManager.java ，路径在：/frameworks/base/core/java/android/app/FragmentManager.java

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

我们发现源码里只有一个地方会给 **`mHost`** 对象赋值，在 **`FragmetnManager#moveToState()`** 方法中

如果当前 Fragment 的状态是 **`INITIALIZING`** ，那么就把 FragmentManager 自身的 **`mHost`** 对象，赋值给 Fragment 的 **`mHost`** 对象

这里多说一句，在 Android 系统中，一个 Activity 只会对应一个 FragmentManager 管理者。而 FragmentManager 中的 **`mHost`** ，其本质上就是 Activity 宿主。

所以，这里把 FragmentManager 的 **`mHost`** 对象，赋值给了 Fragment ，就相当于 Fragment 也持有了宿主 Activity

这也解释了我们之所以能在 Fragment 中调用 **`getResource()`** 、**`startActivity()`** 等需要 **`context`** 的才能访问方法，实际使用的就是 Activity 的上下文

废话说完了，我们来聊正事

**`FragmentManager#moveToState()`** 方法会先去判断 Fragment 的状态，那我们首先得知道 Fragment 有哪几种状态

```java
class Fragment {

    int INITIALIZING = 0;     // Not yet created.
    int CREATED = 1;          // Created.
    int ACTIVITY_CREATED = 2; // The activity has finished its creation.
    ... // 共6种标识
    
    int mState = INITIALIZING; // 默认为 INITIALIZING
}
```

Google 为 Fragment 共声明了6个状态标识符，各个标识符的含义看注释即可

这里重点关注标识符下面的 **`mState`** 变量，它表示的是 Fragment 当前的状态，默认为 **`INITIALIZING`**

了解完 Fragment 的状态标识，我们回过头继续来看 **`FragmentManager#moveToState()`** 方法

``` java
class FragmentManager {
    void moveToState(f,newState){
        switch(f.mState){
            case Fragment.INITIALIZING: // 必走逻辑
                f.mHost = mHost; // 赋值 Fragment 的 mHost 对象
                f.onAttach(mHost.getContext());
        }
        f.mState = newState;
    }    
}

```

在 **`moveToState()`** 方法中，只要当前 Fragment 状态为 **`INITIALIZING`** ，即执行 **`mHost`** 的赋值操作

巧了不是，前面刚说完，**`mState`** 默认值就是 **`INITIALIZING`**

也就是说，在第一次调用 **`moveToState()`** 方法时，不管接下来 Fragment 要转变成什么状态（*根据 **`newState`** 的值来判断*）

首先，它都得从 **`INITIALIZING`** 状态变过去！那么，**`case = Fragment.INITIALIZING`** 这个分支必然会被执行！！这时候，**`mHost`** 也必然会被赋值！！！

再然后，才会有 **`onAttach()`** / **`onCreate()`** / **`onStart()`** 等等这些生命周期的回调！

因此，我们的第一个猜想：**在 `mHost` 对象赋值前，有没有可能调用 startActivity() 方法？**

答案显然是否定的

因为，根据楼主描述，点击按钮以后才发生的崩溃，视图能显示出来，说明 **`mHost`** 已经赋值过并且生命周期都正常走

那就只可能是点击按钮后，发生了什么事情，将 **`mHost`** 又置为 **null** 了

## mHost 对象什么时候会被置空？

继续，来看第二个问题：**`mHost` 对象赋值以后，可能会被置空吗？如果会，什么时候发生？**

我们就不绕弯了，直接说答案，**会！**

置空 **`mHost`** 的逻辑，同样藏在 FragmentManager 的源码里：

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

看上面的代码，分发 Fragment 的 **`performDetach()`** 方法后，紧接着就会把 `mHost` 对象置空！

标记为 "**`重点1号`**" 和 "**`重点2号`**" 的代码都会执行了置空 **`mHost`** 对象的逻辑，两者的区别是：

Fragment 有一个保留实例的接口 **`setRetainInstance(bool)`** ，如果设置为 true ，那么在销毁重建 Activity 时，不会销毁该 Fragment 的实例对象

当然这不是本节的重点，我们只需要知道：**执行完 `performDetach()` 方法后，无论如何，`mHost` 也都活不了了**

那，什么动作会触发 **`performDetach()`** 方法？


### **1、Activity 销毁重建**

不管因为什么原因，只要 Activity 被销毁，Fragment 也不能独善其身，所有的 Fragment 都会被一起销毁，对应的生命周期如下：

>  `Activity#onDestroy()` -> `Fragment#onDestroyView(`) - > `Fragment#onDestroy()` - >`Fragment#onDetach()`

### **2、调用 `FragmentTransaction#remove()` 方法移除 Fragment**

**`remove()`** 方法会移除当前的 Fragment 实例，如果这个 Fragment 正在屏幕上显示，那么 Android 会先移除视图，对应的生命周期如下：

> `Fragment#onPause()` -> `onStop()` -> `onDestroyView()` - > `onDestroy()` - >`onDetach()`

### **3、调用 `FragmentTransaction#replace()` 方法显示新的 Fragment**

**`replace()`** 方法会将所有的 Fragment 实例对象都移除掉，只会保留当前提交的 Fragment 对象，生命周期参考 **`remove()`** 方法

以上三种场景，是我自己做测试得出来的结果，应该还有其他没测出来的场景，欢迎大佬补充

另外，FragmentTransaction 中还有两个常用的 **`detach()`** / **`hide()`** 方法，它俩只会将视图移除或隐藏，而不会触发 **`performDetach()`** 方法

## 真相永远只有一个

好了，现在我们知道了 **`mHost`** 对象置空的时机，答案已经越来越近了

我们先来汇总下已有的线索

从 FragmentManager 源码来看，只要我们的 **`startActivity()`** 页面跳转逻辑写在：

**`onAttach()` 方法执行之后 ，`onDetach()` 方法执行之前**

那结果一定总是能够跳转成功，不会报错！



那么问题就来了

**`onAttach()` 之前，视图不存在，`onDetach()` 之后，视图都已经销毁了，还点击哪门子按钮？**

这句话翻译一下就是：

**视图在，Activity 在，点击事件正常响应**

**视图不在，按钮也不在了呀，也就不存在页面跳转了**

这样看起来，似乎永远不会出现楼主说的错误嘛

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

等待 1s 执行 **`startActivity()`** 方法时，程序就会发生崩溃，这时候终于可以看到我们期待已久的异常：**Fragment not attached to Activity**

为什么会这样？熟悉 Java 的小伙伴这里肯定要说了，因为提交到 Handler 的 Runnable 会持有外部类呀，也就是宿主 Fragment 的引用。如果在执行 **`Runnable#run()`** 方法之前， Fragment 的 **`mHost`** 被清空，那程序肯定会发生崩溃的

那我们怎么样才能防止程序崩溃呢？

- **要么，同步执行 Context 相关方法**

- **要么，异步判空，用到 Context 前调用 `isAdded()` 方法检查 Fragment 存活状态**


# 三、结语

呼~ 这下总算是理清了，我们来尝试回答楼主的问题：**发生 not attached to Activity，可能路径是怎样的？**

首先，必然存在一个异步任务持有 Fragment 引用，并且内部调用了 **`startActivity()`** 方法。

在这个异步任务提交之后，执行之前，一旦发生了下面列表中，一个或多个的情况时，程序就会抛出  **not attached to Activity** 异常：

- **调用 `finishXXX()` 结束了 Activity，导致 Activity 为空**
- **手动调用 `Activity#recreate()` 方法，导致 Activity 重建**
- **旋转屏幕、键盘可用性改变、更改语言等配置更改，导致 Activity 重建**
- **向 FragmentManager 提交 `remove()` / `replace()` 请求，导致 Fragment 实例被销毁**
- **...**

最后，发生这个错误信息的本质，是在 Activity 、Fragment 销毁时，没有同步取消异步任务，这是内存泄漏啊

所以，除了使用 **`isAdded()`** 方法判空，避免程序崩溃外，更应该排查哪里可能会长时间引用该 Fragment

如果可能，在 Fragment 的 **`onDestroy()`** 方法中，取消异步任务，或者，把 Fragment 改为弱引用

# 四、参考资料

- [android-7.1 - Fragment](http://www.aospxref.com/android-7.1.2_r39/xref/frameworks/base/core/java/android/app/Fragment.java)
- [android-7.1 - FragmentManager](http://www.aospxref.com/android-7.1.2_r39/xref/frameworks/base/core/java/android/app/FragmentManager.java)
