package com.android.blackboard.ui.bitmap

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class BitmapActivity : AppCompatActivity() {

    private val TAG = "BitmapActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val info = ActivityManager.MemoryInfo()
        am.getMemoryInfo(info)

        /**
         * @see ActivityManager 单位 mb
         *
         * @memoryClass 虚拟机 java 堆大小的上限，分配对象时突破这个大小就会OOM
         * @largeMemoryClass manifest 中设置 largeheap=true 时虚拟机java堆的上限
         * */
        Log.e(TAG, "memoryClass: " + am.memoryClass + " MB")
        Log.e(TAG, "largeMemoryClass: " + am.largeMemoryClass + " MB")

        /**
         * @see Runtime 单位 byte
         *
         * @maxMemory 当前进程虚拟机实例的内存使用上限，为上述两者之一
         * @totalMemory 当前已经申请的内存，包括已经使用的和还没有使用的
         * @freeMemory 已经申请，但是还没使用的内存
         * */
        Log.e(TAG, "maxMemory: " + format(Runtime.getRuntime().maxMemory(), 2) + " MB")
        Log.e(TAG, "totalMemory: " + format(Runtime.getRuntime().totalMemory(), 2) + " MB")
        Log.e(TAG, "freeMemory: " + format(Runtime.getRuntime().freeMemory(), 2) + " MB")

        /**
         * @see ActivityManager.MemoryInfo 单位 byte
         *
         * @totalMem 设备总内存
         * @availMem 设备当前可用内存
         * @threshold 低内存的阈值 即区分是否低内存的临界值
         * @lowMemory 是否处于低内存状态
         * */
        Log.e(TAG, "MemoryInfo#totalMem: " + format(info.totalMem, 2) + " MB , " + format(info.totalMem, 3) + " GB")
        Log.e(TAG, "MemoryInfo#availMem: " + format(info.availMem, 2) + " MB , " + format(info.availMem, 3) + " GB")
        Log.e(TAG, "MemoryInfo#threshold: " + format(info.threshold, 2) + " MB , " + format(info.threshold, 3) + " GB")
        Log.e(TAG, "MemoryInfo#lowMemory: " + info.lowMemory)

        Log.e(TAG, "maxMemory: " + format(Runtime.getRuntime().maxMemory(), 2) + " MB")
    }

    private fun format(number: Long, pow: Int): String {
        var ret = number * 1.0
        for (index in 1..pow) ret /= 1024.0
        return String.format("%.2f", ret);
    }

}