package com.android.designpattern.structural.flyweight;

public class Message {

    private String val;
    private Message next;

    private static Message root;
    private static int size = 0;
    private static final int MAX_SIZE = 2;

    public static Message obtain() {
        synchronized (Message.class) {
            if (root != null) {
                Message temp = root;
                root = temp.next;
                temp.next = null;
                size--;
                return temp;
            }
        }
        return new Message();
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public void recycle() {
        this.val = null;
        synchronized (Message.class) {
            if (size < MAX_SIZE) {
                next = root;
                root = this;
                size++;
            }
        }
    }

}
