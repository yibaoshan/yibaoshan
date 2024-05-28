package cn.ybs.recyclerview.ui.click

import android.content.Intent
import android.view.View
import cn.ybs.core.utils.extensions.toast
import cn.ybs.recyclerview.viewholder.BaseViewHolder
import cn.ybs.recyclerview.ui.basic.BasicExampleOfUsingRecyclerViewDetailActivity
import cn.ybs.recyclerview.adapter.BasicRecyclerViewAdapter
import cn.ybs.recyclerview.entity.NormalEntity
import cn.ybs.recyclerview.ui.click.listeners.RecyclerItemClickListener.OnItemClickListener

/**
 *  author : <a href="https://yibs.space"/>
 *  e-mail : yibaoshan@foxmail.com
 *  time   : 2024/05/27
 *  desc   : 基于 View#onClickListener 实现的 item 点击
 */
class ItemClicksBaseClickListenerActivity : BasicExampleOfUsingRecyclerViewDetailActivity(), OnItemClickListener {

    override fun initIntentAfterViewCreated(intent: Intent) {
        super.initIntentAfterViewCreated(intent)
        recyclerView?.adapter = ItemClicksBaseClickListenerAdapter(listCache)
        // 如果你想在 Adapter#onBindViewHolder() 函数中设置点击事件，请注释此行代码
        // 因为，当你调用 addOnItemTouchListener() 设置了 item 级别的监听器以后，触摸事件将会被 RecyclerView 拦截，不会分发给子视图。
//        recyclerView.addOnItemTouchListener(RecyclerItemClickListener(recyclerView, this))
    }

    override fun onItemClick(view: View?, position: Int) {
        "click ${listCache[position].text}".toast(this)
    }

    override fun onLongItemClick(view: View?, position: Int) {
        "long click ${listCache[position].text}".toast(this)
    }

    private inner class ItemClicksBaseClickListenerAdapter(data: MutableList<NormalEntity>) : BasicRecyclerViewAdapter(data) {

        override fun onBindViewHolder(vh: BaseViewHolder, position: Int) {
            super.onBindViewHolder(vh, position)
            vh.itemView.rootView.setOnClickListener { "click $position from bind view holder".toast(vh.itemView.context) }
            vh.itemView.rootView.setOnLongClickListener {
                "long click $position from bind view holder".toast(vh.itemView.context)
                return@setOnLongClickListener true
            }
        }

    }

}