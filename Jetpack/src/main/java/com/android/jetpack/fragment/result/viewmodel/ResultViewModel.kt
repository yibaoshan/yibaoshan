package com.android.jetpack.fragment.result.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ResultViewModel : ViewModel() {

    val resultLiveData = MutableLiveData<String>()

}