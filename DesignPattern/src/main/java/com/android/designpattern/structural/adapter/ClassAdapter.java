package com.android.designpattern.structural.adapter;

public class ClassAdapter extends TencentMap implements IAMap {

    @Override
    public AMapParams getAMapParams() {
        AMapParams aMapParams = new AMapParams();
        TencentMapParams tencentMapParams = this.getTencentMapParams();
        aMapParams.aMapLongitude = tencentMapParams.tencentMapLongitude;
        aMapParams.aMapLatitude = tencentMapParams.tencentMapLatitude;
        return aMapParams;
    }
}
