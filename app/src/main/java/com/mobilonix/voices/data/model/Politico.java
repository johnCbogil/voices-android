package com.mobilonix.voices.data.model;

import android.util.Log;

/**
 * Created by cakiadeg on 4/24/16.
 */
public class Politico {

    String mFullName;
    String mGender;
    String mDistrict;
    String mPhoneNumber;
    String mElectionDate;
    String mEmailAddy;
    String mTwitterHandle;
    String mPicUrl;

    private Politico(String fullName, String gender, String district,  String electionDate, String phoneNumber,
                     String emailAddy, String twitterHandle, String picUrl) {
        mFullName = fullName;
        mGender = gender;
        mDistrict = district;
        mPhoneNumber = phoneNumber;
        mElectionDate = electionDate;
        mEmailAddy = emailAddy;
        mTwitterHandle = twitterHandle;
        mPicUrl = picUrl;
    }

    public String getDistrict() { return mDistrict; }

    public String getFullName() {return mFullName;}

    public String getGender() {return mGender;}

    public String getPhoneNumber() { return mPhoneNumber; }

    public String getElectionDate() { return mElectionDate; }

    public String getEmailAddy() { return mEmailAddy; }

    public String getTwitterHandle() { return mTwitterHandle; }

    public String getPicUrl() { return mPicUrl; }

    @Override
    public String toString() {
        return  "\nFull Name: " + mFullName +
                "\nPhone Number: " + mPhoneNumber +
                "\nEmail Addy: " + mEmailAddy +
                "\nTwitter Handle: " + mTwitterHandle +
                "\nPic URL:" + mPicUrl;
    }

    public static class Builder {

        String fullName;
        String gender;
        String district;
        String electionDate;
        String phoneNumber;
        String emailAddy;
        String twitterHandle;
        String picUrl;

        public Politico build(String title, String firstName, String lastName) {

            fullName = title + " " + firstName + " " + lastName;

            Politico a = new Politico(fullName, gender, district, electionDate,
                    phoneNumber, emailAddy,twitterHandle, picUrl);
            Log.i("API",a.toString());
            return a ;
        }

        public Builder setGender(String gender) { this.gender = gender; return this; }

        public Builder setDistrict(String district) { this.district = district; return this; }

        public Builder setElectionDate(String electionDate) { this.electionDate = electionDate; return this; }

        public Builder setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; return this; }

        public Builder setEmailAddy(String emailAddy) { this.emailAddy = emailAddy; return this; }

        public Builder setTwitterHandle(String twitterHandle) { this.twitterHandle = twitterHandle; return this; }

        public Builder setPicUrl(String picUrl) { this.picUrl = picUrl; return this;}

        public Politico build(String fullName) {

            Politico a = new Politico(fullName, gender, district, electionDate,
                    phoneNumber, emailAddy,twitterHandle, picUrl);
            Log.i("API",a.toString());
            return a ;

        }
    }
}