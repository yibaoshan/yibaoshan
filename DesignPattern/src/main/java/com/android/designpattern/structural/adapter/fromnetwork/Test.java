package com.android.designpattern.structural.adapter.fromnetwork;

/**
 * Created by yibaoshan@foxmail.com on 2021/12/28
 * Description : If you have any questions, please contact me
 */
public class Test {

    @org.junit.Test
    public void main() {
        Computer computer = new Computer();

        ChineseSocket chineseSocket = new ChineseSocketImpl();
        System.out.println(computer.useChineseSocket(chineseSocket));

        ChineseAdapterEurope adapter = new ChineseAdapterEurope();
        System.out.println(computer.useChineseSocket(adapter));
    }

}
