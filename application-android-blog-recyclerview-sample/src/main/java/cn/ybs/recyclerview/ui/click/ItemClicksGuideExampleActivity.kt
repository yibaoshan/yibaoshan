package cn.ybs.recyclerview.ui.click

import cn.ybs.core.base.BaseViewBindingActivity
import cn.ybs.recyclerview.AppRouter
import cn.ybs.recyclerview.databinding.ActivityItemClicksGuideExampleBinding

/**
 *  author : <a href="https://yibs.space"/>
 *  e-mail : yibaoshan@foxmail.com
 *  time   : 2024/05/27
 *  desc   : Item 的点击与长按事件
 */
class ItemClicksGuideExampleActivity : BaseViewBindingActivity<ActivityItemClicksGuideExampleBinding>() {

    override fun initListenersAfterViewCreated() {
        val view = viewBinding ?: return
        view.tvItemClicksTouchListener.setOnClickListener { AppRouter.ItemClick.startItemClicksBaseItemTouchListenerActivity(this) }
        view.tvItemClicksClickListener.setOnClickListener { AppRouter.ItemClick.startItemClicksBaseClickListenerActivity(this) }
    }

}