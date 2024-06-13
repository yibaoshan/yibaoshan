package cn.ybs.recyclerview.layoutmanager

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager

/**
 *  author : <a href="https://yibs.space"/>
 *  e-mail : yibaoshan@foxmail.com
 *  time   : 2024/06/13
 *  desc   : 自定义 LinearLayoutManager Demo
 */
class FakeLinearLayoutManager : LayoutManager() {


    // step 1，必须要实现的函数，返回的 lp 将会设置给 item，一般都是 wrap_content
    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

}