package cn.ybs.recyclerview.ui.click

import android.content.Intent
import android.view.View
import cn.ybs.core.utils.extensions.toast
import cn.ybs.recyclerview.ui.basic.BasicExampleOfUsingRecyclerViewDetailActivity
import cn.ybs.recyclerview.ui.click.listeners.RecyclerItemClickListener
import cn.ybs.recyclerview.ui.click.listeners.RecyclerItemClickListener.OnItemClickListener

/**
 *  author : <a href="https://yibs.space"/>
 *  e-mail : yibaoshan@foxmail.com
 *  time   : 2024/05/27
 *  desc   : 基于 RecyclerView.OnItemTouchListener 实现的 item 点击
 *
 * @see android.support.v7.widget.RecyclerView.OnItemTouchListener
 */
open class ItemClicksBaseItemTouchListenerActivity : BasicExampleOfUsingRecyclerViewDetailActivity(), OnItemClickListener {

    override fun initIntentAfterViewCreated(intent: Intent) {
        super.initIntentAfterViewCreated(intent)
        recyclerView?.addOnItemTouchListener(RecyclerItemClickListener(recyclerView, this))
    }

    override fun onItemClick(view: View?, position: Int) {
        "click ${listCache[position].text}".toast(this)
    }

    override fun onLongItemClick(view: View?, position: Int) {
        "long click ${listCache[position].text}".toast(this)
    }

}