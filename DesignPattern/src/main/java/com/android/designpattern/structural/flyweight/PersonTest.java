package com.android.designpattern.structural.flyweight;


import com.android.designpattern.structural.flyweight.field.Country;
import com.android.designpattern.structural.flyweight.field.Gender;

import org.junit.Test;

public class PersonTest {

    @Test
    public void main() {
        testPerson();
    }

    private void testPerson() {
        Person xm = new Person(18, "小明", Gender.MAN, Country.getCountry("China", "9,597,000"));
        Person xh = new Person(18, "小红", Gender.WOMAN, Country.getCountry("China", "9,597,000"));
        Person xg = new Person(18, "小刚", Gender.UNKNOWN, Country.getCountry("USA", "9,834,000"));
        print(xm);
        print(xh);
        print(xg);
    }

    private void print(Person person) {
        System.out.println("name=" + person.name
                + " , country=" + person.country.getCountryName() + "(" + person.country + ")"
                + " , gender=" + person.gender.name
                + " , age=" + person.age
        );
    }

    private void testMessage() {
        Message node = Message.obtain();
    }

    private void print(Message node) {
        System.out.println(node + "," + node.getVal());
    }

}
