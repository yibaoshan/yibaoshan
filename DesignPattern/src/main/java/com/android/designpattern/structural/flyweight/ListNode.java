package com.android.designpattern.structural.flyweight;

public class ListNode {

    private String val;
    private ListNode next;

    private static ListNode root;
    private static int size = 0;
    private static final int MAX_SIZE = 5;

    public static ListNode obtain() {
        if (root != null) {
            ListNode temp = root;
            root = temp.next;
            temp.next = null;
            size--;
            return temp;
        }
        return new ListNode();
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public boolean recycle() {
        if (size < MAX_SIZE) {
            next = root;
            root = this;
            size++;
            recycleUnchecked();
            return true;
        }
        return false;
    }

    private void recycleUnchecked() {
        this.val = null;
    }

}
