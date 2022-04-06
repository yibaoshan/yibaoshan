package com.android.designpattern.structural.flyweight;

import org.junit.Test;

import java.util.Random;

public class MessageTest {

    @Test
    public void main() throws InterruptedException {
        Message eat = Message.obtain();
        eat.setVal("hahaha");

        new Thread(() -> {
            System.out.println(eat.getVal());
            eat.recycle();
        }).start();

        new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("print");
            System.out.println(eat.getVal());
            eat.recycle();
        }).start();

    }

    private void sendMessage(Message msg) {
        new Thread(() -> {
            System.out.println("sending msg:" + msg.getVal() + "(from:" + msg + ")");
            try {
                Thread.sleep(new Random().nextInt(5));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.err.println("msg:" + msg.getVal() + " sent successfully");
            msg.recycle();
        }).start();
    }

}
