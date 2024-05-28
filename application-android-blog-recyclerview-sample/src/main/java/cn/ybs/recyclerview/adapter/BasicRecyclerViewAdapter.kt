package cn.ybs.recyclerview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import cn.ybs.recyclerview.R
import cn.ybs.recyclerview.viewholder.BaseViewHolder
import cn.ybs.recyclerview.entity.NormalEntity
import cn.ybs.recyclerview.entity.NormalEntityType
import cn.ybs.recyclerview.viewholder.BasicRecyclerImageViewHolder
import cn.ybs.recyclerview.viewholder.BasicRecyclerTextViewHolder

/**
 * author : <a href="https://yibs.space"/>
 * e-mail : yibaoshan@foxmail.com
 * time   : 2024/05/19
 * desc   : RV 的数据适配器，控制使用何种 ViewHolder 来显示数据
 */
open class BasicRecyclerViewAdapter(data: MutableList<NormalEntity>) : BaseAdapter<NormalEntity, BaseViewHolder>(data) {

    // step 1, call this method get itemCount
    override fun getItemCount(): Int = data.size

    // step 2, call this method get itemViewType
    override fun getItemViewType(position: Int): Int {
        return data[position].type.ordinal
    }

    // step 3, create ViewHolder based on type(itemViewType)
    override fun onCreateViewHolder(viewGroup: ViewGroup, type: Int): BaseViewHolder {
        return when (type) {
            NormalEntityType.TEXT.ordinal -> BasicRecyclerTextViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.item_normal_recycler_view_text, viewGroup, false))
            NormalEntityType.IMAGE.ordinal -> BasicRecyclerImageViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.item_normal_recycler_view_image, viewGroup, false))
            NormalEntityType.TEXT_FLEXBOX.ordinal -> BasicRecyclerTextViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.item_normal_recycler_view_flexbox_text, viewGroup, false))
            else -> throw IllegalArgumentException("type is not supported")
        }
    }

    // step 4, bind data to ViewHolder
    override fun onBindViewHolder(vh: BaseViewHolder, position: Int) {
        super.onBindViewHolder(vh, position)
        val cur = data[position]
        if (vh is BasicRecyclerTextViewHolder) vh.bindData(cur.text!!)
        else if (vh is BasicRecyclerImageViewHolder) vh.bindData(cur.resId!!)
    }

}