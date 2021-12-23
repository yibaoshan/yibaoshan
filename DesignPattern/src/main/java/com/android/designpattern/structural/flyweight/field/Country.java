package com.android.designpattern.structural.flyweight.field;

import java.util.HashMap;
import java.util.Map;

public class Country {

    private String countryName;//国家名称
    private String countryArea;//国土面积

    private final static Map<String, Country> mpCache = new HashMap<>();

    public static Country getCountry(String countryName, String countryArea) {
        if (mpCache.containsKey(countryName)) return mpCache.get(countryName);
        Country country = new Country(countryName, countryArea);
        mpCache.put(countryName, country);
        return country;
    }

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
