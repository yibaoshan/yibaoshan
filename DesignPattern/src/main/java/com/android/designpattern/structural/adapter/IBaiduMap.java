package com.android.designpattern.structural.adapter;

/*百度地图*/
public interface IBaiduMap {

    BaiduMapParams getBaiduMapParams();

    class BaiduMapParams {

        public float longitude;//经度
        public float latitude;//纬度

    }

}
