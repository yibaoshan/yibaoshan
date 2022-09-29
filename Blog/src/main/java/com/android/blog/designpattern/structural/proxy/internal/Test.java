package com.android.blog.designpattern.structural.proxy.internal;

import com.android.designpattern.structural.proxy.internal.IStack;
import com.android.designpattern.structural.proxy.internal.Stack;
import com.android.designpattern.structural.proxy.internal.StackDynamicProxy;
import com.android.designpattern.structural.proxy.internal.StackProxy;

public class Test {

    @org.junit.Test
    public void main() {
        IStack stack = new StackProxy(new Stack());
        stack.push(1);
        stack.push(2);
        stack.push(3);
        while (!stack.empty()) {
            System.out.println(stack.pop());
        }

        StackDynamicProxy proxy = new StackDynamicProxy();
        IStack iStack = (IStack) proxy.createProxy(new Stack());
        iStack.empty();

    }

}
