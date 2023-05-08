
假设在 Xml 中， typeface，familyName 和 textStyle 我都设置了，那么根据上面分析：

1. textStyle 肯定会生效
2. 当设置了 typeface 属性，typefaceIndex 会被赋值，同时 familyName 会置为空
3. 当设置了 familyName 属性，分情况：1、如果设置的是系统字体，typeface 会被赋值，familyName 还是为空。2、如果设置的是第三方字体，typeface 为空，familyName 被赋值

## textStyle

- normal：默认字体
- bold：粗体
- italic：斜体

## typeface

- normal：普通字体，系统默认使用的字体
- sans：非衬线字体
- serif：衬线字体
- monospace：等宽字体

## fontFamily

- serif
- serif-monospace
- sans-serif
- sans-serif-black
- sans-serif-thin
- sans-serif-light
- sans-serif-medium
- sans-serif-smallcaps
- sans-serif-condensed
- sans-serif-condensed-light
- sans-serif-condensed-medium
- monospace
- casual
- cursive

## 参考资料

- [TextView字体样式](https://juejin.cn/post/7089342797643251720)