package com.android.blog.designpattern.structural.adapter;

public class AliPay {

    public boolean login(String id, String pwd, IAMap location) {
        System.out.println("id=" + id + " ,pwd=" + pwd + " ,location=[" + location.getAMapParams().aMapLongitude + "," + location.getAMapParams().aMapLatitude + "]");
        if (id.equals("admin") && pwd.equals("admin")) {
            return true;
        }
        return false;
    }

}
