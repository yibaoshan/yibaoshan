package com.android.designpattern.structural.adapter;

public class Statistics implements IStatistics{

    private float longitude;//经度
    private float latitude;//纬度
    private String IMEI;//imei
    private String packName;//包名

    public Statistics(float longitude, float latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public void doSomething() {

    }

    @Override
    public float getStatisticsLongitude() {
        return 0;
    }

    @Override
    public float getStatisticsLatitude() {
        return 0;
    }
}
