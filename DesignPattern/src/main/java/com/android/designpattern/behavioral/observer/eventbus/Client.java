package com.android.designpattern.behavioral.observer.eventbus;

import org.junit.Test;

import java.util.Random;

public class Client {

    @Test
    public void main() {
        LocalBroadcastManager.register(new Me());
        LocalBroadcastManager.register(new OneElse());
        while (true) {
            Random random = new Random();
            try {
                StringBuilder sb = new StringBuilder();
                if (random.nextInt() % 2 == 0) sb.append("茅台股涨了%").append(random.nextDouble());
                else sb.append("茅台股跌了%").append(random.nextDouble());
                LocalBroadcastManager.sendBroadcast(sb.toString());
                Thread.sleep(random.nextInt(10000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private static class UserInfoThread extends Thread {
        @Override
        public void run() {
            super.run();
            LocalBroadcastManager.register(new BroadcastReceiver() {
                @Override
                public void onReceive(Object obj) {
                    System.out.println(obj);
                }
            });
        }
    }

}
