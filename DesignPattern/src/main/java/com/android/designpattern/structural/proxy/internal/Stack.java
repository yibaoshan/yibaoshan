package com.android.designpattern.structural.proxy.internal;

import java.util.EmptyStackException;

public class Stack implements IStack {

    private Node root;
    private int size = 0;

    @Override
    public void push(int val) {
        root = new Node(val, root);
        size++;
    }

    @Override
    public int pop() {
        if (root != null) {
            Node temp = root;
            root = root.next;
            size--;
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

    private static final class Node {

        int val;
        Node next;

        public Node(int val, Node next) {
            this.val = val;
            this.next = next;
        }
    }
}
