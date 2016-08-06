package com.mobilonix.voices.data.api.engines;

import android.os.Bundle;
import android.util.Log;

import com.mobilonix.voices.data.api.ApiEngine;
import com.mobilonix.voices.data.api.util.UrlGenerator;
import com.mobilonix.voices.data.model.Politico;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Request;

public class StateOpenStatesApi implements ApiEngine {

    //  legislator strings

    public static final String BASE_URL = "http://openstates.org/api/v1//legislators/geo/";
    public static final String LATITUDE_KEY = "lat";
    public static final String LONGITUDE_KEY = "long";
    public static final String API_KEY = "apikey";

    public static final String API_VALUE = "e39ba83d7c5b4e348db144c4b4c33108";

    public StateOpenStatesApi() {

        Log.i("response","in openstates" );

    }

    @Override
    public Request generateRequest(double latitude, double longitude) {

        Bundle urlBundle = new Bundle();

        urlBundle.putString(LATITUDE_KEY, Double.toString(latitude));
        urlBundle.putString(LONGITUDE_KEY, Double.toString(longitude));
        urlBundle.putString(API_KEY, API_VALUE);

        UrlGenerator generator = new UrlGenerator(BASE_URL, urlBundle);

        Request recordRequest = new Request.Builder()
                .url(generator.generateGetUrl())
                .build();

        return recordRequest;
    }

    @Override
    public ArrayList<Politico> parseData(String response){

        Log.i("response",response );

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
}
