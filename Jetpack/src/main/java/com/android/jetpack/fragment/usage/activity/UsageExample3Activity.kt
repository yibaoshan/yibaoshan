package com.android.jetpack.fragment.usage.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.android.jetpack.R
import com.android.jetpack.fragment.usage.fragment.CommonFragment

/**
 * Created by yibaoshan@foxmail.com on 2021/11/22
 * Description : 通过ViewGroup+FragmentManager
 */
class UsageExample3Activity : AppCompatActivity() {

    private lateinit var leftFragment: Fragment
    private lateinit var rightFragment: Fragment
    private lateinit var motionFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "FM(replace)"
        setContentView(R.layout.activity_usage_example2)
        init()
    }

    private fun init() {
        leftFragment = CommonFragment.newInstance("ViewGroup\nFragment1", resources.getColor(R.color.purple_200))
        rightFragment = CommonFragment.newInstance("ViewGroup\nFragment2", resources.getColor(R.color.purple_500))
        motionFragment = CommonFragment.newInstance("ViewGroup\nFragment3", resources.getColor(R.color.purple_700))

        supportFragmentManager.beginTransaction().replace(R.id.container, leftFragment).commit()
    }

    fun onViewGroup1Click(view: View?) {
        supportFragmentManager.beginTransaction().replace(R.id.container, leftFragment).commit()
    }

    fun onViewGroup2Click(view: View?) {
        supportFragmentManager.beginTransaction().replace(R.id.container, rightFragment).commit()
    }

    fun onViewGroup3Click(view: View?) {
        supportFragmentManager.beginTransaction().replace(R.id.container, motionFragment).commit()
    }

}