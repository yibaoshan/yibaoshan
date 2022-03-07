package com.android.jetpack.fragment.result.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.android.jetpack.R
import com.android.jetpack.fragment.result.viewmodel.ResultViewModel
import java.util.*

class ResultExample2Fragment : Fragment() {

    private val viewModel: ResultViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_result_example2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnSend = view.findViewById<Button>(R.id.fragment_result_example2_btn_send)
        viewModel.resultLiveData.observe(viewLifecycleOwner, { str ->
            Toast.makeText(activity, str, Toast.LENGTH_SHORT).show()
        })
        btnSend.setOnClickListener {
            val random = Random().nextInt(99).toString()
            viewModel.resultLiveData.value = random
        }
    }

}