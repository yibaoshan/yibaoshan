package cn.ybs.recyclerview.adapter

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import cn.ybs.recyclerview.constans.Tags

/**
 *  author : <a href="https://yibs.space"/>
 *  e-mail : yibaoshan@foxmail.com
 *  time   : 2024/05/19
 */
abstract class BaseAdapter<T, VH : RecyclerView.ViewHolder>(val data: MutableList<T>) : RecyclerView.Adapter<VH>() {

    private val viewHolderIdentifiers = hashSetOf<Int>()

    override fun onBindViewHolder(holder: VH, position: Int) {
        testAssertNotInLayoutOrScroll(position)
        val vhId = System.identityHashCode(holder)
        Log.d(Tags.RECYCLER_VIEW, "onBindViewHolder: vh= $holder, position= $position, isExist= ${viewHolderIdentifiers.add(vhId).not()}")
    }

    private fun testAssertNotInLayoutOrScroll(position: Int) {
        // 当 rv 处在 layout 或者 scroll 中调用数据更新时，抛出异常
//        if (position == 20) notifyDataSetChanged()
    }

}