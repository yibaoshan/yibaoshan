
Hook 又叫 “钩子”，它可以在事件传送的过程中截获并监控事件的传输

这样当这些方法被调用时，也就可以执行我们自己的代码

这也是面向切面编程的思想（AOP）

## 前置知识

利用 Java 反射、静/动态代理

## 几个关键点

- Hook 的选择点：尽量静态变量和单例，因为一旦创建对象，它们不容易变化，非常容易定位。
- Hook 过程：
  - 寻找 Hook 点，原则是尽量静态变量或者单例对象，尽量 Hook public 的对象和方法。
  - 选择合适的代理方式，如果是接口可以用动态代理。
  - 偷梁换柱——用代理对象替换原始对象
- Android 的 API 版本比较多，方法和类可能不一样，所以要做好 API 的兼容工作。

## 参考资料

-[Android Hook Activity 的几种姿势](https://blog.csdn.net/gdutxiaoxu/article/details/81459910)
