## 文章大纲（随时更新）

1. RecyclerView 是什么？怎么用？
    1. 适用于什么场景？
    2. 怎么用？如何创建一个 recyclerview ？分一二三四步
    3. 各种layoutmanager ，官方目前提供的类，能够实现哪些效果？
        1. 如何实现抖音视频列表？
    4. ItemAnimator，默认的动画效果有哪些？
    5. ItemDecoration
        1. 分割线怎么设置？
        2. 如何实现首字母导航效果？
        3. 吸顶效果
    6. 各种辅助类的使用
        1. SnapHelper 的使用
        2. ItemTouchHelper
            1. 长按重排序
            2. 侧滑菜单
    7. 为 RV 添加功能
        1. 如何实现下拉刷新，上拉加载更多？
2. 源码解析
    1. 类介绍，继承关系，Adapter、ViewHolder、ItemTouchHelper
    2. Adapter，观察者模式，notifyChanged
    3. ViewHolder，视图持有者，一个屏幕中会创建多少个 ViewHolder？
    4. 缓存原理，嵌套是否会导致缓存无效？
3. recyclerview 支持自定义的部分
    1. 自定义 layoutmanager
    2. 自定义 ItemDecoration
    3. 自定义 SnapHelper
4. 常见问题
    1. 滑动冲突问题
    2. 焦点问题
    3. 显示不全的问题
    4. 如何做好 RV 的性能优化？

## 参考资料

官方文档：

- [Maven AndroidX RecyclerView](https://mvnrepository.com/artifact/androidx.recyclerview/recyclerview)
- [Maven Support V7 RecyclerView](https://mvnrepository.com/artifact/com.android.support/recyclerview-v7)
- [2014 年 10 月，首次在 support v7 21.0 发布 RecyclerView 库](https://developer.android.com/topic/libraries/support-library/rev-archive?hl=zh-cn#october-2014)
- [2018 年 9 月 21 日，最后一次在 support v7 28.0.0 更新 RecyclerView 库](https://developer.android.com/topic/libraries/support-library/revisions?hl=zh-cn#september-21,-2018)
- [RecyclerView 的第一次提交日志](https://android.googlesource.com/platform/frameworks/support/+/009b4ef9d97e1cc237477e3284fc305bb1438cc9)
- [RecyclerView 的第一个版本](https://android.googlesource.com/platform/frameworks/support/+/refs/tags/android-5.0.0_r1/v7/recyclerview/)
- [RecyclerView Support v7 接近最后一个版本](https://android.googlesource.com/platform/frameworks/support/+/refs/tags/android-9.0.0_r61/v7/recyclerview/)

基本使用：

- [Using the RecyclerView](https://guides.codepath.com/android/using-the-recyclerview)

进阶使用：

源码解析：

- [深入理解 RecyclerView 系列之一：ItemDecoration](https://blog.piasy.com/2016/03/26/Insight-Android-RecyclerView-ItemDecoration/index.html)
- [深入理解 RecyclerView 系列之二：ItemAnimator](https://blog.piasy.com/2016/04/04/Insight-Android-RecyclerView-ItemAnimator/index.html)

三方库：

- [YasuoRecyclerViewAdapter](https://github.com/q876625596/YasuoRecyclerViewAdapter)
- [SwipeRecyclerView - 严振杰](https://github.com/yanzhenjie/SwipeRecyclerView)

自定义：

- [万字长文解析侧滑菜单的俩种实现原理 - yuanhao](https://juejin.cn/post/7114557569561001992)
- [跨Item文字选择的RecyclerView - ekibook](https://ekibun.github.io/ekibook/2020/05/17/selectablerecyclerview/)
- [总结一下RecyclerView侧滑菜单的两种实现 - giswangsj](https://juejin.cn/post/6997013239926095880)
- [Android RecyclerView实现侧滑删除 - IT徐师兄](https://blog.csdn.net/yujun2023/article/details/130787198)
- [RecyclerView 侧滑菜单，可轻松自定义菜单动画和样式 - aitsuki](https://www.wanandroid.com/blog/show/3151)
- [Android 侧滑菜单的实现思考 - 徐永红](https://xuyonghong.cn/2021/11/25/Android-SwipeMenuLayout-Ponder/)