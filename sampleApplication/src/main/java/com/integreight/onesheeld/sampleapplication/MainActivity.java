package com.integreight.onesheeld.sampleapplication;

/*
*  This is the first draft of the patient application.
*  It contains functionality for toggling heating and lighting.
*
*  OneSheeld digital pin-3 will be treated as the "heating pin"
*  OneSheeld digital pin-4 will be treated as the "lighting pin"
*/


//Am ghey


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.integreight.onesheeld.sdk.OneSheeldConnectionCallback;
import com.integreight.onesheeld.sdk.OneSheeldDevice;
import com.integreight.onesheeld.sdk.OneSheeldManager;
import com.integreight.onesheeld.sdk.OneSheeldScanningCallback;
import com.integreight.onesheeld.sdk.OneSheeldSdk;

public class MainActivity extends AppCompatActivity {

    // connected to a OneSheeld?
    private boolean connected = false;


    //GUI
    private Button scanButton;
    private Switch toggleLights;
    private Switch toggleHeating;
    private Button disconnectButton;

    //Sheeld
    private OneSheeldManager manager;
    private OneSheeldDevice sheeldDevice;

    private OneSheeldScanningCallback scanningCallback = new OneSheeldScanningCallback() {
        @Override
        public void onDeviceFind(OneSheeldDevice device){
            //cancel further scanning
            manager.cancelScanning();
            //connect to first-found oneSheeld
            device.connect();
        }
    };


    private OneSheeldConnectionCallback connectionCallback = new OneSheeldConnectionCallback() {
        @Override
        public void onConnect(OneSheeldDevice device) {
            sheeldDevice = device;

            // when a connection is established, enable device-specific buttons
            runOnUiThread(new Runnable() {@Override public void run()
                {
                    toggleHeating.setEnabled(true);
                    toggleLights.setEnabled(true);
                    disconnectButton.setEnabled(true);
                }
            });
        }

        public void onDisconnect(OneSheeldDevice device) {
            sheeldDevice = null;

            //when a disconnect occurs, make sure all device-specific buttons are disabled
            runOnUiThread(new Runnable() {@Override public void run()
            {
                toggleHeating.setEnabled(false);
                toggleLights.setEnabled(false);
            }
            });
        }
    };


    // --------------------------- ENTRY POINT ----------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setup - initialise SDK, then set connection retry-counts and timeouts
        OneSheeldSdk.init(this);
        OneSheeldSdk.setDebugging(true);
        manager = OneSheeldSdk.getManager();
        manager.setConnectionRetryCount(1);
        manager.setScanningTimeOut(5);
        manager.setAutomaticConnectingRetriesForClassicConnections(true);
        // add callback functions for handling connections / scanning
        manager.addConnectionCallback(connectionCallback);
        manager.addScanningCallback(scanningCallback);

        // GUI SETUP
        setupGUI();
    }


    //cleanly disconnect all devices if app is nearly destruction
    @Override
    protected void onDestroy(){
        manager.disconnectAll();
        manager.cancelConnecting();
        manager.cancelScanning();

        super.onDestroy();
    }


    // GUI SETUP
    public void setupGUI() {
        scanButton = (Button) findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            //cancel all existing scans / connections in progress befroe rescanning
            public void onClick(View v) {
                manager.cancelScanning();
                manager.cancelConnecting();
                manager.scan();
            }
        });

        //disconnects all devices
        disconnectButton = (Button) findViewById(R.id.disconnectButton);
        disconnectButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                manager.disconnectAll();
                disconnectButton.setEnabled(false);
                toggleHeating.setEnabled(false);
                toggleLights.setEnabled(false);
            }
        });

        // add heating toggling functionality
        toggleHeating = (Switch) findViewById(R.id.toggle_heating);
        toggleHeating.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // pin-3 is treated as the "heating" pin
                if (isChecked) sheeldDevice.digitalWrite(3, true);
                else sheeldDevice.digitalWrite(3, false);

            }
        });

        // add lighting toggling functionality
        toggleLights = (Switch) findViewById(R.id.toggle_lights);
        toggleLights.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // pin 4 is treated as the "lighting" pin
                if (isChecked) sheeldDevice.digitalWrite(4, true);
                else sheeldDevice.digitalWrite(4, false);

            }
        });

        // all device-specific buttons are disabled by default
        toggleHeating.setEnabled(false);
        toggleLights.setEnabled(false);
        disconnectButton.setEnabled(false);
    }


}