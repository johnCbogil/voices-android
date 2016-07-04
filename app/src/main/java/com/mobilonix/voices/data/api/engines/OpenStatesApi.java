package com.mobilonix.voices.data.api.engines;//package com.tryvoices.apis.api.engines;

import android.os.Bundle;

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
import java.util.List;

public class OpenStatesApi implements ApiEngine {

    //  legislator strings

    public static final String BASE_URL = "http://openstates.org/api/v1//legislators/geo/";
    public static final String LATITUDE_KEY = "lat";
    public static final String LONGITUDE_KEY = "long";
    public static final String API_KEY = "apikey";
    public static final String API_KEY_KEY = ""; //TODO REMOVE

    String mLatitude;
    String mLongitude;
    String mApiKey;

    HttpRequestor mRequestor;

    Bundle mUrlBundle;

    public OpenStatesApi() {
        mUrlBundle = new Bundle();
    }

    @Override
    public void initialize(double latitude, double longitude, HttpRequestor requestor) {

        mRequestor = new UrlConnectionRequestor();
        mLatitude = Double.toString(latitude);
        mLongitude = Double.toString(longitude);

        // ---------------------------- Specific to Congress Api ----------------------------------

        mApiKey = API_KEY_KEY;

        mUrlBundle.putString(LATITUDE_KEY, mLatitude);
        mUrlBundle.putString(LONGITUDE_KEY, mLongitude);
        mUrlBundle.putString(API_KEY, mApiKey);
    }


    private ArrayList<Politico> httpResponseToPoliticos(String response){

        ArrayList<Politico> politicos = new ArrayList<>();

        try {

            JSONArray rawJsonArray = new JSONArray(response);

            for(int i = 0; i < rawJsonArray.length(); i++){

                JSONObject jsonPolitico = rawJsonArray.getJSONObject(i);

                String fullName = jsonPolitico.getString("full_name");
                String email = jsonPolitico.getString("email");
                String phoneNumber = jsonPolitico.getJSONArray("offices").getJSONObject(0).getString("phone");
                String picUrl = jsonPolitico.getString("photo_url");

                Politico politico = new Politico.Builder()
                        .setEmailAddy(email)
                        .setPhoneNumber(phoneNumber)
                        .setPicUrl(picUrl)
                        .build(fullName);

                politicos.add(politico);
            }
        } catch (JSONException e) {

            e.printStackTrace(); //TODO handle exception
        }

        return politicos;
    }

    @Override
    public List<Politico> retrieveData() throws IOException {

        List<Politico> list = httpResponseToPoliticos(retrieveRawResponse());

        return list;
    }

    private String openStatesRetrieveData() throws IOException {

        UrlGenerator generator = new UrlGenerator(BASE_URL, mUrlBundle);

        String response = mRequestor.makeGetRequest(generator.generateGetUrlString());

        return response;
    }

    private String retrieveRawResponse() {
        try {
            return openStatesRetrieveData();
        } catch (IOException e) {
            e.printStackTrace();    //TODO handle exception
        }
        return null;
    }
}
