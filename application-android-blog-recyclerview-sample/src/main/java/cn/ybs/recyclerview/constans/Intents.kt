package cn.ybs.recyclerview.constans

object Intents {

    const val INTENT_KEY_RECYCLER_VIEW_TYPE = "intent_key_recycler_view_type"

    const val INTENT_VALUE_VERTICAL_LINEAR_LAYOUT_TEXT = "intent_value_vertical_linear_layout_text"

    const val INTENT_VALUE_VERTICAL_LINEAR_LAYOUT_IMAGE = "intent_value_vertical_linear_layout_image"

    const val INTENT_VALUE_VERTICAL_LINEAR_LAYOUT_MULTI = "intent_value_vertical_linear_layout_multi"

    enum class RecyclerViewType {
        VERTICAL_LINEAR_LAYOUT,
        HORIZONTAL_LINEAR_LAYOUT,
        GRID_LAYOUT,
        STAGGERED_GRID_LAYOUT
    }

}