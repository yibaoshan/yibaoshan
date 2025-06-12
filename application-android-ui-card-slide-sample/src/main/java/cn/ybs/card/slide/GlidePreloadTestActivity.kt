package cn.ybs.card.slide

import cn.ybs.card.slide.databinding.ActivityGlidePreloadBinding
import cn.ybs.core.base.BaseViewBindingActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class GlidePreloadTestActivity : BaseViewBindingActivity<ActivityGlidePreloadBinding>() {

    private val imgUrl = "https://avatars.githubusercontent.com/u/25917729"

    override fun initListenersAfterViewCreated() {
        super.initListenersAfterViewCreated()
        val view = viewBinding ?: return
        view.btnPreloadNoArgs.setOnClickListener {
            // 调用 Glide 无参 preload() 方法，执行图片预加载
            Glide.with(this).load(imgUrl).preload()
        }
        view.btnPreloadMatchArgs.setOnClickListener {
            // 调用 指定宽高的 preload() 方法，并选择 CenterCrop 转换，和 xml 的 ImageView 配置保持相同
            Glide.with(this).load(imgUrl).optionalCenterCrop().preload(dpToPx(88f), dpToPx(88f))
        }
        view.btnLoad.setOnClickListener {
            // 尝试只从内存缓存中加载图片，忽略磁盘缓存
            Glide.with(this).load(imgUrl).diskCacheStrategy(DiskCacheStrategy.NONE).onlyRetrieveFromCache(true).into(view.ivAvatar)
        }
    }

    private fun dpToPx(dp: Float): Int {
        return (dp * resources.displayMetrics.density + 0.5f).toInt()
    }

}