package com.example.wifiltetest.Client.TCP;


// A Java program for a Client
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class clientLTEInTCP implements Runnable{

    // initialize socket and input output streams
    public Socket socket            = null;
    public DataOutputStream out     = null;

    public String serverIP;
    public int serverPort = -1;
    public String localIP;
    public int localPort = -1;
    public boolean isConnected = false;
    public String intrface;
    public boolean ready;
    Context context;

    //constructor
    public clientLTEInTCP(String serverip, int serverPort, String localIP, int localPort, String intrface, Context context){
        this.serverIP = serverip;
        this.serverPort = serverPort;
        this.localIP = localIP;
        this.localPort = localPort;
        this.intrface = intrface;
        this.context = context;
    }


    public void CM(){

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkRequest.Builder req = new NetworkRequest.Builder();

        req.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR);

        cm.requestNetwork(req.build(), new ConnectivityManager.NetworkCallback() {

            @Override
            public void onAvailable(Network network) {

                //make socket
                socket = new Socket();

                //open socket
                try {
                    socket = new Socket();
                    System.out.println("open socket success for " + intrface);
                } catch (Exception e) {
                    System.out.println("could not open socket for " + intrface);
                    e.printStackTrace();
                }

                //bind socket to local ip
                try {
                    socket.bind(new InetSocketAddress(localIP, localPort));
                    System.out.println("bind socket success for " + intrface);
                } catch (IOException e) {
                    System.out.println("could not bind socket to local IP for " + intrface);
                    e.printStackTrace();
                }

                // bind socket to this interface
                try {
                    network.bindSocket(socket);
                } catch (IOException e) {
                    System.out.println("could not bind socket to interface for " + intrface);
                    e.printStackTrace();
                }

                //connect
                try {
                    socket.connect(new InetSocketAddress(serverIP, serverPort));
                    System.out.println("connect socket success for " + intrface);
                } catch (IOException e) {
                    System.out.println("could not connect socket for " + intrface);
                    e.printStackTrace();
                }

                //output stream
                try {
                    out = new DataOutputStream(socket.getOutputStream());
                } catch (IOException e) {
                    System.out.println("could not setup outputstream for " + intrface);
                    e.printStackTrace();
                }

                //send
                int count = 0;
                while (true) {

                    //message
                    String message = intrface + " data from client with local ip " + localIP + " " + count;

                    //send
                    send(message);
                    count++;
                }
            }
        });
    }

    //send
    public void send(String line){

        if(socket.isConnected()) {
            try {
                out.writeUTF(line);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            while(!socket.isConnected()){

                //connect
                try {
                    socket.connect(new InetSocketAddress(serverIP, serverPort));
                    System.out.println("connect socket success for " + intrface);
                } catch (IOException e) {
                    System.out.println("could not connect socket for " + intrface);
                    e.printStackTrace();
                }

                //output stream
                try {
                    out = new DataOutputStream(socket.getOutputStream());
                } catch (IOException e) {
                    System.out.println("could not setup outputstream for " + intrface);
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void run() {
        CM();
    }

}
