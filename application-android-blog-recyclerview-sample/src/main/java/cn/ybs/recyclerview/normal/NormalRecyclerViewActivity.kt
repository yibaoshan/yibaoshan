package cn.ybs.recyclerview.normal

import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import cn.ybs.core.base.BaseViewBindingActivity
import cn.ybs.recyclerview.R
import cn.ybs.recyclerview.constans.Intents
import cn.ybs.recyclerview.databinding.ActivityNormalRecyclerViewBinding
import cn.ybs.recyclerview.normal.adapter.NormalRecyclerAdapter
import cn.ybs.recyclerview.normal.entity.NormalEntity
import cn.ybs.recyclerview.normal.entity.NormalEntityType
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
class NormalRecyclerViewActivity : BaseViewBindingActivity<ActivityNormalRecyclerViewBinding>() {

    override fun initIntentAfterViewCreated(intent: Intent) {

        val recyclerView = viewBinding?.recyclerView ?: return

        when (intent.getStringExtra(Intents.INTENT_KEY_RECYCLER_VIEW_TYPE)) {
            Intents.INTENT_VALUE_VERTICAL_LINEAR_LAYOUT_TEXT -> {
                recyclerView.adapter = NormalRecyclerAdapter(generateSingleTextData())
                recyclerView.layoutManager = LinearLayoutManager(this)
            }

            Intents.INTENT_VALUE_VERTICAL_LINEAR_LAYOUT_IMAGE -> {
                recyclerView.adapter = NormalRecyclerAdapter(generateSingleImageData())
                recyclerView.layoutManager = LinearLayoutManager(this)
            }

            Intents.INTENT_VALUE_VERTICAL_LINEAR_LAYOUT_MULTI -> {
                recyclerView.adapter = NormalRecyclerAdapter(generateMultiTypeData())
                recyclerView.layoutManager = LinearLayoutManager(this)
            }

            Intents.INTENT_VALUE_VERTICAL_GRID_LAYOUT_TEXT -> {
                recyclerView.adapter = NormalRecyclerAdapter(generateSingleTextData())
                recyclerView.layoutManager = GridLayoutManager(this, 2)
            }

            Intents.INTENT_VALUE_VERTICAL_STAGGERED_LAYOUT_TEXT -> {
                recyclerView.adapter = NormalRecyclerAdapter(generateMultiTypeData())
                recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            }

            Intents.INTENT_VALUE_VERTICAL_FLEXBOX_LAYOUT_TEXT -> {
                recyclerView.adapter = NormalRecyclerAdapter(generateSingleTextDataForFlexboxLayout())
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

}