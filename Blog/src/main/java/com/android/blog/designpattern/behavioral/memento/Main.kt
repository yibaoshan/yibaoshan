package com.android.designpattern.behavioral.memento

import android.graphics.Canvas

class Main {

    /**
     * 备忘录模式
     * 亦称： 快照、Snapshot、Memento
     * canvas
     * */

    fun main(){
        Canvas().save()
        Canvas().restore()
    }

}