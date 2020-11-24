package com.example.wifiltetest.Client.UDP;

import android.content.Context;

import java.io.IOException;
import java.net.*;

public class clientWIFIInUDP implements Runnable{

    //global variables
    private DatagramSocket socket;
    private InetAddress serverAddr;
    private byte[] buf;
    private int serverPort;
    private String interrface;

    //constructor
    public clientWIFIInUDP(String serverip, int serverPort, String localIP, int localPort, String intrface) {

        //prepare and bind bind socket to local addr
        try {
            //socket = new DatagramSocket(new InetSocketAddress(localIP, localPort));
            InetAddress inetAddress = InetAddress.getByName(localIP);
            socket = new DatagramSocket(localPort,inetAddress);
            System.out.println("local IP bind for " + interrface + " success!");
        } catch (SocketException e) {
            e.printStackTrace();
            System.out.println("Could not bind to local socket");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        //prepare server address
        try {
            this.serverAddr = InetAddress.getByName(serverip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.out.println("Could not prepare server address");
        }

        this.serverPort = serverPort;
        this.interrface = intrface;
    }

    public void send(String msg) {

        //get message
        buf = msg.getBytes();

        //prepare packet
        DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddr, serverPort);

        //send
        try {
            socket.send(packet);
            System.out.println("data sent over wifi UDP");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not send data");
        }
    }

    public void close() {
        socket.close();
    }

    @Override
    public void run(){
        String msg = "Data from " + interrface + " client-";
        int count = 0;
        while(true){
            send(msg + count);
            count++;
        }
    }


}