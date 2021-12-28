package com.android.designpattern.structural.adapter.classsample;

public class TourGroup {

    public void checkIdentity(IPublicIDCard idCard) {
        System.out.println("姓名：" + idCard.getName() + ",ID：" + idCard.getID());
    }

}
