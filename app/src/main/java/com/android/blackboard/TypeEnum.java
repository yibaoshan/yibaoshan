package com.android.blackboard;

public enum TypeEnum {
    Java("java"),
    Android("android"),
    Network("network"),
    VM("vm");

    private String name;

    TypeEnum(String name) {
        this.name = name;
    }
}
