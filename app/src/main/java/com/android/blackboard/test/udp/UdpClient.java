package com.android.blackboard.test.udp;

import org.junit.Test;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class UdpClient {

    @Test
    public void main() throws Exception {
        DatagramSocket client = new DatagramSocket(8888);
        System.out.println("client started ...");
        String data = "改革春风吹满地";
        byte[] datas = data.getBytes();

        DatagramPacket packet = new DatagramPacket(datas, 0, datas.length,
                new InetSocketAddress("localhost", 9999));

        client.send(packet);
        client.close();
    }

}
