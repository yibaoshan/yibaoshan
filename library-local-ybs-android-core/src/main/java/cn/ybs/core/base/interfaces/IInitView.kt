package cn.ybs.core.base.interfaces

/**
 *  author : <a href="https://yibs.space"/>
 *  e-mail : yibaoshan@foxmail.com
 *  time   : 2024/04/03
 */
interface IInitView {

    fun initViewsAfterCreate() {
        initResourcesWhenInitViews()
        initListenersWhenInitViews()
        initDialogsWhenInitViews()
        initFragmentsWhenInitViews()
        initRecyclerViewWhenInitViews()
    }

    fun initFragmentsWhenInitViews() {
        // 在此初始化 Fragment
    }

    fun initResourcesWhenInitViews() {
        // 在此初始化资源
        initAnimationsWhenInitResources()
    }

    fun initListenersWhenInitViews() {
        // 在此初始化监听器
    }

    fun initDialogsWhenInitViews() {
        // 在此初始化 Dialog
    }

    fun initRecyclerViewWhenInitViews() {
        // 在此初始化 RecyclerView
    }

    fun initAnimationsWhenInitResources() {
        // 在此初始化动画
    }

}