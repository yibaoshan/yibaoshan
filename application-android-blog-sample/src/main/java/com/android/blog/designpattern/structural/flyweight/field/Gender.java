package com.android.blog.designpattern.structural.flyweight.field;

public enum Gender {

    MAN("男"), WOMAN("女"), UNKNOWN("不想透露");

    public String name;

    Gender(String name) {
        this.name = name;
    }

}
