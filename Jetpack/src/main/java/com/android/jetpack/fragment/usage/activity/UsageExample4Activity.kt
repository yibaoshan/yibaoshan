package com.android.jetpack.fragment.usage.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.android.jetpack.R
import com.android.jetpack.fragment.usage.fragment.LazyFragment

/**
 * Created by yibaoshan@foxmail.com on 2021/11/22
 * Description : ViewPager
 */
class UsageExample4Activity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "ViewPager"
        setContentView(R.layout.activity_usage_example3)
        init()
    }

    private fun init() {
        viewPager = findViewById(R.id.view_pager)
        val fragments = arrayListOf(
            LazyFragment.newInstance("ViewPager\nFragment1", resources.getColor(R.color.purple_200)),
            LazyFragment.newInstance("ViewPager\nFragment2", resources.getColor(R.color.purple_500)),
            LazyFragment.newInstance("ViewPager\nFragment3", resources.getColor(R.color.purple_700))
        )
        viewPager.adapter = PagerAdapter(supportFragmentManager, fragments)
        viewPager.currentItem = 0
    }

    fun onViewPager1Click(view: View?) {
        viewPager.currentItem = 0
    }

    fun onViewPager2Click(view: View?) {
        viewPager.currentItem = 1
    }

    fun onViewPager3Click(view: View?) {
        viewPager.currentItem = 2
    }

    private inner class PagerAdapter(fm: FragmentManager, val fragments: List<Fragment>) : FragmentStatePagerAdapter(fm) {

        override fun getCount(): Int = fragments.size

        override fun getItem(position: Int): Fragment = fragments[position]
    }

}