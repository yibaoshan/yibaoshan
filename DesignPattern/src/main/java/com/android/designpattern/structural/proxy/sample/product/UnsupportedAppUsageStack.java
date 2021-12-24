package com.android.designpattern.structural.proxy.sample.product;

import com.android.designpattern.structural.proxy.sample.IStack;

import java.util.EmptyStackException;
import java.util.Random;

public class UnsupportedAppUsageStack implements IStack {

    private Node root;
    private int size = 0;

    @Override
    public void push(int val) {
        root = new Node(val, root);
        size++;
        theGiveMoneyToOptimizeMethod();
    }

    @Override
    public int pop() {
        if (root != null) {
            Node temp = root;
            root = root.next;
            size--;
            theGiveMoneyToOptimizeMethod();
            return temp.val;
        }
        throw new EmptyStackException();
    }

    @Override
    public int peek() {
        if (root != null) {
            return root.val;
        }
        throw new EmptyStackException();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean empty() {
        return size == 0;
    }

    private void theGiveMoneyToOptimizeMethod() {
        try {
            Thread.sleep(new Random().nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static final class Node {

        int val;
        Node next;

        public Node(int val, Node next) {
            this.val = val;
            this.next = next;
        }
    }
}
