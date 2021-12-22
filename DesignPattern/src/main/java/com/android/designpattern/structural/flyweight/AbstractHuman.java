package com.android.designpattern.structural.flyweight;

import com.android.designpattern.structural.flyweight.field.Country;
import com.android.designpattern.structural.flyweight.field.Gender;

abstract class AbstractHuman {

    protected int age;
    protected String name;
    protected Gender gender;

    abstract Country getCountry();

}
