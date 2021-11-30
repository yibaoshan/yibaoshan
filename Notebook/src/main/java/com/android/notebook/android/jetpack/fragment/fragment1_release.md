## Android JetPack Fragment：（一）使用方法和注意事项

> 这篇文章主要探讨Fragment背景和常见的几种使用方式，希望对您有帮助。
>
> 笔者环境：macOS Mojave10.14.6;android-11.0.0_r4;Redmi Note 7

## 一、简介

### 1.1 背景介绍

是主要为具有大屏幕的设备（例如平板电脑）设计的

更好的利用大屏幕的优势

若当时手机屏幕尺寸和现在差不多，我想Android系统在发布之前就一定会考虑页面嵌套的问题

想想在2011年，功能机占据着70%的市场份额([数据来源](https://news.cision.com/reportbuyer/r/feature-phones-accounted-for-70--of-global-handset-sales-in-2011-says-new-report,c9212961))，同年iPhone 4s发布，乔帮主认为3.5 英寸是手机的黄金尺寸

三星note系列打破了人们对于手机大小的想象，随后的手机屏幕越来越大

Fragment是在2011Android 3.0发布的，本意是为了给Pad的使用(没想到手机屏幕发展)

`Fragment`直译过来是`碎片`的意思，官方翻译为`片段`，从使用场景来看，笔者认为翻译成`子页面`可能更贴切



### 1.2 适用场景

目前绝大多数APP首页应该都是采用单Activity+多Fragment的方案，以微信举例：

我是例子

上图是微信首页，在页面中分别包含`微信`、`通讯录`、`发现`、`我`四个不同功能的子页面



### 1.3 Fragment生命周期

虽然Fragment本质上是View



## 二、使用方式

### 2.1 添加依赖

在应用或模块的 build.gradle 文件中添加所需工件的依赖项：

```
dependencies {
    val fragment_version = "1.3.6"

    // Java language implementation
    implementation("androidx.fragment:fragment:$fragment_version")
    // Kotlin
    implementation("androidx.fragment:fragment-ktx:$fragment_version")
}
```

##### 

### 2.2 在xml中使用 \<fragment \>标签

使用

唯一需要注意的一点是必须指定唯一标识，

在Activity获取Fragment实例

通过id获取：findFragmentById

通过tag获取：findFragmentByTag

获取全部：getFragments

[代码示例](https://github.com/yibaoshan/Example-Android-JetPack-Fragment/blob/master/app/src/main/java/com/android/example_android_jetpack_fragment/usage/activity/UsageExample1Activity.kt)



### 2.3 ViewGroup+FragmentManager

#### 2.2.1



### 2.4 ViewPager/ViewPager2



### 2.5 Navigation

### 2.6 使用方式小结



## 三、通信方式

### 3.1 定义接口/方法

### 3.2 共享ViewModel

### 3.3 Result API



## 四、写在最后

### 4.1 关于Fragment的奇技淫巧

### 4.2 Fragment常见注意事项

### 4.3 示例代码

示例代码工程地址：[Example-Android-JetPack-Fragment](https://github.com/yibaoshan/Example-Android-JetPack-Fragment)

#### 参考资料

### 一、Fragment是什么？为什么需要他？

`Fragment`直译过来是`碎片`的意思，官方翻译为`片段`，从使用场景来看，笔者认为翻译成`子页面`可能更贴切

![blog_fragment_wechat_home](/Users/bob/Desktop/Bob/work/workspace/androidstudio/Blackboard/Notebook/src/main/java/com/android/notebook/android/jetpack/fragment/res/blog_fragment_wechat_home.gif)

_图片来源：自己做的_

这是微信首页截图，在页面中分别包含`微信`、`通讯录`、`发现`、`我`四个不同功能的子页面

在`Fragment`未发布之前，我们可以使用[TabHost](https://developer.android.com/reference/android/widget/TabHost)+[ActivityGroup](https://developer.android.com/reference/android/app/ActivityGroup)来实现类似的效果；或者直接使用4个`Activity`来实现也行，控制好跳转逻辑和回退栈即可，但是这样做要维护的代码会有点多，后续代码的移植性和复用性也比较差

同时，Android3.0推出为了Pad

以上，便是`Fragment`诞生的背景



*以上是笔者个人理解，目前主流博客，若您有不同的看法，以您为准。不管有没有Pad，Android迟早会推出可复用且轻量化的`子页面`控件

#### Fragment生命周期
Fragment是依附于Activity生存的

#### Fragment通信

##### Activity与Fragment通信：方法调用

`Activity`与`Fragment`之间通信实现起来比较简单，由于`Activity`必然持有`Fragment`的引用对象

所以我们在`Activity`中直接调用`Fragment`的方法即可，代码实现：

Fragment：

```kotlin
class ExampleFragment : Fragment() {

    fun whoCallsMe(name: String) {
        //do something
    }
  	...
}
```

Activity：

```kotlin
class ExampleActivity : AppCompatActivity() {
  
 		override fun onCreate(savedInstanceState: Bundle?) {
    	super.onCreate(savedInstanceState)
    	val fragment = ExampleFragment()
   	  fragment.whoCallsMe("your father")  
	 }
	...
}
```
##### Fragment与Activity通信：接口定义

`Fragment`与`Activity`通信稍微有些麻烦，为了保证`Fragment`的复用性，在实际项目开发中建议使用`interface`接口实现的方式，代码实现：

Fragment：

```kotlin
//穷儿子
class ExampleFragment : Fragment() {
  
  	//创建通信接口
  	interface OnCallFatherListener {
        fun talk(s : String):String
    }

    var weakResListener: WeakReference<OnCallFatherListener>? = null
    
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val listener = context as? OnCallFatherListener
        if (listener == null) {
            throw ClassCastException("you're not my father")
        }
      	weakResListener = WeakReference(listener)
      	weakResListener?.get()?.talk("Dad I'm poor")
    }
    ...
}
```

Activity：

```kotlin
class Example1Activity : AppCompatActivity(),ExampleFragment.OnCallFatherListener {

        override fun talk(s: String): String {
            return "滚"
        }
        ...
}

class Example2Activity : AppCompatActivity(),ExampleFragment.OnCallFatherListener {

        override fun talk(s: String): String {
            return "支付宝给你转了50000过去，不够再和爸爸说"
        }
        ...
}
```

##### Fragment与Fragment通信

![image-20211125113933402](/Users/bob/Library/Application Support/typora-user-images/image-20211125113933402.png)

_图片来源：[官网文档](https://developer.android.com/training/basics/fragments/communicating?hl=zh-cn)截图_