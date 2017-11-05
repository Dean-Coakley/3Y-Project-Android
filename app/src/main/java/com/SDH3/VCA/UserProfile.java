package com.SDH3.VCA;

public class UserProfile {
    private String PASSWORD = "TEST";
    private String USERNAME = "Jeff";
    private String CARER_NAME = "Tomas";

    public String getUsername(){
        return this.USERNAME;
    }

    public String getPassword(){
        return this.PASSWORD;
    }

    public String getCARER_NAME(){
        return this.CARER_NAME;
    }

    public void setPassword(String pass){
        this.PASSWORD = pass;
    }

    public void setUsername(String un){
        this.USERNAME = un;
    }

    public void setCARER_NAME(String carerName){
        this.CARER_NAME = carerName;
    }

}
