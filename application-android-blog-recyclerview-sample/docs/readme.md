## 文章大纲（随时更新）

1. 怎么用？RecyclerView 提供了哪些 API？能实现什么效果？
    1. 适用于什么场景？
    2. 怎么用？如何创建一个 recyclerview ？分一二三四步
    3. 各种layoutmanager ，官方目前提供的类，能够实现哪些效果？
        1. 如何实现抖音视频列表？
    4. ItemAnimator，默认的动画效果有哪些？
        1. DefaultItemAnimator
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
2. 怎么实现的？各个关键类、API 的源码解析
    1. 类介绍，继承关系，Adapter、ViewHolder、ItemTouchHelper
    2. Adapter，观察者模式，notifyChanged
    3. ViewHolder，视图持有者，一个屏幕中会创建多少个 ViewHolder？
    4. 缓存原理，嵌套是否会导致缓存无效？
3. 怎么扩展 / 自定义？RecyclerView 有哪些支持自定义的 API？能实现什么效果？
    1. 自定义 LayoutManager
    2. 自定义 ItemDecoration
    3. 自定义 SnapHelper
4. 常见问题
    1. 滑动冲突问题
    2. 焦点问题
    3. 显示不全的问题
    4. 图片显示错乱的问题
    5. notifyAll，图片闪烁问题
    6. 如何做好 RV 的性能优化？

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

- [Recycler View setHasStableIds may result unstable output - Mohit Chauhan](https://medium.com/@chauhan7042mohit/recycler-view-sethasstableids-may-result-unstable-output-2ded1153c7dc)

源码解析：

- [深入理解 RecyclerView 系列之一：ItemDecoration - Piasy](https://blog.piasy.com/2016/03/26/Insight-Android-RecyclerView-ItemDecoration/index.html)
- [深入理解 RecyclerView 系列之二：ItemAnimator - Piasy](https://blog.piasy.com/2016/04/04/Insight-Android-RecyclerView-ItemAnimator/index.html)
- [RecyclerView.OnItemTouchListener的用法 - Sunmoon](https://www.sunmoonblog.com/2017/02/24/rv-on-item-touch-listener/)
- [RecyclerView 源码分析(五) - Adapter的源码分析](https://juejin.cn/post/6844903937900822542)
- [RecyclerView animations ](https://frogermcs.github.io/recyclerview-animations-androiddevsummit-write-up/)

三方库：

- [YasuoRecyclerViewAdapter](https://github.com/q876625596/YasuoRecyclerViewAdapter)
- [SwipeRecyclerView - 严振杰](https://github.com/yanzhenjie/SwipeRecyclerView)
- [BaseRecyclerViewAdapterHelper](https://github.com/CymChad/BaseRecyclerViewAdapterHelper)

自定义：

- [万字长文解析侧滑菜单的俩种实现原理 - yuanhao](https://juejin.cn/post/7114557569561001992)
- [跨Item文字选择的RecyclerView - ekibook](https://ekibun.github.io/ekibook/2020/05/17/selectablerecyclerview/)
- [总结一下RecyclerView侧滑菜单的两种实现 - giswangsj](https://juejin.cn/post/6997013239926095880)
- [Android RecyclerView实现侧滑删除 - IT徐师兄](https://blog.csdn.net/yujun2023/article/details/130787198)
- [RecyclerView 侧滑菜单，可轻松自定义菜单动画和样式 - aitsuki](https://www.wanandroid.com/blog/show/3151)
- [Android 侧滑菜单的实现思考 - 徐永红](https://xuyonghong.cn/2021/11/25/Android-SwipeMenuLayout-Ponder/)
- [自定义 LayoutManager，让 RecyclerView 效果起飞 - 快手](https://juejin.cn/post/7044797219878223909)
- [Android自定义LayoutManager第十一式之飞龙在天 - 陈小缘](https://blog.csdn.net/u011387817/article/details/81875021)
- [看完这篇文章你还不会自定义LayoutManager，我吃X！](https://juejin.cn/post/6870770285247725581)
- [RecyclerView-onViewRecycled-什么时候被调用？](http://mesonwang.com/2017/11/25/When-Method-onViewRecycled-Call-in-RecyclerView/)
- [RecyclerView 性能优化 | 是什么在破坏缓存机制?](https://juejin.cn/post/6945638073682100260)
- [RecyclerView 性能优化 | 把加载表项耗时减半 (二)](https://juejin.cn/post/6942276625090215943)
- [每日一问 | RecyclerView的多级缓存机制，每级缓存到底起到什么样的作用？](https://www.wanandroid.com/wenda/show/14222)
- [每日一问 | 关于 RecyclerView$Adapter setHasStableIds(boolean)的一切](https://wanandroid.com/wenda/show/15514)
- [每日一问 RecyclerView卡片中持有的资源，到底该什么时候释放？](https://wanandroid.com/wenda/show/12148)
- [Android | Tangram动态页面之路（一）需求背景](https://juejin.cn/post/6844904152959565837)
- [Android | Tangram动态页面之路（二）介绍](https://juejin.cn/post/6844904154842808334)
- [Android | Tangram动态页面之路（三）使用](https://juejin.cn/post/6844904158126931975)
- [Android | Tangram动态页面之路（四）vlayout原理](https://juejin.cn/post/6844904159255232526)
- [Android | Tangram动态页面之路（五）Tangram原理](https://juejin.cn/post/6844904160333135879)
- [Android | Tangram动态页面之路（六）数据分离](https://juejin.cn/post/6844904161398505479)
- [Android | Tangram动态页面之路（七）硬核的Virtualview](https://juejin.cn/post/6844904165332746253)