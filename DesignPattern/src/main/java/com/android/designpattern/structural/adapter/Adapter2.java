package com.android.designpattern.structural.adapter;

/**
 * Created by yibaoshan@foxmail.com on 2022/1/5
 * Description : If you have any questions, please contact me
 */
class Adapter2 extends Map implements IStatistics {

    @Override
    public float getStatisticsLongitude() {
        return getMapLongitude();
    }

    @Override
    public float getStatisticsLatitude() {
        return getMapLatitude();
    }
}
