package cn.ybs.recyclerview

import cn.ybs.core.base.BaseViewBindingActivity
import cn.ybs.recyclerview.databinding.ActivityMainBinding

/**
 * author : <a href="https://yibs.space"/>
 * e-mail : yibaoshan@foxmail.com
 * time   : 2024/05/19
 * desc   : RV 导航页
 */
class MainActivity : BaseViewBindingActivity<ActivityMainBinding>() {

    override fun initListenersAfterViewCreated() {
        val view = viewBinding ?: return
        view.tvBasicUsage.setOnClickListener { AppRouter.Basic.startBasicUsageActivity(this) }
        view.tvCustomItemClick.setOnClickListener { AppRouter.ItemClick.startItemClicksGuideActivity(this) }
    }

}