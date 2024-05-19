package cn.ybs.recyclerview.normal

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ybs.core.base.BaseViewBindingActivity
import cn.ybs.recyclerview.R
import cn.ybs.recyclerview.constans.Intents
import cn.ybs.recyclerview.databinding.ActivityNormalRecyclerViewBinding
import cn.ybs.recyclerview.normal.adapter.NormalRecyclerAdapter
import cn.ybs.recyclerview.normal.entity.NormalEntity

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
                recyclerView.adapter = NormalRecyclerAdapter(generateDataForVerticalLinearSingleText())
                recyclerView.layoutManager = LinearLayoutManager(this)
            }

            Intents.INTENT_VALUE_VERTICAL_LINEAR_LAYOUT_IMAGE -> {
                recyclerView.adapter = NormalRecyclerAdapter(generateDataForVerticalLinearSingleImage())
                recyclerView.layoutManager = LinearLayoutManager(this)
            }

            Intents.INTENT_VALUE_VERTICAL_LINEAR_LAYOUT_MULTI -> {
                recyclerView.adapter = NormalRecyclerAdapter(generateDataForVerticalLinearMultiType())
                recyclerView.layoutManager = LinearLayoutManager(this)
            }
        }
    }

    private fun generateDataForVerticalLinearSingleText(): MutableList<NormalEntity> {
        val ret = mutableListOf<NormalEntity>()
        for (i in 0..100) ret.add(NormalEntity.createForText("text $i"))
        return ret
    }

    private fun generateDataForVerticalLinearSingleImage(): MutableList<NormalEntity> {
        val ret = mutableListOf<NormalEntity>()
        for (i in 0..100) ret.add(NormalEntity.createForImage(R.mipmap.ic_launcher))
        return ret
    }

    private fun generateDataForVerticalLinearMultiType(): MutableList<NormalEntity> {
        val ret = mutableListOf<NormalEntity>()
        for (i in 0..100) {
            if (i % 3 == 0) ret.add(NormalEntity.createForImage(R.mipmap.ic_launcher))
            else ret.add(NormalEntity.createForText("text $i"))
        }
        return ret
    }

}