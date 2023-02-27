
RecyclerView 本质上也是一个 ViewGroup，那么它的 Item 要显示出来，自然需要 addView() 进来，移出的时候，当然也要 removeView() 出去

## 参考资料

- [自定义 LayoutManager，让 RecyclerView 效果起飞 - 快手](https://juejin.cn/post/7044797219878223909)
- [Android自定义LayoutManager第十一式之飞龙在天 - 陈小缘](https://blog.csdn.net/u011387817/article/details/81875021)
- [看完这篇文章你还不会自定义LayoutManager，我吃X！](https://juejin.cn/post/6870770285247725581)
- [BaseRecyclerViewAdapterHelper](https://github.com/CymChad/BaseRecyclerViewAdapterHelper)
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