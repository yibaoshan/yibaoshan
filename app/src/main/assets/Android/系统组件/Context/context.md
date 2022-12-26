
Context 使用了装饰者模式

ContextWrapper 继承自 Context ，内部功能都由 ContextImpl 来实现

```
- Context 
    - ContextImpl
    - ContextWrapper
        - Application
        - Service
        - ReceiverRestrictedContext // 在 ContextImpl 类中，ContextImpl
        - ContextThemeWrapper
            - Activity
```

在上面的继承图中，Application 和 Service 都直接继承 ContextWrapper  

只有 Activity 比较特殊，因为它是有界面的，所以他需要一个主题：Theme

ContextThemeWrapper 在 ContextWrapper 的基础上增加与主题相关的操作

## Application

Application 的 Context 创建于：

ActivityThread#makeApplication()

## Activity

Activity 的 Context 创建于：

ActivityThread#performLaunchActivity()

## Service

Service 的 Context 创建于：

ActivityThread#handleCreateService()

以上可知，都是创建于对象创建的时候

## 几个没有自己 Context 的组件

- Provider，用的是 Application
- 动态广播用的是注册的 activity 的上下文

## 常见问题

- 使用 Application 启动 Activity 会报错，因为 Application 没有 TaskRecord 任务栈，所以要么加一个任务栈要么使用 NEW_TASK flag

## 参考资料

- [广播onReceive()方法的context类型探究](https://blog.csdn.net/lyl0530/article/details/81105365)