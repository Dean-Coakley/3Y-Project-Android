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
}
