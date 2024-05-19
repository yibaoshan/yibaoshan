package cn.ybs.recyclerview.base

import androidx.recyclerview.widget.RecyclerView

/**
 *  author : <a href="https://yibs.space"/>
 *  e-mail : yibaoshan@foxmail.com
 *  time   : 2024/05/19
 */
abstract class BaseAdapter<T, VH : RecyclerView.ViewHolder>(val data: MutableList<T>) : RecyclerView.Adapter<VH>() {

}