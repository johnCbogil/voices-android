package com.mobilonix.voices.data.model;

public class Politico {

    String mFullName;
    String mParty;
    String mDistrict;
    String mTermEnd;
    String mElectionDate;
    String mPhoneNumber;
    String mTwitterHandle;
    String mContactForm;
    String mEmailAddress;
    String mPicUrl;
    String mLevel;
    String mBioguideId;

    public Politico(){

    }
    private Politico(String fullName, String party, String district, String termEnd, String electionDate,
                     String phoneNumber, String twitterHandle, String contactForm, String emailAddress,
                     String picUrl, String level, String bioguideId) {
        mFullName = fullName;
        mParty = party;
        mDistrict = district;
        mTermEnd = termEnd;
        mElectionDate = electionDate;
        mPhoneNumber = phoneNumber;
        mTwitterHandle = twitterHandle;
        mContactForm = contactForm;
        mEmailAddress = emailAddress;
        mPicUrl = picUrl;
        mLevel = level;
        mBioguideId = bioguideId;

    }

    public String getFullName() {return mFullName;}

    public String getParty() { return mParty; }

    public String getDistrict() { return mDistrict; }

    public String getTermEnd() { return mTermEnd; }

    public String getElectionDate() { return mElectionDate; }

    public String getPhoneNumber() { return mPhoneNumber; }

    public String getContactForm() { return mContactForm; }

    public String getEmailAddress(){
        return mEmailAddress;
    }

    public String getTwitterHandle() { return mTwitterHandle; }

    public String getPicUrl() { return mPicUrl; }

    public String getLevel(){
        return mLevel;
    }

    public String getBioguideId() {
        return mBioguideId;
    }

    @Override
    public String toString() {
        return  "\nFull Name: " + mFullName +
                "\nPhone Number: " + mPhoneNumber +
                "\nTwitter Handle: " + mTwitterHandle +
                "\nContact Form: " + mContactForm +
                "\nEmail Address: " + mEmailAddress +
                "\nPic URL:" + mPicUrl;
    }

    public static class Builder {

        String fullName;
        String party;
        String district;
        String termEnd;
        String electionDate;
        String phoneNumber;
        String twitterHandle;
        String contactForm;
        String emailAddress;
        String picUrl;
        String level;
        String bioguideId;

        public Politico build(String title, String firstName, String lastName) {

            fullName = title + " " + firstName + " " + lastName;

            Politico a = new Politico(fullName, party, district, termEnd, electionDate,
                    phoneNumber, twitterHandle, contactForm, emailAddress, picUrl, level, bioguideId);
            return a ;
        }

        public Builder setParty(String party) { this.party = party; return this; }

        public Builder setDistrict(String district) { this.district = district; return this; }

        public Builder setElectionClass (String electionClass){this.termEnd = electionClass; return this;}

        public Builder setElectionDate(String electionDate) { this.electionDate = electionDate; return this; }

        public Builder setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; return this; }

        public Builder setTwitterHandle(String twitterHandle) { this.twitterHandle = twitterHandle; return this; }

        public Builder setContactForm(String contactForm) { this.contactForm = contactForm; return this; }

        public Builder setEmailAddress(String emailAddress){
            this.emailAddress = emailAddress;
            return this;
        }

        public Builder setPicUrl(String picUrl) { this.picUrl = picUrl; return this;}

        public Builder setLevel(String level){
            this.level = level;
            return this;
        }

        public Builder setBioguideId(String bioguideId){
            this.bioguideId = bioguideId;
            return this;
        }

        public Politico build(String fullName) {

            Politico a = new Politico(fullName, party, district, termEnd, electionDate,
                    phoneNumber, twitterHandle, contactForm, emailAddress, picUrl, level, bioguideId);
            return a ;

        }
    }
}