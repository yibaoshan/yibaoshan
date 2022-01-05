package com.android.designpattern.structural.adapter;

public class Adapter extends Map {

    private Statistics statistics;

    public Statistics create() {
        if (statistics == null) {
            statistics = new Statistics(longitude, latitude);
        }
        return statistics;
    }

}
