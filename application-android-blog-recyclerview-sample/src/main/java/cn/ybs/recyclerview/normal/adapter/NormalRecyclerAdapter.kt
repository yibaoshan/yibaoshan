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

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): BaseViewHolder {
        return when (data[position].type) {
            NormalEntityType.TEXT -> NormalRecyclerTextViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.item_normal_recycler_view_text, viewGroup, false))
            NormalEntityType.IMAGE -> NormalRecyclerImageViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.item_normal_recycler_view_image, viewGroup, false))
        }
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(vh: BaseViewHolder, position: Int) {
        val cur = data[position]
        when (cur.type) {
            NormalEntityType.TEXT -> (vh as NormalRecyclerTextViewHolder).bindData(cur.text!!)
            NormalEntityType.IMAGE -> (vh as NormalRecyclerImageViewHolder).bindData(cur.resId!!)
        }
    }

}