
在 Java 中负责加载指定类的对象是 ClassLoader，Android 中也是类似

BaseDexClassLoader 继承自 ClassLoader 类，实现了许多 DEX 相关的加载操作，其子类包括:

- PathClassLoader: 负责从本地文件中初始化类加载器，开发者代码默认加载器
- DexClassLoader: 负责从 .jar 或者 .apk 中加载类，可以加载未安装的，不在安装目录的文件
- InMemoryDexClassLoader: 从内存中初始化类加载器

注意，因为 art 在启动过程中，需要使用到许多系统的 java 类，但这些类并不是以 dex 文件形式存在的

因此，他们的类加载器多是 java.lang.BootClassLoader

## 双亲委托及破坏

经过测试后，系统类，比如 Activity 、Toast、Fragment等都是 BootClassLoader 负责加载

其他打包进 apk 的，不管是开发者自己写的，还是 jetpack 组件等，统统都是 PathClassLoader 负责加载

BootClassLoader 是继承自 java.lang.ClassLoader

而 PathClassLoader 是继承自 BaseDexClassLoader

虽然它俩没有继承关系，但是 BootClassLoader 的确是 PathClassLoader 的 parent 加载器

PathClassLoader 在初始化时，

## ClassLoader

以常见的 PathClassLoader 为例，其构造函数会调用父类的构造函数，整体调用链路简化如下表:

```
new PathClassLoader	…
new BaseDexClassLoader	libcore/dalvik/src/main/java/dalvik/system/BaseDexClassLoader.java
new DexPathList	libcore/dalvik/src/main/java/dalvik/system/DexPathList.java
DexPathList.makeDexElements	…
DexPathList.loadDexFile	…
new DexFile	libcore/dalvik/src/main/java/dalvik/system/DexFile.java
DexFile.openDexFile	…
DexFile.openDexFileNative	…
DexFile_openDexFileNative	art/runtime/native/dalvik_system_DexFile.cc
OatFileManager::OpenDexFilesFromOat	art/runtime/oat_file_manager.cc
```

在 OpenDexFilesFromOat 中执行了真正的代码加载工作，伪代码如下:

```
std::vector OatFileManager::OpenDexFilesFromOat() {
    std::vector<> dex_files = OpenDexFilesFromOat_Impl(...);
    for (std::dex_file : dex_files) {
      if (!dex_file->DisableWrite()) {
        error_msgs->push_back("Failed to make dex file " + dex_file->GetLocation() + " read-only");
      }
    }
    return dex_files;
}
```

加载过程首先将 vdex 映射到内存中，然后将已经映射到内存中的 dex 或者在磁盘中的 dex 转换为 DexFile 结构体，最后再将 vdex 和 oat 文件关联起来。

## 代码加载

在 Java 这门语言中，方法是需要依赖类而存在的，因此要分析方法的初始化需要先分析类的初始化。

虽然我们前面知道如何从 OAT/VDEX/DEX 文件中构造对应的 ClassLoader 来进行类查找

调用到名为 classForName 的 native 方法，其定义在 art/runtime/native/java_lang_Class.cc:

```
// "name" is in "binary name" format, e.g. "dalvik.system.Debug$1".
static jclass Class_classForName( env, jclass, javaName, initialize, javaLoader) {

    Handle<mirror::ClassLoader> class_loader(
      hs.NewHandle(soa.Decode<mirror::ClassLoader>(javaLoader)));
    ClassLinker* class_linker = Runtime::Current()->GetClassLinker();
    Handle<mirror::Class> c(
      hs.NewHandle(class_linker->FindClass(soa.Self(), descriptor.c_str(), class_loader)));

    if (initialize) {
        class_linker->EnsureInitialized(soa.Self(), c, true, true);
    }
    return soa.AddLocalReference<jclass>(c.Get());
}
```

### FindClass

FindClass 实现了根据类名查找类的过程，定义在 art/runtime/class_linker.cc 中

### DefineClass

### LoadClass

### LinkCode

### 类初始化

## 应用场景

正所谓无利不起早，之所以花费这么多时间精力去学习 ART，是因为其在 Android 运行过程中起着举足轻重的作用，下面就列举一些常见的应用场景。

### 热修复 & Hook

根据前文对方法调用和代码加载的分析，Android 中的 Java 方法在 ART 中执行都会通过 ArtMethod::Invoke 进行调用

在其内部要么通过解释器直接解释执行(配合 JIT)；

要么通过 GetEntryPointFromQuickCompiledCode 获取本地代码进行执行

当然后者在某些场景下依然会回退到解释器，但入口都是固定的

即 entry_point_from_quick_compiled_code 所指向的 quick 代码。

因此，要想实现 Java 方法调用的劫持，可以有几种思路:

- 修改 ArtMethod::Invoke 这个 C++ 函数为我们自己的实现，在其中增加劫持逻辑；
- 修改目标 Java 方法属性，令所有调用都走 quick 分支，然后将 entry_point_from_quick_compiled_code 修改为指向我们自己的实现，从而实现劫持；
- 类似于上述方法，不过不修改指针的值，而是修改 stub code；
- ……

当然，前途是光明的，道路是曲折的，这些方法看起来都很直观，但实现起来有很多工程化的难点。比如需要仔细处理调用前后的堆栈令其保持平衡，这涉及到 inline-hook 框架本身的鲁棒性；有比如在新版本中对于系统类方法的调用，ART 会直接优化成汇编跳转而绕过 ArtMethod 方法的查找过程，因此方法 1、2 无法覆盖到这些场景，……不一而足。

### 安全加固 & 脱壳

### 方法跟踪



