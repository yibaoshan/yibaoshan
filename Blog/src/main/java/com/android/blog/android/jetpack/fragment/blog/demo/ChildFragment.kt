package com.android.blog.android.jetpack.fragment.blog.demo

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment

/**
 * Created on 2022/11/3
 */
class ChildFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val button = AppCompatButton(context ?: return null)
        button.text = "跳转"
        Log.e("ChildFragment", "onCreateView: " )
        val intent = Intent(context, FragmentTestMainActivity::class.java)
        button.setOnClickListener {
            Handler().postDelayed({
                startActivity(intent)
            }, 2000)

        }
        return button
    }

}