package com.mobilonix.voices.data.api.engines;

import android.os.Bundle;
import android.util.Log;

import com.mobilonix.voices.R;
import com.mobilonix.voices.VoicesApplication;
import com.mobilonix.voices.data.api.ApiEngine;
import com.mobilonix.voices.data.api.util.UrlGenerator;
import com.mobilonix.voices.data.model.Politico;
import com.mobilonix.voices.representatives.RepresentativesManager;
import com.mobilonix.voices.util.JsonUtil;

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
                //GeneralUtil.toast("Got Federal api response");
            }
        });

        //Log.e(TAG, "FEDERAL API RESPONSE: " + response);

        try {
            JSONObject rawJson = new JSONObject(response);
            JSONArray officeArray = rawJson.getJSONArray("offices");
            JSONArray indices = new JSONArray();
            ArrayList<Integer> integerArrayList = new ArrayList<Integer>();
            for(int i=0; i<officeArray.length(); i++) {
                JSONObject officeObject = officeArray.getJSONObject(i);
                Log.e(TAG, "Office object " + i + ": " + officeObject.toString());

                String level = null;
                String name = null;

                try {
                    level = officeObject.optJSONArray("levels").optString(0);
                    name = officeObject.optString("name");
                } catch (Exception e) {
                    level = "None";
                    name = "None";
                }

                if(level.equals("country") || level.equals("federal")){
                    if(!name.equals("President of the United States")
                            &&!name.equals("Vice-President of the United States")) {
                        indices = officeObject.getJSONArray("officialIndices");
                        for (int j = 0; j < indices.length(); j++) {
                            integerArrayList.add(indices.getInt(j));
                        }
                    }
                }
            }

//            try {
//                phone = jsonPolitico.optJSONArray("phones").optJSONObject(0).getString("phones");
//            } catch (Exception e) {
//                phone = "NONE";
//            }

            Log.e(TAG, "Federal Official indices: " + integerArrayList);
            Log.e(TAG, "Federal Official indices Array: " + indices.toString());

            JSONArray officialsArray = rawJson.getJSONArray("officials");
            Log.e(TAG, "Federal Officials: " + officialsArray);
            for(int i=0; i < integerArrayList.size(); i++){
                JSONObject jsonPolitico = officialsArray.getJSONObject(integerArrayList.get(i));
                String name = jsonPolitico.optString("name");
                String party = jsonPolitico.optString("party");

                String phone = "";
                if(jsonPolitico.optJSONArray("phones") != null) {
                    phone = jsonPolitico.optJSONArray("phones").getString(0);
                }

                String email = "";
                if(jsonPolitico.optJSONArray("emails") != null) {
                    phone = jsonPolitico.optJSONArray("emails").getString(0);
                }

                String twitter = safeGetArrayIndex(jsonPolitico, "channels", 0, "id");
                String picUrl = jsonPolitico.optString("photoUrl");
                String gender = "";
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
                        .setGender(gender)
                        .setDistrict(district)
                        .setElectionDate(electionDate)
                        .build(name);

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

    public String setTitle(String title) {
        if (title.equals("Sen")) {
            return "Senator";
        } else if (title.equals("Rep")) {
            return "Representative";
        } else if (title.equals("Del")) {
            return "Delegate";
        } else if (title.equals("Com")) {
            return "Com";
        } else {
            return title;
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

    public String getContactFormUrl(String bioguideId){
        JSONObject reps = JsonUtil.getJsonFromResource(R.raw.contact_forms);
        if(reps.has(bioguideId)){
            return reps.optString(bioguideId);
        }
        return "";
    }
    @Override
    public RepresentativesManager.RepresentativesType getRepresentativeType() {
        return RepresentativesManager.RepresentativesType.CONGRESS;
    }
}