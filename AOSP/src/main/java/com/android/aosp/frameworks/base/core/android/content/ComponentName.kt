package com.android.aosp.frameworks.base.core.android.content

/**
 *
 * 用于保存发起跳转context的包名和目标Activity类目
 * 该类只有两个变量
 *
 * mPackage 发起跳转context包名
 * mClass 目标Activity类目
 *
 */
class ComponentName {

    private lateinit var mPackage: String
    private lateinit var mClass: String

    constructor()

    constructor(context: Context, clz: Class<*>) {
        mPackage = context.getPackageName()
        mClass = clz.name
    }
}