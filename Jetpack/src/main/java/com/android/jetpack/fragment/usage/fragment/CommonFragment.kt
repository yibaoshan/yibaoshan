package com.android.jetpack.fragment.usage.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.android.jetpack.R

class CommonFragment : Fragment() {

    companion object {

        private var TAG = "CommonFragment"

        private const val KEY_TEXT = "KEY_TEXT"
        private const val KEY_COLOR = "KEY_COLOR"

        fun newInstance(text: String, bgColor: Int): Fragment {
            val fragment = CommonFragment()
            val bundle = Bundle()
            bundle.putString(KEY_TEXT, text)
            bundle.putInt(KEY_COLOR, bgColor)
            fragment.arguments = bundle
            return fragment
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val text = arguments?.getString(KEY_TEXT)
        if (text?.isNotEmpty() == true) TAG = "${text}Fragment"
        Log.d(TAG, "onAttach: ")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_common, container, false)
        val textView = view.findViewById<TextView>(R.id.tv_main)

        val text = arguments?.getString(KEY_TEXT)
        val bgColor = arguments?.getInt(KEY_COLOR, -1) ?: -1

        if (text != null) textView.text = arguments?.getString(KEY_TEXT)
        if (bgColor != -1) textView.setBackgroundColor(bgColor)

        Log.d(TAG, "onCreateView: ")
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(TAG, "onActivityCreated: ")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: ")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach: ")
    }

}