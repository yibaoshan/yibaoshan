package com.android.blackboard;

public enum TypeEnum {
    Java("java"),
    Android("android"),
    Network("network"),
    VM("vm"),
    OS("os"),
    BOOK("book");

    private String name;

    TypeEnum(String name) {
        this.name = name;
    }
}
