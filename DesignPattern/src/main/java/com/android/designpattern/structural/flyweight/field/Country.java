package com.android.designpattern.structural.flyweight.field;

public class Country {

    private String countryName;//国家名称
    private String countryArea;//国土面积

    public Country(String countryName, String countryArea) {
        this.countryName = countryName;
        this.countryArea = countryArea;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryArea() {
        return countryArea;
    }

    public void setCountryArea(String countryArea) {
        this.countryArea = countryArea;
    }
}
