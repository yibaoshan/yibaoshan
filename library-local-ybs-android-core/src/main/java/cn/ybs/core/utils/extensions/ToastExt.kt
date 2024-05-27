package cn.ybs.core.utils.extensions

import android.content.Context
import android.widget.Toast
import cn.ybs.core.BuildConfig

fun String.toast(context: Context) {
    Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
}

fun String.toastLong(context: Context) {
    Toast.makeText(context, this, Toast.LENGTH_LONG).show()
}

fun String.toastDev(context: Context) {
    if (BuildConfig.DEBUG) toast(context)
}

fun String.toastLongDev(context: Context) {
    if (BuildConfig.DEBUG) toastLong(context)
}