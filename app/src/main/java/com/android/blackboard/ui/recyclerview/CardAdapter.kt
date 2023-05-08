package com.android.blackboard.ui.recyclerview

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.widget.NestedScrollView
import androidx.core.widget.NestedScrollView.OnScrollChangeListener
import androidx.recyclerview.widget.RecyclerView
import com.android.blackboard.R

/**
 *  author : xiaobao
 *  e-mail : yibaoshan@foxmail.com
 *  time   : 2023/04/12
 *  desc   :
 */
class CardAdapter(val context: Context) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {
    private var datas = ArrayList<CardBean>()

    fun setDatas(datas: ArrayList<CardBean>) {
        this.datas = datas
        notifyDataSetChanged()
    }

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView? = null
        var textView: TextView? = null
        private val TAG = "CardAdapter"

        init {
            imageView = itemView.findViewById(R.id.iv_cover)
            textView = itemView.findViewById(R.id.tv_index)

            val view_progress_background =
                itemView.findViewById<View>(R.id.view_progress_background)
            val view_progress = itemView.findViewById<View>(R.id.view_progress)

            val scrollView = itemView.findViewById<NestedScrollView>(R.id.scrollView)
            scrollView.setOnScrollChangeListener(object : OnScrollChangeListener {
                @RequiresApi(Build.VERSION_CODES.N)
                override fun onScrollChange(
                    v: NestedScrollView?,
                    scrollX: Int,
                    scrollY: Int,
                    oldScrollX: Int,
                    oldScrollY: Int
                ) {

                    val childView = v?.getChildAt(0) ?: return


                    val processs =  scrollY * 1.0f / (childView.height - v.height)

                    Log.e(TAG, "onScrollChange: " + processs)

                    view_progress.translationY =
                        (view_progress_background.height - view_progress.height) * processs

                    val sb = StringBuilder()

                    sb.append("scrollY").append(scrollY).append(" , ")
                    sb.append("height").append(v.height).append(" , ")
                    sb.append("sum height").append(scrollY + v.height).append(" , ")
                    sb.append("child height").append(childView.height).append(" , ")
                    sb.append("processs").append(processs).append(" , ")

                    Log.e(TAG, "onScrollChange: " + sb.toString() )


                }
            })
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return CardViewHolder(LayoutInflater.from(context).inflate(R.layout.item, parent, false))
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.imageView?.setImageResource(datas[position].coverRes)
        holder.textView?.text = "$position"
    }

    override fun getItemCount() = datas.size
}