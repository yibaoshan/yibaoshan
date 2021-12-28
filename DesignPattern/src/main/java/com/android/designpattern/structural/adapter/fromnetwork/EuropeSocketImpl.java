package com.android.designpattern.structural.adapter.fromnetwork;

/**
 * Created by yibaoshan@foxmail.com on 2021/12/28
 * Description : If you have any questions, please contact me
 */
public class EuropeSocketImpl implements EuropeSocket {

    @Override
    public String useEuropesocket() {
        String msg ="使用欧式三叉充电";
        return msg;
    }
}