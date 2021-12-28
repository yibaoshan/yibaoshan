package com.android.designpattern.structural.adapter.fromnetwork;

/**
 * Created by yibaoshan@foxmail.com on 2021/12/28
 * Description : If you have any questions, please contact me
 */
public class Computer {

    public String useChineseSocket(ChineseSocket chineseSocket) {
        if(chineseSocket == null) {
            throw new NullPointerException("sd card null");
        }
        return chineseSocket.useChineseSocket();
    }
}