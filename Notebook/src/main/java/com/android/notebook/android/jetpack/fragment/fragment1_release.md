## Android JetPack Fragment：（一）使用方法和注意事项

> 这篇文章围绕以下几个问题展开，主要探讨的是Fragment诞生的背景和使用方式，希望对您有帮助。
>
> 1. Fragment是什么，我们为什么需要使用他？
> 2. 如何在项目中使用，在使用过程中的需要注意什么？
> 3. 关于Fragment还有哪些奇技淫巧？
>
> 笔者环境：macOS Mojave10.14.6;android-11.0.0_r4;Redmi Note 7



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