package com.android.designpattern.structural.adapter;

import java.util.Random;

public class TencentMap implements ITencentMap {

    private final TencentMapParams params;

    public TencentMap() {
        params = new TencentMapParams();
        params.tencentMapLongitude = new Random().nextFloat();
        params.tencentMapLatitude = new Random().nextFloat();
    }

    @Override
    public TencentMapParams getTencentMapParams() {
        return params;
    }
}
