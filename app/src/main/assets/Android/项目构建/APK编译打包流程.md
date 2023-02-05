
聊聊 Android APK 编译打包流程

APK（Android Application Package） 是 Android 系统应用程序的包文件格式，

它就像，可以在任一 Android 设备中运行

它的本质是压缩包，我们可以把文件后缀名改成 .rar ，然后解压缩，相信大家都

构建 APK 文件可以分为，生成 DEX 文件、资源文件

其中生成 Dex 文件又可以分为， class 编译，脱糖优化，代码混淆等

今天，我们来简单了解

打包工具都在 sdk 目录下的 build-tools 中

dex 文件的生成

- 使用 aapt 打包资源文件， 生成 R.java 类

## 参考资料

- [Android Apk 编译打包流程，了解一下~ - 程序员江同学](https://juejin.cn/post/7113713363900694565)
- [APK构建原理由浅入深](https://juejin.cn/post/6882328361294069773)
- [每日一问 | 我们经常说到的 Android 脱糖指的是什么？](https://www.wanandroid.com/wenda/show/18615)
