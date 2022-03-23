package com.android.blackboard

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.*
import android.view.Gravity
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import java.lang.ref.WeakReference
import kotlin.concurrent.thread

@ExperimentalStdlibApi
class MenuActivity : AppCompatActivity() {

    companion object {

        fun startActivity(context: Context?, type: TypeEnum) {
            val intent = Intent(context, MenuActivity::class.java)
            intent.putExtra("name", type.name);
            context?.startActivity(intent)
        }
    }

    private lateinit var weakReference: WeakReference<ViewGroup>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val scrollView = ScrollView(this)
        val contentView = LinearLayout(this)
        contentView.orientation = LinearLayout.VERTICAL
        contentView.gravity = Gravity.CENTER
        contentView.setPadding(20, 20, 20, 20)

        scrollView.addView(contentView)

        weakReference = WeakReference(contentView)
        setContentView(scrollView)
        init()
    }

    private fun init() {
        val name = intent.getStringExtra("name")
        title = name
        when {
            TypeEnum.Java.name == name -> {
                initJava()
            }
            TypeEnum.Android.name == name -> {
                initAndroid()
            }
            TypeEnum.Network.name == name -> {
                initNetwork()
            }
            TypeEnum.VM.name == name -> {
                initVM()
            }
            TypeEnum.OS.name == name -> {
                initOS()
            }
            TypeEnum.BOOK.name == name -> {
                initBook()
            }
        }
    }

    private fun initJava() {
        createButtonViewAddedToRootView("Java-基础-面向对象三大特性.md")
        createButtonViewAddedToRootView("Java-基础-四大引用类型.md")
        createButtonViewAddedToRootView("Java-基础-基础数据类型.md")
        createButtonViewAddedToRootView("Java-基础-Object类.md")
        createButtonViewAddedToRootView("Java-基础-String类.md")
        createButtonViewAddedToRootView("Java-基础-泛型(Generic).md")
        createButtonViewAddedToRootView("Java-基础-注解(Annotation).md")
        createButtonViewAddedToRootView("Java-基础-异常(Exception).md")
        createButtonViewAddedToRootView("Java-基础-反射(Reflection).md")
        createButtonViewAddedToRootView("Java-容器-框架预览图.PNG", true, R.mipmap.java_collections_overview)
        createButtonViewAddedToRootView("Java-容器-集合框架简介.md", true)
        createButtonViewAddedToRootView("Java-容器-List.md", true)
        createButtonViewAddedToRootView("Java-容器-Set.md", true)
        createButtonViewAddedToRootView("Java-容器-Queue.md", true)
        createButtonViewAddedToRootView("Java-容器-Map.md", true)
        createButtonViewAddedToRootView("Java-并发-基础理论.md")
        createButtonViewAddedToRootView("Java-并发-内存模型(JMM).md")
        createButtonViewAddedToRootView("Java-并发-线程基础.md")
        createButtonViewAddedToRootView("Java-并发-Synchronized.md")
        createButtonViewAddedToRootView("Java-并发-volatile.md")
        createButtonViewAddedToRootView("Java-并发-final.md")
        createButtonViewAddedToRootView("Java-JUC预览图.PNG", true, R.mipmap.java_concurrent_overview)
        createButtonViewAddedToRootView("Java-JUC-原子类.md")
        createButtonViewAddedToRootView("Java-JUC-同步器.md")
        createButtonViewAddedToRootView("Java-JUC-执行器.md")
        createButtonViewAddedToRootView("Java-JUC-锁框架.md")
        createButtonViewAddedToRootView("Java-JUC-集合框架.md")
        createButtonViewAddedToRootView("Java-其他-值传递.md")
    }

    private fun initAndroid() {
        createButtonViewAddedToRootView("Android-四大组件-Activity.md")
        createButtonViewAddedToRootView("Android-四大组件-Service.md")
        createButtonViewAddedToRootView("Android-四大组件-BroadcastReceiver.md")
        createButtonViewAddedToRootView("Android-四大组件-LocalBroadcastManager.md")
        createButtonViewAddedToRootView("Android-四大组件-ContentProvider.md")
        createButtonViewAddedToRootView("Android-系统组件-Context.md")
        createButtonViewAddedToRootView("Android-系统组件-Application.md")
        createButtonViewAddedToRootView("Android-通信-Handler机制.md")
        createButtonViewAddedToRootView("Android-通信-Binder机制.md")
        createButtonViewAddedToRootView("Android-View-动画.md")
        createButtonViewAddedToRootView("Android-JetPack-Fragment.md")
        createButtonViewAddedToRootView("Android-JetPack-RecyclerView.md")
        createButtonViewAddedToRootView("Android-JetPack-ViewModel.md")
        createButtonViewAddedToRootView("Android-JetPack-DataBinding.md")
        createButtonViewAddedToRootView("Android-JetPack-LiveData.md")
        createButtonViewAddedToRootView("Android-JetPack-Lifecycle.md")
        createButtonViewAddedToRootView("Android-Widget-NestedScrollView.md")
        createButtonViewAddedToRootView("Android-三方库-OkHttp.md")
        createButtonViewAddedToRootView("Android-三方库-Retrofit.md")
        createButtonViewAddedToRootView("Android-三方库-Glide.md")
        createButtonViewAddedToRootView("Android-三方库-ButterKnife.md")
        createButtonViewAddedToRootView("Android-三方库-LeakCanary.md")
        createButtonViewAddedToRootView("Android-三方库-VLayout.md")
        createButtonViewAddedToRootView("Android-三方库-ARouter.md")
        createButtonViewAddedToRootView("Android-三方库-屏幕适配.md")
        createButtonViewAddedToRootView("Android-优化-启动优化.md")
        createButtonViewAddedToRootView("Android-优化-布局优化.md")
        createButtonViewAddedToRootView("Android-优化-网络优化.md")
        createButtonViewAddedToRootView("Android-优化-卡顿优化.md")
        createButtonViewAddedToRootView("Android-优化-包体积优化.md")
        createButtonViewAddedToRootView("Android-优化-WebView.md")
        createButtonViewAddedToRootView("Android-系统服务-AMS.md")
        createButtonViewAddedToRootView("Android-系统服务-PMS.md")
        createButtonViewAddedToRootView("Android-系统服务-WMS.md")
        createButtonViewAddedToRootView("Android-安全-打包&签名.md")
        createButtonViewAddedToRootView("Android-安全-混淆&加固.md")
        createButtonViewAddedToRootView("Android-其他-Root&Hook.md")
        createButtonViewAddedToRootView("Android-其他-模块化&组件化.md")
        createButtonViewAddedToRootView("Android-其他-热修复&插件化.md")
    }

    private fun initNetwork() {
    }

    /**
     * 逃逸分析是编译器阶段
     * dex文件生成
     * dex和class区别
     * jvm的操作数栈是用来执行字节码指令的，基于栈，art基于寄存器的指令集
     * */
    private fun initVM() {
        createButtonViewAddedToRootView("JVM-类加载.md")
        createButtonViewAddedToRootView("JVM-内存区域预览图.PNG", true, R.mipmap.jvm_memory_struct_overview)
        createButtonViewAddedToRootView("JVM-内存结构-概述.md")
        createButtonViewAddedToRootView("JVM-内存结构-程序计数器(线程私有).md")
        createButtonViewAddedToRootView("JVM-内存结构-虚拟机栈(线程私有).md")
        createButtonViewAddedToRootView("JVM-内存结构-本地方法栈(线程私有).md")
        createButtonViewAddedToRootView("JVM-内存结构-堆内存(公开).md")
        createButtonViewAddedToRootView("JVM-内存结构-方法区(公开).md")
        createButtonViewAddedToRootView("JVM-垃圾回收-对象是否可回收.md")
        createButtonViewAddedToRootView("JVM-垃圾回收-回收算法.md")
        createButtonViewAddedToRootView("JVM-垃圾回收-垃圾收集器.md")
    }

    private fun initOS() {
        createButtonViewAddedToRootView("Linux-线程&进程.md")
    }

    private fun initBook() {
        createButtonViewAddedToRootView("Book-邓凡平-深入理解Android-ART虚拟机.md")
    }

    private fun createButtonViewAddedToRootView(text: String, deep: Boolean = false, resId: Int = -1): Button {
        val button = AppCompatButton(this)
        button.text = text
        if (deep) button.setTextColor(Color.BLUE)
        if (resId > 0) {
            button.setCompoundDrawablesWithIntrinsicBounds(resources.getDrawable(R.mipmap.ic_launcher), null, null, null)
            button.setOnClickListener { ImageActivity.startContentActivity(this, text, resId) }
        } else button.setOnClickListener { thread { LocalBroadcastManager.getInstance(this@MenuActivity).sendBroadcastSync(Intent("hhh")) } }
        weakReference.get()?.addView(button)
        return button
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}