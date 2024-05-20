package cn.ybs.recyclerview

import android.content.Context
import android.content.Intent
import cn.ybs.recyclerview.constans.Intents
import cn.ybs.recyclerview.normal.NormalRecyclerViewActivity

/**
 * author : <a href="https://yibs.space"/>
 * e-mail : yibaoshan@foxmail.com
 * time   : 2024/05/19
 * desc   : 临时路由导航类
 */
object AppRouter {

    fun startTextVerticalLinearLayoutNormalRecyclerViewActivity(context: Context) {
        startNormalRecyclerViewActivity(context, Intents.INTENT_VALUE_VERTICAL_LINEAR_LAYOUT_TEXT)
    }

    fun startImageVerticalLinearLayoutNormalRecyclerViewActivity(context: Context) {
        startNormalRecyclerViewActivity(context, Intents.INTENT_VALUE_VERTICAL_LINEAR_LAYOUT_IMAGE)
    }

    fun startMultiTypeVerticalLinearLayoutNormalRecyclerViewActivity(context: Context) {
        startNormalRecyclerViewActivity(context, Intents.INTENT_VALUE_VERTICAL_LINEAR_LAYOUT_MULTI)
    }

    fun startTextVerticalGridLayoutNormalRecyclerViewActivity(context: Context) {
        startNormalRecyclerViewActivity(context, Intents.INTENT_VALUE_VERTICAL_GRID_LAYOUT_TEXT)
    }

    fun startMultiTypeVerticalStaggeredGridLayoutNormalRecyclerViewActivity(context: Context) {
        startNormalRecyclerViewActivity(context, Intents.INTENT_VALUE_VERTICAL_STAGGERED_LAYOUT_TEXT)
    }

    fun startTextVerticalFlexboxLayoutNormalRecyclerViewActivity(context: Context) {
        startNormalRecyclerViewActivity(context, Intents.INTENT_VALUE_VERTICAL_FLEXBOX_LAYOUT_TEXT)
    }

    private fun startNormalRecyclerViewActivity(context: Context, type: String) {
        val intent = Intent(context, NormalRecyclerViewActivity::class.java)
        intent.putExtra(Intents.INTENT_KEY_RECYCLER_VIEW_TYPE, type)
        context.startActivity(intent)
    }

}