package com.android.designpattern.structural.adapter;

import java.util.Random;

public class Map implements IMap {

    protected float longitude;//经度
    protected float latitude;//纬度

    public Map() {
        longitude = new Random().nextFloat();
        latitude = new Random().nextFloat();
    }

    @Override
    public float getMapLongitude() {
        return longitude;
    }

    @Override
    public float getMapLatitude() {
        return latitude;
    }
}
