package cn.ybs.core.base

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import cn.ybs.core.base.interfaces.IInitComponents
import cn.ybs.core.base.interfaces.IInitView

/**
 *  author : <a href="https://yibs.space"/>
 *  e-mail : yibaoshan@foxmail.com
 *  time   : 2024/04/03
 */
abstract class BaseAppCompatActivity : AppCompatActivity(), IInitView, IInitComponents {

    // 记录 Activity 是否暂停
    private var _isPaused: Boolean = false

    // 主线程 Handler
    private val _handler: Handler by lazy { Handler(Looper.getMainLooper()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!requestInterceptInitViewsAfterCreate()) {
            initViewsAfterCreate()
        }
        if (!requestInterceptInitComponentsAfterCreate()) {
            initComponentsAfterCreate()
        }
    }

    override fun onResume() {
        super.onResume()
        _isPaused = false
    }

    override fun onPause() {
        super.onPause()
        _isPaused = true
    }

    /**
     * @return 判断 Activity 是否暂停
     **/
    protected fun isPaused(): Boolean = _isPaused

    /**
     * 提交到主线程执行
     * */
    protected open fun post(r: Runnable) {
        _handler.post(r)
    }

    /**
     * 提交到主线程延迟执行
     * */
    protected open fun postDelayed(r: Runnable, delayMillis: Long) {
        _handler.postDelayed(r, delayMillis)
    }

    /**
     * 子类是否请求自行调用初始化视图函数
     * */
    protected open fun requestInterceptInitViewsAfterCreate(): Boolean = false

    /**
     * 子类是否请求自行调用初始化组件函数
     * */
    protected open fun requestInterceptInitComponentsAfterCreate(): Boolean = false


}