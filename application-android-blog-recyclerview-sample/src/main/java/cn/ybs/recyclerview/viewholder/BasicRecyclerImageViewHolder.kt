package cn.ybs.recyclerview.viewholder

import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import cn.ybs.recyclerview.R
import cn.ybs.recyclerview.viewholder.BaseViewHolder

/**
 * author : <a href="https://yibs.space"/>
 * e-mail : yibaoshan@foxmail.com
 * time   : 2024/05/19
 * desc   : 图片类型的视图处理者。
 */
open class BasicRecyclerImageViewHolder(itemView: View) : BaseViewHolder(itemView) {

    private val mIvContent: ImageView by lazy { itemView.findViewById(R.id.iv_content) }

    fun bindData(@DrawableRes data: Int) {
        mIvContent.setImageResource(data)
    }

}