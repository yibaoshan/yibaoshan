
## 基本使用

- 创建 RecyclerView ：xml 创建，或者 new 对象。注意依赖 androidx
- 设置布局管理器：管理各个子 View 的布局显示
  - 线性布局（LinearLayoutManager）：横向或者纵向显示
  - 网格布局（GridLayoutManager ）：一排 2 个，3 个等
  - 交错布局（StaggeredGridLayoutManager）：在上一个基础上，允许子 View 高度不一
- 设置 Adapter 适配器：管理 ViewHolder、数据和视图，设置点击回调等也都在这
  - 自定义 ViewHolder 类，对应 xml 文件
  - onCreateViewHolder() ，创建 ViewHolder ，可根据自定义 ItemType 实现多布局
  - onBindViewHolder()，绑定数据
- 设置数据，notifyDataSetChanged()，刷新视图

## 下拉刷新，上拉加载

GitHub 开源控件：SmartRefreshLayout

Google 控件：SwipeRefreshLayout

- 自定义 LinearLayout，支持 header，content，footer，三个布局
- 根据实现效果，使用 margin，或者 padding ，让 header、footer 超出屏幕之外
- 重写 onInterceptTouchEvent()，接管事件，判断 RV 到底或到顶，改变 RV
  位置让它露出来
- 关闭下拉刷新，上拉加载窗口可使用属性动画

## 滑动冲突

## 自定义 LayoutManager

## 万能 Adapter
