简单聊聊 Android 类加载器

Activity 和 AppCompatActivity 用的是同一个类加载器吗？

最近在复习虚拟机相关内容，想起来之前有次面试被问到关于 "类加载" 的一道题，当时的上下文大致是这样的：

> 面试官：简单描述 "类加载" 的几个步骤
>
> 我：类加载大致可以分为："加载"、"链接" 和 "初始化" 这几步，类的加载使用了双亲委托机制~巴拉巴拉~
>

> 面试官：Android 虚拟机和 Java 虚拟机的类加载流程是一样的吗？
>
> 我：不完全一样，不过也大差不差。Android 区别于 Java 的一点是，它是从 Dex 文件读的 Class ~ 巴拉巴拉~

> ...

> 面试官：那我再问你，Activity 和 AppCompatActivity 用的是同一个类加载器吗？
>
> 我：这俩都是 Activity ，应该是同一个吧~
>

> 面试官：好，今天就到这了，回去等通知吧

当然，最后说等通知是开玩笑的。回家复盘的时候，我把面试官的问题写代码测试了一遍：

我是图片

从打印日志来看，Activity 是被 java.lang.BootClassLoader 加载

而 AppCompatActivity 是被 dalvik.system.PathClassLoader 加载

**很显然，我的回答是错误的**

事后来看，当时我对虚拟机的了解只停留在八股阶段，没看过 Java / Android 的 ClassLoader 源码实现，也就没有自己的理解。即使这道题蒙对了，只要面试官后面再深入一点点，我就不知道该怎么回答了，因为我已经到底了

好，闲言少叙，借着这道题，接下来我们简单的聊一聊，Android 系统中的类加载器，以及类加载的流程

以下，enjoy（*全文基于 Android 7.1*）

## 开篇

在 Android 系统中，常用的类加载器其实只有两个：

- BootClassLoader ：继承自 java.lang.ClassLoader，通常负责加载系统的 java 类
- PathClassLoader ：继承自 BaseDexClassLoader，负责加载 dex 文件，我们打包进 apk 的类都由此加载器加载

BootClassLoader 很容易理解，Android 虚拟机启动以后，有很多系统

在应用启动之前，Android 虚拟机也需要加载 Java 类来执行任务

总之，系统中有许多 Java 类并不是存在于 apk 中的

PathClassLoader 负责加载 dex 文件，从它的构造函数也可以看出来，它需要的指定一个 dexPath ，通常是安装目录

还有一个用于加载 dex 文件的类加载器：DexClassLoader，它也继承自 BaseDexClassLoader，插件化用的比较多

## 双亲委托及破坏(loadClass)

每个 ClassLoader 都有一个 parent 成员变量，代表的是父加载器

需要加载一个类的时候，会优先使用父加载器去加载，如果在父类加载器中没有找到，自己再执行加载

这就是类加载的双亲委托机制

如果 parent 为空，那么就用系统类加载器来加载。通过这样的机制可以保证系统类都是由系统类加载器加载的。 下面是 ClassLoader 的 loadClass 方法的具体实现。

"**类加载**" 过程是递归的双亲委托机制，首先拜托父加载器去尝试加载，没有父加载器或者父加载器没找到自己再去加载

```
Class<?> loadClass(String name, boolean resolve) {
    // 首先，检查类是否已加载
    public Class<?> c = findLoadedClass(name);
    if (c == null) {
        if (parent != null) {
            // 父加载器不为空，委托父加载器去加载
            c = parent.loadClass(name, false);  //调用父类ClassLoader加载
        } else {
            // 父加载器为空，表示为 BootstrapClassLoader，在 Android 中这段代码没用，因为顶级 dex 加载器和 boot 都重写了
            c = findBootstrapClassOrNull(name);
        }
        // 父加载器没找到，调用自身的查找方法
        if (c == null) { 
            c = findClass(name);
        }
    }
    return c;
}
```

抽象类 ClassLoader 的逻辑如上，这种先委托父加载的方式就是 "**双亲委托机制**"

破坏 "**双亲委托**" 也很简单，重写 loadClass() 方法，不使用 parent 执行查找

直接调用自身的 findClass() 方法，然后在查找方法里实现自己的逻辑就可以了

### Android 顶级加载器 BootClassLoader

在 Android 系统中，顶级的类加载器是 **BootClassLoader** ，它重写了 loadClass() 方法

废除了原先判断 parent 是不是为空的逻辑，如果类没有被加载过，直接调用自身方法执行查找

```
class BootClassLoader {
    public Class<?> loadClass(String name, boolean resolve) {
        // 内部调用虚拟机的 findLoadedClass() 来判断是不是已经加载过了
        Class<?> clazz = findLoadedClass(className);
        if (clazz == null) {
            // 没有加载过，因为自己就是顶级加载器了，执行自身查找逻辑
            clazz = findClass(className);
        }
        return clazz;
    }
}
```

你看，Android 系统的顶级加载器的逻辑非常简单：判断该类有没有加载过，没有的话执行自身的查找

从两个类 loadClass() 方法我们可以总结出：

- loadClass() 方法一切类加载的起点
- 类加载使用了双亲委托，优先使用父加载器来加载类；没有父加载器或父加载器找不到时，才执行自身查找逻辑
- 双亲委托可以被破坏，方式是重写 loadClass() 方法，不调用父加载器
- BootClassLoader 是 Android 的顶级父加载器，它重写了 loadClass() 方法

下面我们来看类的查找过程

## 类的查找(findClass)

在上面的 loadClass() 方法我们发现，不管是由父加载器去加载，还是自身加载，最后都是调用 findClass() 方法完成查找

那我们就来看看 findClass() 的代码实现，看看它是如何完成类查找工作的

### BootClassLoader

首先是 BootClassLoader，它负责加载没有打包进 dex 的系统 Java 类，是 Android 系统的顶级加载器

```
class BootClassLoader {
    public Class<?> findClass(String name) {
        return Class.classForName(name, false, null);
    }
}
```

代码中调用了 Class 的静态方法 classForName(name)，这是一个 native 方法，具体实现我们先不管

我们只需要知道，对于系统的 Java 类，最终是由 Class 来完成了类的查找工作

在 Class#classForName(name) 这个 native 方法中，不但会执行类查找，找到类文件以后，还会完成类加载的工作

也就是执行完整的类加载过程：加载、链接与初始化

- 加载：把类的信息从 class 文件读取到虚拟机内存
- 链接：主要分为3个小任务
  - 验证：类的格式是否正确，文件是否合法等等
  - 准备：为这个类准备一块存储空间
  - 解析：如果类成员有引用其他类，可能还需要把其他类也加载进来
- 初始化：执行初始化静态成员变量的值、static 语法块等

执行完上述步骤以后，Class#classForName(name) 方法才会返回，如果中间没有抛出异常，那么表示该类的类加载成功完成

### PathClassLoader

再来看 PathClassLoader ，它负责加载 dex 文件，在 ActivityThread 创建 Application 对象时完成了初始化，具体逻辑在 ActivityThread#handleBindApplication() 方法中，感兴趣的朋友可以搜索看看

PathClassLoader 自身并没有重写 findClass() 方法，因为查找类的逻辑主要发生在它的父类：BaseDexClassLoader#findClass() 中

```
class BaseDexClassLoader {
    final DexPathList pathList; // 初始化类加载器时创建
    public Class<?> findClass(String name) {
        Class c = pathList.findClass(name, suppressedExceptions);
        if (c == null) throw ClassNotFoundException();
        return c;
    }
}
```

而 findClass() 调用的是成员变量 pathList 的 findClass() 方法，查找逻辑都是在 DexPathList 中发生的

所以我们得去看 DexPathList 中的具体实现

### DexPathList

DexPathList 包含的成员变量和成员方法比较多，我们重点关注 Element[] 数组类型的 dexElements 变量

大部分情况下，dexElements 指的就是 apk 压缩包中一个个 dex 文件

回到 DexPathList#findClass() 方法

```
class DexPathList {
    public Class findClass(String name) {
        for (Element element : dexElements) {
            DexFile dex = element.dexFile;
            Class clazz = dex.loadClassBinaryName(name, definingContext, suppressed);
            if (clazz != null) {
                return clazz;
            }
        }
        return null;
    }
}
```

从代码可以看到，DexPathList 实际上是遍历 Element[] 数组，获取元素的 DexFile 对象来加载 Class 文件

数组的遍历是有序的，假设有两个 DexFile 对象包含了同一个 class，那么按照遍历顺序，肯定优先加载在放在前面的 Class 文件

很多热修复技术就是这个原理，把修复后的 dex 放在 Element[] 数组的前面，以此达到修复 bug 的目的

回到主线任务，DexFile#loadClassBinaryName() 执行了最终的类加载，来看它做了什么

```
class DexFile{
    public Class loadClassBinaryName(String name, ClassLoader loader) {
        return defineClass(name, loader, mCookie, this, suppressed);
    }
    
    private static Class defineClass( name,  loader,  cookie, dexFile,) {
        Class result = defineClassNative(name, loader, cookie, dexFile);
        return result;
    }
}
```

哦~

Element[] 数组

在 Java 中负责加载指定类的对象是 ClassLoader，Android 中也是类似

BaseDexClassLoader 继承自 ClassLoader 类，实现了许多 DEX 相关的加载操作，其子类包括:

- PathClassLoader: 负责从本地文件中初始化类加载器，开发者代码默认加载器
- DexClassLoader: 负责从 .jar 或者 .apk 中加载类，可以加载未安装的，不在安装目录的文件
- InMemoryDexClassLoader: 从内存中初始化类加载器

注意，因为 art 在启动过程中，需要使用到许多系统的 java 类，但这些类并不是以 dex 文件形式存在的

因此，他们的类加载器多是 java.lang.BootClassLoader

无论是热修复还是插件化，都需要对类加载机制比较熟悉。本文只是简单了过了一遍类加载的流程，中间很多实现细节都没有展开讨论

邓凡平老师的《深入理解 Android：Java 虚拟机 ART》这本书中，详细介绍了类加载的过程，有需要的同学可以自行查阅

## 双亲委托及破坏(loadClass)

经过测试后，系统类，比如 Activity 、Toast、Fragment等都是 BootClassLoader 负责加载

其他打包进 apk 的，不管是开发者自己写的，还是 jetpack 组件等，统统都是 PathClassLoader 负责加载

BootClassLoader 是继承自 java.lang.ClassLoader

PathClassLoader 是继承自 BaseDexClassLoader ，而 BaseDexClassLoader 才是继承自 java.lang.ClassLoader

它俩没有直接的继承关系，PathClassLoader 的 parent 是在 ActivityThread#handleBindApplication() 创建 Application 时

加载 apk 文件时， 在 PathClassLoader 构造函数中传入了 mPackageInfo.getClassLoader() 中的 BootClassLoader

## 类的加载、链接和初始化

- 加载：把类的信息从 class 文件，或者 dalvik 的 dex、art 中的 oat 文件里，读取到虚拟机内存
- 链接：主要分为3个小任务
  - 验证：类的格式是否正确，文件是否合法等等
  - 准备：为这个类准备一块存储空间
  - 解析：如果类成员有引用其他类，可能还需要把其他类也加载进来
- 初始化：执行初始化静态成员变量的值、static 语法块等 

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

```
# dalvik.system.BaseDexClassLoader
protected Class<?> findClass(String name) throws ClassNotFoundException {
    List<Throwable> suppressedExceptions = new ArrayList<Throwable>();
    // 调用DexPathList对象的findClass()方法
    Class c = pathList.findClass(name, suppressedExceptions);
    if (c == null) {
        ClassNotFoundException cnfe = new ClassNotFoundException("Didn't find class \"" + name + "\" on path: " + pathList);
        for (Throwable t : suppressedExceptions) {
            cnfe.addSuppressed(t); // 找不到类抛出异常
        }
        throw cnfe;
    }
    return c;
}
```

可以看到，实际上 BaseDexClassLoader 调用的是其成员变量 DexPathList pathList 的 findClass() 方法。

```
# dalvik.system.DexPathList
public Class findClass(String name, List<Throwable> suppressed) {
    // 遍历Element
    for (Element element : dexElements) {
        // 获取DexFile，然后调用DexFile对象的loadClassBinaryName()方法来加载Class文件。
        DexFile dex = element.dexFile;
       
        if (dex != null) {
            Class clazz = dex.loadClassBinaryName(name, definingContext, suppressed);
            if (clazz != null) {
                return clazz;
            }
        }
    }
    return null;
}
```

实际上DexPathList最终还是遍历其自身的 Element[] 数组，获取 DexFile 对象来加载 Class 文件

数组的遍历是有序的，假设有两个dex文件存放了二进制名称相同的Class，类加载器肯定就会加载在放在数组前面的dex文件中的Class。

现在很多热修复技术就是把修复的dex文件放在DexPathList中Element[]数组的前面，这样就实现了修复后的Class抢先加载了，达到了修改bug的目的。

### DefineClass

Android加载一个Class是调用DexFile的defineClass()方法。而不是调用ClassLoader的defineClass()方法。

这一点与Java不同，毕竟Android虚拟机加载的dex文件，而不是class文件。

```
# dalvik.system.DexFile
public Class loadClassBinaryName(String name, ClassLoader loader, List<Throwable> suppressed) {
    return defineClass(name, loader, mCookie, suppressed);
}

private static Class defineClass(String name, ClassLoader loader, long cookie,
                                 List<Throwable> suppressed) {
    Class result = null;
    try {
        result = defineClassNative(name, loader, cookie);
    } catch (NoClassDefFoundError e) {
        if (suppressed != null) {
            suppressed.add(e);
        }
    } catch (ClassNotFoundException e) {
        if (suppressed != null) {
            suppressed.add(e);
        }
    }
    return result;
}

```

Android中加载一个类是遍历PathDexList的Element[]数组，这个Element包含了DexFile，调用DexFile的方法来获取Class文件，如果获取到了Class，就跳出循环。否则就在下一个Element中寻找Class。

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



