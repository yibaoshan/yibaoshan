package com.android.designpattern.structural.flyweight;

public class People {

    private Gender gender;

    private String name;

    public class Gender {

        private String gender;

        public Gender(String gender) {
            this.gender = gender;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }
    }

}
