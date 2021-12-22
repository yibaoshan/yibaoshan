package com.android.designpattern.structural.flyweight;


import org.junit.Test;

public class Main {

    @Test
    public void main() {
        Message node = Message.obtain();
        node.setVal("I");
        print(node);
        node.recycle();
        print(node);
    }

    private void print(Message node) {
        System.out.println(node + "," + node.getVal());
    }

}
