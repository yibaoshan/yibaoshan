package com.android.designpattern.structural.flyweight;

public class Message {

    private String val;
    private Message next;

    private static Message root;
    private static int size = 0;
    private static final int MAX_SIZE = 10;

    public static Message obtain() {
        if (root != null) {
            //获取链表表头的对象
            Message temp = root;
            root = temp.next;
            temp.next = null;
            size--;
            return temp;
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
        //回收对象，将属性内容清空
        this.val = null;
        //若缓存池还没满，将该对象保存至链表表头位置
        if (size < MAX_SIZE) {
            next = root;
            root = this;
            size++;
        }
    }

}
