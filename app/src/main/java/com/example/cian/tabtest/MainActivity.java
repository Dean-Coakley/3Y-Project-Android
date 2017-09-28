package com.example.cian.tabtest;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


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


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
