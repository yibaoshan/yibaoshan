package cn.ybs.recyclerview.ui.stable

import android.content.Intent
import android.view.View
import cn.ybs.recyclerview.adapter.AsyncRecyclerViewAdapter
import cn.ybs.recyclerview.constans.Intents
import cn.ybs.recyclerview.entity.NormalEntity
import cn.ybs.recyclerview.ui.click.ItemClicksBaseItemTouchListenerActivity

/**
 *  author : <a href="https://yibs.space"/>
 *  e-mail : yibaoshan@foxmail.com
 *  time   : 2024/05/28
 */
class StableIDSettingActivity : ItemClicksBaseItemTouchListenerActivity() {

    override fun initIntentAfterViewCreated(intent: Intent) {
        val list = mutableListOf<NormalEntity>()
        val adapter = AsyncRecyclerViewAdapter(list)
        adapter.setHasStableIds(intent.getBooleanExtra(Intents.INTENT_KEY_STABLE_ID, false))
        super.initIntentAfterViewCreated(intent)
        list.addAll(listCache)
        recyclerView?.adapter = adapter
    }

    override fun onItemClick(view: View?, position: Int) {
        super.onItemClick(view, position)
        recyclerView?.adapter?.notifyDataSetChanged()
    }

}