package com.android.blog.designpattern.structural.proxy.external;

import com.android.designpattern.structural.proxy.external.StackProxy;

import java.util.Stack;

public class Test {

    @org.junit.Test
    public void main() {
        Stack<Integer> stack = new StackProxy<>();
        stack.push(1);
        stack.push(2);
        stack.push(3);
        while (!stack.empty()) {
            System.out.println(stack.pop());
        }
    }

}
