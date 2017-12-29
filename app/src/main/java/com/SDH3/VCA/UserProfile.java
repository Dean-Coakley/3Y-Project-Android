package com.SDH3.VCA;

import android.location.Location;

public class UserProfile {
    private String fName = "Jeff";
    private String lName = "Nama";
    private String uID = "Jeff";
    private String CARER_ID = "Tomas";
    private int age = 1000;
    private String condition = "Alzheimer's";
    private double latitude = 50;
    private double longitude = 8;

    private double geofenceLatitude;
    private double geofenceLongitude;
    private double geofenceRadius;

    public String getCondition() {
        return condition;
    }
    public void setCondition(String condition) {
        this.condition = condition;
    }

    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }

    public String getFName(){
        return this.fName;
    }
    public String getLName(){
        return this.lName;
    }
    public void setFName(String fName) {
        this.fName = fName;
    }
    public void setLName(String lName){
        this.lName = lName;
    }

    public String getCARER_ID(){
        return this.CARER_ID;
    }
    public void setCARER_ID(String carerID){
        this.CARER_ID = carerID;
    }

    public double getLat(){
        return this.latitude;
    }
    public void setLat(double lat){
        this.latitude = lat;
    }
    public double getLong(){
        return this.longitude;
    }
    public void setLong(double lon){
        this.longitude = lon;
    }

    public String getuID() {
        return uID;
    }
    public void setuID(String uID) {
        this.uID = uID;
    }

    public double getGeofenceLatitude() {return geofenceLatitude;}
    public void setGeofenceLatitude(double geofenceLatitude) {this.geofenceLatitude = geofenceLatitude;}

    public double getGeofenceLongitude() {return geofenceLongitude;}
    public void setGeofenceLongitude(double geofenceLongitude) {this.geofenceLongitude = geofenceLongitude;}

    public double getGeofenceRadius() {return geofenceRadius;}
    public void setGeofenceRadius(double geofenceRadius) {this.geofenceRadius = geofenceRadius;}

    public boolean isPatientWithinGeofence(){
        // Pythagoras's Beard! The patient has escaped!
        double x1 = geofenceLatitude;
        double x2 = latitude;
        double y1 = geofenceLongitude;
        double y2 = longitude;

        // Formula: root(  sqr(x2 - x1) + sqr(y2 - y1) )
        double a = Math.pow( (x2 - x1) , 2.0 );
        double b = Math.pow( (y2 - y1) , 2.0 );
        double c = a + b; //What is this, the junior cert?

        double distanceFromOrigin = Math.sqrt(c);
        return distanceFromOrigin < geofenceRadius;
    }
}
