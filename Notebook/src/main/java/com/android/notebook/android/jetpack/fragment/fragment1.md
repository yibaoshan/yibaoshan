### 【AndroidX】Fragment（一）：介绍和使用说明

##### fragment出现是为了解决导航栏和内容区，参考微信
##### fragment可以是个有生命周期的viewgroup
##### fragment的生命周期不是由activity决定的

1. Fragment是什么，以及我们为什么需要使用他
2. 在项目中如何使用Fragment
   2.1 经典使用方式（懒加载）老三样
   2.2 JetPack组合使用方式
   2.3 Fragment的生命周期和可见性
   2.4 Fragment通信
   2.5 Fragment嵌套
3. 奇技淫巧，关于fragment的妙用
4. 注意事项
   5. 空的构造函数
   6. activity强引用

关于无参构造函数为什么一定要声明我想大部分开发者都知道，
就算你不知道，bugly也会让你知道。
我们都知道，fragment是通过newInstance创建的

> 这篇文章主要向大家介绍Android fragment 标签加载过程分析,主要内容包括基础应用、实用技巧、原理机制等方面，希望对大家有所帮助。
> 笔者环境：macOS Mojave10.14.6;android-11.0.0_r4;Redmi Note 7

一、背景

####  二、如何使用Fragment

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

##### 2.3 ViewPager
为您的Fragment添加切换动画：https://developer.android.google.cn/training/animation/screen-slide?hl=zh-cn#pagetransformer
懒加载：setUserVisibleHint
* 基于AndroidX下的Fragment/AppCompatActivity/ViewPager，若您的项目未开启AndroidX选项，文中部分内容可能不适合直接使用在您的项目当中，具体请查看/参考v4/v7/androidx
* 由于笔者能力有限，文中难免会存在遗漏甚至错误的地方。如果您发现了任何逻辑不顺/无法理解/等问题，请及时联系我，万分感谢
