package com.android.notebook.backup.android.framework.component

class ContentProvider {

    /**
     * 目标：
     * 1. 了解使用流程：如何注册给别人使用和如何使用别人的
     * 2. 了解大概实现原理
     *
     * ContentProvider是本APP方法数据给其他APP使用的一种方案
     * 由于涉及到跨进程，所以通信方案是Android的binder
     * ContentProvider也是四大组件中唯一一个没有继承context的组件
     *
     * 关于提供方的使用流程，请查看：
     * @see Provider
     *
     * 如何使用请查看：
     * @see User
     *
     * 关键词：binder、匿名共享内存、URI
     *
     * TODO ：
     * 1. 外部APP调用我的ContentProvider时会自动创建Application，那么我的进程由谁来管理生命周期呢？不可能查询一次创建一次进程吧
     * 2. Context中的ContentResolver什么时候创建的？
     * 3. registerContentObserver是如何做到数据监听的？
     * 4. 多个APP同时操作，如何保证并发？(xml里面的multiprocess标签)
     *
     * */

    private class Provider {

        /**
         * 提供方注册流程：
         * 1. 继承ContentProvider，重写onCreate()、query()、update()等方法，使用Android官方提供的UriMatcher来过滤意图
         * 2. 在xml中注册，其中authorities为提供给访问者唯一标识，也可以使用readPermission、writePermission设置读/写权限
         *
         * 注意：
         * 1. 当两个APP使用相同的authorities时，会导致第二个APP安装失败报错：INSTALL_FAILED_CONFLICTING_PROVIDER
         * 2. 一般存储建议使用SQLite，因为增删改查都是返回影响的行号，其中query()方法返回值为Cursor，所以实现Cursor接口的类型也可以
         * 3. 外部通过Uri调用ContentProvider访问数据时，若APP所在进程未创建，则创建该进程并初始化Application，但不会启动任何Activity
         *
         * */

    }

    private class User {

        /**
         * 使用者调用流程：
         * 1. 若调用的ContentProvider需要读写权限，在xml声明即可
         * 2. 调用Context.getContentResolver()增删改查方法，并指定要操作的URI
         *
         * 注意：
         * 1. 调用方可以使用Context.getContentResolver().registerContentObserver来观察监听数据变化
         * */

    }

}