package cn.ybs.core.base.interfaces

/**
 *  author : <a href="https://yibs.space"/>
 *  e-mail : yibaoshan@foxmail.com
 *  time   : 2024/04/03
 */
interface IInitView {

    fun initViewsAfterViewCreated() {
        initResourcesAfterViewCreated()
        initListenersAfterViewCreated()
        initDialogsAfterViewCreated()
        initFragmentsAfterViewCreated()
        initRecyclerViewAfterViewCreated()
    }

    fun initFragmentsAfterViewCreated() {
        // 在此初始化 Fragment
    }

    fun initResourcesAfterViewCreated() {
        // 在此初始化资源
        initAnimationsAfterViewCreated()
    }

    fun initListenersAfterViewCreated() {
        // 在此初始化监听器
    }

    fun initDialogsAfterViewCreated() {
        // 在此初始化 Dialog
    }

    fun initRecyclerViewAfterViewCreated() {
        // 在此初始化 RecyclerView
    }

    fun initAnimationsAfterViewCreated() {
        // 在此初始化动画
    }

}