package cn.ybs.recyclerview.ui.basic

import android.content.Intent
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.RecyclerListener
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import cn.ybs.core.base.BaseViewBindingActivity
import cn.ybs.recyclerview.R
import cn.ybs.recyclerview.constans.Intents
import cn.ybs.recyclerview.constans.Tags
import cn.ybs.recyclerview.databinding.ActivityBasicUsageExampleDetailBinding
import cn.ybs.recyclerview.ui.basic.adapter.BasicRecyclerAdapter
import cn.ybs.recyclerview.ui.basic.entity.NormalEntity
import cn.ybs.recyclerview.ui.basic.entity.NormalEntityType
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import java.lang.StringBuilder
import java.util.Random

/**
 * author : <a href="https://yibs.space"/>
 * e-mail : yibaoshan@foxmail.com
 * time   : 2024/05/19
 * desc   : 基础 RV 演示 demo
 */
open class BasicExampleOfUsingRecyclerViewDetailActivity : BaseViewBindingActivity<ActivityBasicUsageExampleDetailBinding>() {

    protected var listCache: MutableList<NormalEntity> = mutableListOf()

    override fun initIntentAfterViewCreated(intent: Intent) {

        val recyclerView = viewBinding?.recyclerView ?: return

        // 设置 ViewHolder 的回收监听器
        recyclerView.setRecyclerListener(ExampleRecyclerListener())

        when (intent.getStringExtra(Intents.INTENT_KEY_RECYCLER_VIEW_TYPE)) {
            Intents.INTENT_VALUE_VERTICAL_LINEAR_LAYOUT_TEXT -> {
                listCache = generateSingleTextData()
                recyclerView.adapter = BasicRecyclerAdapter(listCache)
                recyclerView.layoutManager = LinearLayoutManager(this)
            }

            Intents.INTENT_VALUE_VERTICAL_LINEAR_LAYOUT_IMAGE -> {
                listCache = generateSingleImageData()
                recyclerView.adapter = BasicRecyclerAdapter(listCache)
                recyclerView.layoutManager = LinearLayoutManager(this)
            }

            Intents.INTENT_VALUE_VERTICAL_LINEAR_LAYOUT_MULTI -> {
                listCache = generateMultiTypeData()
                recyclerView.adapter = BasicRecyclerAdapter(listCache)
                recyclerView.layoutManager = LinearLayoutManager(this)
            }

            Intents.INTENT_VALUE_VERTICAL_GRID_LAYOUT_TEXT -> {
                listCache = generateSingleTextData()
                recyclerView.adapter = BasicRecyclerAdapter(listCache)
                recyclerView.layoutManager = GridLayoutManager(this, 2)
            }

            Intents.INTENT_VALUE_VERTICAL_STAGGERED_LAYOUT_TEXT -> {
                listCache = generateMultiTypeData()
                recyclerView.adapter = BasicRecyclerAdapter(listCache)
                recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            }

            Intents.INTENT_VALUE_VERTICAL_FLEXBOX_LAYOUT_TEXT -> {
                listCache = generateSingleTextDataForFlexboxLayout()
                recyclerView.adapter = BasicRecyclerAdapter(listCache)
                recyclerView.layoutManager = FlexboxLayoutManager(this, FlexDirection.ROW)
            }
        }
    }

    private fun generateSingleTextData(): MutableList<NormalEntity> {
        val ret = mutableListOf<NormalEntity>()
        for (i in 0..100) ret.add(NormalEntity.createForText("text $i"))
        return ret
    }

    private fun generateSingleTextDataForFlexboxLayout(): MutableList<NormalEntity> {
        val ret = mutableListOf<NormalEntity>()
        for (i in 0..100) {
            val sb = StringBuilder("$i")
            val random = Random().nextInt(5)
            for (j in 0..random) sb.append(sb.toString())
            ret.add(NormalEntity.createForText("text $sb", NormalEntityType.TEXT_FLEXBOX))
        }
        return ret
    }

    private fun generateSingleImageData(): MutableList<NormalEntity> {
        val ret = mutableListOf<NormalEntity>()
        for (i in 0..100) ret.add(NormalEntity.createForImage(R.mipmap.ic_launcher))
        return ret
    }

    private fun generateMultiTypeData(): MutableList<NormalEntity> {
        val ret = mutableListOf<NormalEntity>()
        for (i in 0..100) {
            if (i % (Random().nextInt(5) + 1) == 0) ret.add(NormalEntity.createForImage(R.mipmap.ic_launcher))
            else ret.add(NormalEntity.createForText("text $i"))
        }
        return ret
    }

    private inner class ExampleRecyclerListener : RecyclerListener {
        override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
            Log.d(Tags.RECYCLER_VIEW, "onViewRecycled: ${holder.layoutPosition}")
        }

    }

}