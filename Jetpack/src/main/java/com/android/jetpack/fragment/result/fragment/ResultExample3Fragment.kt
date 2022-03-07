package com.android.jetpack.fragment.result.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.android.jetpack.R
import java.util.*

class ResultExample3Fragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_result_example3, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        view?.findViewById<Button>(R.id.fragment_result_example3_btn_send)?.setOnClickListener {
            val result = Random().nextInt(99).toString()
            parentFragmentManager.setFragmentResult("requestKey", bundleOf("bundleKey" to result))
        }

    }

}