package com.mobilonix.voices.data.api.engines;

import android.os.Bundle;
import android.util.Log;

import com.mobilonix.voices.VoicesApplication;
import com.mobilonix.voices.data.api.ApiEngine;
import com.mobilonix.voices.data.api.util.UrlGenerator;
import com.mobilonix.voices.data.model.Politico;
import com.mobilonix.voices.representatives.RepresentativesManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Request;

public class UsCongressSunlightApi implements ApiEngine {

    public static final String TAG = UsCongressSunlightApi.class.getCanonicalName();

    public static final String BASE_URL_FEDERAL = "https://www.googleapis.com/civicinfo/v2/representatives";
    public static final String ADDRESS_KEY = "address";
    public static final String API_KEY_FEDERAL = "key";

    public static final String IMAGE_BASE_URL = "https://theunitedstates.io/images/congress/225x275/";
    public static final String IMAGE_FILE_EXTENSION = ".jpg";

    /* TODO: Extract to config.xml */
    public static final String API_KEY_VALUE_FEDERAL = "AIzaSyD2DCNhYOdx8t2GsGbvlXd8nfVcugl4nK8";

    public UsCongressSunlightApi() {
    }

    @Override
    public Request generateRequestForFederal(String address){
        Bundle urlBundle = new Bundle();

        Log.e(TAG, "FEDERAL REQUEST ADDRESS: " + address);

        urlBundle.putString(ADDRESS_KEY, address);
        urlBundle.putString(API_KEY_FEDERAL, API_KEY_VALUE_FEDERAL);

        UrlGenerator generator = new UrlGenerator(BASE_URL_FEDERAL, urlBundle);

        Log.e(TAG, "API Request: " + generator.generateGetUrlString());

        final Request recordRequest = new Request.Builder()
                .url(generator.generateGetUrl())
                .build();

        return recordRequest;
    }

    @Override
    public Request generateRequestForState(double latitude, double longitude) {
        return null;
    }

    public ArrayList<Politico> parseData(String response) throws IOException  {
        ArrayList<Politico> politicos = new ArrayList<>();

        VoicesApplication.getGlobalHandler().post(new Runnable() {
            @Override
            public void run() {
            }
        });

        try {
            JSONObject rawJson = new JSONObject(response);
            JSONArray officeArray = rawJson.getJSONArray("offices");
            JSONArray indices = new JSONArray();
            ArrayList<Integer> integerArrayList = new ArrayList<Integer>();
            ArrayList<String> roleList = new ArrayList<String>();
            String levelName = null;
            String title = null;
            for(int i=0; i<officeArray.length(); i++) {
                JSONObject officeObject = officeArray.getJSONObject(i);
                Log.e(TAG, "Office object " + i + ": " + officeObject.toString());
                try {
                    levelName = officeObject.optJSONArray("levels").optString(0);
                    title = officeObject.optString("name");
                    JSONArray officialIndices = officeObject.optJSONArray("officialIndices");

                    if(!title.equals("President of the United States")
                            &&!title.equals("Vice-President of the United States")) {
                        if(title.contains("Senate")){
                            title = "Senator";
                        }
                        if(title.contains("Representatives")){
                            title = "Representative";
                        }
                        for(int j=0; j<officialIndices.length(); j++){
                            roleList.add(title);
                        }
                    }
                } catch (Exception e) {
                    levelName = "None";
                    title = "";
                }

                if(levelName.equals("country") || levelName.equals("federal")){
                    if(!title.equals("President of the United States")
                            &&!title.equals("Vice-President of the United States")) {
                        indices = officeObject.getJSONArray("officialIndices");
                        for (int j = 0; j < indices.length(); j++) {
                            integerArrayList.add(indices.getInt(j));
                        }
                    }
                }
            }

            Log.e(TAG, "Federal Official indices: " + integerArrayList);
            Log.e(TAG, "Federal Official indices Array: " + indices.toString());

            JSONArray officialsArray = rawJson.getJSONArray("officials");
            Log.e(TAG, "Federal Officials: " + officialsArray);

            for(int i=0; i < integerArrayList.size(); i++){
                JSONObject jsonPolitico = officialsArray.getJSONObject(integerArrayList.get(i));
                String roleTitle = roleList.get(i);
                String name;
                String party;
                String picUrl;

                try{
                    name = jsonPolitico.getString("name");
                } catch(NullPointerException e){
                    name = "";
                }

                try{
                    party = jsonPolitico.getString("party");
                } catch(NullPointerException e){
                    party = "";
                }

                String fullName = roleTitle + " " + name;

                String phone = "";
                if(jsonPolitico.optJSONArray("phones") != null) {
                    phone = jsonPolitico.optJSONArray("phones").getString(0);
                }

                String email = "";
                if(jsonPolitico.optJSONArray("emails") != null) {
                    phone = jsonPolitico.optJSONArray("emails").getString(0);
                }

                String twitter = safeGetArrayIndex(jsonPolitico, "channels", 0, "id");

                try{
                    picUrl = jsonPolitico.getString("photoUrl");
                } catch(NullPointerException e){
                    picUrl = "";
                }

                String level = "Federal";
                String district = "";
                String electionDate = "";

                Politico politico = new Politico.Builder()
                        .setLevel(level)
                        .setParty(party)
                        .setPhoneNumber(phone)
                        .setEmailAddress(email)
                        .setTwitterHandle(twitter)
                        .setPicUrl(picUrl)
                        .setDistrict(district)
                        .setElectionDate(electionDate)
                        .build(fullName);

                politicos.add(politico);
            }
        } catch (JSONException e) {

            Log.e(TAG, "JSON PARSING Exception: " + e.getMessage());
            e.printStackTrace();
        }
        return politicos;
    }

    public String safeGetArrayIndex(JSONObject object, String arrayKey, int index, String keyIndex) {
        String value = "";

        try {
            value = object.optJSONArray(arrayKey).optJSONObject(index).getString(keyIndex);
        } catch (Exception e) {
            value = "NONE";
        }

        return value;
    }

    @Override
    public RepresentativesManager.RepresentativesType getRepresentativeType() {
        return RepresentativesManager.RepresentativesType.CONGRESS;
    }
}