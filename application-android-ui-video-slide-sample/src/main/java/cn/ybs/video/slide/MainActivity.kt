package cn.ybs.video.slide

import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import cn.ybs.core.base.BaseViewBindingActivity
import cn.ybs.video.slide.databinding.ActivityMainBinding
import cn.ybs.video.slide.interfaces.IPageController
import cn.ybs.video.slide.interfaces.IPageState
import cn.ybs.video.slide.viewpager.VerticalPagerAdapter

class MainActivity : BaseViewBindingActivity<ActivityMainBinding>(), IPageState, IPageController, OnPageChangeListener {

    override fun initViewsAfterViewCreated() {
        super.initViewsAfterViewCreated()
        initViewPager()
    }

    private fun initViewPager() {
        val view = viewBinding ?: return
        val fragments = listOf(VideoPlayFragment(), VideoMatchFragment())
        fragments.forEach {
            it.setPageState(this)
            it.setPageController(this)
        }

        val titles = listOf("One", "Two")

        val adapter = VerticalPagerAdapter(supportFragmentManager, fragments, titles)
        view.viewPager.adapter = adapter
        view.viewPager.setCanScroll(false)
        view.viewPager.currentItem = fragments.size - 1
        view.viewPager.addOnPageChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

    override fun switchToVideoPlayPage() {
        viewBinding?.viewPager?.currentItem = 0
    }

    override fun switchToVideoMatchPage() {
        viewBinding?.viewPager?.currentItem = 1
    }

    override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
        // 忽略
    }

    override fun onPageSelected(index: Int) {
        viewBinding?.viewPager?.setCanScroll(index == 0)
    }

    override fun onPageScrollStateChanged(p0: Int) {
        // 忽略
    }

    override fun isContinuePlayOrMatch(): Boolean {
        return true
    }

}