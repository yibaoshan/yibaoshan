package com.android.notebook.backup.android.framework.other


class Context {

    /**
     * 这是一个抽象类，它的实现是由Android系统提供
     * 它允许访问特定应用的资源和类，以及向上调用应用程序级的操作，如启动活动，广播和接收Intent等
     *
     * Context采用装饰模式设计
     *
     * Context实现类：ContextImpl
     * Context包装类：ContextWrapper、ContextThemeWrapper
     * Context子类：
     * 1. Activity继承自ContextThemeWrapper
     * 2. Application继承自ContextWrapper
     * 3. Service继承自ContextWrapper
     *
     * 在ContextWrapper中，有个getBaseContext方法
     * 该方法会返回ContextImpl，可以当做hook点用反射做点什么
     *
     * 具体实现请查看：
     * @see com.android.aosp.frameworks.base.core.android.content.Context
     *
     * @TODO
     * - 使用Application启动Activity会遇到什么问题？
     * - activity是什么时候被创建的，他的contextImpl是如何被赋值的？Application呢？为什么说ContextProvider的context是Application，Broadcast的context是Activity？contextImpl又是如何被创建的？
     * */

    private class AboutContextQuestions {

        /**
         * 1. 在Activity调用getApplication()和getApplicationContext()得到的对象有什么区别呢？
         * 答：返回的对象没有区别，都是当前应用的application对象
         * 虽然两者返回值是一样的，但两个方法意义不一样
         * getApplication()是Activity的final方法，返回值为Application
         * getApplicationContext()是Context的方法，返回值为Context
         *
         * */

    }

}