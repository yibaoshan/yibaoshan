package com.android.blog.android.jetpack.fragment.demo

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.blog.R

/**
 * Created on 2022/11/3
 */
class FragmentTestMainActivity : AppCompatActivity() {

    private val childFragment = ChildFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("TAG", "onCreate: " )
//        childFragment.retainInstance = true
        supportFragmentManager.beginTransaction().replace(R.id.frame_layout, childFragment).show(childFragment).commitNow();
        setContentView(R.layout.activity_fragment_test_main)
    }

    fun onReplaceFragment(view: View) {
        supportFragmentManager.beginTransaction().detach(childFragment).commitNow();
//        supportFragmentManager.beginTransaction().remove(childFragment).commitNow();
//        supportFragmentManager.beginTransaction().replace(R.id.frame_layout, ChildFragment()).commitNow();
//        supportFragmentManager.beginTransaction().remove(childFragment).replace(R.id.frame_layout, ChildFragment()).commitNow();
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        Log.e("TAG", "onSaveInstanceState: " )
    }

    fun onRecreateActivity(view: View) {
        recreate()
        finish()
    }

}