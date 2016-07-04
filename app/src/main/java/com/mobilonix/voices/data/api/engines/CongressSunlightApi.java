package com.mobilonix.voices.data.api.engines;

import android.os.Bundle;
import android.util.Log;


import com.mobilonix.voices.data.api.ApiEngine;
import com.mobilonix.voices.data.api.util.HttpRequestor;
import com.mobilonix.voices.data.api.util.UrlConnectionRequestor;
import com.mobilonix.voices.data.api.util.UrlGenerator;
import com.mobilonix.voices.data.model.Politico;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class CongressSunlightApi implements ApiEngine {

    public static final String BASE_URL = "https://congress.api.sunlightfoundation.com/legislators/locate";
    public static final String LATITUDE_KEY = "latitude";
    public static final String LONGITUDE_KEY = "longitude";
    public static final String API_KEY = "apikey";
    public static final String API_KEY_KEY = ""; //TODO remove

    public static final String IMAGE_BASE_URL = "https://theunitedstates.io/images/congress/225x275/";
    public static final String IMAGE_FILE_EXTENSION = ".jpg";

    String mLatitude;
    String mLongitude;
    String mApiKey;

    HttpRequestor mRequestor;

    Bundle mUrlBundle;

    public CongressSunlightApi() {
        mUrlBundle = new Bundle();
    }

    @Override
    public void initialize(double latitude, double longitude, HttpRequestor requestor) {

        mRequestor = new UrlConnectionRequestor();
        mLatitude = Double.toString(latitude);
        mLongitude = Double.toString(longitude);

        // ---------------------------- Specific to Sunlight Api ----------------------------------

        mApiKey = API_KEY_KEY;

        mUrlBundle.putString(LATITUDE_KEY, mLatitude);
        mUrlBundle.putString(LONGITUDE_KEY, mLongitude);
        mUrlBundle.putString(API_KEY, mApiKey);
    }

    @Override
    public ArrayList<Politico> retrieveData() throws IOException {

        ArrayList<Politico> politicos;

        politicos = httpResponseToPoliticos(retrievePoliticos());

        return politicos;
    }

    private ArrayList<Politico> httpResponseToPoliticos(String response){

        try {
            JSONObject rawJson = new JSONObject(response);
            JSONArray p = rawJson.getJSONArray("results");

            int count = rawJson.getInt("count");

            for (int i = 0; i < count; i++) {

                JSONObject jsonPolitico = (JSONObject) p.get(i);

                String firstName = jsonPolitico.getString("first_name");
                String lastName = jsonPolitico.getString("first_name");
                String phoneNumber = jsonPolitico.getString("phone");
                String twitter = jsonPolitico.getString("twitter_id");
                String bioguide_id = jsonPolitico.getString("bioguide_id");
                String email = jsonPolitico.getString("oc_email");

                Politico politico = new Politico.Builder()
                        .setEmailAddy(email)
                        .setPhoneNumber(phoneNumber)
                        .setPicUrl(IMAGE_BASE_URL + bioguide_id + IMAGE_FILE_EXTENSION)
                        .setTwitterHandle(twitter)
                        .build(firstName, lastName);

                Log.i("sunlight", politico.toString());

            }
        } catch (JSONException e) {
            e.printStackTrace(); //TODO handle exception
        }

        return null;
    }

    private String sunlightRetrieveData() throws IOException {

        UrlGenerator generator = new UrlGenerator(BASE_URL, mUrlBundle);

        Log.i("sunlight", "request made");
        String response = mRequestor.makeGetRequest(generator.generateGetUrlString());

        Log.i("sunlight", response);

        return response;
    }

    private String retrievePoliticos() {
        try {
            return sunlightRetrieveData();
        } catch (IOException e) {
            e.printStackTrace();    //TODO handle exception
        }

        return null;
    }
}