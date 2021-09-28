##### LeakCanary

简介：用于监听APP是否有内存泄漏，2.0之后版本用kotlin重写，分析hprof文件的工具有haha替换为shark

使用：2.0之后直接Gradle引用即可，leakcanary内部自动初始化，流程如下：

```
class AppWatcherInstaller : ContentProvider(){
    
    override fun onCreate(): Boolean {
        val application = context!!.applicationContext as Application
        AppWatcher.manualInstall(application)
        return true
    }
}
```

原理：在一个Activity执行完onDestroy()之后，将它放入WeakReference中，然后将这个WeakReference类型的Activity对象与ReferenceQueque关联。这时再从ReferenceQueque中查看是否有没有该对象，如果没有，执行gc，再次查看，还是没有的话则判断发生内存泄露了。最后用HAHA这个开源库去分析dump之后的heap内存。

#### 腾讯Tinker

简介：腾讯微信开源的热修复框架，需要重启生效

原理：将有bug的类打包成dex文件，使用dexdiff工具和旧的dex文件做对比，打出来的差分包下发到手机。使用DexClassLoader加载patch.dex文件，利用反射将PathClassLoader中的DexPathList中dexElements数组做替换，将patch.dex放在数组0号位，这样出现问题的类直接替换掉

加载资源补丁：

Tinker的资源更新采用的InstantRun的资源补丁方式，全量替换资源。由于App加载资源是依赖Context.getResources()方法返回的Resources对象，Resources 内部包装了 AssetManager，最终由 AssetManager 从 apk 文件中加载资源。我们要做的就是新建一个AssetManager()，hook掉其中的addAssetPath()方法，将我们的资源补丁目录传递进去，然后循环替换Resources对象中的AssetManager对象，达到资源替换的目的。

加载补丁so：

so的更新方式跟dex和资源都不太一样，因为系统提供给了开发者自定义so目录的选项

依然根据so_meta.txt中的补丁信息校验so文件是否都存在。然后将so补丁列表存放在结果中libs的字段。

####美团Robust

简介：基于Instant run原理的即时生效热修复框架

原理：Robust插件对每个产品代码的每个函数都在编译打包阶段自动的插入了一段代码

```java
public static ChangeQuickRedirect changeQuickRedirect;
    public long getIndex() {
        if(changeQuickRedirect != null) {
          return changeQuickRedirect.fix();//伪代码
        }
        return 100L;
    }
```

当客户端出现问题后，将新的修复类打包进patch.dex，下发给客户端

客户端拿到patch.dex后用DexClasssLoader加载，反射拿到修复类，并赋值给changeQuickRedirect

至此，整个方法修复完成

技术总结：

1. 打包阶段使用字节码插桩对每个函数插入代码
2. 使用类加载+反射技术来替换出现问题的类方法

缺点：

1. 增大class体积
2. 兼容性好，但无法新增字段和类

####阿里~~AndFix~~

简介：阿里早期的热修复框架

原理：提供MethodReplace注解，输入要替换的类名和方法，然后在native层将两个ArtMethod方法进行替换

####腾讯Bugly

#### JavaPoet

简介：用来生成Java代码，使用build建造者模式。常见方法，addField、addMethod等

#### Retrofit

####OkHttp

####神策埋点

####阿里推送

#### 阿里vlayout布局框架

####RecyclerView

####美团多渠道walle

### DataBinding

使用方法

```bash
android {
    ...
    //利用Android-transform来拦截xml文件
    dataBinding {
        enabled = true
    }
}

<layout>
android:text={xxx.text}
</layout>
```

DataBinding是基于观察者Obsevable模式机制，无论ViewModel何时修改它们的值，它都会通知`executeBindings()`方法，这个方法依赖于这个值并且会在View上执行动作

在声明了layout的xml中，会生成activity_main-layout.xml和binding的Java文件

理解了生成的代码是怎样在View和ViewModel之间绑定之后，在这一部分，我们会找出编译生成神奇代码的方法。

注意这两个模块：`compiler`和`compilerCommon`。我们看的最多的地方。

- 编译器的核心为`compiler.android.databinding.annotationprocessor`包下的`ProcessDataBinding`类。这个类的职责是一步一步执行处理列表。