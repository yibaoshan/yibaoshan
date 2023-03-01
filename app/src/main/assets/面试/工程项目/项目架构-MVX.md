
## MVP

- **View**：XML布局文件
- **Model**：实体模型（数据的获取、存储、数据状态变化）
- **Controller**：对应于 Activity，处理数据、业务和 UI

从个结构来看，Android 本身的设计还是符合 MVC 架构的

但是，Android 中纯粹作为 View 的 XML 视图功能太弱，我们大量处理 View 的逻辑只能写在 Activity 中

这样 Activity 就充当了 View 和 Controller 两个角色，直接导致 Activity 中的代码大爆炸

所以，更贴切的说法是，这个 MVC 结构最终其实只是一个 Model-View（Activity:View&Controller）的结构。

## MVC

- **View**：对应于 Activity 和 XML，负责 View 的绘制以及与用户的交互，Activity 此时只负责控制 View ，原先的逻辑抽到了 Present
- **Model**：依然是实体模型
- **Presenter**：负责完成 View 与 Model 间的交互和业务逻辑，持有 View（Activity）的公共接口
- **ViewInterface**：新角色，定义了 View 的动作，由 Activity 实现，交给 Presenter 控制

业务是抽离出来了，但 MVP 本身也引入了几个问题：

- **事件驱动**：View 和 Presenter 通过接口交换，本质是事件驱动 UI 的模型，粒度不好控制
  - 粒度太小，接口太多，代码过于碎片化
  - 粒度太大，耦合度太高，更新一个控件会影响其他原本不用更改的 View
  - 如果业务如果改动，为了方便（懒得写新接口），最后下场往往是把 ViewInterface 强转为 Activity ，进而拿到将控件的引用，在 Presenter 层直接修改，跳过接口
- **内存泄漏**：由于是单向控制（P 控制 V），生命周期无法同步，Presenter 持有的 ViewInterface（也就是 Activity）可能已经被销毁却无法被回收

## MVVM

- **View**： 对应于 Activity 和 XML，负责 View 的绘制以及与用户交互
- **Model**：实体模型，被 LiveData 包装，拥有数据状态变化主动通知的功能
- **ViewModel**：在 MVP 的基础上，将原来的 Presenter 层换成了 ViewModel（有生命周期感知能力），角色定位是相同的，负责接口请求（业务数据），业务逻辑

MVVM 优点：

- **数据驱动**：数据独立于 UI ，简单来说，不再受接口制约，一个接口可能对应多个 LiveData
- **生命周期同步**：lifecycle 组件让 ViewModel 有生命周期感知能力，避免内存泄漏
- **线程安全**：网络请求回来后，无需手动切换到 ui 线程更新
- **低耦合**：ViewModel 不持有 View 引用，也没有中间层。所以完全可以一个人开发 View，一个人写代码逻辑

##