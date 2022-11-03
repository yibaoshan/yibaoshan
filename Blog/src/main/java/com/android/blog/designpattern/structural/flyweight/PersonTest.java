package com.android.blog.designpattern.structural.flyweight;



import com.android.blog.designpattern.structural.flyweight.field.Country;
import com.android.blog.designpattern.structural.flyweight.field.Gender;

import org.junit.Test;

public class PersonTest {

    @Test
    public void main() {
        testPerson();
    }

    private void testPerson() {
        Person xm = new Person(18, "小明", Gender.MAN, Country.query("China"));
        Person xh = new Person(18, "小红", Gender.WOMAN, Country.query("China"));
        Person xg = new Person(18, "小刚", Gender.UNKNOWN, Country.query("The USA"));
        print(xm);
        print(xh);
        print(xg);
    }

    private void print(Person person) {
        System.out.println("name=" + person.name
                + " , country=" + person.country
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
