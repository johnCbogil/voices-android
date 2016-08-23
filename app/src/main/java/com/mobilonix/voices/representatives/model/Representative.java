package com.mobilonix.voices.representatives.model;

/**
 * This model holds the representative info
 */
public class Representative {

    //private String gender;
    private String phoneNumber;
    private String twitterHandle;
    private String emailAddress;
    private String representativeImageUrl;
    private String title;
    private String name;
    private String location;

    //public String getGender() { return gender; }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getTwitterHandle() {
        return twitterHandle;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getRepresentativeImageUrl() {
        return representativeImageUrl;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getTitle() {
        return title;
    }

    public Representative(String title,
                          String name,
                          String location,
                          String phoneNumber,
                          String twitterHandle,
                          String emailAddress,
                          String representativeImageUrl) {

        this.title = title;
        this.name = name;
        this.location = location;
        //this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.twitterHandle = twitterHandle;
        this.emailAddress = emailAddress;
        this.representativeImageUrl = representativeImageUrl;

    }
}
