package com.example.wifiltetest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;

import com.example.wifiltetest.Client.TCP.clientLTEInTCP;
import com.example.wifiltetest.Client.TCP.clientWIFIInTCP;
import com.example.wifiltetest.Client.UDP.clientLTEInUDP;
import com.example.wifiltetest.Client.UDP.clientWIFIInUDP;
import com.example.wifiltetest.EdgeKeeper.EdgeKeeper;
import com.example.wifiltetest.HealthStatusUpdate.HealthStatusUpdate;

import org.apache.log4j.Level;

import java.io.IOException;

import edu.tamu.cse.lenss.edgeKeeper.utils.EKUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //must check permission
        checkPermissions();

        multiInterfaceUDP();

    }

    private static final int REQEUST_PERMISSION_GNSSERVICE = 22;
    private void checkPermissions() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQEUST_PERMISSION_GNSSERVICE);
            }
        }
    }

    //multi interface test in TCP
    public void multiInterfaceTCP(){

        //create LTE client
        new Thread( new clientLTEInTCP("172.21.0.129", 6000, "172.21.0.7", 1206, "LTE", getApplicationContext())).start();

        //create wifi client
        new Thread( new clientWIFIInTCP("192.168.1.131", 5000, "192.168.1.194", 1205, "WLAN0")).start();

    }

    //multi interface test in UDP
    public void multiInterfaceUDP(){

        //create LTE client
        new Thread(new clientLTEInUDP("172.21.0.129", 6000, "172.21.0.12", 1206, "LTE", getApplicationContext())).start();

        //create WIFI client
        new Thread(new clientWIFIInUDP("192.168.1.131", 5000, "192.168.1.160", 1205, "WLAN0")).start();

    }

    //cluster health monitor test
    public void clusterHealthMonitor(){
        //init log
        try {
            String path = Environment.getExternalStorageDirectory() + "/distressnet/TEST/test_log.log";
            EKUtils.initLogger(path, Level.ALL);
        } catch (IOException e) {
            System.out.println("Could not init log ");
        }

        //we need EdgeKeeper, so init EdgeKeeper first
        new EdgeKeeper();

        //start health status update thread
        new Thread(new HealthStatusUpdate()).start();
    }


}
