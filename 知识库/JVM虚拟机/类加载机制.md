
## 类加载器

在 Android 系统中，常用的类加载器其实只有两个：

- **BootClassLoader** ：继承自 java.lang.ClassLoader，通常负责加载系统不以 dex 形式存在的 java 类。如 Activity 是被 java.lang.BootClassLoader 加载
- **PathClassLoader** ：继承自 BaseDexClassLoader，负责加载 dex 文件，我们打包进 apk 的类都由此加载器加载，如 AppCompatActivity 是被 dalvik.system.PathClassLoader 加载

还有一个不常用的类加载器：**DexClassLoader**

- 同样继承自 BaseDexClassLoader，一般是用于自定义加载器去加载外部的 dex／apk，插件化用的比较多
- DexClassLoader 与 PathClassLoader 都需要的指定一个 dexPath，通常是安装目录
- Android 8.0 以前，DexClassloader 可以指定 optimizedDirectory 目录，8.0 以后，两者在加载 dex 并无区别，都可以加载外部 dex

## 类加载-触发时机

- 使用 **new** 关键字创建对象
- 反射，**Class.forName() / newInstance()** 啥的
- 访问 **static** 关键字修饰的 **静态变量** 或 **静态方法**
- 访问 **static**、**final** 共同修饰的 **引用数据** 类型时，比如 Object，会触发加载
- Java虚拟机启动时被标明为启动类的类，不重要，忽略

注意，以上几种动作，不但会触发自身的 **类加载**，还会触发 "**父类的加载**"。

并且，**父类的加载动作会在子类之前发生**

## 双亲委托及破坏(loadClass)

**双亲委托机制**：

- 每个 ClassLoader 都有一个 parent 成员变量，代表的是父加载器
- 需要加载一个类的时候，会优先使用父加载器去加载，如果在父类加载器中没有找到，自己再执行加载
- 如果 parent 为空，那么就用系统类加载器来加载。通过这样的机制可以保证系统类都是由系统类加载器加载的。 下面是 ClassLoader 的 loadClass 方法的具体实现。

**双亲委托代码流程**：

```
Class<?> loadClass(String name, boolean resolve) {
    // 首先，检查类是否已加载
    public Class<?> c = findLoadedClass(name);
    if (c == null) {
        if (parent != null) {
            // 父加载器不为空，委托父加载器去加载
            c = parent.loadClass(name, false);  //调用父类ClassLoader加载
        }
        // 父加载器没找到，调用自身的查找方法
        if (c == null) { 
            c = findClass(name);
        }
    }
    return c;
}
```

**破坏双亲委托**：

1. 自定义 ClassLoader 继承 BaseDexClassLoader
2. 重写 loadClass() 方法逻辑，改为调用自身的 ClassLoader#findClass() 执行查找
3. 在 findClass() 方法中实现具体的查找逻辑

## 类加载过程（class）

BootClassLoader 负责加载没有打包进 dex 的系统 Java 类，是 Android 系统的顶级加载器

```
class BootClassLoader {
    public Class<?> findClass(String name) {
        return Class.classForName(name, false, null);
    }
}
```

- 调用了 Class 的静态方法 classForName(name)，这是一个 native 方法，由虚拟机完成具体实现，先不管
- 在 Class#classForName(name) 这个 native 方法中，不但会执行类查找，找到类文件以后，还会完成类加载的工作

也就是执行完整的类加载过程：加载、链接与初始化

- 加载：把类的信息从 class 文件读取到虚拟机内存
- 链接：主要分为3个小任务
  - 验证：类的格式是否正确，文件是否合法等等
  - 准备：为这个类准备一块存储空间
  - 解析：如果类成员有引用其他类，可能还需要把其他类也加载进来
- 初始化：执行初始化静态成员变量的值、static 语法块等

执行完上述步骤以后，Class#classForName(name) 方法才会返回，如果中间没有抛出异常，那么表示该类的类加载成功完成

## 类加载过程（dex）

- PathClassLoader 负责加载解压安装到设备的 dex 文件，加载器初始化工作在 ActivityThread#handleBindApplication() 方法完成
- 类加载工作委托给父类 BaseDexClassLoader#findClass() 执行

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

DexPathList#findClass() 完成最终查找加载过程，其内部包含所有 dex 文件，以 Element[] 数组形式保存，查找类的过程就是遍历一个个 DexFile，调用链：

- BaseDexClassLoader#findClass()
- DexPathList#findClass()
- DexFile#loadClassBinaryName()
- DexFile#defineClass()
- DexFile#defineClassNative()，native 方法，由虚拟机实现，不管

