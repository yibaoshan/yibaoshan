package com.android.designpattern.structural.adapter;

/*高德地图*/
public interface IAMap {

    AMapParams getAMapParams();

    class AMapParams {

        public float longitude;//经度
        public float latitude;//纬度

    }

}
