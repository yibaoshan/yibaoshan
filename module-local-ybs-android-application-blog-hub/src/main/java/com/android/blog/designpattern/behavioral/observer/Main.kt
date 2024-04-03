package com.android.blog.designpattern.behavioral.observer

import org.junit.Test

class Main {

    /**
     * 观察者模式
     * 亦称： 事件订阅者、监听者、Event-Subscriber、Listener、Observer
     * */

    @Test
    fun main() {
        ControlCenter.registerObserver(ObserverA())
        ControlCenter.registerObserver(ObserverB())
        ControlCenter.notifyObservers("eat")
    }

    class ObserverA : IObserver {

        override fun update(msg: String?) {
            println("A :$msg")
        }

    }

    class ObserverB : IObserver {

        override fun update(msg: String?) {
            println("B:$msg")
        }

    }

}