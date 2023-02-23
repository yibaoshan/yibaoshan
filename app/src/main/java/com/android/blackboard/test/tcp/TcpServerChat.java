package com.android.blackboard.test.tcp;


import org.junit.Test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class TcpServerChat {

    @Test
    public void main() throws IOException {
        int port = 8888;
        ServerSocket server = new ServerSocket(port);
        while (true) {
            // 等待连接
            System.err.println("Server started , port = " + port + ", Wait for the connection..");
            Socket socket = server.accept();
            System.err.println("Server get a connection , create new thread for work..");
            new Thread(() -> {
                DataInputStream inputStream = null;
                DataOutputStream outputStream = null;
                try {
                    inputStream = new DataInputStream(socket.getInputStream());
                    outputStream = new DataOutputStream(socket.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                boolean isRunning = true;
                while (isRunning) {
                    try {
                        // 读取消息
                        String clientMessage = inputStream.readUTF();

                        System.out.println("client ：" + clientMessage);

                        if (clientMessage.equals("...")) {
                            System.out.println("\n\n------------------------------第 " + cnt + " 天-------------------------------\n\n");
                            continue;
                        }
                        String serverResponse = getResponse();
                        System.err.println("server ：" + serverResponse);
                        outputStream.writeUTF(serverResponse);
                        outputStream.flush();

                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    inputStream.close();
                    socket.close();
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private int flag = 0, cnt = 1;

    public String getResponse() throws InterruptedException {
        Thread.sleep(new Random().nextInt(10 * 1000));
        switch (flag) {
            case 0:
                flag = 1;
                return "马冬什么？？";
            case 1:
                flag = 2;
                return "什么冬梅啊？";
            case 2:
                flag = 3;
                return "马什么梅？";
        }
        flag = 0;
        cnt++;
        return "哎 好嘞 ~";
    }
}
