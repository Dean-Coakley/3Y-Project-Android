package com.SDH3.VCA;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class GeoFenceService extends Service implements LocationListener {

    private LocationManager locationManager;
    private static UserProfile user;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.d(getPackageName(), "Handling Intent...");
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Log.d(getPackageName(), "Initializing Location services");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 10, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 10, this);
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 3000, 10, this);

            Log.d(getPackageName(), "Location services requested");
        }


        user = new UserProfile();
        user.setGeofenceRadius( Double.valueOf(intent.getStringExtra("geofence_radius")));
        user.setGeofenceLatitude( Double.valueOf(intent.getStringExtra("geofence_latitude")));
        user.setGeofenceLongitude( Double.valueOf(intent.getStringExtra("geofence_longitude")));
        Log.d(getPackageName(), "User profile initialized");


        return START_REDELIVER_INTENT;
    }

    @Override
    public void onLocationChanged(Location l) {
        Log.d(getPackageName(), "Location updated, checking geofence...");
        if (l != null) {
            double lon = l.getLongitude();
            double lat = l.getLatitude();
            user.setLat(lat);
            user.setLong(lon);

            // check if the user is within the geofence
            boolean isPatientWithinFence = user.isPatientWithinGeofence();
            if(!isPatientWithinFence){
                Log.d(getPackageName(), "Patient left geofence, sending notification...");
                //send notification
                alertPatientGeofence();
            }
        }
    }

    public void alertPatientGeofence(){
        Log.d(getPackageName(), "Preparing geofence alert notification...");

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.vca)
                .setContentTitle("VCA")
                .setContentText(getString(R.string.you_are_beyond_the_safe_distance_from_your_home));

        manager.notify(66, builder.build());
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
}
