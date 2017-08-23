package com.mobilonix.voices.data.api.engines;

import android.util.Log;

import com.mobilonix.voices.R;
import com.mobilonix.voices.VoicesApplication;
import com.mobilonix.voices.data.api.ApiEngine;
import com.mobilonix.voices.data.api.util.NycCouncilGeoUtil;
import com.mobilonix.voices.data.model.Politico;
import com.mobilonix.voices.representatives.RepresentativesManager;
import com.mobilonix.voices.util.JsonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;

public class NycLocalOfficialsApi implements ApiEngine {

    //TODO: this is a temporary way to convert from lat/lon to address

    public NycCouncilGeoUtil geoUtil;

    public static final String BASE_URL = "http://www.greens.org/about/software/editor.txt";
    public static final String ADDRESS_KEY = "lookup_address";
    public static final String BOROUGH_KEY = "lookup_borough";

    public static final String POST_CONTENT_KEY = "Content-Type";
    public static final String POST_CONTENT_VALUE = "application/x-www-form-urlencoded";

    static final String TAG = "NycLocalOfficialsApi";

    double mLatitude, mLongitude;

    @Override
    public Request generateRequestForState(double latitude, double longitude) {

        mLatitude = latitude;
        mLongitude = longitude;

        geoUtil = new NycCouncilGeoUtil(latitude,longitude);

        String address = geoUtil.getAddressLine();
        String borough = geoUtil.getBorough();

        URL url;

        try {
            url = new URL(BASE_URL);
        } catch(MalformedURLException a) {
            url = null;
        }

        if(address != null && !borough.equals("0")) {
            Headers headers = new Headers.Builder()
                    .add(POST_CONTENT_KEY, POST_CONTENT_VALUE)
                    .build();

            RequestBody body = new FormBody.Builder()
                    .add(ADDRESS_KEY, address)
                    .add(BOROUGH_KEY, borough)
                    .build();

            Request recordRequest = new Request.Builder()
                    .url(url)
                    .headers(headers)
                    .post(body)
                    .build();

            return recordRequest;
        }
        return  null;
    }

    @Override
    public Request generateRequestForFederal(String address) {
        return null;
    }

    @Override
    public ArrayList<Politico> parseData(String response) throws IOException {

        ArrayList<Politico> politicos = getOtherReps();

        Politico politico = getLocalPolitician();

        if(politico != null) {
            politicos.add(politico);
        }
        return politicos;
    }

    public Politico getLocalPolitician() {

        int district = geoUtil.filterDistrict(mLatitude, mLongitude);

        try {
            JSONObject districts = JsonUtil.getJsonFromResource(R.raw.nyc_district_data);
            JSONObject member = districts.getJSONObject("districts");
            member = member.getJSONObject(district + "");

            String firstName = member.optString("firstName");
            String lastName = member.optString("lastName");
            String gender = "";
            String party = member.optString("party");
            String level = "Local";
            String repDistrict = member.optString("district");
            String electionDate = VoicesApplication.getContext().getResources().getString(R.string.nyc_election_date);
            String title = VoicesApplication.getContext().getResources().getString(R.string.nyc_title);
            String phoneNumbers = member.optString("phoneNumber");
            String photos = member.optString("photoURLPath");
            String twitter = member.optString("twitter");
            String email = member.optString("email");

            Politico politico = new Politico.Builder()
                    .setGender(gender)
                    .setParty(party)
                    .setLevel(level)
                    .setDistrict(repDistrict)
                    .setElectionDate(electionDate)
                    .setPhoneNumber(phoneNumbers)
                    .setEmailAddress(email)
                    .setPicUrl(photos)
                    .setTwitterHandle(twitter)
                    .build(title, firstName, lastName);

            return politico;

        } catch (JSONException e) {
            Log.e(TAG, "json parse: " + e);
            return null;
        }
    }

    public ArrayList<Politico> getOtherReps () {
        ArrayList<Politico> politicos;
        politicos = new ArrayList<Politico>();
        try {
            JSONObject reps = JsonUtil.getJsonFromResource(R.raw.nyc_reps).getJSONObject("reps");
            Iterator<?> keys = reps.keys();

            while (keys.hasNext()) {
                String key = (String) keys.next();
                JSONObject rep = reps.getJSONObject(key);
                String firstName = rep.optString("firstName");
                String lastName = rep.optString("lastName");
                String title = rep.optString("title");
                String gender = rep.optString("gender");
                String party = rep.optString("party");
                String level = "Local";
                String district = "";
                String electionDate = rep.optString("nextElection");
                String phoneNumber = rep.optString("phoneNumber");
                String photo = rep.optString("photoURLPath");
                String twitter = rep.optString("twitter");
                String email = rep.optString("email");
                Politico politico = new Politico.Builder()
                        .setGender(gender)
                        .setParty(party)
                        .setLevel(level)
                        .setDistrict(district)
                        .setElectionDate(electionDate)
                        .setEmailAddress(email)
                        .setPhoneNumber(phoneNumber)
                        .setPicUrl(photo)
                        .setTwitterHandle(twitter)
                        .build(title, firstName, lastName);
                politicos.add(politico);
            }
        } catch (JSONException e) {
            return politicos;
        }
        return politicos;
    }

    @Override
    public RepresentativesManager.RepresentativesType getRepresentativeType() {
        return RepresentativesManager.RepresentativesType.COUNCIL_MEMBERS;
    }
}
