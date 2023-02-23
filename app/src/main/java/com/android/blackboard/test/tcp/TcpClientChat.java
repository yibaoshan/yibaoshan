package com.android.blackboard.test.tcp;

import org.junit.Test;

import java.io.*;
import java.net.Socket;
import java.util.Random;


public class TcpClientChat {

    @Test
    public void main() throws IOException, InterruptedException {
        int port = 8888;
        System.out.println("client started , port = " + port);
        //1. 建立连接，使用Socket创建客户端
        Socket client = new Socket("localhost", port);

        boolean isRunning = true;
        DataOutputStream outputStream = new DataOutputStream(client.getOutputStream());
        DataInputStream inputStream = new DataInputStream(client.getInputStream());

        String response = "";
        while (isRunning) {
            outputStream.writeUTF(getMessage());
            outputStream.flush();
            if (response.equals("哎 好嘞 ~")) continue;
            response = inputStream.readUTF();
            System.out.println("server ：" + response);
        }
        outputStream.close();
        inputStream.close();
        client.close();

    }

    private int flag = 0;

    public String getMessage() throws InterruptedException {
        Thread.sleep(new Random().nextInt(10 * 1000));
        switch (flag) {
            case 0:
                flag = 1;
                return "大爷你认识马冬梅吗？";
            case 1:
                flag = 2;
                return "马冬梅";
            case 2:
                flag = 3;
                return "马冬梅啊！~~";
            case 3:
                flag = 4;
                return "行了 大爷你歇着吧~";
        }
        flag = 0;
        return "...";
    }
}
