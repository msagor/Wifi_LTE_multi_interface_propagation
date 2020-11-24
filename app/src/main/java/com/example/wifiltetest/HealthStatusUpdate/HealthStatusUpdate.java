package com.example.wifiltetest.HealthStatusUpdate;

import com.example.wifiltetest.EdgeKeeper.EdgeKeeper;

import org.json.JSONException;
import org.json.JSONObject;

import edu.tamu.cse.lenss.edgeKeeper.client.EKClient;

public class HealthStatusUpdate implements Runnable {

    public HealthStatusUpdate(){}

    @Override
    public void run(){


        while (true) {
            put();
            get();
            Sleep(3000);

        }
    }

    public void put(){
        try {
            //make json app
            JSONObject mdfsHealth = new JSONObject();
            mdfsHealth.put("test_status", "Alive");
            mdfsHealth.put("test_network_status", "Good");

            //send
            EKClient.putAppStatus("TEST", mdfsHealth);
        }catch(JSONException e ){
            e.printStackTrace();
        }
    }


    public void get(){
        try {

            //get edge status
            try {
                System.out.println(EKClient.getEdgeStatus().toString(4));
            }catch(Exception e){
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void Sleep(int milliSec){
        try {
            Thread.sleep(milliSec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
