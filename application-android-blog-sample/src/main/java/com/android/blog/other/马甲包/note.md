
要比较两个 APK 文件的相似度，通常需要进行以下步骤：

提取 APK 文件内容：APK 文件是 Android 应用程序的压缩文件，你需要先解压缩它并提取其内容。你可以使用一些工具或库来实现这一步骤，如 apktool（命令行工具）或 dexlib2（Java 库）。

提取源代码：一旦你提取了 APK 文件的内容，你需要提取其中的源代码。在 APK 文件中，源代码通常位于 classes.dex 文件或其他 .dex 文件中。你可以使用 dexlib2 等工具来解析这些文件，并提取出包含的 Java 代码。

进行代码相似度比较：一旦你提取了两个 APK 文件的源代码，你可以使用之前提到的代码相似度比较方法，如 Levenshtein 距离、Simian 等，来比较两个源代码的相似度。你可以逐个比较类、方法或代码片段的相似度，然后计算总体的相似度指标。

## Androguard

pygments>=2.3.1
lxml>=4.3.0
colorama>=0.4.1
asn1crypto>=0.24.0
click>=7.0
pydot>=1.4.1
ipython>=5.0.0
mutf8
dataset
frida
loguru

里面使用了一个日志库 loguru 要求 Python 3.5+

Mac 默认版本 2.7 ，已安装的可以用 pip3 / python3

## 马甲包判定标准

相似度分析：

1. 代码相似度 85% 以上
2. 文本型资源文件相似度 60% 以上
3. 图像资源文件相似度 75% 以上
4. 目录结构相似度 70% 以上

## 参考资料

- [Androguard 官网](https://androguard.readthedocs.io/en/latest/index.html)
- [Android Androguard 静态分析 _](https://s1lenc3-chenmo.github.io/2020/03/10/Android%E9%9D%99%E6%80%81%E5%88%86%E6%9E%90/)
- [Android-Androguard使用小记](https://findme.cool/Android/androguard-compliance.html)
- [使用androguard对APK进行静态分析](https://blog.csdn.net/qq_40644809/article/details/106814146)
- [[原创]通过相似度分析的恶意应用检测方法](https://bbs.kanxue.com/thread-214931.htm)
- [Google Play开发者账号被封，损失惨重！又是账号关联！](https://enjoyglobal.net/detail/news/LNews10023)
- [AndroidJunkCode](https://github.com/qq549631030/AndroidJunkCode)
- [基于代码流的相似度分析](http://noverguo.github.io/2015/02/27/android-asm-03-1/)
- [通过全文相似度来寻找相同或相似的代码](https://www.jianshu.com/p/4eab43f59581)
- [开发一个基于Dalvik字节码的相似性检测引擎，比较同一款应用程序的不同版本之间的代码差异（一）](https://zhuanlan.zhihu.com/p/69614241)
- [JPlag的使用](https://canjuly.github.io/2019/04/15/JPlag/)