package com.johnbogil.voices.district;

/**
 * Created by cakiadeg on 4/24/16.
 */
public class Politician {

    String fullName = "loading...";



    String district = "loading...";
    String phoneNumber = "loading...";
    String emailAddy = "loading...";
    String twitterHandle = "loading...";
    String picUrl = "loading...";

    public Politician(){}

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmailAddy() {
        return emailAddy;
    }

    public void setEmailAddy(String emailAddy) {
        this.emailAddy = emailAddy;
    }

    public String getTwitterHandle() {
        return twitterHandle;
    }

    public void setTwitterHandle(String twitterHandle) {
        this.twitterHandle = twitterHandle;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    @Override
    public String toString() {
        return "Full Name: " + fullName +
                "\nPhone Number: " + phoneNumber +
                "\nEmail Addy: " + emailAddy +
                "\nTwitter Handle: " + twitterHandle +
                "\nPic URL:" + picUrl;
    }
}