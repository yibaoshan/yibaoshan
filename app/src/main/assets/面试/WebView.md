
## WebSettings

基础设置，密码保存、访问文件、js权限、编码格式、指定缓存目录啊等等

## WebChromeClient

- onConsoleMessage()：转发网页控制台日志，方便前端定位问题
- onJsAlert() / onJsConfirm()：js 的警告，确认弹窗，交由原生实现

## WebViewClient

这里主要是 H5 离线包方案实现类

APP 每次发版时，前端团队把 h5 页面用到的 js、css、jpg、png、html等资源打包给我们

为了包体积考虑，通常只要求即将发生的活动页的资源，其他的历史 H5 页资源不打包进 apk ，应用启动后全量下载到本地

在 main 的 onResume 检查版本的接口中携带本地下载的离线包版本，若有新版本或者是打包版本（资源不全），启动子线程下载，解压到本地，更新内存缓存


WebView 的 shouldInterceptRequest() 方法中拦截每个请求，和内存缓存做匹配，匹配成功返回 WebResourceResponse 使用本地资源

至于在 shouldInterceptRequest() 方法利用 Glide 加载图片，和 APP 共用图片缓存，这个我们没做

一来是我们的 H5 大多是活动页，业务和 APP 几乎没什么交集，二来主要的资源已经下载到本地了，不需要共用图片缓存

## WebView 缓存池

在上线 H5 离线包的第一个版本，为了更快的展现 H5 内容，我们选择了在 Application 提前初始化 WebView

后来发现 WebView 初始化耗时在 100ms 上下（Pixel 3 数据，机型越老耗时越长），增加了启动耗时，这点不可以忍受

WebView 的确有提前初始化的必要，但又不适合放在 Application。于是，我们在网上参考了 WebView 池化方案

- 建立了 WebView 单例类，利用 MutableContextWrapper 解决不同 Activity 的 context 问题

- 同时，把初始化工作移到了 main

## WebView 白屏检测

网上的方案，主要是为了避免 H5 页面发生异常白屏，这块我们没有做，大致的原理是：

- 在 BaseWebView 增加定时任务，定期截图转成 bitmap
- 计算白色像素占比，超过阈值则弹窗询问用户是否手动刷新？

## js 桥接

js call APP 使用方法

- WebView 中，调用 addJavascriptInterface() 添加桥接方法管理类，我们是单独抽出来 JsFun 来管理
- 管理类方法标注 @JavascriptInterface 注解即可

APP call js 方法

- WebView#evaluateJavascript("javascript:showToast('hello world')")

## 独立进程

从内存管理和安全隔离的角度，项目一般都会把 WebView 拉出来单开进程，但是，我们还是没做。

主要是当时没有选定跨进程框架，一些资源缓存呐，js方法，用户信息同步啦等等有点麻烦，就一直拖着没做。。此节终