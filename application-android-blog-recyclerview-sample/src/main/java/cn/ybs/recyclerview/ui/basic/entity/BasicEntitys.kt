package cn.ybs.recyclerview.ui.basic.entity

import androidx.annotation.DrawableRes

class NormalEntity {

    @DrawableRes
    var resId: Int? = null
    var text: String? = null

    lateinit var type: NormalEntityType

    companion object {

        fun createForText(text: String): NormalEntity {
            return createForText(text, NormalEntityType.TEXT)
        }

        fun createForText(text: String, type: NormalEntityType): NormalEntity {
            val ret = NormalEntity()
            ret.text = text
            ret.type = type
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
    TEXT_FLEXBOX,
    IMAGE

}