### 【AndroidX】Fragment（一）：介绍和使用说明

1. Fragment是什么，以及我们为什么需要使用他
   2. 众所周知，以微信举例，通讯录聊天页面之间显然没有直接联系，基于单一原则。我们可以把这两个页面分开设计，但在fragment出现之前
   3. 用activity实现显然太重了，需要处理跳转逻辑和维护一套回退栈
2. 在项目中如何使用Fragment
   2.1 经典使用方式（懒加载）老三样
   2.2 JetPack组合使用方式
   2.3 Fragment的生命周期和可见性
   2.4 Fragment通信(为保证fragment的复用性，建议用接口)
   2.5 Fragment嵌套
3. 奇技淫巧，关于fragment的妙用
4. 注意事项
   5. 空的构造函数
   6. activity强引用

关于无参构造函数为什么一定要声明我想大部分开发者都知道，
就算你不知道，bugly也会让你知道。
我们都知道，fragment是通过newInstance创建的

一、背景

####  二、如何使用Fragment

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

##### 2.1 在xml中使用fragment标签
示例@Example1Activity
```
    <fragment
            android:id="@+id/fragment"
            android:name="Fragment"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />
```
这种方式属于静态添加，目前笔者还没找到从activity获取fragment的方法。日常开发不建议使用

##### 2.2 ViewGroup+FragmentManager
重叠
addToBackStack，回退栈影响生命周期
hide&show&onHiddenChanged

AndroidX&FragmentStatePagerAdapter

|  方法   | 释义  |
|  ----  | ----  |
| replace  | 每次销毁重建 |

|  方法   | 释义  | 可见性 |
|  ----  | ----  | |
| add  | 单元格 | |
| hide  | 单元格 | |
| show  | 单元格 | |

提示：对于每个片段事务，您都可通过在提交前调用 setTransition() 来应用过渡动画。

1. 获取FragmentManager
2. 获取FragmentTransaction
3. 

##### 2.3 ViewPager
为您的Fragment添加切换动画：https://developer.android.google.cn/training/animation/screen-slide?hl=zh-cn#pagetransformer
懒加载：setUserVisibleHint
* 基于AndroidX下的Fragment/AppCompatActivity/ViewPager，若您的项目未开启AndroidX选项，文中部分内容可能不适合直接使用在您的项目当中，具体请查看/参考v4/v7/androidx
* 由于笔者能力有限，文中难免会存在遗漏甚至错误的地方。如果您发现了任何逻辑不顺/无法理解/等问题，请及时联系我，万分感谢

注意事项：
为什么Fragment中要使用viewLifecycleOwner代替this：https://juejin.cn/post/6915222252506054663#comment