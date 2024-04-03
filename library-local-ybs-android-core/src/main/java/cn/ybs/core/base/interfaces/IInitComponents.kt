package cn.ybs.core.base.interfaces

/**
 *  author : <a href="https://yibs.space"/>
 *  e-mail : yibaoshan@foxmail.com
 *  time   : 2024/04/03
 */
interface IInitComponents {

    fun initComponentsAfterCreate() {
        initServicesWhenInitComponent()
        initContentProvidersWhenInitComponent()
        initBroadcastReceiversWhenInitComponent()
    }

    fun initServicesWhenInitComponent(){
        // 在此初始化 Service
    }
    fun initContentProvidersWhenInitComponent(){
        // 在此初始化 ContentProvider
    }
    fun initBroadcastReceiversWhenInitComponent(){
        // 在此初始化 BroadcastReceiver
    }

}