package com.mobilonix.voices.data.model;

/**
 * Created by cakiadeg on 4/24/16.
 */
public class Politico {

    String mFullName;
    String mDistrict;
    String mPhoneNumber;
    String mElectionDate;
    String mEmailAddy;
    String mTwitterHandle;
    String mPicUrl;

    private Politico(String fullName, String district,  String electionDate, String phoneNumber,
                     String emailAddy, String twitterHandle, String picUrl) {
        mFullName = fullName;
        mDistrict = district;
        mPhoneNumber = phoneNumber;
        mElectionDate = electionDate;
        mEmailAddy = emailAddy;
        mTwitterHandle = twitterHandle;
        mPicUrl = picUrl;
    }

    public String getDistrict() { return mDistrict; }

    public String getFullName() {return mFullName;}

    public String getPhoneNumber() { return mPhoneNumber; }

    public String getElectionDate() { return mElectionDate; }

    public String getEmailAddy() { return mEmailAddy; }

    public String getTwitterHandle() { return mTwitterHandle; }

    public String getPicUrl() { return mPicUrl; }

    @Override
    public String toString() {
        return "Full Name: " + getFullName() +
                "\nPhone Number: " + mPhoneNumber +
                "\nEmail Addy: " + mEmailAddy +
                "\nTwitter Handle: " + mTwitterHandle +
                "\nPic URL:" + mPicUrl;
    }

    public static class Builder {

        String fullName;
        String district;
        String electionDate;
        String phoneNumber;
        String emailAddy;
        String twitterHandle;
        String picUrl;

        public Builder setDistrict(String district) { this.district = district; return this; }

        public Builder setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; return this; }

        public Builder setEmailAddy(String emailAddy) { this.emailAddy = emailAddy; return this; }

        public Builder setTwitterHandle(String twitterHandle) { this.twitterHandle = twitterHandle; return this; }

        public Builder setPicUrl(String picUrl) { this.picUrl = picUrl; return this;}

        public Politico build(String firstName, String lastName) {

            fullName = firstName + " " + lastName;

            return new Politico(fullName, district, electionDate,
                    phoneNumber, emailAddy,twitterHandle, picUrl);
        }

        public Politico build(String fullName) {

            return new Politico(fullName, district, electionDate,
                    phoneNumber, emailAddy,twitterHandle, picUrl);
        }
    }
}