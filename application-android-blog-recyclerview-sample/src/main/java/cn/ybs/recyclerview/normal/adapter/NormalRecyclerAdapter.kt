package cn.ybs.recyclerview.normal.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import cn.ybs.recyclerview.R
import cn.ybs.recyclerview.base.BaseAdapter
import cn.ybs.recyclerview.base.BaseViewHolder
import cn.ybs.recyclerview.normal.entity.NormalEntity
import cn.ybs.recyclerview.normal.entity.NormalEntityType
import cn.ybs.recyclerview.normal.viewholder.NormalRecyclerImageViewHolder
import cn.ybs.recyclerview.normal.viewholder.NormalRecyclerTextViewHolder

/**
 * author : <a href="https://yibs.space"/>
 * e-mail : yibaoshan@foxmail.com
 * time   : 2024/05/19
 * desc   : RV 的数据适配器，控制使用何种 ViewHolder 来显示数据
 */
class NormalRecyclerAdapter(data: MutableList<NormalEntity>) : BaseAdapter<NormalEntity, BaseViewHolder>(data) {

    override fun onCreateViewHolder(viewGroup: ViewGroup, type: Int): BaseViewHolder {
        return when (type) {
            NormalEntityType.TEXT.ordinal -> NormalRecyclerTextViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.item_normal_recycler_view_text, viewGroup, false))
            NormalEntityType.IMAGE.ordinal -> NormalRecyclerImageViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.item_normal_recycler_view_image, viewGroup, false))
            NormalEntityType.TEXT_FLEXBOX.ordinal -> NormalRecyclerTextViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.item_normal_recycler_view_flexbox_text, viewGroup, false))
            else -> throw IllegalArgumentException("type is not supported")
        }
    }

    override fun getItemCount(): Int = data.size

    override fun getItemViewType(position: Int): Int {
        return data[position].type.ordinal
    }

    override fun onBindViewHolder(vh: BaseViewHolder, position: Int) {
        val cur = data[position]
        if (vh is NormalRecyclerTextViewHolder) vh.bindData(cur.text!!)
        else if (vh is NormalRecyclerImageViewHolder) vh.bindData(cur.resId!!)
    }

}