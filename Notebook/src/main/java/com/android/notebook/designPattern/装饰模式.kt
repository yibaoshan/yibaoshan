package com.android.notebook.designPattern

import org.junit.Test

class 装饰模式 {

    /**
     * 装饰模式(Decorator)又名包装(Wrapper)模式，是继承关系的一个替代方案
     *
     * Android中Context、ContextImpl、ContextWrapper三者间就是典型的装饰者模式
     *
     * 它的好处在于，由于Application继承的是ContextImpl，而ContextImpl里面的实现是依赖attach方法传入的mBase
     * 所以，假设要更改Context实现的话，只需要调用attach传入不同的实现类即可
     * Application、Context、ContextWrapper都不需要更改
     *
     * 如果不使用装饰模式的话，Application要想更改callName的实现，要么重写方法，要么重写继承其他的类
     * 这两种方法，哪一个都不是Android团队想要看到的
     *
     * */

    @Test
    fun test() {
        val app = Application()
        val contextImpl = ContextImpl()
        val contextImpl2 = ContextImpl2()
        app.attach(contextImpl2)
        println(app.callName())
    }

    private abstract class Context {
        abstract fun callName(): String
    }

    private class ContextImpl : Context() {
        override fun callName(): String {
            return "我是你爹"
        }
    }

    private class ContextImpl2 : Context() {
        override fun callName(): String {
            return "我是你爷"
        }
    }

    /*重点：虽然继承自Context，但其方法委托给mBase实现*/
    private open class ContextWrapper : Context() {

        private lateinit var mBase: Context

        fun attach(context: Context) {
            mBase = context
        }

        override fun callName(): String {
            return mBase.callName()
        }
    }

    private class Application : ContextWrapper() {

    }

}