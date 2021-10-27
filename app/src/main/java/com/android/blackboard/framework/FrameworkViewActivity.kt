package com.android.blackboard.framework

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.android.blackboard.R

/**
 * Created by yibaoshan@foxmail.com on 2021/10/27
 */
class FrameworkViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_framework_view)
    }

    class MyTextView : androidx.appcompat.widget.AppCompatTextView {
        constructor(context: Context) : super(context) {}
        constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
        constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}
    }

    class MyFrameLayout : FrameLayout {
        constructor(context: Context) : super(context) {}
        constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
    }

    class MyRelativeLayout : RelativeLayout {
        constructor(context: Context) : super(context) {}
        constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
    }

}