package cn.ybs.core.base.interfaces

import android.content.Intent

/**
 *  author : <a href="https://yibs.space"/>
 *  e-mail : yibaoshan@foxmail.com
 *  time   : 2024/05/19
 */
interface IInitData {

    fun initIntentBeforeViewCreate(intent: Intent) {
    }

    fun initIntentAfterViewCreated(intent: Intent) {
    }

}