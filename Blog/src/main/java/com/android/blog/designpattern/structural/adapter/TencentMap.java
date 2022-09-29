package com.android.blog.designpattern.structural.adapter;

import java.util.Random;

public class TencentMap {

    private final TencentMapParams params;

    public TencentMap() {
        params = new TencentMapParams();
        params.tencentMapLongitude = new Random().nextFloat();
        params.tencentMapLatitude = new Random().nextFloat();
    }

    public TencentMapParams getTencentMapParams() {
        return params;
    }

    public static class TencentMapParams {

        public float tencentMapLongitude;//经度
        public float tencentMapLatitude;//纬度

    }
}
