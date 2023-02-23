package com.android.blackboard.test.udp;

import org.junit.Test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpServer {

    @Test
    public void main() throws IOException {
        DatagramSocket server = new DatagramSocket(9999);
        System.out.println("Server started ...");

        byte[] container = new byte[1024 * 60];
        DatagramPacket packet = new DatagramPacket(container, 0, container.length);
        server.receive(packet);
        byte[] datas = packet.getData();
        int len = packet.getLength();
        System.out.println(new String(datas, 0, len, "UTF-8"));

        server.close();
    }

}
