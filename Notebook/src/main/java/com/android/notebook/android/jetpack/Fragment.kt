package com.android.notebook.android.jetpack

class Fragment {

    /**
     * Android Jetpack架构组件(五)之Navigation
     * 一、 Navigation简介
     * 1.1 Navigation诞生背景
     * 介绍组件诞生背景以及能够做哪些事
     * 1.2 Navigation元素
     * 介绍相关类
     *
     * 二、Navigation使用
     * 2.1 添加依赖
     * gradle依赖讲解
     * 2.2 创建导航图
     * 2.3 Navigation graph
     * 2.4 NavHostFragment
     * 2.5 NavController
     * 2.6 添加动画
     *
     * 三、参数传递
     * 这里介绍了传参的几种方式以及注意事项
     * 3.1 使用Bundle传递数据
     * 示例代码
     * 3.2 使用 Safe Args传递数据
     * 示例代码
     *
     * 四、深层链接DeepLink
     * 4.1 PendingIntent
     * 4.2 URL
     * */

    /**
     * fragment是什么？
     * 诞生的背景
     * 解决了什么问题
     * 如何做到的
     * 注意事项
     *
     * blog内容点：
     * 1. 历史背景，诞生的意义，解决什么样的问题
     *  早期的ActivityGroup
     *  要不要去下载微信早期版本，尝试反编译看看里面实现方式
     *
     * 2. 生命周期
     *  介绍里面需要和activity比较
     *  原理里面需要知道是谁调用的生命周期，以及activity对象被谁保存着，用什么数据结构
     *  构造函数和生命周期之间的关系，无参构造函数为什么一定需要，FragmentFactory是如何解决这个问题
     *  不同的使用方法带来的生命周期不同，比如是否可见
     *  是否可见onHiddenChanged、setUserVisibleHint、setMaxLifecycle
     *  Fragment的过渡动画
     *  Activity被低内存或者配置发生变化比如转屏(Configuration Change)销毁重建，如何保存状态
     *  权限请求requestPermissions
     *  掘金上看到的：
     *  问：replace方式导致的重建吧
     *  答：replace 只会导致 View 重建，即仅走到 onDestroyView，未走到 onDestroy。
     *
     * 3. 通信方式
     *  ActivityResultAPI
     *  FragmentResultAPI
     *  在fragment中拦截返回键OnBackPressed（能不能拦截view事件）
     *
     * 4. 使用及组合使用
     *  Navigation
     *  viewPager
     *  组合使用过程中遇到预/懒加载的问题，FragmentStatePagerAdapter
     *
     * 5. 其他作用
     *  5.1 监听生命周期
     *  Jetpack Lifecycle和Glide
     *  5.2 方法代理
     *  比如权限请求
     *
     * 6. 开发注意事项
     * Jetpack MVVM七宗罪 之一 拿Fragment当LifecycleOwner
     * 需要研究一下，貌似是注册livedata时内存泄漏的问题，下次注册时取消上一次的即可，可写可不写
     *
     * 最后，聊聊微信聊天页面以及返回键的逻辑
     * */

    /**
     * 浅析Fragment为什么需要空的构造方法：https://blog.csdn.net/ruancoder/article/details/52001801
     * Android ActivityGroup简介：https://blog.csdn.net/heqiangflytosky/article/details/46224915
     * 浅析Fragment中startActivityForResult()与getActivity().startActivityForResult()的异同：https://blog.csdn.net/ruancoder/article/details/53490500
     * */

    /**
     * Fragment的包依赖方式
     * 添加依赖后的包大小
     * Android x的fragment和Android.app包的fragment异同
     * gradle.properties中使用android.useAndroidX=true
     * androidx.fragment.app.fragment v1.1.0
     *
     * implementation 'androidx.appcompat:appcompat:1.3.1'
     * androidx.fragment.app.fragment v1.3.6
     *
     * fragment构造函数
     * 带参方式
     * fragment与fragment通信，fragment和activity通信
     *
     * */

    /**
     * 官网介绍：https://developer.android.com/guide/fragments?hl=zh-cn
     * Fragment 表示应用界面中可重复使用的一部分。
     * Fragment 定义和管理自己的布局，具有自己的生命周期，并且可以处理自己的输入事件。
     * Fragment 不能独立存在，而是必须由 Activity 或另一个 Fragment 托管。
     * Fragment 的视图层次结构会成为宿主的视图层次结构的一部分，或附加到宿主的视图层次结构。
     * 注意：某些 Android Jetpack 库（如 Navigation、BottomNavigationView 和 ViewPager2）经过精心设计，可与 Fragment 配合使用。
     *
     * 模块化
     * 假设我们的APP首页在不同大小屏幕上需要有不同的表现，如图
     * 在大屏上展示时，我们希望导航栏和内容区左右显示
     * 在小屏上展示时，我们则希望导航栏和内容上下显示
     * 这样的需求用Activity实现可能会有些麻烦
     * @see com.android.notebook.android.jetpack.fragment.res.blog_android_jetpack_fragment_screen_sizes.png
     *
     * 可复用性
     * 这个我想都有类似的项目需求，简单的例子比如购物车页面
     * 您可以在同一 Activity 或多个 Activity 中使用同一 Fragment 类的多个实例，甚至可以将其用作另一个 Fragment 的子级。
     * 考虑到这一点，您应只为 Fragment 提供管理它自己的界面所需的逻辑。
     *
     * 关于如何使用Fragment的主题
     * 1. Fragment 管理器
     * FragmentManager 类负责对应用的 Fragment 执行一些操作，如添加、移除或替换它们，以及将它们添加到返回堆栈。
     * 如何获取fragmentManager？FragmentActivity中的和Activity的和Fragment嵌套中的FM有什么区别？
     * 2. Fragment 事务
     * 3. 在 Fragment 之间添加动画过渡效果
     * 4. Fragment 生命周期
     * 5. 保存与 Fragment 相关的状态
     * 6. 在 Fragment 与 Activity 之间通信
     * 7. 使用应用栏
     * 8. 使用 DialogFragment 显示对话框
     * 9. 测试 Fragment
     *
     * */

    /**
     * 为什么要使用/什么情况下使用
     * 如何使用
     * 使用的注意事项
     * 实现的原理
     * */

    private class 使用方法 {

        /**
         * 1. <fragment>标签：静态方式
         * 2. ViewGroup+FragmentManager
         * 聊聊可见性的问题
         * 3. ViewPager/ViewPager2+Fragment
         * 懒加载的问题
         * */

        /**
         * 1. xml中使用fragment标签
         * name=全量类目
         * id=唯一标识
         * 注意要声明id或者tag值，不然会收到如下错误错误
         * java.lang.IllegalArgumentException: Binary XML file line #: Must specify unique android:id, android:tag
         *
         * */

    }
}