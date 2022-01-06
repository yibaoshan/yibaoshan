package com.android.designpattern.structural.adapter;

/*腾讯地图*/
public interface ITencentMap {

    TencentMapParams getTencentMapParams();

    class TencentMapParams {

        public float tencentMapLongitude;//经度
        public float tencentMapLatitude;//纬度

    }

}
