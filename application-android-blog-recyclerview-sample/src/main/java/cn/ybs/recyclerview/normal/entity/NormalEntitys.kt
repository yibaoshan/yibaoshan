package cn.ybs.recyclerview.normal.entity

import androidx.annotation.DrawableRes

class NormalEntity {

    @DrawableRes
    var resId: Int? = null
    var text: String? = null

    lateinit var type: NormalEntityType

    companion object {

        fun createForText(text: String): NormalEntity {
            val ret = NormalEntity()
            ret.text = text
            ret.type = NormalEntityType.TEXT
            return ret
        }

        fun createForImage(@DrawableRes resId: Int): NormalEntity {
            val ret = NormalEntity()
            ret.resId = resId
            ret.type = NormalEntityType.IMAGE
            return ret
        }

    }

}

enum class NormalEntityType {

    TEXT,
    IMAGE

}