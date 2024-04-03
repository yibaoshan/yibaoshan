package com.android.blog.designpattern.structural.flyweight.field;

import java.util.HashMap;
import java.util.Map;

public class Country {

    private String countryName;//国家名称
    private String countryArea;//国土面积
    private String population;//人口总数

    private final static Map<String, Country> mpCache = new HashMap<>();

    private Country() {

    }

    private Country(String countryName, String countryArea, String population) {
        this.countryName = countryName;
        this.countryArea = countryArea;
        this.population = population;
    }

    public static Country query(String countryName) {
        if (!mpCache.containsKey(countryName)) {
            queryLatest();
        }
        return mpCache.get(countryName);
    }

    private static void queryLatest() {
        mpCache.put("China", new Country("中国(China)", "959.7万平方公里(2021)", "14.02亿(2020)"));
        mpCache.put("The USA", new Country("美国(The United States of America)", "983.4万平方公里(2021)", "3.295亿(2020)"));
    }

    public String getCountryName() {
        return countryName;
    }

    public String getCountryArea() {
        return countryArea;
    }

    public String getPopulation() {
        return population;
    }

    @Override
    public String toString() {
        return "Country{" +
                "countryName='" + countryName + '\'' +
                ", countryArea='" + countryArea + '\'' +
                ", population='" + population + '\'' +
                ", address='" + Integer.toHexString(hashCode()) + '\'' +
                '}';
    }
}
