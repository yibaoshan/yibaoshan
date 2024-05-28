package cn.ybs.recyclerview.ui.stable

import cn.ybs.core.base.BaseViewBindingActivity
import cn.ybs.recyclerview.AppRouter
import cn.ybs.recyclerview.databinding.ActivityStableIdsGuideExampleBinding

/**
 *  author : <a href="https://yibs.space"/>
 *  e-mail : yibaoshan@foxmail.com
 *  time   : 2024/05/28
 *  desc   : 设置稳定 ID 效果
 */
class StableIDGuideExampleActivity : BaseViewBindingActivity<ActivityStableIdsGuideExampleBinding>() {

    override fun initListenersAfterViewCreated() {
        val view = viewBinding ?: return
        view.tvStableIdsEnable.setOnClickListener { AppRouter.StableID.startStableIDEnableActivity(this) }
        view.tvStableIdsDisable.setOnClickListener { AppRouter.StableID.startStableIDDisableActivity(this) }
    }

}