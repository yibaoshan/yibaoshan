## 环境配置

反编译 apk 主要依赖 apktool ，它的安装方式有两种：jar 包 和 brew 安装

为了可以随时随地反编译文件，我个人比较喜欢使用 brew 安装，自动配置环境变量和权限配置等

所以，我们先来配置 brew 环境：

- 安装 brew 环境，点击[这里](https://tech-latest.com/fix-command-not-found-brew-mac/)查看教程，注意 Intel 和 M1 系列区别

接着再安装 apktool ：

- 命令行执行 brew install apktool 一键安装，自动配置环境变量和权限配置等，过程中可能提示 unlink xxx，我们根据提示执行指令即可
- 等待安装完成，输入 apktool 检查是否安装成功，如未成功，自己百度

## apktool 使用

环境配好了以后，我们就可以反编译 apk 了

首先，准备一个 apk 文件，放到一个新的文件夹中，然后 cd 到该文件夹

1. 执行 apktool d xxx.apk 命令

其中 d 是 decode 的意思，表示我们要对 xxx.apk 进行解码，我们还可以再加上一些附加参数来控制 decode 的更多行为：

> -f ：如果目标文件夹已存在，则强制删除现有文件夹（默认如果目标文件夹已存在，则解码失败）
> -o ：指定解码目标文件夹的名称（默认使用 APK 文件的名字来命名目标文件夹）
> -s ：不反编译dex文件，也就是说 classes.dex 文件会被保留（默认会将 dex 文件解码成 smali 文件）
> -r ：不反编译资源文件，也就是说 resources.arsc 文件会被保留（默认会将 resources.arsc 解码成具体的资源文件）

命令执行结束后，我们会得到 apk 同名的目录，里面的内容大致有这些：

> - AndroidManifest.xml：经过反编译还原后的 manifest 文件
> - original 文件夹：存放了未经反编译过、原始的 AndroidManifest.xml 文件
> - res 文件夹：存放了反编译出来的所有资源
> - smali 文件夹：存放了反编译出来的所有代码，只不过格式都是.smali类型的。看得懂的话可以修改，重新打包发布

到这里，apk 文件已经反编译完成，只不过代码都是 smali 格式的，我们需要借助一些工具，翻译成我们能看得懂的代码

## dex2jar + jd-gui

接下来我们需要去下载 dex2jar 包，和 jd-gui 工具

从名称也可以看出来，dex2jar 是将 dex 文件转换成 jar 文件的，而 jd-gui 则是用来查看转化后的 jar 文件的

### dex2jar 下载和使用

链接：https://sourceforge.net/projects/dex2jar/files/

点击上方链接把 zip 包下载到本地，然后我一般会把包解压到新的文件夹，比如 Desktop / tool

解压完成后，我们会得到一大堆 .bat 、.sh 结尾的脚步文件，在 Mac 下，我们主要使用 d2j-dex2jar.sh 这个文件

执行以下命令，得到 dex 同名的 .jar 文件

```
sudo sh d2j-dex2jar.sh classes.dex
```

此时 terminal 会报错：Permission denied

这是因为文件权限不足导致的，执行如下命令提权即可：

```
chmod 777 d2j_invoke.sh
```

接下来，我们就可以利用 jd-gui 查看代码啦

### jd-gui 使用

下载地址：http://java-decompiler.github.io/

jd-gui 的使用非常的简单，下载解压以后，打开 JD-GUI ，导入上一步生成的 .jar 文件就可以阅读代码啦

注意：Mac 的 Big Sur 及以上版本会有兼容性，打开会提示错误：

> No suitable Java version found on your system! This program requires Java 1.8+ Make sure you
> install the required Java version.

解决方案，选中 JD-GUI ，右键 "显示包内容" ，依次打开 / Contents / MacOS，找到 universalJavaApplicationStub.sh ，进入文本编辑

将原先的内容全部替换为 https://raw.githubusercontent.com/tofi86/universalJavaApplicationStub/v3.0.6/src/universalJavaApplicationStub
这个文件

## 重新打包

```
apktool b xxx -o xxx.apk
```

```
jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore 签名文件名 -storepass 签名密码 待签名的APK文件名 签名的别名
```

## jadx 使用

jadx 相当于是 apktool + dex2jar + jd-gui 的结合体，既能反编译代码也能反编译资源，关键使用起来还特别简单，你只需要将文件拖进来即可，一定程度上提高了我们的开发效率

1、能将 APK，AAR，JAR，DEX，AAB，ZIP 等文件中的代码反编译为 Java 类

2、能反编译 APK，AAR，AAB，ZIP 中的资源

安装：

```
brew install jadx
```

使用，执行以下命令打开可视化窗口

```
jadx-gui
```

## 资源混淆

apk 打包方式，可以使用腾讯张绍文的 AndResGuard 来完成资源文件的混淆： https://github.com/shwenzhang/AndResGuard

aab 打包方式，可以使用字节跳动的 AabResGuard： https://github.com/bytedance/AabResGuard

## 参考资料

- [Android 逆向系列（一）：反编译 APK 技术完全解析](https://juejin.cn/post/7158107697907236878)
- [Android 逆向系列（二）：Android APK 代码混淆](https://juejin.cn/post/7168086915445424136)
- [](https://apk119.com/)