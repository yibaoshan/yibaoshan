package com.android.jetpack.fragment.result.activity

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.jetpack.R
import com.android.jetpack.fragment.result.fragment.ResultExample1Fragment
import java.util.*

class ResultExample1Activity : AppCompatActivity(), ResultExample1Fragment.OnCallFatherListener {

    var fragment: ResultExample1Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_example1)
        title = "通信-接口&方法调用"
        init()
    }

    private fun init() {
        fragment = ResultExample1Fragment()
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment ?: return).commit()
    }

    fun onRandomClick(view: View) {
        val random = Random().nextInt(9999).toString()
        findViewById<EditText>(R.id.result_example1_activity_tv_content_edt_to_fragment)?.setText(random)
        fragment?.callChildFragment(random)
    }

    fun onSendToFragmentClick(view: View) {
        fragment?.callChildFragment(findViewById<EditText>(R.id.result_example1_activity_tv_content_edt_to_fragment)?.text?.toString() ?: "")
    }

    override fun callFatherActivity(s: String): String {
        findViewById<TextView>(R.id.result_example1_activity_tv_content)?.text = s
        return s
    }

}