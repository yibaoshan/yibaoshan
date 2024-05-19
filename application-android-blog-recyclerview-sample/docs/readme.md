
文章大纲，随时更新

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