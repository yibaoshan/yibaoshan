
问各位一个比较常见的问题。一个fragment在点击按钮直接跳转一个新的activity的时候，报崩溃异常：fragment not attached to Activity。复现路径可能是什么样的呢

这个问题之前在项目中也有碰到过，不过那时候忙于业务没有深入研究

当时的解决方案是，通过调用 isAdded() 判断 Fragment 是否添加到 Activity ，来避免因为上下文为空导致的崩溃

现在来研究一下下，描述：在一个 Fragment 页面中，点击按钮跳转到新的 Activity 时，报崩溃异常：Fragment not attached to Activity

问：可能路径是怎样的？

网上搜了一圈，没发现靠谱的答案，那我们就来跟踪源码，试试能不能从源码中推出来

首先，打开 Fragment 源码，路径：frameworks/base/core/java/android/app/Fragment.java

用 “not attached to Activity” 作为关键字搜索，可以发现 getResources() 、getLoaderManager() 、startActivity() 等等共计 6 处地方，都可能抛出这个错误信息

题目明确提到，是在跳转 Activity 时发生的错误，那我们直接来看 Fragment#startActivity() 方法

void startActivity(){
    if (mHost == null)
        throw new IllegalStateException("Fragment " + this + " not attached to Activity");
}

从代码可以看到，mHost 对象为空时，就会抛出 Fragment not attached to Activity 异常

好，现在我们的问题转变为:

1. mHost 对象什么时候会被赋值？

很显然，如果在赋值前调用了 startActivity() 方法，那程序必然会崩溃

2. mHost 对象会被置空吗？如果会，什么时候发生？

我们都知道，Fragment 依赖 Activity 才能生存，那我们有理由怀疑：

当 Activity 执行 stop/destory ，或者，配置发生变化（比如屏幕旋转）导致 Activity 重建，会不会发生 mHost 对象置空的情况？

先来看第一个问题，mHost 对象什么时候会被赋值？

平时我们使用 Fragment 时，通常都是先 new 一个对象出来，然后再提交给 FragmentManager 去显示

我们在创建 Fragment 对象时不需要传入 mHost ，那 mHost 对象只能是 Android 系统帮我们赋值的了

得，又得去翻源码

打开 FragmentManager.java ，路径在：/frameworks/base/core/java/android/app/FragmentManager.java

void moveToState(f){
    if (f.mState < newState) {
        switch(f.mState){
            case Fragment.INITIALIZING:
                f.mHost = mHost; // 赋值 Fragment 的 mHost 对象
        }
    } else if (f.mState > newState) {
        case Fragment.CREATED:
            f.initState();
            f.mHost = null; // mHost 对象置空
    }

}

接上文，FragmentManager#moveToState() 方法中，有对 mHost 对象进行赋值、置空的操作

那么明天的重点在于，理清 moveToState() 方法逻辑，什么时候执行的赋值和置空？

--------------------------------------------------------------------------------------------------------------------


即使 Activity 因为各种原因被销毁重建，在 FragmentActivity 的 onSaveInstance()/onRestoreInstance()

这个问题就可以下结论了：不要在异步任务中执行跳转 Activity ！！！

同步代码：

```
void onClick(){  
    startActivity();
}
```

异步代码（拿 Handler 举例子，实际上业务可能复杂的多）：

```

void onClick(){  
    Handler().postDelayed(startActivity(),0);
}

```

看到上面的代码，有同学可能要问了，你这 delay 时间传入的是0，代码应该会立即执行，不能算是异步吧~

要知道，Android

class Fragment$1 implements Runnable {
    Fragment$1(Fragment var1) {
        this.this$0 = var1;
    }

    public void run() {
        this.this$0.startActivity();
    }
}

class ActivityThread {
    void main(){  
        message.callback.run();
    }
}

Runnable 会持有 Fragment 的实例对象

一旦该 Fragment 对象发生 remove 、replace 操作，

并且，一旦 Activity 发生重建，

1. 异步回调 + Activity 销毁重建，内部类持有的 Activity 已经不在了

--------------------------------------------------------------------------------------------------------------------

书接上回，昨天我们说到，调用 Fragment#startActivity() 发生崩溃是因为 mHost 对象为空

接着，我们又提出两个猜想：

1. 在 mHost 对象赋值前，有没有可能调用 startActivity() 进行页面跳转？
2. 在 mHost 赋值后，会不会因为发生一些事情，将 mHost 被置空，导致调用 startActivity() 时发生崩溃

先来看第一个，有没有可能在 mHost 对象赋值前调用 startActivity() ？这个问题的关键点在于，mHost 对象什么时候会被赋值？

打开 FragmentManager 源码，搜索 "mHost" 关键字

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

我们会发现源码里只有一个地方会给 mHost 对象赋值，在 moveToState() 方法中

如果 Fragment 的状态是 INITIALIZING ，那么就把 FragmentManager 自身的 mHost 对象，赋值给 Fragment 的 mHost 对象

这里多说一句，在 Android 中，一个 Activity 只会对应一个 FragmentManager 管理者。而 FragmentManager 中的 mHost ，其本质上就是 Activity 宿主。

所以，这里又把 FragmentManager 的 mHost 对象，赋值给了 Fragment ，相当于 Fragment 也持有了宿主 Activity

我们在 Fragment 调用的 getResource() 、startActivity() 等需要 context 的才能访问方法时，实际使用的就是 Activity 的上下文

废话说完了，我们来聊正事

moveToState() 方法会去判断 Fragment 的状态，那我们首先得知道 Fragment 有哪几种状态？

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

我们这里重点关注 mState 变量，它表示的是 Fragment 当前的状态，默认为 INITIALIZING ，记住它，很重要

好，Fragment 的状态介绍完了，我们回到 FragmentManager#moveToState() 方法

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

在 moveToState() 方法中，只要 Fragment 状态为 INITIALIZING ，即执行 mHost 的赋值操作

也就是说，不管接下来 Fragment 要转变成什么状态（假设 newState 的值为 CREATED）

首先，它都得从 INITIALIZING 状态变过去！那么，case = Fragment.INITIALIZING 这个分支必然会被执行！！mHost 也必然会被赋值！！！

再然后，才会有 onAttach() / onCreate() / onStart() 等等这些生命周期的回调

回到楼主的提问：fragment 在点击按钮跳转一个新的 activity 的时候报错

fragment 能被点击，表示 fragment 视图肯定已经加载渲染完成，生命周期是正常走的

而通过刚才源码跟踪我们发现，只有当 mHost 对象被赋值以后，才会执行 fragment 的生命周期

因此，我们的第一个猜想：在 mHost 对象赋值前，有没有可能调用 startActivity() 进行页面跳转？

答案显然是否定的


继续，来看第二个问题：会不会因为发生一些事情，将 mHost 被置空？

直接说答案，会！

置空 mHost 的操作，同样藏在 FragmentManager 的源码里

``` java
class FragmentManager {
    
    void moveToState(f,newState){
        if (f.mState < newState) {
            switch(f.mState){
                case Fragment.INITIALIZING:
                    f.mHost = mHost; // 赋值 Fragment 的 mHost 对象
            }
        } else if (f.mState > newState) {
            switch (f.mState) {
                case Fragment.CREATED:
                    if (newState < Fragment.CREATED) {
                        if (!keepActive) {
                            if (!f.mRetaining) {
                                makeInactive(f); // 重点1号，这里会清空 mHost
                            } else {
                                f.mHost = null; // 重点2号，这里也会清空 mHost 对象
                            }
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

先看代码，moveToState() 方法中，有两处会置空 mHost 的地方，我把它们标记为 "重点1号" 和 "重点2号"

至于什么时候会走这两个分支，ummmmmmmm~ 我也不清楚，这里面判断条件有点多，我也懒得去一步步跟逻辑了

不过，虽然没有 debug 源码，但是我写了 demo 验证什么时候会抛错，好歹也算有个交代~

验证的结果是：

remove() / replace() 这两个方法会将 Fragment 状态置为失效，mHost 对象也会跟着置空

detach() / hide() 方法只是将视图移除或隐藏，不会更改 Fragment 的状态

有意思的地方来了！

这说明，只要用户能看到 Fragment 的视图，那就表示这是个活着的 Fragment

既然是活着的 Fragment 对象，那它的点击事件一定能响应！！！

从我的测试结果来看，在 Fragment 同步调用 startActivity() 方法时，总是能够跳转成功，不会报错，和猜想是对得上的

Activity 销毁重建的测试我也做了，不管是使用"不保留活动"还是旋转屏幕，或是手动调用 Activity#recreate()，只要 Activity 销毁重建，从日志上来看，Fragment 也会跟着重建，一旦重建对象，那 mHost 肯定不为空

那到底什么情况下才会抛出 "Fragment not attached to Activity" 异常呢？

答案是，异步调用！！！

什么意思呢？举个例子

同步调用代码：

```java
class Fragment {
    void onClick(){  
        startActivity();
    }
}
```

将上面的同步代码改成异步：

```java
class Fragment {
    void onClick(){  
        Handler().postDelayed(startActivity(),0);
    }
}

```

我这里是用 Handler 把同步改成异步，加了一句代码，将 startActivity() 方法放到了 postDelayed() 里面执行

就改了这么一点，如果此时刚好发生了 remove() / replace() 或者宿主 Activity 被销毁重建，那么等到执行 startActivity() 方法时，就会看到我们期待已久的异常：Fragment not attached to Activity

为什么会这样？熟悉 JVM 的小伙伴这里肯定要说了，因为提交到 Handler 的 Runnable 是内部类呀，它会持有外部类，也就是宿主 Fragment 的引用，如果在执行 run() 方法之前 Fragment 的 mHost 被清空，就会发生崩溃

上面的代码其实还有个问题，有眼尖的同学可能已经发现了，我们的延迟时间 delay 传入的是0，代码应该会立即执行，不能算是异步吧~

其实，这个 runnable 会被封装成消息提交到消息队列，

好了，分析到这里我们可以尝试给出结论了，