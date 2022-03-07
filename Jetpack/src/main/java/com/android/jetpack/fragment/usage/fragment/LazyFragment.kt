package com.android.jetpack.fragment.usage.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.android.jetpack.R

class LazyFragment : Fragment() {

    //视图是否加载标识
    private var viewLoaded = false

    //数据是否加载标识
    private var dataLoaded = false

    companion object {

        private const val TAG = "LazyFragment"

        private const val KEY_TEXT = "KEY_TEXT"
        private const val KEY_COLOR = "KEY_COLOR"

        fun newInstance(text: String, bgColor: Int): Fragment {
            val fragment = LazyFragment()
            val bundle = Bundle()
            bundle.putString(KEY_TEXT, text)
            bundle.putInt(KEY_COLOR, bgColor)
            fragment.arguments = bundle
            return fragment
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_common, container, false)
        val textView = view.findViewById<TextView>(R.id.tv_main)
        val bgColor = arguments?.getInt(KEY_COLOR, -1) ?: -1
        if (bgColor != -1) textView.setBackgroundColor(bgColor)
        textView.text = "Loading"
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //走到该方法说明视图加载完成，此时getView()不为空，可以操作视图了
        viewLoaded = true
        //dataLoaded有两种情况：
        //1. true，说明fragment会被fm销毁重建，数据还在内存中，调用showView()恢复视图即可
        //2. false，说明是首次创建or低内存or配置变化等内存被释放，需要加载数据
        if (dataLoaded) showView()
        else lazyLoad()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) lazyLoad()
    }

    private fun lazyLoad() {
        if (dataLoaded || !userVisibleHint || !viewLoaded) return
        loadData()
    }

    private fun loadData() {
        //do something
        dataLoaded = true
        showView()
    }

    private fun showView() {
        val tv = view?.findViewById<TextView>(R.id.tv_main)
        tv?.text = (arguments?.getString(KEY_TEXT) + "\nLoaded")
    }

}