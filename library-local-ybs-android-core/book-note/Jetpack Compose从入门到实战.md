
### 第二章 逻辑常用 UI 组件

- 布局方面，Compose 提供了非常丰富的布局组件，如：Row、Column、Box、LazyColumn、LazyRow 等。类似 LinearLayout、RelativeLayout 等。
- Modifier 描述组件样式，类似 CSS，替代 View 的 XML 布局文件。
  - size，描述组件大小，同时修饰宽高。
    - fillMaxSize 整个填满 match_parent；fillMaxHeight、fillMaxWidth 负责宽高填满。
    - matchParentSize 宽度、高度都填满。
  - background，设置背景色，颜色支持：Color、Brush（渐变色）、ImageBitmap、Painter。
  - fillMaxSize，填充剩余空间。
  - border & padding，边框、内边距。xml 如果要写边框，需要单独写样式；compose 只有 padding 没有 margin 。
  - offset，偏移量。** modifier 是链式调用，书写顺序决定 UI **，所以偏移最好写在前面，比如要写偏移再画背景。
  - weight，比重，百分比。
- 资源访问，stringResource、colorResource、painterResource（drawable 资源），类似 R.string.xxx、R.color.xxx、R.dimen.xxx。

作用域限定