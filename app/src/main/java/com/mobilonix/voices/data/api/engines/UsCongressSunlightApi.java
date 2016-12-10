package com.mobilonix.voices.data.api.engines;

import android.os.Bundle;
import android.util.Log;

import com.mobilonix.voices.data.api.ApiEngine;
import com.mobilonix.voices.data.api.util.UrlGenerator;
import com.mobilonix.voices.data.model.Politico;
import com.mobilonix.voices.representatives.RepresentativesManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Request;

public class UsCongressSunlightApi implements ApiEngine {

    public static final String BASE_URL = "https://congress.api.sunlightfoundation.com/legislators/locate";
    public static final String LATITUDE_KEY = "latitude";
    public static final String LONGITUDE_KEY = "longitude";
    public static final String API_KEY = "apikey";

    public static final String IMAGE_BASE_URL = "https://theunitedstates.io/images/congress/225x275/";
    public static final String IMAGE_FILE_EXTENSION = ".jpg";

    public static final String API_VALUE = "939e3373a07c468bac51ddd604ebba1f";

    public UsCongressSunlightApi() {
        Log.i("response","in sunlight" );
    }

    @Override
    public Request generateRequest(double latitude, double longitude) {

        Log.i("sunlight", "lat: " + latitude + "lon: " + longitude);

        Bundle urlBundle = new Bundle();

        urlBundle.putString(LATITUDE_KEY, Double.toString(latitude));
        urlBundle.putString(LONGITUDE_KEY, Double.toString(longitude));
        urlBundle.putString(API_KEY, API_VALUE);

        UrlGenerator generator = new UrlGenerator(BASE_URL, urlBundle);

        final Request recordRequest = new Request.Builder()
                .url(generator.generateGetUrl())
                .build();

        return recordRequest;
    }

    @Override
    public ArrayList<Politico> parseData(String response) {
        Log.i("response",response );
        return httpResponseToPoliticos(response);
    }

    private ArrayList<Politico> httpResponseToPoliticos(String response){


        ArrayList<Politico> politicos = new ArrayList<>();

        try {
            JSONObject rawJson = new JSONObject(response);
            JSONArray p = rawJson.getJSONArray("results");

            int count = rawJson.getInt("count");

            for (int i = 0; i < count; i++) {

                JSONObject jsonPolitico = (JSONObject) p.get(i);

                String firstName = jsonPolitico.optString("first_name");
                String lastName = jsonPolitico.optString("last_name");
                String title = jsonPolitico.optString("title") + ".";
                String gender = jsonPolitico.optString("gender");
                String party = jsonPolitico.optString("party");
                String district;
                if(jsonPolitico.optString("district").equals("null")){
                    district = "";
                } else {
                    district = jsonPolitico.optString("district");
                }
                String electionDate = setElectionDate(jsonPolitico.optString("term_end"));
                String phoneNumber = jsonPolitico.optString("phone");
                String twitter = jsonPolitico.optString("twitter_id");
                String bioguide_id = jsonPolitico.optString("bioguide_id");
                String email = jsonPolitico.optString("oc_email");
                Politico politico = new Politico.Builder()
                        .setGender(gender)
                        .setParty(party)
                        .setDistrict(district)
                        .setElectionDate(electionDate)
                        .setEmailAddy(email)
                        .setPhoneNumber(phoneNumber)
                        .setTwitterHandle(twitter)
                        .setPicUrl(IMAGE_BASE_URL + bioguide_id + IMAGE_FILE_EXTENSION)
                        .build(title, firstName, lastName);

                politicos.add(politico);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("TAG",e.getMessage());//TODO handle exception
        }

        return politicos;
    }

    public String setElectionDate(String termEnd) {
        if (termEnd.contains("2018")) {
            return "November 7, 2017";
        } else if (termEnd.contains("2019")) {
            return "November 6, 2018";
        } else if (termEnd.contains("2020")) {
            return "November 5, 2019";
        } else if (termEnd.contains("2021")){
            return "November 3, 2020";
        } else {
            return "N/A";
        }
    }
    @Override
    public RepresentativesManager.RepresentativesType getRepresentativeType() {
        return RepresentativesManager.RepresentativesType.CONGRESS;
    }
}