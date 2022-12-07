package com.android.notebook.笔记.资深带你破解Android高级;

/**
 * author :Bob.
 * date : 2020/10/29
 * desc :
 */
public class Java_3_6_onActivityResult为什么不设计成回调 {

    /*
     * @description: 内部类存有当前类的引用，会引发内存泄漏
     * 可以设计成回调，大概思路：
     *
     * 1.新建DummyFragment，activity传回调引用给fragment
     * 2.fragment调用activity的startActivityForResult打开activityB
     * 3.activityB结束后调用setResult
     * 4.fragment同样会收到onActivityResult，那么在第一步持有回调的引用回调给activity就可以了
     *
     * ps：
     * 1.activity销毁重建
     * 2.引用成员变量，因为成员变量会被直接放到构造方法里面，解决方法findViewById
     *
     */
    public void main(String[] args) {
        
    }
}
