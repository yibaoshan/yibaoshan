package com.android.aosp.frameworks.base.core.android.content

/**
 * 用于描述要启动的Activity
 *
 * */
class Intent {

    //启动activity类型，默认为空
    private var mFlags: Int = 0

    //保存的参数/数据
    private lateinit var mExtras: Bundle

    //保存的目标Activity类名信息
    private lateinit var mComponent: ComponentName

    constructor()

    constructor(context: Context, clz: Class<*>) {
        mComponent = ComponentName(context, clz)
    }

    /*android.os.Bundle*/
    class Bundle {}

}