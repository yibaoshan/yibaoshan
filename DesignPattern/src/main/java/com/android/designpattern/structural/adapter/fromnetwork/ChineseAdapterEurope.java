package com.android.designpattern.structural.adapter.fromnetwork;

/**
 * Created by yibaoshan@foxmail.com on 2021/12/28
 * Description : If you have any questions, please contact me
 */
public class ChineseAdapterEurope extends EuropeSocketImpl implements ChineseSocket {

    @Override
    public String useChineseSocket() {
        System.out.println("使用转换器转换完成");
        return useEuropesocket();
    }
}