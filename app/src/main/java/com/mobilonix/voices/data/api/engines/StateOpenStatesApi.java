package com.mobilonix.voices.data.api.engines;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.mobilonix.voices.R;
import com.mobilonix.voices.VoicesApplication;
import com.mobilonix.voices.data.api.ApiEngine;
import com.mobilonix.voices.data.api.util.UrlGenerator;
import com.mobilonix.voices.data.model.Politico;
import com.mobilonix.voices.representatives.RepresentativesManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import okhttp3.Request;

public class StateOpenStatesApi implements ApiEngine {

    public static final String BASE_URL = "http://openstates.org/api/v1//legislators/geo/";
    public static final String LATITUDE_KEY = "lat";
    public static final String LONGITUDE_KEY = "long";
    public static final String API_KEY = "apikey";
    String state;

    public static final String API_VALUE = "a0c99640cc894383975eb73b99f39d2f";

    static final String TAG = "StateOpenStatesApi";

    public StateOpenStatesApi() {

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

        ArrayList<Politico> politicos = new ArrayList<>();

        try {

            JSONArray rawJsonArray = new JSONArray(response);

            for(int i = 0; i < rawJsonArray.length(); i++){

                JSONObject jsonPolitico = rawJsonArray.getJSONObject(i);

                state = jsonPolitico.optString("state");

                String chamber = jsonPolitico.optString("chamber");
                String title = setTitle(chamber);
                String fullName = title + jsonPolitico.getString("full_name");

                String gender = "";
                String party = jsonPolitico.optString("party");
                String level = "State";
                String district = jsonPolitico.optString("district");

                JSONObject rolesObject;
                String electionDate;
                JSONArray rolesArray = jsonPolitico.optJSONArray("roles");
                if(rolesArray!=null) {
                    rolesObject = rolesArray.optJSONObject(i);
                    if(rolesObject!=null) {
                        electionDate = setElectionDate(rolesObject.optString("end_date"));
                    } else {
                        electionDate = "";
                    }
                } else {
                    electionDate = "";
                }

                String phoneNumber = "";
                if (jsonPolitico.optJSONArray("offices").optJSONObject(0).has("phone")) {
                    phoneNumber = jsonPolitico.optJSONArray("offices").optJSONObject(0).getString("phone");
                } else if (jsonPolitico.getJSONArray("offices").optJSONObject(1).has("phone")) {
                    phoneNumber = jsonPolitico.optJSONArray("offices").optJSONObject(1).getString("phone");
                }

                String email = jsonPolitico.optString("email");

                String picUrl = jsonPolitico.optString("photo_url");

                Politico politico = new Politico.Builder()
                        .setGender(gender)
                        .setParty(party)
                        .setLevel(level)
                        .setDistrict(district)
                        .setElectionDate(electionDate)
                        .setEmailAddress(email)
                        .setPhoneNumber(phoneNumber)
                        .setPicUrl(picUrl)
                        .build(fullName);

                politicos.add(politico);
            }
            Politico gov = getGov(state);
            politicos.add(gov);
        } catch (JSONException e) {
            e.printStackTrace(); //TODO handle exception
        }

        return politicos;
    }

    public Politico getGov (String state) {
        Politico politico;
        try {
            JSONObject govs = getJsonFromResource(VoicesApplication.getContext(), R.raw.state_governors).getJSONObject("govs");
            JSONObject gov = govs.getJSONObject(state);
            String firstName = gov.optString("first_name");
            String lastName = gov.optString("last_name");
            String title = VoicesApplication.getContext().getResources().getString(R.string.gov_title);
            String gender = gov.optString("gender");
            String party = gov.optString("party");
            String level = "State";
            String district = gov.optString("state");
            String electionDate = gov.optString("next_election_date");
            String phoneNumber = gov.optString("phone");
            String email = gov.optString("email");
            String twitter = gov.optString("twitter");
            String photo = gov.optString("photo_url");
            politico = new Politico.Builder()
                .setGender(gender)
                .setParty(party)
                .setLevel(level)
                .setDistrict(district)
                .setElectionDate(electionDate)
                .setPhoneNumber(phoneNumber)
                .setTwitterHandle(twitter)
                .setContactForm("")
                .setEmailAddress(email)
                .setPicUrl(photo)

                .build(title, firstName, lastName);
        } catch (JSONException e) {
            Log.e(TAG, "json parse: " + e);
            politico = new Politico();
            return politico;
        }
        return politico;
    }

    public static JSONObject getJsonFromResource(Context context, int jsonResource)  {
        InputStream inputStream = context.getResources().openRawResource(jsonResource); // getting XML

        if(jsonResource > 0){

            try {
                JSONObject jsonObject = new JSONObject(convertStreamToString(inputStream));
                return jsonObject;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private static String convertStreamToString(InputStream inputStream) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace(); //TODO handle exception
        }
        return outputStream.toString();
    }

    public String setTitle(String chamber){
        if (chamber.equals("upper")) {
            return "Senator ";
        } else if (chamber.equals("lower")){
            return "Representative ";
        } else{
            return "";
        }
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
        return RepresentativesManager.RepresentativesType.STATE_LEGISLATORS;
    }
}
