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

import static java.lang.Thread.sleep;

public class clientWIFIInTCP implements Runnable{

    // initialize socket and input output streams
    private Socket socket            = null;
    private DataOutputStream out     = null;

    public String serverIP;
    public int serverPort = -1;
    public String localIP;
    public int localPort = -1;
    public boolean isConnected = false;
    public String intrface;
    public boolean ready;

    //constructor
    public clientWIFIInTCP(String serverip, int serverPort, String localIP, int localPort, String intrface){
        this.serverIP = serverip;
        this.serverPort = serverPort;
        this.localIP = localIP;
        this.localPort = localPort;
        this.intrface = intrface;

    }





    //connects the socket and returns true if success
    public boolean connect(){

        if(socket==null){
            try {
                this.socket = new Socket();
                System.out.println("open socket success for " + intrface);
            }catch(Exception e){
                System.out.println("could not open socket for " + intrface);
                e.printStackTrace();
                return false;
            }

            try {
                socket.bind(new InetSocketAddress(localIP, localPort));
                System.out.println("bind socket success for " + intrface);
            }catch (IOException e){
                System.out.println("could not bind socket for " + intrface);
                e.printStackTrace();
                return false;
            }

            try{
                this.socket.connect(new InetSocketAddress(serverIP, serverPort));
                System.out.println("connect socket success for " + intrface);
            } catch (IOException e) {
                System.out.println("could not connect socket for " + intrface);
                e.printStackTrace();
                return false;
            }

            try {
                this.out = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                System.out.println("could not setup outputstream for " + intrface);
                e.printStackTrace();
            }


            if(socket!=null){
                isConnected = true;
                System.out.println("client Socket is connected for " + intrface);
                return true;
            }else{
                System.out.println("client Socket is not connected for " + intrface);
                return false;
            }
        }
        return false;
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

                //sleep
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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

    @Override
    public void run() {


        //connect
        ready = connect();


        if(ready) {
            int max = 10;
            int count = 0;

            while (true) {

                //message
                String message = intrface + " data from client with local ip " + localIP + " " + count;

                //send
                send(message);
                count++;

                //break
                //if (count == max) { break; }
                //if (count == max) { message = "Over"; }


            }
        }else{
            System.out.println("connection is not ready");
        }

    }

}
