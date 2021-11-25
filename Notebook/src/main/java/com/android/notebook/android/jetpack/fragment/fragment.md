
## Android JetPack Fragment：（一）使用方法和注意事项
## Android JetPack Fragment：（二）浅析实现原理

### 前言
1. Fragment的出现是为了解决各个厂商屏幕物理尺寸&屏幕像素不一致而产生的
2. Fragment的生命周期和Activity不一定同步
3. Activity被lowkill杀掉时你看到的Fragment残影和Fragment没关系
4. 嵌套Fragment不一定要用superFragmentManager
###  一、Fragment是什么？
###  二、如何使用
####  2.1 声明依赖项
在应用或模块的 build.gradle 文件中添加所需工件的依赖项：
```
dependencies {
    val fragment_version = "1.3.6"

    // Java language implementation
    implementation("androidx.fragment:fragment:$fragment_version")
    // Kotlin
    implementation("androidx.fragment:fragment-ktx:$fragment_version")
    // Testing Fragments in Isolation
    debugImplementation("androidx.fragment:fragment-testing:$fragment_version")
}
```
### 三、如何做到的？实现原理