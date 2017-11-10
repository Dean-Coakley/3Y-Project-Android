package com.SDH3.VCA;

/**
 * Created by Alex on 10/11/2017.
 */

public class Business {
    private String name;
    private String website;
    private String phoneNumber;
    private String type;

    public Business(){
        name = null;
        website = null;
        phoneNumber = null;
        type = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
