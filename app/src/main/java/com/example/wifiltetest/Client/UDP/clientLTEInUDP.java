package com.example.wifiltetest.Client.UDP;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class clientLTEInUDP implements Runnable{

    //global variables
    private DatagramSocket socket;
    private InetAddress serverAddr;
    private byte[] buf;
    private String localIP;
    private int localPort;
    private String serverIP;
    private int serverPort;
    private String interrface;
    private Context context;

    //constructor
    public clientLTEInUDP(String serverip, int serverPort, String localIP, int localPort, String intrface, Context context) {

        this.localIP = localIP;
        this.localPort = localPort;
        this.serverIP = serverip;
        this.serverPort = serverPort;
        this.interrface = intrface;
        this.context = context;
    }

    public void send(String msg) {

        //get message
        buf = msg.getBytes();

        //prepare packet
        DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddr, serverPort);

        //send
        try {
            socket.send(packet);
            System.out.println("data sent over LTE UDP");
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
        CM();
    }

    private void CM() {


        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkRequest.Builder req = new NetworkRequest.Builder();

        req.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR);

        cm.requestNetwork(req.build(), new ConnectivityManager.NetworkCallback() {

            @Override
            public void onAvailable(Network network) {


                if(localIP!=null || localPort!=-1){

                    //prepare and bind socket to local addr
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
                }else{
                    try {
                        socket = new DatagramSocket();
                    } catch (SocketException e) {
                        e.printStackTrace();
                        System.out.println("could not init socket");
                    }
                }

                //bind socket to this network
                try {
                    network.bindSocket(socket);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("could not bid socket to this network.");
                }

                //prepare server address
                try {
                    serverAddr = InetAddress.getByName(serverIP);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    System.out.println("Could not prepare server address");
                }

                //send
                int count = 0;
                while (true) {

                    //message
                    String message = interrface + " data from client with local ip " + localIP + " " + count;

                    //send
                    send(message);
                    count++;
                }
            }
        });
    }

}
