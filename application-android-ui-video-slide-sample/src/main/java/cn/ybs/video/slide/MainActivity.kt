package cn.ybs.video.slide

import androidx.coordinatorlayout.widget.CoordinatorLayout
import cn.ybs.core.base.BaseViewBindingActivity
import cn.ybs.video.slide.databinding.ActivityMainBinding

class MainActivity : BaseViewBindingActivity<ActivityMainBinding>() {

    override fun initViewsAfterCreate() {
        super.initViewsAfterCreate()
        viewBinding?.apply {
            val params = playingView.layoutParams as CoordinatorLayout.LayoutParams
            params.behavior = SlideBehavior(this@MainActivity, null)
            playingView.layoutParams = params
        }
    }

}