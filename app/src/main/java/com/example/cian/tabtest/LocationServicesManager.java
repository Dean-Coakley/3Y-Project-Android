package com.example.cian.tabtest;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Alex on 01/10/2017.
 */


// a class used to keep track of the device's last-known coordinates
public class LocationServicesManager implements LocationListener {
    private Context currentContext;
    private LocationManager locMan;
    private Location lastLocation;

    // has the location manager been set up to send LocationServicesManager updates?
    private boolean updatesRequested = false;

    // pass the current application context to this class, if in the MainActivity, simply pass 'this'
    public LocationServicesManager(Context c){
        this.currentContext = c;
    }

    // Overrided methods will be called by LocationManager if it initialises correctly
    @Override
    public void onLocationChanged(Location location) {
        this.lastLocation = location;
        String message = "onLocationChanged: Lat: " + location.getLatitude() + ", Lon: " + location.getLongitude();
        Log.i("VCA", message);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public boolean locationManagerInit() {
        boolean initSuccess;

        // TL;DR :  if the permissions for fine location are not granted to the application then...
        if (ActivityCompat.checkSelfPermission(
                currentContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // this toast message should be replaced with a permissions prompt
            Toast.makeText(currentContext,
                    "Location Permissions need to be granted for location-based features.",
                    Toast.LENGTH_LONG).show();

            initSuccess = false;


        }
        // if permissions are granted then...
        else {
            //initialize a location manager
            this.locMan = (LocationManager) this.currentContext.getSystemService(Context.LOCATION_SERVICE);

            updatesRequested = setupUpdateRequests(this.locMan);
            initSuccess = updatesRequested;
        }

        return initSuccess;
    }

    // if location-permissions are granted but GPS is disabled, requestLocationUpdates is never called.
    // this method
    private boolean setupUpdateRequests(LocationManager lm){
        //returns true if location updates were successful
        try {
            boolean isGPSEnabled = this.locMan.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if(isGPSEnabled) {
                this.locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6000, 10, this);
                this.locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 6000, 10, this);
                this.locMan.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 6000, 10, this);
                return true;
            }else{
                Toast.makeText(currentContext,
                        "Please enable your device's GPS for location-based features",
                        Toast.LENGTH_LONG).show();
                return false;
            }

        }catch(SecurityException e){return false;}
    }

    //gets
    public Location getLastLocation(){
        // if locman is null, permissions need granting.
        // they may have been granted since last check, so re-init
        if (locMan == null) locationManagerInit();

        // if permissions or GPS-services were disabled,
        // location updates will not have been requested, so re-setup.
        if (!updatesRequested) {
            this.updatesRequested = setupUpdateRequests(this.locMan); }

        return this.lastLocation;
    }
}
