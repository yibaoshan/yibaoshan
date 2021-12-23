package com.android.designpattern.structural.flyweight;

import org.junit.Test;

import java.util.Random;

public class MessageTest {

    @Test
    public void main() throws InterruptedException {
        Message eat = Message.obtain();
        eat.setVal("let's go eat together");
        sendMessage(eat);

        Thread.sleep(new Random().nextInt(5));

        Message drink = Message.obtain();
        drink.setVal("let's go drink together");
        sendMessage(drink);

        Thread.sleep(new Random().nextInt(5));

        Message play = Message.obtain();
        play.setVal("let's go play together");
        sendMessage(play);

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
