package cn.ybs.core.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import cn.ybs.core.utils.extensions.getTClass

/**
 *  author : <a href="https://yibs.space"/>
 *  e-mail : yibaoshan@foxmail.com
 *  time   : 2024/04/03
 */
abstract class BaseViewBindingActivity<VB : ViewBinding> : BaseAppCompatActivity() {

    private var _binding: VB? = null

    protected val viewBinding: VB?
        get() = _binding

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflate = getTClass<VB>(javaClass).getDeclaredMethod("inflate", LayoutInflater::class.java)
        _binding = inflate.invoke(null, LayoutInflater.from(this)) as VB
        setContentView(requireNotNull(_binding).root)
        initViewsAfterViewCreated()
        initComponentsAfterCreate()
        initIntentAfterViewCreated(intent)
    }

    protected open fun requireViewBinding(): VB = requireNotNull(_binding)

    override fun requestInterceptInitViewsAfterCreate(): Boolean = true

    override fun requestInterceptInitComponentsAfterCreate(): Boolean = true

    override fun requestInterceptInitIntentAfterViewCreated(): Boolean = true

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}