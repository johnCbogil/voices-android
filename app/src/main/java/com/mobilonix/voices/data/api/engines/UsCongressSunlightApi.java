package com.mobilonix.voices.data.api.engines;

import android.content.res.Resources;
import android.os.Bundle;

import com.mobilonix.voices.R;
import com.mobilonix.voices.data.api.ApiEngine;
import com.mobilonix.voices.data.api.ApiUtil;
import com.mobilonix.voices.data.api.util.HttpRequestor;
import com.mobilonix.voices.data.api.util.UrlGenerator;
import com.mobilonix.voices.data.model.Politico;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UsCongressSunlightApi implements ApiUtil {

    public static final String BASE_URL = "https://congress.api.sunlightfoundation.com/legislators/locate";
    public static final String LATITUDE_KEY = "latitude";
    public static final String LONGITUDE_KEY = "longitude";
    public static final String API_KEY = "apikey";

    public static final String IMAGE_BASE_URL = "https://theunitedstates.io/images/congress/225x275/";
    public static final String IMAGE_FILE_EXTENSION = ".jpg";

    public static final String API_VALUE = "939e3373a07c468bac51ddd604ebba1f";

    public UsCongressSunlightApi() {}

    @Override
    public String generateUrl(double latitude, double longitude) {

        Bundle urlBundle = new Bundle();

        urlBundle.putString(LATITUDE_KEY, Double.toString(latitude));
        urlBundle.putString(LONGITUDE_KEY, Double.toString(longitude));
        urlBundle.putString(API_KEY, API_VALUE);

        UrlGenerator generator = new UrlGenerator(BASE_URL, urlBundle);

        return generator.generateGetUrlString();
    }

    @Override
    public ArrayList<Politico> parseData(String response) {
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

                String firstName = jsonPolitico.getString("first_name");
                String lastName = jsonPolitico.getString("last_name");
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

                politicos.add(politico);
            }
        } catch (JSONException e) {
            e.printStackTrace(); //TODO handle exception
        }

        return politicos;
    }
}