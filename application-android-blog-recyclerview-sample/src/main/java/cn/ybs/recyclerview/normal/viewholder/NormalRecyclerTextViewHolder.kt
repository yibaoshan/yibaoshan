package cn.ybs.recyclerview.normal.viewholder

import android.view.View
import android.widget.TextView
import cn.ybs.recyclerview.R
import cn.ybs.recyclerview.base.BaseViewHolder

/**
 * author : <a href="https://yibs.space"/>
 * e-mail : yibaoshan@foxmail.com
 * time   : 2024/05/19
 * desc   : RV 的视图持有者 / 控制器，一般来说，所有对 View 的操作，都应该在 ViewHolder 中完成。
 */
class NormalRecyclerTextViewHolder(itemView: View) : BaseViewHolder(itemView) {

    private val mTvContent: TextView by lazy { itemView.findViewById(R.id.tv_content) }

    fun bindData(data: String) {
        mTvContent.text = data
    }

}