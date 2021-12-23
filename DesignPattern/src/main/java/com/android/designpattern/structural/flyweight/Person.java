package com.android.designpattern.structural.flyweight;

import com.android.designpattern.structural.flyweight.field.Country;
import com.android.designpattern.structural.flyweight.field.Gender;

public class Person {

    public int age;
    public String name;
    public Gender gender;
    public Country country;

    public Person(int age, String name, Gender gender, Country country) {
        this.age = age;
        this.name = name;
        this.gender = gender;
        this.country = country;
    }
}
