package cn.ybs.recyclerview.viewholder

import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import cn.ybs.recyclerview.R
import java.util.Random

/**
 * author : <a href="https://yibs.space"/>
 * e-mail : yibaoshan@foxmail.com
 * time   : 2024/05/19
 * desc   : 延迟加载的 TextView
 */
open class BasicRecyclerAsyncTextViewHolder(itemView: View) : BaseViewHolder(itemView) {

    private val mTvContent: TextView by lazy { itemView.findViewById(R.id.tv_content) }

    private var updateTextRunnable: Runnable? = null

    private val handler = Handler(Looper.getMainLooper())

    open fun bindData(data: String) {
        updateTextRunnable = Runnable { mTvContent.text = data }
        handler.postDelayed(updateTextRunnable!!, Random().nextInt(3) * 1000L)
    }

    fun recycler() {
        updateTextRunnable?.let { handler.removeCallbacks(it) }
    }

}