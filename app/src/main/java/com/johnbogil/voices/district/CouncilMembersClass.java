package com.johnbogil.voices.district;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by chrislinder1 on 1/31/16.
 */
public class CouncilMembersClass implements Parcelable {

    public String full_name;
    public String phone;
    public String district;
    public String email;
    public int id;
    public String photo_url;

    Politician politician;

    public String getFull_name() {
        return full_name;
    }

    public  void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getPhone() {
        return phone;
    }

    public  void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDistrict() {
        return district;
    }

    public  void setDistrict(String district) {
        this.district = district;
    }

    public String getEmail() {
        return email;
    }

    public  void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public  void setId(int id) {
        this.id = id;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public CouncilMembersClass() {}

    public CouncilMembersClass(Parcel in) {
        full_name = in.readString();
        phone = in.readString();
        district = in.readString();
        email = in.readString();
        id = in.readInt();
        photo_url = in.readString();
    }

    public void setCouncilMembersClass(Politician politico) {

        full_name = politico.getFullName();
        phone = politico.getPhoneNumber();
        email = politico.getEmailAddy();
        photo_url = politico.getPicUrl();
    }

    public static final Creator<CouncilMembersClass> CREATOR = new Creator<CouncilMembersClass>() {
        @Override
        public CouncilMembersClass createFromParcel(Parcel in) {
            return new CouncilMembersClass(in);
        }

        @Override
        public CouncilMembersClass[] newArray(int size) {
            return new CouncilMembersClass[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(full_name);
        dest.writeString(phone);
        dest.writeString(district);
        dest.writeString(email);
        dest.writeInt(id);
        dest.writeString(photo_url);
    }
}
