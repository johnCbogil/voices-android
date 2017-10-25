package com.mobilonix.voices.representatives.model;

/**
 * This model holds the representative info
 */
public class Representative {

    private String gender;
    private String party;
    private String district;
    private String electionDate;
    private String phoneNumber;
    private String twitterHandle;
    private String emailAddress;
    private String contactForm;
    private String representativeImageUrl;
    private String title;
    private String name;
    private String location;
    private String level;


    public String getGender() { return gender; }

    public String getParty() { return party; }

    public String getDistrict() { return district; }

    public String getElectionDate() { return electionDate; }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmailAddress(){
        return emailAddress;
    }

    public String getContactForm() {
        return contactForm;
    }

    public String getTwitterHandle() {
        return twitterHandle;
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

    public String getLevel(){
        return level;
    }

    public Representative(String title,
                          String name,
                          String location,
                          String gender,
                          String party,
                          String district,
                          String electionDate,
                          String phoneNumber,
                          String twitterHandle,
                          String contactForm,
                          String emailAddress,
                          String representativeImageUrl,
                          String level) {

        this.title = title;
        this.name = name;
        this.location = location;
        this.gender = gender;
        this.party = party;
        this.district = district;
        this.electionDate = electionDate;
        this.phoneNumber = phoneNumber;
        this.twitterHandle = twitterHandle;
        this.contactForm = contactForm;
        this.emailAddress = emailAddress;
        this.representativeImageUrl = representativeImageUrl;
        this.level = level;
    }
}
