package com.android.designpattern.structural.adapter;

import java.util.Random;

public class BaiduMap implements IBaiduMap {

    private final BaiduMapParams params;

    public BaiduMap() {
        params = new BaiduMapParams();
        params.longitude = new Random().nextFloat();
        params.latitude = new Random().nextFloat();
    }

    @Override
    public BaiduMapParams getBaiduMapParams() {
        return params;
    }
}
