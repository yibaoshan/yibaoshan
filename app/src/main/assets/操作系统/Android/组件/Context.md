##  Overview
1. Context介绍

## 一、Context介绍

Context是一个抽象类，它的实现由Android系统提供。Context允许访问特定资源和类，还拥有调用系统级别的能力，比如发送广播，启动Activity等

### 1、Context分类介绍

Context采用装饰模式设计
Context实现类：ContextImpl
Context包装类：ContextWrapper、ContextThemeWrapper
Context子类：
1. Activity继承自ContextThemeWrapper
2. Application继承自ContextWrapper
3. Service继承自ContextWrapper

#### 1.1 Activity继承自ContextThemeWrapper

Activity的Context是由AMS控制的，创建动作发生在ActivityThread.performLaunchActivity()方法中：
1. 创建ContextImpl实例对象
2. 创建Activity实例对象，调用Context.setOuterContext()方法将Activity实例传递进去，Context和Activity相互引用
3. 创建Application实例对象，若已创建，直接返回
5. 调用Activity的attach方法传递ContextImpl实例
6. 回调onCreate()方法

#### 1.2 Service继承自ContextWrapper

Activity的Context是由AMS控制的，创建动作发生在ActivityThread.handleCreateService()方法中：
创建流程同上

#### 1.3 Application继承自ContextWrapper

Activity的Context是由AMS控制的，创建动作发生在ActivityThread.handleBindApplication()方法中：
最终调用的是LoadedApk.makeApplication()方法

创建流程同上上

#### 1.4 Broadcast和ContentProvider的Context

- Broadcast
> 因为只有组件才可以注册全局广播，所以BroadcastReceiver在哪个组件注册的使用的就是哪个组件的上下文  
> 调用的就是Context.setOuterContext传入的组件的实例对象，getOuterContext()

- ContentProvider
> ContentProvider是在创建Application时启动的，而且轻易不会死，所以ContentProvider的Context是Application的上下文


## 常见问题

- 为什么使用Application启动Activity会报错？
> 答：因为Application没有TaskRecord任务栈，所以要么加一个任务栈要么使用NEW_TASK flag

- 在Activity调用getApplication()和getApplicationContext()得到的对象有什么区别呢？
> 答：返回的对象没有区别，都是当前应用的application对象  
> 虽然两者返回值是一样的，但两个方法意义不一样  
> getApplication()是Activity的final方法，返回值为Application  
> getApplicationContext()是Context的方法，返回值为Context
