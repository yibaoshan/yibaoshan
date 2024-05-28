package cn.ybs.recyclerview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import cn.ybs.recyclerview.R
import cn.ybs.recyclerview.viewholder.BaseViewHolder
import cn.ybs.recyclerview.entity.NormalEntity
import cn.ybs.recyclerview.viewholder.BasicRecyclerAsyncTextViewHolder

/**
 * author : <a href="https://yibs.space"/>
 * e-mail : yibaoshan@foxmail.com
 * time   : 2024/05/19
 * desc   : 启用稳定 ID 的 Adapter。
 */
class AsyncRecyclerViewAdapter(data: MutableList<NormalEntity>) : BasicRecyclerViewAdapter(data) {

    override fun onCreateViewHolder(viewGroup: ViewGroup, type: Int): BaseViewHolder {
        return BasicRecyclerAsyncTextViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.item_normal_recycler_view_text, viewGroup, false))
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(vh: BaseViewHolder, position: Int) {
        super.onBindViewHolder(vh, position)
        if (vh is BasicRecyclerAsyncTextViewHolder) vh.bindData(data[position].text!!)
    }

    override fun onViewRecycled(holder: BaseViewHolder) {
        super.onViewRecycled(holder)
        if (holder is BasicRecyclerAsyncTextViewHolder) holder.recycler()
    }

}