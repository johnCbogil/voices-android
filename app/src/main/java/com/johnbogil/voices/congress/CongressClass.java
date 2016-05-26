package com.johnbogil.voices.congress;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by chrislinder1 on 1/30/16.
 */
public class CongressClass implements Parcelable {

    public String first_name;
    public String last_name;
    public String title;
    public String term_end;
    public String phone;
    public String oc_email;
    public String twitter_id;
    public String bioguide_id;
    public String image_url;

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getBioguide_id() {
        return bioguide_id;
    }

    public void setBioguide_id(String bioguide_id) {
        this.bioguide_id = bioguide_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTerm_end() {
        return term_end;
    }

    public void setTerm_end(String term_end) {
        this.term_end = term_end;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOc_email() {
        return oc_email;
    }

    public void setOc_email(String oc_email) {
        this.oc_email = oc_email;
    }

    public String getTwitter_id() {
        return twitter_id;
    }

    public void setTwitter_id(String twitter_id) {
        this.twitter_id = twitter_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(first_name);
        dest.writeString(last_name);
        dest.writeString(title);
        dest.writeString(term_end);
        dest.writeString(phone);
        dest.writeString(oc_email);
        dest.writeString(twitter_id);
        dest.writeString(bioguide_id);


    }
    private CongressClass(Parcel in){
        first_name = in.readString();
        last_name = in.readString();
        title = in.readString();
        term_end = in.readString();
        phone = in.readString();
        oc_email = in.readString();
        twitter_id = in.readString();
        bioguide_id = in.readString();
    }

    public static final Creator<CongressClass> CREATOR =
            new Creator<CongressClass>() {
                @Override
                public CongressClass createFromParcel(Parcel source) {

                    return new CongressClass(source);
                }

                @Override
                public CongressClass[] newArray(int size) {
                    return new CongressClass[size];
                }
            };

    public CongressClass() {

    }
}
