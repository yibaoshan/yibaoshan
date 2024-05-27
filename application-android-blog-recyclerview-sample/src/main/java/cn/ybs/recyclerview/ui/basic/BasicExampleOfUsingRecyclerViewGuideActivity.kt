package cn.ybs.recyclerview.ui.basic

import cn.ybs.core.base.BaseViewBindingActivity
import cn.ybs.recyclerview.AppRouter
import cn.ybs.recyclerview.databinding.ActivityBasicUsageExampleGuideBinding

/**
 * author : <a href="https://yibs.space"/>
 * e-mail : yibaoshan@foxmail.com
 * time   : 2024/05/19
 * desc   : RV 基础使用导航页
 */
open class BasicExampleOfUsingRecyclerViewGuideActivity : BaseViewBindingActivity<ActivityBasicUsageExampleGuideBinding>() {

    override fun initListenersAfterViewCreated() {
        val view = viewBinding ?: return
        view.tvNormalLinearLayoutVerticalTextType.setOnClickListener { AppRouter.Basic.startTextVerticalLinearLayoutRecyclerViewActivity(this) }
        view.tvNormalLinearLayoutVerticalImageType.setOnClickListener { AppRouter.Basic.startImageVerticalLinearLayoutRecyclerViewActivity(this) }
        view.tvNormalLinearLayoutVerticalMultiType.setOnClickListener { AppRouter.Basic.startMultiTypeVerticalLinearLayoutRecyclerViewActivity(this) }

        view.tvNormalGridLayoutVerticalTextType.setOnClickListener { AppRouter.Basic.startTextVerticalGridLayoutRecyclerViewActivity(this) }
        view.tvNormalStaggeredGridLayoutVerticalMultiType.setOnClickListener { AppRouter.Basic.startMultiTypeVerticalStaggeredGridLayoutRecyclerViewActivity(this) }
        view.tvNormalFlexboxLayoutVerticalTextType.setOnClickListener { AppRouter.Basic.startTextVerticalFlexboxLayoutRecyclerViewActivity(this) }
    }

}